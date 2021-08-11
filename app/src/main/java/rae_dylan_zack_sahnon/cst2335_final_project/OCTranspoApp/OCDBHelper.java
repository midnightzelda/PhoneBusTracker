package rae_dylan_zack_sahnon.cst2335_final_project.OCTranspoApp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OCDBHelper extends SQLiteOpenHelper {
    public final static String CLASSNAME = "OCDBHelper";
    public final static String DATABASE_NAME = "OCDB";
    public final static String TABLE_NAME = "OC";
    public static int VERSION_NUM = 1;

    public final static String KEY_ID = "id";
    public final static String KEY_STOP = "stop";
    public final static String KEY_BUS = "bus";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_STOP + " INTEGER not null, "
            + KEY_BUS + " INTEGER not null );";

    public OCDBHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(DATABASE_CREATE);
        Log.i("OCDBHelper", "Calling onCreate");
    }


    public void onUpgrade(SQLiteDatabase db, int oldV, int newV){

        Log.i("OCDatabaseHelper", "Calling onUpgrade, oldVersion="+oldV+"newVersion="+newV+ "newVersion=");
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldV, int newV){
        Log.i("OCDatabaseHelper", "Calling onDowngrade, oldVersion="+oldV+"newVersion="+newV+ "newVersion=");
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
