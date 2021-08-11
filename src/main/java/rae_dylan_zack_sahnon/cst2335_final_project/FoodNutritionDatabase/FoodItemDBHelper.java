package rae_dylan_zack_sahnon.cst2335_final_project.FoodNutritionDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper method for SQLite extends SQLiteOpenHelper
 */
public class FoodItemDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "food.db";
    private static final int VERSION_NUM = 2;
    public static final String KEY_ID = "foodId";
    public static final String KEY_LABEL = "label";
    public static final String KEY_BRAND = "brand";
    public static final String KEY_ENERGY = "energy";
    public static final String KEY_CARBS = "carbohydrates";
    public static final String KEY_FAT = "fat";
    public static final String KEY_PROTEIN = "protein";
    public static final String KEY_FIBER = "fiber";
    public static final String KEY_TAG = "tag";
    public static final String TABLE_NAME = "food";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "( "
            + KEY_ID + " text not null, "
            + KEY_LABEL + " text not null, "
            + KEY_BRAND + " text not null, "
            + KEY_ENERGY + " numeric not null, "
            + KEY_CARBS + " numeric not null, "
            + KEY_FAT + " numeric not null, "
            + KEY_PROTEIN + " numeric not null, "
            + KEY_FIBER + " numeric not null, "
            + KEY_TAG + " text DEFAULT 'Not set');";

    /**
     * contructor
     * @param ctx
     */
    public FoodItemDBHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * oncreate method for SQLiteOpenHelper to execute database create query
     * @param db - database object passed
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(DATABASE_CREATE);
        Log.i("FoodItemDBHelper", "Calling onCreate");
    }

    /**
     * onupgrade method from SQLiteOpenHelper
     * @param db - database object
     * @param oldVer - int previous version
     * @param newVer - int new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
        Log.i("FoodItemDBHelper", "Calling onUpgrade, oldVersion=" + oldVer + "newVersion=" + newVer);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
