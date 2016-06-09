package fr.fusoft.qbooru.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.fusoft.qbooru.ExternalStorageManager;
import fr.fusoft.qbooru.QBooruUtils;
import fr.fusoft.qbooru.R;
import fr.fusoft.qbooru.model.BooruPicture;

/**
 * Created by Florent on 02/03/2016.
 */
public class MainGrid extends BaseAdapter {

    private List<BooruPicture> picture = new ArrayList<>();
    private List<String> web = new ArrayList<>();
    HashMap<Integer, Drawable> drawables = new HashMap<>();
    private LayoutInflater mInflater;
    boolean add = false;
    private Context c;


    public MainGrid(Context c,List<BooruPicture> pics) {
        this.picture = pics;
        add = false;
        generateStrings(pics);
        this.c = c;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addContent(List<BooruPicture> pics) {
        this.picture.addAll(pics);
        add = true;
        generateStrings(pics);
        this.notifyDataSetChanged();
    }

    public void getReport(){
        Log.w("MainGrid", "Adapter holds " + picture.size() + " pics and " + getThumbs().size() + " thumbs");
    }

    public void setDrawable(int pos, Drawable d){
        drawables.put(pos, d);
        this.notifyDataSetChanged();
    }
    public void setDrawableMap(HashMap<Integer, Drawable> drawables){this.drawables = drawables;}

    private void generateStrings(List<BooruPicture> new_pictures){
        List<String> sizes = new ArrayList<>();

        for(BooruPicture p : new_pictures){
            sizes.add(p.getSizeString());
        }

        if(add){
            web.addAll(sizes);
        }else{
            web = sizes;
        }
    }

    public List<BooruPicture> getPictures(){return this.picture;}
    public HashMap<Integer, Drawable> getThumbs(){return this.drawables;}

    public HashMap<Integer, String> saveThumbs(){
        HashMap<Integer, String> thumb_paths = new HashMap<>();

        for(BooruPicture p : getPictures()){
            Drawable d = drawables.get(p.getID());
            if(d != null){
                thumb_paths.put(p.getID(), ExternalStorageManager.cacheDrawable(c,d,p.getFullID()));
            }else{
                Log.e("GridAdapter","null drawable while saving " + p.getFullID());
            }
        }

        return thumb_paths;
    }

    public void loadThumbs(HashMap<Integer, String> paths){
        for(BooruPicture p : getPictures()){
            Drawable d = ExternalStorageManager.loadCachedDrawable(paths.get(p.getID()));
            drawables.put(p.getID(),d);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return web.size();
    }

    public List<String> getSizes(){
        return this.web;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.image_view_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.imageViewItemText);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewItemImage);

            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBarMainViewItem);
            holder.progressBar.setVisibility(View.VISIBLE);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(web.size() > 0) {holder.textView.setText(web.get(position));}

        Drawable d = drawables.get(picture.get(position).getID());
        if (d != null) {
            holder.progressBar.setVisibility(View.GONE);
            holder.imageView.setImageDrawable(d);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
        ProgressBar progressBar;
    }

    public void refresh(){
        this.notifyDataSetInvalidated();
    }
}
