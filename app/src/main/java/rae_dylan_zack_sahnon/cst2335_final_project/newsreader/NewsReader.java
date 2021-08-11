package rae_dylan_zack_sahnon.cst2335_final_project.newsreader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import rae_dylan_zack_sahnon.cst2335_final_project.FoodNutritionDatabase.FoodNutritionDatabase;
import rae_dylan_zack_sahnon.cst2335_final_project.Movies.MovieActivity;
import rae_dylan_zack_sahnon.cst2335_final_project.OCTranspoApp.OCMain;
import rae_dylan_zack_sahnon.cst2335_final_project.R;

/**
 * Displays more information from feed, such as date published, category, and description
 * @author Rae Ehret
 */
public class NewsReader extends AppCompatActivity {
    /**
     * The activity name, used for debugging
     */
    public final String ACTIVITY_NAME = "NewsReader";

    /**
     * Creates the fragment and transaction, loads toolbar
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader);
        setTitle(getString(R.string.CBCNewsReaderText));

        Log.i(ACTIVITY_NAME, "in onCreate");

        // toolbar
        Toolbar toolbar = findViewById(R.id.news_toolbar);
        toolbar.setTitle(R.string.news_title_abbr);
        setSupportActionBar(toolbar);

        // get fragment
        NewsFragment frag = new NewsFragment();
        Bundle extras = getIntent().getExtras();
        frag.setArguments(extras);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.news_frame, frag).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Inflates the menu layout
     * @param menu The menu object related to the layout inflating
     * @return true upon success.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(NewsReader.this);
                builder1.setTitle(R.string.warning)
                        .setMessage(getString(R.string.news_error))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
                return true;

            case R.id.FoodIcon:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(NewsReader.this);
                builder2.setTitle(R.string.warning)
                        .setMessage(R.string.exit_msg)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent foodDatabase = new Intent(NewsReader.this, FoodNutritionDatabase.class);
                                foodDatabase.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(foodDatabase);
                                finish();
                            }
                        })
                        .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // nothing
                            }
                        })
                        .create()
                        .show();
                return true;

            case R.id.OCTranspoIcon:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(NewsReader.this);
                builder3.setTitle(R.string.warning)
                        .setMessage(R.string.exit_msg)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent ocTranspo = new Intent(NewsReader.this, OCMain.class);
                                ocTranspo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(ocTranspo);
                                finish();
                            }
                        })
                        .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // nothing
                            }
                        })
                        .create()
                        .show();
                return true;

            case R.id.MovieIcon:
                AlertDialog.Builder builder4 = new AlertDialog.Builder(NewsReader.this);
                builder4.setTitle(R.string.warning)
                        .setMessage(R.string.exit_msg)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent movieInfo = new Intent (NewsReader.this, MovieActivity.class);
                                movieInfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(movieInfo);
                                finish();
                            }
                        })
                        .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // nothing
                            }
                        })
                        .create()
                        .show();
                return true;

            case R.id.help:
                AlertDialog.Builder builder5 = new AlertDialog.Builder(this);
                builder5.setTitle(R.string.Help)
                        .setMessage(getString(R.string.news_help))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
