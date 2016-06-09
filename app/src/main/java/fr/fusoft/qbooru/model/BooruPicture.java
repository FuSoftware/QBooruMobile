package fr.fusoft.qbooru.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Florent on 28/02/2016.
 */
public class BooruPicture implements Serializable, Parcelable {

    private static final String LOG_TAG = "BooruPicture";
    private BooruSite parent;

    /*Data*/
    int id = 0;
    String created_at = "";
    String author = "";
    String source = "";

    int score = 0;
    int file_size = 0;

    List<String> tags_array = new ArrayList<>();
    String tags = "";

    String file_url = "";
    int width = 0;
    int height = 0;

    String preview_url = "";
    int preview_height = 0;
    int preview_width = 0;
    int actual_preview_height = 0;
    int actual_preview_width = 0;

    String sample_url = "";
    int sample_width = 0;
    int sample_height = 0;
    int sample_file_size = 0;

    String full_url = "";
    int full_height = 0;
    int full_width = 0;
    int full_file_size = 0;

    String rating = "a";

    String sample_path = "";
    String download_path = "";
    String show_url = "";
    String ext = "";

    public BooruPicture(BooruSite parent){
        this.parent = parent;
    }

    public BooruPicture(BooruSite parent, String json_str) {
        this.parent = parent;
        loadData(json_str);
    }

    public BooruPicture(BooruSite parent, JSONObject json) {
        this.parent = parent;
        loadData(json);
    }

    public void loadData(String json_str){
        try {
            JSONObject jsonObj = new JSONObject(json_str);
            loadData(jsonObj);
        }catch(Exception e){
            Log.e(LOG_TAG,e.getMessage());
        }
    }

    public void loadData(JSONObject json){
        switch(parent.getType()){
            case DANBOORU:
                loadDanbooru(json);
                break;
            case MOEBOORU:
                loadMoebooru(json);
                break;
            case GELBOORU:
                loadGelbooru(json);
                break;
            case E621:
                loadE621(json);
                break;
        }
        generateMiscData();
        parseTags();
    }

    public void generateMiscData(){
        ext = full_url.substring(full_url.lastIndexOf('.')+1);
        sample_path = parent.getName() + "_" + getID() + ".jpg";
        download_path = getID() + "." + ext;
        show_url = parent.getUrlShow() + getID();
    }

    public void parseTags(){
        tags_array.clear();

        int pos_start = 0;
        int pos_end;

        if(!tags.isEmpty()) {
            pos_end = tags.indexOf(' ');

            while (pos_end > -1) {
                tags_array.add(tags.substring(pos_start, pos_end));
                pos_start = pos_end + 1;
                pos_end = tags.indexOf(' ', pos_start);
            }
            tags_array.add(tags.substring(pos_start, tags.length()));
        }
    }

    private void loadGelbooru(JSONObject json){
        try {
            String image = json.getString("image");
            String dir = json.getString("directory");

            String ext = ".jpg";
            if(parent.getUrl().equals("http://safebooru.org")){
                //Safebooru
                int i = image.lastIndexOf('.');
                if (i > 0) {
                    ext = image.substring(i);
                }
            }

            this.id = json.getInt("id");
            this.created_at = new Date(json.getLong("change") * 1000).toString();
            this.author = json.getString("owner");
            this.score = json.getInt("score");
            this.file_size = 0;
            this.full_url = parent.getUrl() + "/images/" + dir + "/" + image;
            this.width = json.getInt("width");
            this.height = json.getInt("height");
            this.preview_url = parent.getUrl() + "/thumbnails/" + dir + "/thumbnail_" + image.substring(0, image.lastIndexOf('.')) + ext;

            if (json.getBoolean("sample")) {
                this.sample_url = parent.getUrl() + "/samples/" + dir + "/sample_" + image.substring(0, image.lastIndexOf('.')) + ext;
            } else {
                this.sample_url = parent.getUrl() + "/images/" + dir + "/" + image;
            }
            this.tags = json.getString("tags");
            this.rating = json.getString("rating");
        }catch(Exception e){
            Log.e(LOG_TAG,e.getMessage());
        }
    }

