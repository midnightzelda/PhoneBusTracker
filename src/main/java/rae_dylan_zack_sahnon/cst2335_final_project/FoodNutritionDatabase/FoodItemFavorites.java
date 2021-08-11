package rae_dylan_zack_sahnon.cst2335_final_project.FoodNutritionDatabase;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rae_dylan_zack_sahnon.cst2335_final_project.MainActivity;
import rae_dylan_zack_sahnon.cst2335_final_project.Movies.MovieActivity;
import rae_dylan_zack_sahnon.cst2335_final_project.OCTranspoApp.OCMain;
import rae_dylan_zack_sahnon.cst2335_final_project.R;
import rae_dylan_zack_sahnon.cst2335_final_project.newsreader.NewsMain;

/**
 * Class for the favorites list activity
 */
public class FoodItemFavorites extends AppCompatActivity {
    Cursor c;
    private SQLiteDatabase db;

    ListView favoritesView;
    FoodFavoritesViewAdapter adapter;
    private ArrayList<String> favoritesList;

    double min, max, average, total;
    Button tagInfo;
    EditText tagText;

    private final static String ACTIVITY_NAME = "FoodItemFavorites";

    /**
     * method that initializes the different attributes of associated layout to
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_favorites);

        //setting toolbar as the action bar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.foodTitle);
        setSupportActionBar(toolbar);

        favoritesView = (ListView) findViewById(R.id.favorites);

        //initializing ArrayList and adapter and setting adapter to ListView
        favoritesList = new ArrayList<>();
        adapter = new FoodFavoritesViewAdapter(getApplicationContext());
        favoritesView.setAdapter(adapter);

        FoodItemDBHelper dbHelper = new FoodItemDBHelper(this);
        db = dbHelper.getWritableDatabase();

        //Populate favorites listview from favorites database
        c = db.rawQuery("SELECT * FROM food", null);
        int labelIndex = c.getColumnIndex(FoodItemDBHelper.KEY_LABEL);
        int brandIndex = c.getColumnIndex(FoodItemDBHelper.KEY_BRAND);
        int energyIndex = c.getColumnIndex(FoodItemDBHelper.KEY_ENERGY);
        int proteinIndex = c.getColumnIndex(FoodItemDBHelper.KEY_PROTEIN);
        int fatIndex = c.getColumnIndex(FoodItemDBHelper.KEY_FAT);
        int carbIndex = c.getColumnIndex(FoodItemDBHelper.KEY_CARBS);
        int fiberIndex = c.getColumnIndex(FoodItemDBHelper.KEY_FIBER);
        int foodIdIndex = c.getColumnIndex(FoodItemDBHelper.KEY_ID);
        c.moveToFirst();

        while(!c.isAfterLast()){
            favoritesList.add("Item=" + c.getString(labelIndex) + "Brand=" + c.getString(brandIndex) + "Energy=" + c.getString(energyIndex) + "Protein=" + c.getString(proteinIndex) + "Fat=" + c.getString(fatIndex) + "Carbohydrates=" + c.getString(carbIndex) + "Fiber=" + c.getString(fiberIndex) + "FoodID=" + c.getString(foodIdIndex));
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + c.getString( c.getColumnIndex(FoodItemDBHelper.KEY_LABEL)));
            Log.i(ACTIVITY_NAME,"Cursor's column count=" + c.getColumnCount());
            Log.i(ACTIVITY_NAME,"Cursor's column name=" + c.getColumnName(labelIndex));
            c.moveToNext();
        }

        //Go through the bundle if the foodId does not exist
        Bundle item = getIntent().getExtras();
        if(item != null && !ifExists(item.getString("foodId")) && item.getInt("function") == 5){
            String foodId = item.getString("foodId");
            String label = item.getString("label");
            String brand = item.getString("brand");
            double energy = item.getDouble("energy");
            double carbs = item.getDouble("carbs");
            double protein = item.getDouble("protein");
            double fat = item.getDouble("fat");
            double fiber = item.getDouble("fiber");

            favoritesList.add("Item=" + label + "Brand=" + brand + "Energy=" + energy + "Protein=" + protein + "Fat=" + fat + "Carbohydrates=" + carbs + "Fiber=" + fiber + "FoodID=" + foodId);

            ContentValues cv = new ContentValues();

            cv.put(FoodItemDBHelper.KEY_ID, foodId);
            cv.put(FoodItemDBHelper.KEY_LABEL, label);
            cv.put(FoodItemDBHelper.KEY_BRAND, brand);
            cv.put(FoodItemDBHelper.KEY_ENERGY, energy);
            cv.put(FoodItemDBHelper.KEY_CARBS, carbs);
            cv.put(FoodItemDBHelper.KEY_PROTEIN, protein);
            cv.put(FoodItemDBHelper.KEY_FAT, fat);
            cv.put(FoodItemDBHelper.KEY_FIBER, fiber);

            db.insert(FoodItemDBHelper.TABLE_NAME, null, cv);

            adapter.notifyDataSetChanged();

            Toast.makeText(getBaseContext(), R.string.food_add_msg , Toast.LENGTH_SHORT ).show();
        } else if(item != null && ifExists(item.getString("foodId")) && item.getInt("function") == 5){
            deleteItem(item.getString("foodId"));
            Toast.makeText(getBaseContext(), R.string.food_remove_msg , Toast.LENGTH_SHORT ).show();
        } else if(item != null && ifExists(item.getString("foodId")) && item.getInt("function") == 1){
            setTag(item.getString("foodId"), item.getString("tag"));
            Toast.makeText(getBaseContext(), R.string.food_tag_msg , Toast.LENGTH_SHORT ).show();
        }

        favoritesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
                //bundle strings
                Bundle passInfo = new Bundle();
                passInfo.putString("food item", adapter.getItem(position));

                Locale locale = new Locale("en", "US");
                DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
                df.applyPattern("0.00");

                String foodItem = adapter.getItem(position);
                String label = foodItem.substring(foodItem.indexOf("Item=") + 5, foodItem.indexOf("Brand="));
                String brand = foodItem.substring(foodItem.indexOf("Brand=") + 6, foodItem.indexOf("Energy="));
                String energy = foodItem.substring(foodItem.indexOf("Energy=") + 7, foodItem.indexOf("Protein="));
                String protein = foodItem.substring(foodItem.indexOf("Protein=") + 8, foodItem.indexOf("Fat="));
                String fat = foodItem.substring(foodItem.indexOf("Fat=") + 4, foodItem.indexOf("Carbohydrates="));
                String carbohydrates = foodItem.substring(foodItem.indexOf("Carbohydrates=") + 14, foodItem.indexOf("Fiber="));
                String fiber = foodItem.substring(foodItem.indexOf("Fiber=") + 6, foodItem.indexOf("FoodID="));
                String foodId = foodItem.substring(foodItem.lastIndexOf("FoodID=") + 7);

                passInfo.putString("foodId", foodId);
                passInfo.putString("label", label);
                passInfo.putString("brand", brand);
                passInfo.putDouble("energy", new Double(df.format(new Double(energy))));
                passInfo.putDouble("protein", new Double(df.format(new Double(protein))));
                passInfo.putDouble("fat", new Double(df.format(new Double(fat))));
                passInfo.putDouble("carbs", new Double(df.format(new Double(carbohydrates))));
                passInfo.putDouble("fiber", new Double(df.format(new Double(fiber))));

                passInfo.putBoolean("fromFavorites", new Boolean(true));

                passInfo.putString("tag", getTag(foodId));

                Intent intent = new Intent(FoodItemFavorites.this, FoodDetailsFrame.class);
                intent.putExtras(passInfo);
                startActivity(intent);
            }
        });

        tagInfo = (Button) findViewById(R.id.tagSearchButton);
        tagText = (EditText) findViewById(R.id.tagSearch);
        tagInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodItemFavorites.this);

                if(ifTagExists(tagText.getText().toString())) {
                    getTagInfo(tagText.getText().toString());
                    String tagMsg = getResources().getString(R.string.food_min) + " " + min + "\n" + getResources().getString(R.string.food_max) + " " + max + "\n" + getResources().getString(R.string.food_avg) + " " + average + "\n" + getResources().getString(R.string.food_total) + " " + total;
                    builder.setMessage(tagMsg);
                }
                else
                    builder.setMessage(R.string.food_no_tag_msg);
                builder.setTitle(R.string.food_tag_info_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    // User clicked OK button
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.show();
            }
        });
    }

    /**
     * Inflates the menu layout
     * @param menu The menu object related to the layout inflating
     * @return true upon success.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Provides functionality for icons on the tool bar
     * @param item - pass MenuItem clicked
     * @return true if successful
     */
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.NewsReaderIcon:
                startActivity(new Intent(FoodItemFavorites.this, NewsMain.class));
                return true;

