package rae_dylan_zack_sahnon.cst2335_final_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import rae_dylan_zack_sahnon.cst2335_final_project.FoodNutritionDatabase.FoodNutritionDatabase;
import rae_dylan_zack_sahnon.cst2335_final_project.Movies.MovieActivity;
import rae_dylan_zack_sahnon.cst2335_final_project.OCTranspoApp.OCMain;
import rae_dylan_zack_sahnon.cst2335_final_project.newsreader.NewsMain;

public class MainActivity extends AppCompatActivity {
    Intent newsReader;
    Intent foodDatabase;
    Intent ocTranspo;
    Intent movieInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        newsReader = new Intent(MainActivity.this, NewsMain.class);
        foodDatabase = new Intent(MainActivity.this, FoodNutritionDatabase.class);
        ocTranspo = new Intent(MainActivity.this, OCMain.class);
        movieInfo = new Intent(MainActivity.this,   MovieActivity.class);

        // CBC NEWS READER - Rae
        Button newsButton = findViewById(R.id.openCBCNewsReader);
        newsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(newsReader);
            }
        });

        // Food Nutrition Database - Sahnon
        Button foodButton = (Button) findViewById(R.id.openFoodNutritionDatabase);
        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(foodDatabase);
            }
        });

        // OC Transpo App - Dylan
        Button ocButton = findViewById(R.id.openOCTranspoApp);
        ocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(ocTranspo);
            }
        });

        // Movie App - Zach
        Button movieButton = findViewById(R.id.openMovieInformation);
        movieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(movieInfo);
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
     * The options and actions associated with options. Option can be an icon
     * @param item the menuitem object clicked on
     * @return true upon success
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.NewsReaderIcon:

                startActivity(newsReader);
                return true;

            case R.id.FoodIcon:

                startActivity(foodDatabase);
                return true;

            case R.id.OCTranspoIcon:

                startActivity(ocTranspo);
                return true;

            case R.id.MovieIcon:

                startActivity(movieInfo);
                return true;

            case R.id.help:
                AlertDialog.Builder helpAlert = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.Help))
                        .setMessage(getString(R.string.help_msg))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { }
                        });
                helpAlert.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}