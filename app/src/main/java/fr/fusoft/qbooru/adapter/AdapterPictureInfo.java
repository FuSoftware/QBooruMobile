package fr.fusoft.qbooru.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import fr.fusoft.qbooru.R;
import fr.fusoft.qbooru.model.BooruPicture;

/**
 * Created by Florent on 06/03/2016.
 */
public class AdapterPictureInfo extends BaseAdapter {
    private List<String> keys;
    private List<String> data;

    private LayoutInflater mInflater;


    public AdapterPictureInfo(Context c,BooruPicture image ) {
        mInflater = LayoutInflater.from(c);
        this.keys = image.getDataKeys();
        this.data = image.getDataList();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return keys.size();
    }

    public List<String> getKeys(){
        return this.keys;
    }
    public List<String> getData(){
        return this.data;
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
            convertView = mInflater.inflate(R.layout.image_info_item, null);
            holder = new ViewHolder();
            holder.infoKey = (TextView) convertView.findViewById(R.id.infoKey);
            holder.infoData = (TextView) convertView.findViewById(R.id.infoData);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.infoKey.setText(keys.get(position));
        holder.infoData.setText(data.get(position));
        //Log.d("MainGrid", "Loading at pos " + (position+1) + " ID " + web.get(position));
        return convertView;
    }

    static class ViewHolder {
        TextView infoKey;
        TextView infoData;
    }
}