            case R.id.FoodIcon:
                Toast.makeText(getBaseContext(), R.string.food_activity_msg , Toast.LENGTH_SHORT ).show();
                return true;

            case R.id.OCTranspoIcon:
                startActivity(new Intent(FoodItemFavorites.this, OCMain.class));
                return true;

            case R.id.MovieIcon:
                startActivity(new Intent(FoodItemFavorites.this, MovieActivity.class));
                return true;

            case R.id.help:
                dialogMaker(getString(R.string.Help), getString(R.string.food_help));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Helper function that allows for alert dialogs to be made
     * @param title - the string title for the dialog
     * @param message - the string message to be displayed by the dialog
     */
    public void dialogMaker(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodItemFavorites.this);
        builder.setMessage(message); //Add a dialog message to strings.xml
        builder.setTitle(title);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // User clicked OK button
            public void onClick(DialogInterface dialog, int id) { }
        });
        builder.show();
    }

    /**
     * ArrayAdapter of type string to set up the ListView in te associated layout
     */
    public class FoodFavoritesViewAdapter extends ArrayAdapter<String> {
        public FoodFavoritesViewAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount() {
            return favoritesList.size();
        }

        public String getItem(int position) {
            return favoritesList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = FoodItemFavorites.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.food_list_view, null);

            TextView foodItem = (TextView)result.findViewById(R.id.food_item);

            String full = getItem(position);
            String forDisplay = "Item: " + full.substring(full.indexOf("Item=") + 5, full.indexOf("Brand=")) + "\nBrand: " + full.substring(full.indexOf("Brand=") + 6, full.indexOf("Energy=")) + "\nTag: " + getTag(full.substring(full.lastIndexOf("FoodID=") + 7));


            foodItem.setText(forDisplay); // get the string at position

            return result;
        }


        public long getItemId(int position){
            return position;
        }
    }

    /**
     * Deletes an item from the favorites list view by querying the database
     * based on the foodId and deleting the item from the database and then
     * refreshing the list view
     * @param id - the foodId parameter being passed based on the item selected
     */
    public void deleteItem(String id){
        favoritesList.clear();

        db.delete("food", "foodId = '" + id + "'", null);

        //Populate favorites listview from favorites database
        c = db.rawQuery("SELECT * FROM food", null);
        int labelIndex = c.getColumnIndex(FoodItemDBHelper.KEY_LABEL);
        int brandIndex = c.getColumnIndex(FoodItemDBHelper.KEY_BRAND);
        int energyIndex = c.getColumnIndex(FoodItemDBHelper.KEY_ENERGY);
        int proteinIndex = c.getColumnIndex(FoodItemDBHelper.KEY_PROTEIN);
        int fatIndex = c.getColumnIndex(FoodItemDBHelper.KEY_FAT);
        int carbIndex = c.getColumnIndex(FoodItemDBHelper.KEY_CARBS);
        int fiberIndex = c.getColumnIndex(FoodItemDBHelper.KEY_FIBER);
        int foodIdIndex = c.getColumnIndex(FoodItemDBHelper.KEY_ID);
        c.moveToFirst();

        while(!c.isAfterLast()){
            favoritesList.add("Item=" + c.getString(labelIndex) + "Brand=" + c.getString(brandIndex) + "Energy=" + c.getString(energyIndex) + "Protein=" + c.getString(proteinIndex) + "Fat=" + c.getString(fatIndex) + "Carbohydrates=" + c.getString(carbIndex) + "Fiber=" + c.getString(fiberIndex) + "FoodID=" + c.getString(foodIdIndex));
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + c.getString( c.getColumnIndex(FoodItemDBHelper.KEY_LABEL)));
            Log.i(ACTIVITY_NAME,"Cursor's column count=" + c.getColumnCount());
            Log.i(ACTIVITY_NAME,"Cursor's column name=" + c.getColumnName(labelIndex));
            c.moveToNext();
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * A helper method that queries the database for an item with the passed foodId to see if
     * it exists.
     * Reference: https://stackoverflow.com/questions/37675906/check-if-row-already-exist-in-sqlite-database
     * @param id - foodId being passed
     * @return exists - if the query successfully identifies
     * an item based on the id then the method returns true
     * otherwise it returns false
     */
    public boolean ifExists(String id){
        Cursor cursor = null;
        String checkQuery = "SELECT * FROM food WHERE foodId= '" + id + "'";
        cursor = db.rawQuery(checkQuery, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    /**
     * A helper method that queries the database to see if the tag input exists.
     * Reference: https://stackoverflow.com/questions/37675906/check-if-row-already-exist-in-sqlite-database
     * @param tag - tag being passed
     * @return exists - if the query successfully identifies
     * an item based on the tag then the method returns true
     * otherwise it returns false
     */
    public boolean ifTagExists(String tag){
        Cursor cursor = null;
        String checkQuery = "SELECT * FROM food WHERE tag= '" + tag + "'";
        cursor = db.rawQuery(checkQuery, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    /**
     * Helper method that executes update sql query based tag and foodId input
     * @param id - string foodId
     * @param tag - string tag input
     */
    public void setTag(String id, String tag){
        String setTagQuery = "UPDATE food SET tag= '" + tag + "' WHERE foodId= '" + id + "'";
        db.execSQL(setTagQuery);
    }

    /**
     * helper method that queries the database according to foodId input
     * @param id - string foodId
     * @return string tag returned from the database query
     */
    public String getTag(String id){
        Cursor cursor = null;
        String getTagQuery = "SELECT * FROM food WHERE foodId= '" + id + "'";

        cursor = db.rawQuery(getTagQuery, null);
        int tagIndex = cursor.getColumnIndex(FoodItemDBHelper.KEY_TAG);
        cursor.moveToFirst();
        String tag = cursor.getString(tagIndex);

        cursor.close();
        return tag;
    }

    /**
     * Query the database multiple times for minimum, maximum, average and total energy according to tag input
     * and stores the information in the class variables
     * @param tag - input tag
     */
    public void getTagInfo(String tag){
        Cursor cursor = null;
        String getMin = "SELECT MIN(energy) FROM food WHERE tag = '" + tag + "'";
        String getMax = "SELECT MAX(energy) FROM food WHERE tag = '" + tag + "'";
        String getAvg = "SELECT AVG(energy) FROM food WHERE tag = '" + tag + "'";
        String getSum = "SELECT SUM(energy) FROM food WHERE tag = '" + tag + "'";

        cursor = db.rawQuery(getMin, null);
        cursor.moveToFirst();
        min = cursor.getDouble(0);

        cursor = db.rawQuery(getMax, null);
        cursor.moveToFirst();
        max = cursor.getDouble(0);

        cursor = db.rawQuery(getAvg, null);
        cursor.moveToFirst();
        average = cursor.getDouble(0);

        cursor = db.rawQuery(getSum, null);
        cursor.moveToFirst();
        total = cursor.getDouble(0);
        cursor.close();
    }

    /**
     * override on backpressed so that it starts the previous activity
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FoodItemFavorites.this, FoodNutritionDatabase.class);
        startActivity(intent);
    }
}