    private void loadDanbooru(JSONObject json){
        try {
            this.id = json.getInt("id");
            this.created_at = json.getString("created_at");
            this.author = json.getString("uploader_name");
            this.source = json.getString("source");
            this.score = json.getInt("score");
            this.file_size = json.getInt("file_size");
            this.full_url = parent.getUrl() + json.getString("file_url");
            this.width = json.getInt("image_width");
            this.height = json.getInt("image_height");
            this.preview_url = parent.getUrl() + json.getString("preview_file_url");
            this.sample_url = parent.getUrl() + json.getString("large_file_url");
            this.rating = json.getString("rating");
            this.tags = json.getString("tag_string_general");
        }catch(Exception e){
            Log.e(LOG_TAG,e.getMessage());
        }
    }

    private void loadMoebooru(JSONObject json){
        try {
            this.id = json.getInt("id");
            this.created_at = new Date(json.getLong("created_at") * 1000).toString();
            this.author = json.getString("author");
            this.source = json.getString("source");
            this.score = json.getInt("score");
            this.file_size = json.getInt("file_size");
            this.full_url = json.getString("file_url");
            this.width = json.getInt("width");
            this.height = json.getInt("height");
            this.preview_url = json.getString("preview_url");
            this.sample_url = json.getString("sample_url");
            this.rating = json.getString("rating");
            this.tags = json.getString("tags");
        }catch(Exception e){
            Log.e(LOG_TAG,e.getMessage());
        }
    }

    private void loadE621(JSONObject json){
        try {
            this.id = json.getInt("id");
            this.created_at = new Date(json.getJSONObject("created_at").getLong("s") * 1000).toString();
            this.author = json.getString("author");
            this.source = json.getString("source");
            this.score = json.getInt("score");
            this.file_size = json.getInt("file_size");
            this.full_url = json.getString("file_url");
            this.width = json.getInt("width");
            this.height = json.getInt("height");
            this.preview_url = json.getString("preview_url");
            this.sample_url = json.getString("sample_url");
            this.rating = json.getString("rating");
            this.tags = json.getString("tags");
        }catch(Exception e){
            Log.e(LOG_TAG,e.getMessage());
        }
    }

    public int getID(){return this.id;}
    public String getFullUrl(){return this.full_url;}
    public String getSampleUrl(){return this.sample_url;}
    public String getPreviewUrl(){return this.preview_url;}
    public String getSizeString(){return width + "x" + height;}
    public String getFullID(){return parent.getName() + " #" + Integer.toString(id);}
    public String getDownloadPath(){return this.download_path;}
    public String getSamplePath(){return this.sample_path;}
    public String getShowUrl(){return this.show_url;}
    public String getFormat(){return this.ext;}
    public BooruSite getParent(){return this.parent;}

    public List<String> getTagsArray(){return this.tags_array;}
    public List<String> getDataKeys(){
        List<String> keys = new ArrayList<>();

        keys.add("ID"); //ID
        keys.add("Author"); //Author
        keys.add("Created"); //Creation
        keys.add("Source"); //Source
        keys.add("Size"); //Size
        keys.add("Rating"); //Rating
        keys.add("Score"); //Score

        return keys;
    }
    public List<String> getDataList(){
        List<String> data = new ArrayList<>();

        data.add(getFullID()); //ID
        data.add(author); //Author
        data.add(created_at); //Creation
        data.add(source); //Source
        data.add(getSizeString()); //Size
        data.add(BooruRating.getRatingFromString(rating).getName()); //Rating
        data.add(Integer.toString(score)); //Score

        return data;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeSerializable(parent);
        out.writeInt(id);
        out.writeString(created_at);
        out.writeString(author);
        out.writeString(source);
        out.writeInt(score);
        out.writeInt(file_size);
        out.writeString(full_url);
        out.writeInt(width);
        out.writeInt(height);
        out.writeString(preview_url);
        out.writeString(sample_url);
        out.writeString(rating);
        out.writeString(tags);
    }

    public static final Parcelable.Creator<BooruPicture> CREATOR
            = new Parcelable.Creator<BooruPicture>() {
        public BooruPicture createFromParcel(Parcel in) {
            return new BooruPicture(in);
        }

        public BooruPicture[] newArray(int size) {
            return new BooruPicture[size];
        }
    };

    private BooruPicture(Parcel in) {
        parent = (BooruSite) in.readSerializable();

        this.id = in.readInt();
        this.created_at = in.readString();
        this.author = in.readString();
        this.source = in.readString();
        this.score = in.readInt();
        this.file_size = in.readInt();
        this.full_url = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.preview_url = in.readString();
        this.sample_url = in.readString();
        this.rating = in.readString();
        this.tags = in.readString();

        generateMiscData();
        parseTags();
    }
}
