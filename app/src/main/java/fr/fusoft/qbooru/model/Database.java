package fr.fusoft.qbooru.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by Florent on 28/02/2016.
 */
public class Database extends SQLiteOpenHelper {

    public static final String TABLE_SITES = "table_sites";
    public static final String COL_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_URL = "URL";
    public static final String COL_URL_SEARCH = "URL_SEARCH";
    public static final String COL_URL_SHOW = "URL_SHOW";
    public static final String COL_TYPE = "TYPE";
    public static final String COL_SAFE = "SAFE";
    public static final String COL_LOGIN = "LOGIN";
    public static final String COL_LOGIN_URL = "LOGIN_URL";

    private static final String CREATE_DB = "CREATE TABLE " + TABLE_SITES + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NAME + " TEXT NOT NULL, "
            + COL_URL + " TEXT NOT NULL, "
            + COL_URL_SEARCH + " TEXT NOT NULL, "
            + COL_URL_SHOW + " TEXT NOT NULL, "
            + COL_TYPE + " TEXT NOT NULL, "
            + COL_SAFE + " INTEGER, "
            + COL_LOGIN + " INTEGER, "
            + COL_LOGIN_URL + " TEXT);";

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Created from the "CREATE_DB" request
        db.execSQL(CREATE_DB);

        List<String> str = BooruSiteDB.getBaseSitesSQL();
        for(String s : str){
            db.execSQL(s);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Resets the table on upgrade
        db.execSQL("DROP TABLE " + TABLE_SITES + ";");
        onCreate(db);
    }

    public static String[] getBooruSiteColumns(){
        return new String[] {COL_ID, COL_NAME, COL_URL, COL_URL_SEARCH, COL_URL_SHOW, COL_TYPE, COL_SAFE, COL_LOGIN, COL_LOGIN_URL};
    }

}
