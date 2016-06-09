package fr.fusoft.qbooru.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;

import fr.fusoft.qbooru.network.ConnectionManager;
import fr.fusoft.qbooru.QBooruUtils;
import fr.fusoft.qbooru.R;
import fr.fusoft.qbooru.model.BooruPicture;

/**
 * A placeholder fragment containing a simple view.
 */
public class PictureViewerFragment extends Fragment {


    BooruPicture picture;
    Drawable image;
    private View myFragmentView;
    private String LOG_TAG = "PicViewerFrag";

    PictureLoaderTask loadTask = new PictureLoaderTask();

    public PictureViewerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        picture = (BooruPicture) getArguments().getSerializable("Picture");
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPicture(picture);
        ImageView view = (ImageView) myFragmentView.findViewById(R.id.imageViewPicViewer);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicture();
            }
        });
    }

    public void onStop(){
        super.onStop();
        loadTask.cancel(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_picture_viewer, container, false);
        return myFragmentView;
    }

    public void loadPicture(BooruPicture pic){
        String url = pic.getSampleUrl();
        loadTask = new PictureLoaderTask();
        loadTask.execute(url);
    }

    public void showPicture(){
        File file = new File(getActivity().getCacheDir(),picture.getSamplePath());
        Uri uri = Uri.fromFile(file);

        Log.d(LOG_TAG,"Showing pic at " + file.getAbsolutePath());

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    public void setProgressBarVisibility(boolean show){
        ProgressBar spinner;
        spinner = (ProgressBar) myFragmentView.findViewById(R.id.progressBarViewItem);

        if(show){
            spinner.setVisibility(View.VISIBLE);
        }else{
            spinner.setVisibility(View.GONE);
        }
    }

    public class PictureLoaderTask extends AsyncTask<String, Void,Drawable> {

        protected void onPreExecute(){
            setProgressBarVisibility(true);
        }

        protected Drawable doInBackground(String... urls) {
            File file = new File(getActivity().getCacheDir(),picture.getSamplePath());
            if(file.exists()){
                //Loads local file
                Log.d(LOG_TAG, "Loading thumbnail " + file.getAbsolutePath());
                image = Drawable.createFromPath(file.getAbsolutePath());
            }else{
                //Loads url
                Log.d(LOG_TAG, "Downloading thumbnail " + urls[0]);
                //image = QBooruUtils.loadDrawableFromUrl(urls[0]);
                image = new ConnectionManager().downloadFromUrl(urls[0]);

                //Saves url
                Log.d(LOG_TAG, "Saving thumbnail " + file.getAbsolutePath());
                QBooruUtils.saveDrawableToFile(getActivity().getCacheDir(), picture.getSamplePath(), image, Bitmap.CompressFormat.JPEG, 100);
            }


            return image;
        }

        protected void onPostExecute(Drawable picture){
            ImageView view = (ImageView) myFragmentView.findViewById(R.id.imageViewPicViewer);
            view.setImageDrawable(picture);
            setProgressBarVisibility(false);
        }
    }

}
