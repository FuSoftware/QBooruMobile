package fr.fusoft.qbooru.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.fusoft.qbooru.BuildConfig;


/**
 * Created by Florent on 28/02/2016.
 */
public class BooruSiteDB {

    private static final String LOG_TAG = "BooruSiteDB";

    private static final int DB_VERSION = 12;
    private static final String DB_NAME = "booru_sites.db";

    private SQLiteDatabase bdd;

    private Database maBaseSQLite;

    public BooruSiteDB(Context context){
        //On créer la BDD et sa table
        maBaseSQLite = new Database(context, DB_NAME, null, DB_VERSION);
    }

    public void open(){
        //on ouvre la BDD en écriture
        bdd = maBaseSQLite.getWritableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public long insertBooruSite(BooruSite site){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)

        ContentValues values = new ContentValues();
        values.put(Database.COL_NAME, site.getName());
        values.put(Database.COL_URL, site.getUrl());
        values.put(Database.COL_URL_SEARCH, site.getUrlSearch());
        values.put(Database.COL_URL_SHOW, site.getUrlShow());
        values.put(Database.COL_TYPE, site.getTypeID());
        values.put(Database.COL_SAFE, 0);
        values.put(Database.COL_LOGIN, 0);
        values.put(Database.COL_LOGIN_URL, "");

        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(Database.TABLE_SITES, null, values);
    }

    public int updateBooruSite(int id, BooruSite site){
        //La mise à jour d'un site dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simple préciser quelle site on doit mettre à jour grâce à l'ID
        //{COL_ID, COL_NAME, COL_URL, COL_URL_SEARCH, COL_URL_SHOW, COL_TYPE};

        ContentValues values = new ContentValues();
        values.put(Database.COL_NAME, site.getName());
        values.put(Database.COL_URL, site.getUrl());
        values.put(Database.COL_URL_SEARCH, site.getUrlSearch());
        values.put(Database.COL_URL_SHOW, site.getUrlShow());
        values.put(Database.COL_TYPE, site.getTypeID());

        return bdd.update(Database.TABLE_SITES, values, Database.COL_ID + " = " + id, null);
    }

    public int updateBooruSite(String name, BooruSite site){
        //La mise à jour d'un site dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simple préciser quelle site on doit mettre à jour grâce à l'ID
        //{COL_ID, COL_NAME, COL_URL, COL_URL_SEARCH, COL_URL_SHOW, COL_TYPE};

        ContentValues values = new ContentValues();
        values.put(Database.COL_NAME, site.getName());
        values.put(Database.COL_URL, site.getUrl());
        values.put(Database.COL_URL_SEARCH, site.getUrlSearch());
        values.put(Database.COL_URL_SHOW, site.getUrlShow());
        values.put(Database.COL_TYPE, site.getTypeString());

        return bdd.update(Database.TABLE_SITES, values, Database.COL_NAME + " = " + name, null);
    }

    public int removeBooruSiteWithID(int id){
        //Suppression d'un site de la BDD grâce à l'ID
        return bdd.delete(Database.TABLE_SITES, Database.COL_ID + " = " + id, null);
    }

    public int removeBooruSiteWithName(String name){
        //Suppression d'un site de la BDD grâce à l'ID
        return bdd.delete(Database.TABLE_SITES, Database.COL_NAME + " = " + name, null);
    }

    public List<BooruSite> getBooruSites(){
        Cursor c = bdd.query(Database.TABLE_SITES, Database.getBooruSiteColumns(), Database.COL_ID + ">=0", null, null, null, null);

        List<BooruSite> sites = new ArrayList<>();
        BooruSite s;

        Log.d(LOG_TAG, c.getCount() + " records to read");
        int i=0;

        while (c.moveToNext()) {
            //Log.d(LOG_TAG, "Reading record " + i);
            s = cursorToBooruSite(c);

            if(BuildConfig.SAFE_BUILD) {
                Log.d(LOG_TAG,"Website " + s.getName() + " is SFW : " + s.getSFW());
                if (s.isSFW()) {

                    sites.add(s);
                }
            }else{
                sites.add(s);
            }

            //Log.d(LOG_TAG, "Read record " + i++);
        }
        c.close();

        return sites;
    }

