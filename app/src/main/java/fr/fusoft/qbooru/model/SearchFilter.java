package fr.fusoft.qbooru.model;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.fusoft.qbooru.BuildConfig;

/**
 * Created by Florent on 31/03/2016.
 */
public class SearchFilter implements Serializable {
    //Date Filters
    public static final int DATE_FILTER_ANY = 0;
    public static final int DATE_FILTER_SELECT = 1;

    //Sorting Filters
    public static  final int SORT_FILTER_DATE = 0;
    public static  final int SORT_FILTER_DATE_REVERSE = 1;
    public static  final int SORT_FILTER_SCORE = 2;

    //Score Filters
    public static  final int SCORE_FILTER_SUP = 1;
    public static  final int SCORE_FILTER_INF = 2;
    public static  final int SCORE_FILTER_EQU = 3;

    BooruRating rating;

    int H = 0;
    int W = 0;
    int score = 0;

    int date_filter = 0;
    int sorting_filter = 0;
    int score_filter = 0;

    BooruSite site;

    Date date = new Date();

    public SearchFilter(BooruSite site){
        this.site = site;
    }

    public void setSize(int w, int h){
        H = h;
        W = w;
    }

    public void setRating(BooruRating rating){this.rating = rating;}

    public void setDateFilter(int f){
        date_filter = f;
    }

    public void setSortingFilter(int f){
        sorting_filter = f;
    }

    public void setScoreFilter(int comp){
        score_filter = comp;
    }

    public void setScore(int s){
        score = s;
    }

    public void setDate(Date d){
        this.date = d;
    }

    public String generateTags(){
        String tags = "";

        if(getSizeTag() != null){
            tags += getSizeTag() + "+";
        }
        if(getScoreTag() != null){
            tags += getScoreTag() + "+";
        }
        if(getDateTag() != null){
            tags += getDateTag() + "+";
        }
        if(generateOrder() != null){
            tags += generateOrder() + "+";
        }
        if(getRatingTag() != null){
            tags += getRatingTag() + "+";
        }

        if(tags.length() > 0 && tags.charAt(tags.length()-1)=='+'){
            tags = tags.substring(0, tags.length()-1);
        }

        Log.d("Filters","Filters for " + site.getName() + " " + tags);

        return tags;
    }

    public String getSizeTag(){
        String tag = "";

        if(H > 0){
            tag += "height:" + H;
        }

        if(W > 0){
            tag += "+width:" + W;
        }

        return tag;
    }

    public String getScoreTag(){
        String comp = "";

        switch(score_filter){
            case SCORE_FILTER_SUP:
                comp = ">";
                break;
            case SCORE_FILTER_INF:
                comp = "<";
                break;
            case SCORE_FILTER_EQU:
                comp = "=";
                break;
        }

        if(score > 0){
            return "score:" + comp + score;
        }else{
            return null;
        }
    }

    public String getDateTag(){
        if(date_filter > 0) {
            switch (site.getType()) {
                case GELBOORU:
                    return null;
                case DANBOORU:
                case MOEBOORU:
                case E621:
                    return "date:<" + generateDate();
            }
        }else{
            return null;
        }

        return null;
    }

    public String getRatingTag(){

        //Adds the rating
        if(BuildConfig.SAFE_BUILD){
            return BooruRating.SAFE.getRatingExtension(site.getType());
        }

        if(rating != null){
            Log.e("Filter","Using tag " + rating.getName());
            if(rating != BooruRating.ALL) {
                return rating.getRatingExtension(site.getType());
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    public String generateDate(){
        switch (site.getType()) {
            case GELBOORU:
                return null;
            case DANBOORU:
            case MOEBOORU:
            case E621:
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                return formatter.format(date);
            default:
                return null;
        }
    }

    public String generateOrder(){
        switch (site.getType()) {
            case GELBOORU:
                return null;
            case DANBOORU:
            case MOEBOORU:
            case E621:
                switch(sorting_filter){
                    case SORT_FILTER_DATE_REVERSE:
                        return "order:id";
                    case SORT_FILTER_SCORE:
                        return "order:score";
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    //Getters
    public int getW(){return this.W;}
    public int getH(){return this.H;}
    public int getDateFilter(){return this.date_filter;}
    public int getScoreFilter(){return this.score_filter;}
    public int getScore(){return this.score;}
    public int getSortingFilter(){return this.sorting_filter;}
    public BooruRating getR(){return this.rating;}
}
