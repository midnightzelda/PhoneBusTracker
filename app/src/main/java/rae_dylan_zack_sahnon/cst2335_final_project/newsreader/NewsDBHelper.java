package rae_dylan_zack_sahnon.cst2335_final_project.newsreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper for saving and loading articles
 */
public class NewsDBHelper extends SQLiteOpenHelper {
    /**
     * Class name, i.g., NewsDBHelper
     */
    public final static String CLASSNAME = "NewsDBHelper";

    // database and table names
    public final static String DATABASE_NAME = "NewsDB";
    public final static String TABLE_NAME = "News";
    private static int VERSION_NUM = 1;

    // KEYS
    public final static String KEY_ID = "_id";
    public final static String KEY_TITLE = "title";
    public final static String KEY_LINK = "link";
    public final static String KEY_PUBDATE = "pubDate";
    public final static String KEY_AUTHOR = "author";
    public final static String KEY_CATEGORY = "category";
    public final static String KEY_DESCRIPTION = "description";

    //String title, link, pubDate, author, category, description;

    public NewsDBHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(CLASSNAME, "in onCreate");
        db.execSQL( "CREATE TABLE " + TABLE_NAME
                + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TITLE + " text, "
                + KEY_LINK + " text, "
                + KEY_PUBDATE + " text, "
                + KEY_AUTHOR + " text, "
                + KEY_CATEGORY + " text, "
                + KEY_DESCRIPTION + " text);" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(CLASSNAME, "in onUpgrade, oldVersion=" + oldVersion + ", newVersion=" + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public int getVersion() {return VERSION_NUM;}
}
