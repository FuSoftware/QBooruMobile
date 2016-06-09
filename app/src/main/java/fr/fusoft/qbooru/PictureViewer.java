package fr.fusoft.qbooru;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.fusoft.qbooru.model.BooruPicture;
import fr.fusoft.qbooru.network.PictureDownloader;
import fr.fusoft.qbooru.view.PictureViewerFragment;
import fr.fusoft.qbooru.view.PictureViewerInfoFragment;
import fr.fusoft.qbooru.view.PictureViewerTagsFragment;

public class PictureViewer extends AppCompatActivity {

    private final static String LOG_TAG = "PicViewer";

    File path;

    private BooruPicture picture;
    private Drawable image;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_viewer);

        picture = (BooruPicture) getIntent().getSerializableExtra("Picture");

        setTitle(picture.getFullID());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pagerPictureViewer);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabsPictureViewer);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment picViewer = new PictureViewerFragment();
        Fragment picInfo = new PictureViewerInfoFragment();
        Fragment picTags = new PictureViewerTagsFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("Picture", this.picture);

        picViewer.setArguments(bundle);
        picInfo.setArguments(bundle);
        picTags.setArguments(bundle);

        adapter.addFragment(picViewer, "Picture");
        adapter.addFragment(picInfo, "Info");
        adapter.addFragment(picTags, "Tags");
        viewPager.setAdapter(adapter);
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

    public void download(){
        PictureDownloader dl = new PictureDownloader(this);
        dl.download(picture);
    }

}
