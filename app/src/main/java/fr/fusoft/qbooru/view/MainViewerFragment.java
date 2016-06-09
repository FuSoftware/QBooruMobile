package fr.fusoft.qbooru.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import fr.fusoft.qbooru.ExternalStorageManager;
import fr.fusoft.qbooru.MainViewer;
import fr.fusoft.qbooru.model.BooruSearchEngine;
import fr.fusoft.qbooru.PictureViewer;
import fr.fusoft.qbooru.QBooruUtils;
import fr.fusoft.qbooru.R;
import fr.fusoft.qbooru.adapter.MainGrid;
import fr.fusoft.qbooru.model.BooruPicture;
import fr.fusoft.qbooru.model.BooruSite;
import fr.fusoft.qbooru.model.BooruSiteDB;
import fr.fusoft.qbooru.model.SearchFilter;
import fr.fusoft.qbooru.network.ConnectionManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainViewerFragment extends Fragment {

    private View  myFragmentView;
    String LOG_TAG = "ViewerFragment";
    private BooruSite site;
    List<String> last_data = new ArrayList<>();
    BooruSearchEngine engine = new BooruSearchEngine();
    HashMap<Integer, String> saved_thumb_paths = new HashMap<>();
    List<BooruPicture> saved_pictures = new ArrayList<>();
    boolean loading = false;
    int index = 0;
    int scroll = 0;
    int selected_index = 0;
    int gridX = 0;
    int gridY = 0;
    boolean reload_search = true;
    SearchTask searchTask = new SearchTask();
    MainGrid adapter;
    private List<String> mTagList = new ArrayList<>();
    boolean resumed = false;

    Bundle savedState;

    public MainViewerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_main_viewer, container, false);
        return myFragmentView;
    }

    public void onStart(){
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInstance(outState);

    }

    public void onPause(){
        super.onPause();
        savedState = new Bundle();
        saveInstance(savedState);
    }

    public void saveInstance(Bundle outState){

        GridView gridView = (GridView) myFragmentView.findViewById(R.id.gridViewMainView);

        if(selected_index == 0){
            //Tabs got switched
            selected_index = gridView.getFirstVisiblePosition();
        }

        if(adapter != null){
            outState.putSerializable("Thumbs", adapter.saveThumbs());
            outState.putParcelableArrayList("Pictures", new ArrayList<>(adapter.getPictures()));
        }else{
            outState.putSerializable("Thumbs", new HashMap<>());
            outState.putParcelableArrayList("Pictures", new ArrayList<Parcelable>());
        }
        outState.putSerializable("Engine", engine);
        outState.putSerializable("Site", site);
        outState.putInt("Scroll", selected_index);

        savedState = outState;

        Log.d(LOG_TAG, "Instance of Fragment saved");
    }

    public void onResume(){
        super.onResume();

        if(savedState != null) {
            //probably orientation change
            Log.i(LOG_TAG,"Resuming and reloading Fragment");
            loadFromBundle(savedState);
            resumed = true;
        }

        loadUI();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            //probably orientation change
            Log.i(LOG_TAG,"Reloading Fragment");
            loadFromBundle(savedInstanceState);
            resumed = true;
        }else{
            if (site != null) {
                //returning from backstack, data is fine, do nothing
                Log.i(LOG_TAG,"Returning from backstack");
                loadFromBundle(savedState);
                resumed = true;
            } else {
                //newly created, compute data
                Log.w(LOG_TAG,"Fragment has no saved instance");
            }
        }

    }

    public void loadFromBundle(Bundle savedInstanceState){
        engine = (BooruSearchEngine) savedInstanceState.getSerializable("Engine");
        saved_thumb_paths = (HashMap<Integer, String>) savedInstanceState.getSerializable("Thumbs");
        saved_pictures = savedInstanceState.getParcelableArrayList("Pictures");
        site = (BooruSite) savedInstanceState.getSerializable("Site");
        scroll = savedInstanceState.getInt("Scroll");
        Log.i(LOG_TAG, "Reloaded data for booru " + site.getName() + " with " + saved_pictures.size() + " pics and " + saved_thumb_paths.size() + " thumbs at " + scroll);
    }

    public void loadUI(){
        if(resumed){
            initGrid();
            GridView gridView = (GridView) myFragmentView.findViewById(R.id.gridViewMainView);
            adapter = new MainGrid(getActivity(), saved_pictures);
            //adapter.loadThumbs(saved_thumb_paths);
            //Load thumbs
            int i = 0;
            for(BooruPicture p : saved_pictures){
                new ThumbsLoader().execute(i);
                i++;
            }
            //Sets adapter
            gridView.setAdapter(adapter);
            gridView.setSelection(scroll);
        }else {
            if (getArguments() != null) {
                if (getArguments().containsKey("BooruSite")) {
                    site = (BooruSite) getArguments().getSerializable("BooruSite");
                    Log.i(LOG_TAG, "Starting new UI for booru " + site.getName());
                    checkCookie();
                    loadEngine(site);
                    initGrid();
                    new SearchTask().execute(true);
                }
            }
        }
    }

    public void checkCookie(){
        if(site.getLoginUrl() != null){
            new ConnectionManager().getCookie(site,"QBooru","xARGU3MjioyXh4DR");
        }
    }

    public void setFilters(SearchFilter filter){engine.setFilter(filter);}

    public void setTag(String tag){
        mTagList.clear();
        mTagList.add(tag);
    }

    public void addTag(String tag){
        mTagList.add(tag);
    }

    private void parseTags(String tagString){
        String[] tagArray = tagString.split(" ");

        for(String s : tagArray){
            Log.e(LOG_TAG, "Tag " + s);
        }

        mTagList.clear();
        mTagList.addAll(Arrays.asList(tagArray));
    }

    private void initGrid(){

        final GridView gridView = (GridView) myFragmentView.findViewById(R.id.gridViewMainView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BooruPicture pic = adapter.getPictures().get(position);
                selected_index = position;
                loadViewer(pic);
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            private void isScrollCompleted() {
                if (this.currentVisibleItemCount + this.currentFirstVisibleItem >= adapter.getPictures().size() - 4 && this.currentScrollState == SCROLL_STATE_IDLE) {
                    /*** In this way I detect if there's been a scroll which has completed ***/
                    /*** do the work! ***/
                    if (!loading) {
                        gridX = gridView.getScrollX();
                        gridY = gridView.getScrollY();
                        //Toast toast = Toast.makeText(getApplicationContext(), "Reached end of scroll", Toast.LENGTH_SHORT);toast.show();
                        loadSearch(false);
                    }

                } else if (this.currentFirstVisibleItem == 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
                    Toast toast = Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_SHORT);
                    toast.show();
                    loadSearch(true);
                }
            }
        });

    }

    public View getViewByPosition(int position) {
        GridView gridView = (GridView) myFragmentView.findViewById(R.id.gridViewMainView);
        int firstPosition = gridView.getFirstVisiblePosition();
        int lastPosition = gridView.getLastVisiblePosition();

        if ((position < firstPosition) || (position > lastPosition))
            return null;

        return gridView.getChildAt(position - firstPosition);
    }

    public void loadTagAdderDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add a Tag");
        builder.setMessage("Only enter 1 tag");

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Ex : touhou");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText text = (EditText) myFragmentView.findViewById(R.id.editTextTagAdder);
                addTag(text.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void loadTagDeleteDialog(int pos){
        final AlertDialog.Builder searchDialog = new AlertDialog.Builder(getActivity());
        searchDialog.setMessage("Delete Tag ?");
        searchDialog.setCancelable(true);

        final int position = pos;

        searchDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mTagList.remove(position);
            }
        });
        searchDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = searchDialog.create();
        dialog.show();
    }

    public void loadEngine(BooruSite site){
        engine = new BooruSearchEngine(site);
    }

    public void loadThumbs(List<BooruPicture> pictures){
        int start = adapter.getPictures().size() - pictures.size();

        for(int i=0;i<pictures.size();i++){
            new ThumbsLoader().execute(start + i);
        }
    }

    public void loadPictures(List<BooruPicture> pictures){
        GridView gridView = (GridView) myFragmentView.findViewById(R.id.gridViewMainView);

        Log.d(LOG_TAG, "Loading the " + pictures.size() + " pictures on the UI");

        if (reload_search) {
            adapter = new MainGrid(getContext(), pictures);
            gridView.setAdapter(adapter);
        }else{
            adapter.addContent(pictures);
            gridView.invalidateViews();
        }

        loadThumbs(pictures);
    }

    public void loadViewer(BooruPicture picture){
        Intent intent = new Intent(getActivity(), PictureViewer.class);
        intent.putExtra("Picture", (Serializable) picture);
        getActivity().startActivityForResult(intent, MainViewer.VIEWER_REQUEST);
    }

    public void showTagPicker(){
        //set up dialog

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tags");
        builder.setMessage("Enter the tags as you would on any booru site");

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("touhou hat moriya_suwako");
        input.setText(engine.getTagsString());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tags = input.getText().toString().toLowerCase().trim();
                parseTags(tags);
                Toast toast = Toast.makeText(getActivity(), "Searching for " + tags, Toast.LENGTH_SHORT);toast.show();
                loadSearch(true);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void loadSearch(boolean reload){
        if(reload){
            Log.d(LOG_TAG,"Loading new search object");
            searchTask.cancel(true);
        }else{
            Log.d(LOG_TAG,"Loading old search object");
        }
        searchTask = new SearchTask();
        searchTask.execute(reload);
    }

    public class SearchTask extends AsyncTask<Boolean, Void, List<BooruPicture>> {
        protected List<BooruPicture> doInBackground(Boolean... reload) {
            loading = true;
            reload_search = reload[0];

            List<BooruPicture> pics ;
            if(reload[0]) {
                index = 0;

                gridX = 0;
                gridY = 0;

                pics = engine.search(mTagList);
            }else{
                pics = engine.search(engine.getTags(),engine.getPage() + 1);
            }

            return pics;
        }

        protected void onPostExecute(List<BooruPicture> pictures){

            if(pictures.isEmpty()){
                Toast toast = Toast.makeText(getActivity(), "No results", Toast.LENGTH_SHORT);toast.show();
            }

            loadPictures(pictures);
            GridView gridView = (GridView) myFragmentView.findViewById(R.id.gridViewMainView);
            gridView.scrollTo(gridX, gridY);
            loading = false;
        }
    }

    public class ThumbsLoader extends AsyncTask<Integer, Void, Drawable> {

        int id;
        int pic_id;

        protected Drawable doInBackground(Integer ...ids) {
            id = ids[0];

            if(id > adapter.getPictures().size()-1){
                //Shouldn't land there, but sometimes OOB happen
                return null;
            }

            BooruPicture p = adapter.getPictures().get(id);
            pic_id = p.getID();

            Drawable d= ExternalStorageManager.loadCachedDrawable(getContext(),p);

            if(d != null){
                //Log.d(LOG_TAG,"Loading thumb " + p.getFullID() + " from cache");
                return d;
            }else{
                //Log.d(LOG_TAG,"Loading thumb " + p.getFullID() + " from network");
                ExternalStorageManager.cacheDrawable(getContext(),QBooruUtils.loadDrawableFromUrl(p.getPreviewUrl()),p.getFullID());
                return QBooruUtils.loadDrawableFromUrl(p.getPreviewUrl());
            }
        }

        protected void onPostExecute(Drawable d){
            GridView gridView = (GridView) myFragmentView.findViewById(R.id.gridViewMainView);
            adapter.setDrawable(pic_id, d);
            //Refresh UI
            gridView.invalidateViews();
            //adapter.getReport();
        }

    }

    public BooruSite getBooruSite(){
        return this.site;
    }
    public SearchFilter getFilter(){return engine.getFilter();}

}
