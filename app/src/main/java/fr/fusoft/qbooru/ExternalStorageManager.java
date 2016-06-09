package fr.fusoft.qbooru;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.GridView;

import java.io.File;

import fr.fusoft.qbooru.model.BooruPicture;
import fr.fusoft.qbooru.model.BooruSite;

/**
 * Created by Florent on 28/02/2016.
 */
public class ExternalStorageManager {

    private static String DOWNLOAD_FOLDER = "QBooru";

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("STR_MGR", "Directory not created");
        }
        return file;
    }

    public static File getPictureStorageDir(String albumName) {
        return  getAlbumStorageDir(DOWNLOAD_FOLDER);
    }

    public static String cacheDrawable(Context c, Drawable d, String id){
        File cache = c.getCacheDir();
        File file = new File(cache,id);

        if(!file.exists()){
            QBooruUtils.saveDrawableToFile(c.getCacheDir(),id,d, Bitmap.CompressFormat.PNG,100);
        }

        return file.getAbsolutePath();
    }

    public static Drawable loadCachedDrawable(String path){
        return Drawable.createFromPath(path);
    }

    public static Drawable loadCachedDrawable(Context c, BooruPicture pic){
        File cache = c.getCacheDir();
        File file = new File(cache,pic.getFullID());
        if(file.exists()){
            return Drawable.createFromPath(file.getAbsolutePath());
        }else{
            return null;
        }
    }

    public static void clearCache(Context c){
        try {
            File dir = c.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            Log.e("CacheDelete","Cache couldn't be cleared : " + e.getMessage());
        }
    }

    public static boolean deleteDir(File dir) {
        if(dir != null) {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            // The directory is now empty so delete it
            return dir.delete();
        }else{
            return false;
        }

    }
}
