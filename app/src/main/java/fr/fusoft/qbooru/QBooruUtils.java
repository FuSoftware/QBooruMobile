package fr.fusoft.qbooru;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Florent on 02/03/2016.
 */
public class QBooruUtils {

    public static String appName = "QBooru";


    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static void printLargeString(String str) {
        int maxLogSize = 1000;
        for (int i = 0; i <= str.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > str.length() ? str.length() : end;
            Log.v("LargeString", str.substring(start, end));
        }
    }

    public static Bitmap.CompressFormat getFormatFromString(String str){
        switch(str){
            case "jpg":
            case "jpeg":
                return Bitmap.CompressFormat.JPEG;
            case "png":
                return Bitmap.CompressFormat.PNG;
            case "webp":
                return Bitmap.CompressFormat.WEBP;
            default:
                return Bitmap.CompressFormat.JPEG;
        }
    }

    public static Drawable loadDrawableFromUrl(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            return Drawable.createFromStream(is, "url");
        } catch (Exception e) {
            Log.w("PictureDL","Couldn't download " + url + " " + e.toString());
            return null;
        }
    }

    public static Bitmap drawableToBitmap(Drawable d) {
        Bitmap bitmap = null;

        if (d instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) d;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(d.getIntrinsicWidth() <= 0 || d.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return bitmap;
    }
    public static boolean saveDrawableToFile(File dir, String fileName, Drawable d, Bitmap.CompressFormat format, int quality) {
        return saveBitmapToFile(dir, fileName, drawableToBitmap(d), format, quality);
    }
    public static boolean saveBitmapToFile(File dir, String fileName, Bitmap bm, Bitmap.CompressFormat format, int quality) {

        /*
        * Bitmap.CompressFormat can be PNG,JPEG or WEBP.
        *
        * quality goes from 1 to 100. (Percentage).
        *
        * dir you can get from many places like Environment.getExternalStorageDirectory() or mContext.getFilesDir()
        * depending on where you want to save the image.
        */

        File imageFile = new File(dir,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bm.compress(format,quality,fos);
            fos.close();
            return true;
        }
        catch (IOException e) {
            Log.e("FileSaver",e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }
    public static boolean fileExists(String path){
        File file = new File(path);
        return file.exists();
    }

    public static boolean fileExists(File file){
        return file.exists();
    }

    public static File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        }catch (IOException e) {
                // Error while creating file
        }
            return file;
        }

    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
        File dir = new File(file, albumName);

        //Creating QBooru Dir
        if (!file.exists()) {
            Log.w("Utils", appName + " doesn't exist");
            if (!file.mkdirs()) {
                Log.e("Utils", appName + " directory not created");
            }
        }

        //Creating Booru Dir
        if (!dir.exists()) {
            Log.w("Utils", albumName + " doesn't exist");
            if (!dir.mkdirs()) {
                Log.e("Utils", albumName + " directory not created");
            }
        }


        return dir;
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