    public BooruSite getBooruSiteWithName(String name){
        //Récupère dans un Cursor les valeur correspondant à un site contenu dans la BDD (ici on sélectionne le site grâce à son nom)
        Cursor c = bdd.query(Database.TABLE_SITES, Database.getBooruSiteColumns(), Database.COL_NAME + " LIKE \"" + name +"\"", null, null, null, null);

        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        BooruSite s =  cursorToBooruSite(c);
        c.close();

        return s;
    }

    private BooruSite cursorToBooruSite(Cursor c){
        //On créé un site
        BooruSite site = new BooruSite();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        site.setName(c.getString(c.getColumnIndex(Database.COL_NAME)));
        site.setUrl(c.getString(c.getColumnIndex(Database.COL_URL)));
        site.setUrlSearch(c.getString(c.getColumnIndex(Database.COL_URL_SEARCH)));
        site.setUrlShow(c.getString(c.getColumnIndex(Database.COL_URL_SHOW)));
        site.setTypeFromID(c.getInt(c.getColumnIndex(Database.COL_TYPE)));
        site.setSFW(c.getInt(c.getColumnIndex(Database.COL_SAFE)));

        //On retourne le livre
        return site;
    }

    public static List<String> getBaseSitesSQL(){

        List<String> SQL = new ArrayList<>();
        List<BooruSite> sites = new ArrayList<>();

        //Danbooru
        BooruSite Dan = new BooruSite("Danbooru","http://danbooru.donmai.us", BooruSite.SiteType.DANBOORU);

        //Gelbooru
        BooruSite Gel = new BooruSite("Gelbooru","http://gelbooru.com", BooruSite.SiteType.GELBOORU);
        Gel.setLoginRequired(1,"http://gelbooru.com/index.php?page=account&s=login&code=00");

        //Yandere
        BooruSite Yan = new BooruSite("Yandere","https://yande.re", BooruSite.SiteType.MOEBOORU);
        Yan.setSFW(1);

        //Konachan
        BooruSite Kon = new BooruSite("Konachan","http://konachan.com", BooruSite.SiteType.MOEBOORU);
        Kon.setSFW(1);

        //Safebooru
        BooruSite SB = new BooruSite("Safebooru","http://safebooru.org", BooruSite.SiteType.GELBOORU);
        SB.setSFW(1);

        //Rule34
        BooruSite R34 = new BooruSite("Rule34","http://rule34.xxx", BooruSite.SiteType.GELBOORU);

        //E621
        BooruSite E621 = new BooruSite("E621","https://e621.net", BooruSite.SiteType.E621);

        //TODO: Add Gelbooru's logging process
        //sites.add(Gel);

        //Unsafe
        sites.add(Gel);

        //With Rating=S, are safe
        sites.add(SB);
        sites.add(Yan);
        sites.add(Kon);

        //Unsafe
        sites.add(R34);
        sites.add(Dan);
        sites.add(E621);

        for(BooruSite s : sites){
            String str = "INSERT INTO " + Database.TABLE_SITES + "(" + Database.COL_NAME + "," + Database.COL_URL + "," + Database.COL_URL_SEARCH + "," + Database.COL_URL_SHOW + "," + Database.COL_TYPE + "," + Database.COL_SAFE + ")"
                    + " VALUES(\"" + s.getName() + "\",\"" + s.getUrl() + "\",\"" + s.getUrlSearch() + "\",\"" + s.getUrlShow() + "\"," + s.getTypeID() + "," + s.getSFW() + ");";
            SQL.add(str);
        }

        return SQL;

    }
}
