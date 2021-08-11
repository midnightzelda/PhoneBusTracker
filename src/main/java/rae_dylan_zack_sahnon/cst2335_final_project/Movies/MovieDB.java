package rae_dylan_zack_sahnon.cst2335_final_project.Movies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieDB extends SQLiteOpenHelper{
        public static String DATABASE_NAME = "MovieData";
        private static int VERSION_NUM = 0;
        public final static String KEY_ID = "movie_id";
        public static String KEY_Movie = "movie";

        public MovieDB (Context ctx) {
            super(ctx,DATABASE_NAME, null, VERSION_NUM);
            Log.i("ChatdatabaseHelper", "constructor");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY autoincrement, " + KEY_Movie +
                    " text not null);");
            Log.i("ChatDatabaseHelper", "Calling onCreate");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
            onCreate(db);
            Log.i("ChatDatabaseHelper", "Calling onUpgrade, oldVersion=" + oldVersion + " newVersion=" + newVersion);
        }
    }

