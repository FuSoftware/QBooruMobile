package fr.fusoft.qbooru.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Florent on 29/02/2016.
 */
public enum BooruRating implements Serializable {
    ALL(0,"All","all",""),
    SAFE(1,"Safe","safe","s"),
    QUESTIONABLE(2, "Quest.","questionable","q"),
    EXPLICIT(3, "Explicit","explicit","e");


    private int id;
    private String name;
    private String rid ="";
    private String title;

    BooruRating(int id, String title, String name, String rid){
        this.id = id;
        this.name = name;
        this.rid = rid;
        this.title = title;
    }

    public int getID(){
        return this.id;
    }

    public String getRID(){
        return this.rid;
    }

    public String getName(){
        return this.name;
    }

    public String getTitle(){return this.title;}

    public String getRatingExtension(BooruSite.SiteType type){
        switch (type){
            case GELBOORU:
                return "rating:" + getName();
            case DANBOORU:
            case MOEBOORU:
            case E621:
                return "rating:" + getRID();
            default:
                return "";
        }
    }

    public static BooruRating getRatingFromID(int id){
        for(BooruRating r : BooruRating.values()){
            if(r.getID() == id){
                return r;
            }
        }
        return null;
    }

    public static BooruRating getRatingFromString(String rating){
        switch(rating){
            case "safe":
            case "s":
                return SAFE;
            case "questionable":
            case "q":
                return QUESTIONABLE;
            case "explicit":
            case "e":
                return EXPLICIT;
            default:
                return ALL;
        }
    }

    public static List<BooruRating> getList(){
        List<BooruRating> ratings = new ArrayList<>();
        ratings.add(BooruRating.ALL);
        ratings.add(BooruRating.SAFE);
        ratings.add(BooruRating.QUESTIONABLE);
        ratings.add(BooruRating.EXPLICIT);
        return ratings;
    }
}
