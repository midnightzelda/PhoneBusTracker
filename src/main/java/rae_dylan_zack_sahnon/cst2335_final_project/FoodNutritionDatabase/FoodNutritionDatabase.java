package rae_dylan_zack_sahnon.cst2335_final_project.FoodNutritionDatabase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import rae_dylan_zack_sahnon.cst2335_final_project.MainActivity;
import rae_dylan_zack_sahnon.cst2335_final_project.Movies.MovieActivity;
import rae_dylan_zack_sahnon.cst2335_final_project.OCTranspoApp.OCMain;
import rae_dylan_zack_sahnon.cst2335_final_project.R;
import rae_dylan_zack_sahnon.cst2335_final_project.newsreader.NewsMain;

public class FoodNutritionDatabase extends AppCompatActivity {
    private Button search;
    private EditText searchInput;
    private ListView list;
    private ProgressBar progressBar;
    private Button favorites;

    private ArrayList<String> foodList;
    FoodViewAdapter adapter;

    /**
     * method that initializes the different attributes of associated layout to
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_nutrition_database);

        //setting toolbar as the action bar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.foodTitle);
        setSupportActionBar(toolbar);

        list = (ListView) findViewById(R.id.searchResults);
        searchInput = (EditText) findViewById(R.id.searchInput);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        foodList = new ArrayList<>();
        adapter = new FoodViewAdapter(getApplicationContext());
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
                //bundle strings
                Bundle passInfo = new Bundle();
                passInfo.putString("food item", adapter.getItem(position));

                Locale locale = new Locale("en", "US");
                DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
                df.applyPattern("0.00");

                //substring the ArrayList to pass bundle with appropriate information
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



                Intent intent = new Intent(FoodNutritionDatabase.this, FoodDetailsFrame.class);
                intent.putExtras(passInfo);
                startActivity(intent);
            }
        });

        //snackbar
        Snackbar.make(findViewById(R.id.foodTitle),R.string.food_activity_msg,Snackbar.LENGTH_SHORT).show();

        search = (Button) findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                Toast.makeText(getBaseContext(), R.string.food_search_info_msg , Toast.LENGTH_SHORT ).show();
                new foodDBQuery().execute();
            }
        });

        favorites = (Button) findViewById(R.id.goFavoritesButton);
        favorites.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                Intent intent = new Intent(FoodNutritionDatabase.this, FoodItemFavorites.class);
                startActivity(intent);
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
                startActivity(new Intent(FoodNutritionDatabase.this, NewsMain.class));
                return true;

            case R.id.FoodIcon:
                Toast.makeText(getBaseContext(), R.string.food_activity_msg , Toast.LENGTH_SHORT ).show();
                return true;

            case R.id.OCTranspoIcon:
                startActivity(new Intent(FoodNutritionDatabase.this, OCMain.class));
                return true;

            case R.id.MovieIcon:
                startActivity(new Intent(FoodNutritionDatabase.this, MovieActivity.class));
                return true;

            case R.id.help:
                dialogMaker(getString(R.string.Help), getString(R.string.food_help));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Override onBackPressed() method to display an alert dialog asking for confirmation of exit
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodNutritionDatabase.this);
        builder.setMessage(R.string.food_exit_msg);
        builder.setTitle(R.string.exit);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            // User clicked OK button
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(FoodNutritionDatabase.this, MainActivity.class);
                startActivity(intent);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            // User cancelled the dialog
            public void onClick(DialogInterface dialog, int id) { }
        });
        builder.show();
    }

    /**
     * Helper function that allows for alert dialogs to be made
     * @param title - the string title for the dialog
     * @param message - the string message to be displayed by the dialog
     */
    public void dialogMaker(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodNutritionDatabase.this);
        builder.setMessage(message); //Add a dialog message to strings.xml
        builder.setTitle(title);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // User clicked OK button
            public void onClick(DialogInterface dialog, int id) { }
        });
        builder.show();
    }

    /**
     * Accessing the web api to grab information and store within class variables
     * Referenced: https://www.tutorialspoint.com/android/android_json_parser.htm
     */
    class foodDBQuery extends AsyncTask<String, Integer, String> {
        String result;

        String foodId, label, brand;
        double energy, fat, fiber, protein, carbohydrates;

        ArrayList<String> asyncFoodList = new ArrayList<String>();


        @Override
        public String doInBackground(String ... args){
            try {
                //connection
                URL url = new URL("https://api.edamam.com/api/food-database/parser?app_id=f67c9c10&app_key=16b23008ce3a9f9f349817089917aed4&ingr=" + searchInput.getText().toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream response = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                publishProgress(25);
                String line = null;
                while ((line = reader.readLine()) != null){
                    sb.append(line + "\n");
                }
                result = sb.toString();
                Log.i("Result", result);

                publishProgress(50);

                if(result != null) {
                    JSONObject jObject = new JSONObject(result);

                    JSONArray hints = jObject.getJSONArray("hints");
                    publishProgress(75);
                    for(int i = 0; i< hints.length(); i++) {
                        JSONObject h = hints.getJSONObject(i);

                        JSONObject food = h.getJSONObject("food");
                        foodId = food.getString("foodId");
                        label = food.getString("label");

                        JSONObject nutrients = food.getJSONObject("nutrients");
                        energy = nutrients.getDouble("ENERC_KCAL");

                        if(nutrients.has("PROCNT"))
                            protein = nutrients.getDouble("PROCNT");
                        else
                            protein = 0;

                        if(nutrients.has("FAT"))
                            fat = nutrients.getDouble("FAT");
                        else
                            fat = 0;

                        if(nutrients.has("CHOCDF"))
                            carbohydrates = nutrients.getDouble("CHOCDF");
                        else
                            carbohydrates = 0;

                        if(nutrients.has("FIBTG"))
                            fiber = nutrients.getDouble("FIBTG");
                        else
                            fiber = 0;


                        if(food.has("brand"))
                            brand = food.getString("brand");
                        else
                            brand = "Generic";
                        asyncFoodList.add("Item=" + label + "Brand=" + brand + "Energy=" + energy + "Protein=" + protein + "Fat=" + fat + "Carbohydrates=" + carbohydrates + "Fiber=" + fiber + "FoodID=" + foodId);
                    }
                    publishProgress(100);
                }
            }
            catch (Exception e) {
                Log.i("Exception", e.getMessage());
            }
            return "";
        }

        @Override
        public void onProgressUpdate(Integer ... args) {
            progressBar.setProgress(args[0]);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(String result) {
            foodList = asyncFoodList;
            searchInput.setText("");
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * ArrayAdapter of type string to set up the ListView in te associated layout
     */
    public class FoodViewAdapter extends ArrayAdapter<String> {
        public FoodViewAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount() {
            return foodList.size();
        }

        public String getItem(int position) {
            return foodList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = FoodNutritionDatabase.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.food_list_view, null);

            TextView foodItem = (TextView)result.findViewById(R.id.food_item);

            String full = getItem(position);
            String forDisplay = "Item: " + full.substring(full.indexOf("Item=") + 5, full.indexOf("Brand=")) + "\nBrand: " + full.substring(full.indexOf("Brand=") + 6, full.indexOf("Energy="));

            foodItem.setText(forDisplay); // get the string at position

            return result;
        }


        public long getItemId(int position){
            return position;
        }
    }
}
