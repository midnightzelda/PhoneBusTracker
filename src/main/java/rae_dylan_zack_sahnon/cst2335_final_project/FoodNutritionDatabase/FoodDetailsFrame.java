package rae_dylan_zack_sahnon.cst2335_final_project.FoodNutritionDatabase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import rae_dylan_zack_sahnon.cst2335_final_project.Movies.MovieActivity;
import rae_dylan_zack_sahnon.cst2335_final_project.OCTranspoApp.OCMain;
import rae_dylan_zack_sahnon.cst2335_final_project.R;
import rae_dylan_zack_sahnon.cst2335_final_project.newsreader.NewsMain;

/**
 * Activity that contains an empty FrameLayout that displays the fragment
 */
public class FoodDetailsFrame extends AppCompatActivity {

    Bundle passInfo;

    /**
     * method that initializes the different attributes of associated layout to
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details_frame);

        //setting toolbar as the action bar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.foodTitle);
        setSupportActionBar(toolbar);

        passInfo = getIntent().getExtras();

        FoodDBFragment fragment = new FoodDBFragment();

        fragment.setArguments(passInfo);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.emptyFrame, fragment).addToBackStack(null).commit();
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
                startActivity(new Intent(FoodDetailsFrame.this, NewsMain.class));
                return true;

            case R.id.FoodIcon:
                Toast.makeText(getBaseContext(), R.string.food_activity_msg , Toast.LENGTH_SHORT ).show();
                return true;

            case R.id.OCTranspoIcon:
                startActivity(new Intent(FoodDetailsFrame.this, OCMain.class));
                return true;

            case R.id.MovieIcon:
                startActivity(new Intent(FoodDetailsFrame.this, MovieActivity.class));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodDetailsFrame.this);
        builder.setMessage(message); //Add a dialog message to strings.xml
        builder.setTitle(title);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // User clicked OK button
            public void onClick(DialogInterface dialog, int id) { }
        });
        builder.show();
    }

    /**
     * Override onBackPressed() method to display an alert dialog asking for confirmation of exit
     */
    @Override
    public void onBackPressed() {
        Intent intent = null;
        if(passInfo.getBoolean("fromFavorites"))
            intent = new Intent(FoodDetailsFrame.this, FoodItemFavorites.class);
        else
            intent = new Intent(FoodDetailsFrame.this, FoodNutritionDatabase.class);
        startActivity(intent);
    }
}
