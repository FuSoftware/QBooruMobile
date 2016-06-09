package fr.fusoft.qbooru.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.fusoft.qbooru.BuildConfig;
import fr.fusoft.qbooru.QBooruUtils;
import fr.fusoft.qbooru.network.*;

/**
 * Created by Florent on 28/02/2016.
 */
public class BooruSearchEngine implements Serializable {

    private final static int FIRST_PAGE = 1;
    private final static String LOG_TAG = "SearchEngine";
    private BooruSite site;

    private List<String> tags = new ArrayList<>();
    private List<String> tags_org = new ArrayList<>();
    private String tags_str = "";
    private int page = 1;
    private int limit = 20;
    SearchFilter filter;

    public BooruSearchEngine(){
    }

    public BooruSearchEngine(BooruSite site){
        this.site = site;
        filter = new SearchFilter(site);
    }

    public BooruSearchEngine(BooruSite site, List<String> tags, int page){
        this.site = site;
        this.tags = tags;
        this.page = page;
    }

    private String generateUrlExtension(){
        Map<String,String> params = new HashMap<String,String>();
        String ext="";

        tags_org = tags;

        //Converts the tags to a GET format
        tags_str = "";

        for(String s : tags){
            tags_str += s + "+";
        }

        if(site.useFilters()) {
            String filters_str = filter.generateTags();

            //Generates the filters
            if (filters_str != null && !filters_str.isEmpty() && !filters_str.equals("null")) {
                tags_str += filters_str;
            }
        }

        switch(site.getType()){
            case GELBOORU:
                if(limit>0){params.put("limit",Integer.toString(limit));}
                if(!tags_str.isEmpty() && tags != null){params.put("tags",tags_str);}
                if(page>0){params.put("pid",Integer.toString(page-1));}
                ext = "&";
                break;

            case E621:
            case DANBOORU:
                if(limit>0){params.put("limit",Integer.toString(limit));}
                if(!tags_str.isEmpty() && tags != null){params.put("tags",tags_str);}
                if(page>0){params.put("page",Integer.toString(page));}
                ext = "?";
                break;

            case MOEBOORU:
                params.put("api_version", "2");
                if(limit>0){params.put("limit",Integer.toString(limit));}
                if(!tags_str.isEmpty() && tags != null){params.put("tags",tags_str);}
                if(page>0){params.put("page",Integer.toString(page));}
                ext = "?";
                break;
        }

        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ext += pair.getKey() + "=" + pair.getValue() + "&";
            it.remove(); // avoids a ConcurrentModificationException
        }

        ext = ext.substring(0,ext.length()-1); // Deletes the last "&"
        return ext;
    }

    public List<BooruPicture> search(){
        String url = site.getUrlSearch();
        String params = generateUrlExtension();

        Log.d(LOG_TAG, "Loading search with parameters" + params);

        String rep = new ConnectionManager().execGetRequest(url, params);

        //QBooruUtils.printLargeString(rep);

        return parseJson(rep);
    }

    public List<BooruPicture> search(List<String> tags){
        setTags(tags);
        setPage(FIRST_PAGE);
        return search();
    }

    public List<BooruPicture> search(List<String> tags, int page){
        setTags(tags);
        setPage(page);
        return search();
    }

    public List<BooruPicture> parseJson(String jsonStr){
        Log.d(LOG_TAG, "Parsing search file");
        switch(site.getType()){
            case GELBOORU:
                return parseGelbooru(jsonStr);
            case MOEBOORU:
                return parseMoebooru(jsonStr);
            case DANBOORU:
                return parseDanbooru(jsonStr);
            case E621:
                return parseE621(jsonStr);
            default:
                return new ArrayList<>();
        }
    }

    public List<BooruPicture> parseDanbooru(String jsonStr){
        List<BooruPicture> pictures = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(jsonStr);
            Log.i(LOG_TAG,"Loading " + data.length() + " pictures for " + site.getName());
            for(int i = 0; i<data.length();i++){
                JSONObject obj = data.getJSONObject(i);
                pictures.add(new BooruPicture(site,obj));
            }
        }catch(Exception e){
            Log.e(LOG_TAG,e.getMessage());
        }
        return pictures;
    }

    public List<BooruPicture> parseE621(String jsonStr){
        List<BooruPicture> pictures = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(jsonStr);
            Log.i(LOG_TAG, "Loading " + data.length() + " pictures for " + site.getName());
            for(int i = 0; i<data.length();i++){
                JSONObject obj = data.getJSONObject(i);
                pictures.add(new BooruPicture(site,obj));
            }
        }catch(Exception e){
            Log.e(LOG_TAG,e.getMessage());
        }
        return pictures;
    }

    public List<BooruPicture> parseMoebooru(String jsonStr){
        List<BooruPicture> pictures = new ArrayList<>();
        Log.d(LOG_TAG,"Is JSON : " + QBooruUtils.isJSONValid(jsonStr));
        try {
            JSONArray data;
            Object json = new JSONTokener(jsonStr).nextValue();
            if (json instanceof JSONObject){
                //JSON starts with a "posts" object
                data = ((JSONObject) json).getJSONArray("posts");
            } else if (json instanceof JSONArray){
                //JSON is an array
                data = new JSONArray(jsonStr);
            }else{
                return null;
            }
                //you have an array
            Log.d(LOG_TAG, "Loading " + data.length() + " pictures for " + site.getName());
            for(int i = 0; i<data.length();i++){
                pictures.add(new BooruPicture(site,data.getJSONObject(i)));
            }
            Log.d(LOG_TAG,"Pictures loaded");
        }catch(Exception e){
            Log.e(LOG_TAG, e.toString());
        }
        return pictures;
    }

    public List<BooruPicture> parseGelbooru(String jsonStr){
        List<BooruPicture> pictures = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(jsonStr);
            Log.i(LOG_TAG, "Loading " + data.length() + " pictures for " + site.getName());
            for(int i = 0; i<data.length();i++){
                JSONObject obj = data.getJSONObject(i);
                pictures.add(new BooruPicture(site,obj));
            }
        }catch(Exception e){
            Log.e(LOG_TAG,e.getMessage());
        }
        return pictures;
    }

    public void setFilter(SearchFilter filter){this.filter = filter;}
    public SearchFilter getFilter(){return this.filter;}
    public void setTags(List<String> tags){this.tags = tags;}

    public void setPage(int page){this.page = page;}

    public void setBooru(BooruSite booru){this.site = booru;filter = new SearchFilter(site);}

    public List<String> getTags(){return this.tags_org;}
    public int getPage(){return this.page;}

    public String getTagsString()
    {
        String str = "";

        for(String s: tags_org){
            str += s + " ";
        }

        return str.trim();
    }
}
