package fr.fusoft.qbooru;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.fusoft.qbooru.model.BooruSite;
import fr.fusoft.qbooru.model.BooruSiteDB;
import fr.fusoft.qbooru.model.SearchFilter;
import fr.fusoft.qbooru.view.MainViewerFragment;

public class MainViewer extends AppCompatActivity {

    private final String LOG_TAG = "MainViewer";
    public static final int VIEWER_REQUEST = 143;
    public static final int FILTER_REQUEST = 102;

    List<BooruSite> sites;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    List<MainViewerFragment> fragments = new ArrayList<>();

    AlertDialog.Builder builderResult;
    AlertDialog dialog;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            Log.e(LOG_TAG, "Saved State found");
        }else{
            Log.e(LOG_TAG, "New Activity");
        }

        setContentView(R.layout.activity_main_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current_fragment = viewPager.getCurrentItem();
                fragments.get(current_fragment).showTagPicker();
            }
        });

        loadSites();

        viewPager = (ViewPager) findViewById(R.id.viewPagerMainViewer);
        setupViewPager(viewPager);

        /*
        if(BuildConfig.SAFE_BUILD) {
            builderResult = new AlertDialog.Builder(this);
            builderResult.setTitle("Error");
            builderResult.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            mAdView = (AdView) findViewById(R.id.adViewMainViewer);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("BAD41C3099F60BE96D65C9CEEEF6EB8C")  // An example device ID
                    .build();
            mAdView.loadAd(adRequest);
        }
        */
    }

    //Fires after the OnStop() state
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ExternalStorageManager.clearCache(this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        loadSites();

        int i = 0;
        for(BooruSite s : sites){
            Fragment f = new MainViewerFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("BooruSite", s);

            f.setArguments(bundle);


            fragments.add((MainViewerFragment) f);
            adapter.addFragment(f, s.getName());
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i=0;i<fragments.size();i++){
                    if(i != position) {
                        MainViewerFragment f = fragments.get(position);
                        if (f != null && f.isVisible() && !f.isMenuVisible()) {
                            f.onPause();
                        }
                    }
                }

                MainViewerFragment f = fragments.get(position);
                f.onResume();

                Log.d(LOG_TAG,"Working on " + f.getBooruSite().getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Log.e(LOG_TAG, viewPager.getAdapter().getCount() + " boorus loaded");

        tabLayout = (TabLayout) findViewById(R.id.tabsMainViewer);

        if(BuildConfig.SAFE_BUILD){
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }

        tabLayout.setupWithViewPager(viewPager);
    }

    public MainViewerFragment getFragmentAt(int pos){
        return (MainViewerFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPagerMainViewer + ":" + pos);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_viewer, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear_cache) {
            ExternalStorageManager.clearCache(this);
            return true;
        }

        if (id == R.id.action_set_filters) {
            loadFilters();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean search = false;


        int current_fragment = viewPager.getCurrentItem();

        MainViewerFragment f = getFragmentAt(current_fragment);

        Log.e(LOG_TAG, "Changing tag for fragment " + current_fragment);

        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case MainViewer.VIEWER_REQUEST:
                    //Tag request
                    Log.d(LOG_TAG, "Loading tag " + data.getStringExtra("Tag"));
                    if(data.getBooleanExtra("Reset",false)){
                        f.setTag(data.getStringExtra("Tag"));
                    }else{
                        f.addTag(data.getStringExtra("Tag"));
                    }

                    search = true;
                    break;

                case MainViewer.FILTER_REQUEST:
                    SearchFilter filter = (SearchFilter) data.getSerializableExtra("Filter");
                    Log.i(LOG_TAG,"Setting filter " +  filter.generateTags());
                    f.setFilters(filter);
                    search = true;
                    break;
            }

            if(search){
                f.loadSearch(true);
            }
        }

    }

    public void loadSites(){
        BooruSiteDB db = new BooruSiteDB(this);
        db.open();
        sites = db.getBooruSites();
        db.close();
    }

    public void loadFilters(){
        int current_fragment = viewPager.getCurrentItem();
        MainViewerFragment f = getFragmentAt(current_fragment);

        Intent intent = new Intent(this, SearchFiltersActivity.class);
        intent.putExtra("Booru",  f.getBooruSite());
        intent.putExtra("Filter",  f.getFilter());
        startActivityForResult(intent, MainViewer.FILTER_REQUEST);
    }

}
