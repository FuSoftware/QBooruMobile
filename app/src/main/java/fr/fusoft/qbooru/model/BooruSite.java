package fr.fusoft.qbooru.model;

import android.content.Context;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Florent on 28/02/2016.
 */
public class BooruSite implements Serializable {

    public enum SiteType {
        //Objets directement construits
        GELBOORU(0, "gelbooru", "/index.php?page=dapi&json=1&s=post&q=index", "/search.json", "/index.php?page=post&s=view&id=", "http://gelbooru.com/index.php?page=help&topic=dapi"),
        MOEBOORU(1, "moebooru", "/post.json", "post.json", "/post/show/","https://konachan.com/help/api"),
        DANBOORU(2, "danbooru2", "/posts.json", "posts.json", "/posts/", "https://danbooru.donmai.us/wiki_pages/43568"),
        E621(3, "e621", "/post/index.json", "posts.json", "/post/show/", "https://e621.net/help/api");

        private String name = "";
        private String source = "";
        private String ext_search_url = "";
        private String ext_search_file = "";
        private String ext_show_url = "";
        private int id = -1;

        //Constructeur
        SiteType(int id, String name, String ext_search_url, String ext_search_file, String ext_show_url, String source){
            this.name = name;
            this.source = source;
            this.id = id;
            this.ext_search_url = ext_search_url;
            this.ext_search_file = ext_search_file;
            this.ext_show_url = ext_show_url;
        }

        public int getID(){
            return id;
        }

        public String getExtSearchUrl(){
            return this.ext_search_url;
        }

        public String getExtSearchFile(){
            return this.ext_search_file;
        }

        public String getExtShowUrl(){
            return this.ext_show_url;
        }

        public String getName(){
            return name;
        }

        public static SiteType getTypeFromID(int id){
            for(SiteType s : SiteType.values()){
                if(s.getID() == id){
                    return s;
                }
            }
            return null;
        }
    }

    private String name;
    private SiteType type;

    private String url_main;
    private String url_search;
    private String url_show;
    private String login_url;

    private int safe_ver = 0;
    private int login_required;

    public BooruSite(){
        this("",null,"","","");
    }

    public BooruSite(String name, SiteType type, String url_main, String url_search, String url_show){
        this.name = name;
        this.type = type;
        this.url_main = url_main;
        this.url_search = url_search;
        this.url_show = url_show;
    }

    public BooruSite(String name, String url, SiteType type){
        this.name = name;
        this.url_main = url;
        this.type = type;
        generateSite(type);
    }

    public BooruSite(String name, String url, int type_id){
        this(name, url, SiteType.getTypeFromID(type_id));
    }

    public void generateUrls(){
        if(type != null){
            generateSite(type);
        }
    }

    public String toString(){
        return name + ", " + type.getName() + "-based website hosted at " + url_main;
    }

    public void generateSite(SiteType type) {
        //URLs
        this.url_search = this.url_main + type.getExtSearchUrl();
        this.url_show = this.url_main + type.getExtShowUrl();
    }

    public String getName(){
        return this.name;
    }
    public String getUrl(){
        return this.url_main;
    }
    public String getUrlSearch(){
        return this.url_search;
    }
    public String getUrlShow(){
        return this.url_show;
    }
    public String getTypeString(){
        return type.getName();
    }
    public int getTypeID(){
        return type.getID();
    }
    public SiteType getType(){
        return this.type;
    }
    public int getSFW(){return this.safe_ver;}
    public boolean isSFW(){return safe_ver == 1;}
    public String getLoginUrl(){if(login_required == 1){return login_url;}else{return null;}}

    public void setName(String name){
        this.name = name;
    }
    public void setUrl(String url){
        this.url_main = url;
    }
    public void setUrlSearch(String url){
        this.url_search = url;
    }
    public void setUrlShow(String url){
        this.url_show = url;
    }
    public void setTypeString(String type){
        this.type = SiteType.valueOf(type);
    }
    public void setTypeFromID(int id){
        this.type = SiteType.getTypeFromID(id);
    }
    public void setType(SiteType type){
        this.type = type;
    }
    public void setSFW(int r){this.safe_ver = r;}
    public void setLoginRequired(int required, String url){this.login_required = required;this.login_url = url;}

    public boolean useFilters(){return !name.equals("http://danbooru.donmai.us");}
}
