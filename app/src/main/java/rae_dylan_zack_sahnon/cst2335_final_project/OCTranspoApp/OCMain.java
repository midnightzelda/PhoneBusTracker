package rae_dylan_zack_sahnon.cst2335_final_project.OCTranspoApp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import rae_dylan_zack_sahnon.cst2335_final_project.FoodNutritionDatabase.FoodNutritionDatabase;
import rae_dylan_zack_sahnon.cst2335_final_project.MainActivity;
import rae_dylan_zack_sahnon.cst2335_final_project.Movies.MovieActivity;
import rae_dylan_zack_sahnon.cst2335_final_project.R;
import rae_dylan_zack_sahnon.cst2335_final_project.newsreader.NewsMain;

public class OCMain extends AppCompatActivity {


    Bundle passInfo;

    private Cursor c;
    private SQLiteDatabase db;

    private Button search;
    private Button favourite;
    private ProgressBar loadTime;
    private EditText routeName;
    private ArrayList<String> ocList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocmain);

        Toolbar toolbar = findViewById(R.id.oc_toolbar);
        toolbar.setTitle(R.string.oc_main_text);
        setSupportActionBar(toolbar);


        //snackbar
        Snackbar.make(findViewById(R.id.oc_toolbar), "This is the OC main page", Snackbar.LENGTH_LONG).show();

        ocList = new ArrayList<>();

        //connect to xml
        routeName = findViewById(R.id.oc_editText);
        search = findViewById(R.id.oc_button);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                Toast.makeText(getBaseContext(), "Your search results will be shown." , Toast.LENGTH_SHORT ).show();
                ocList.clear();
                new OCDBQuery().execute("https://api.octranspo1.com/v1.2/GetRouteSummaryForStop?appID=223eb5c3&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo="+routeName.getText().toString());
                search.setVisibility(View.GONE);
            }
        });
        favourite = findViewById(R.id.oc_favourites);
        favourite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                startActivity(new Intent(OCMain.this, OCSaved.class));
            }
        });

        loadTime = findViewById(R.id.oc_progressBar);
        loadTime.setVisibility(View.VISIBLE);
        loadTime.setProgress(0);






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
                startActivity(new Intent(OCMain.this, NewsMain.class));
                return true;

            case R.id.FoodIcon:
                startActivity(new Intent(OCMain.this, FoodNutritionDatabase.class));
                return true;

            case R.id.OCTranspoIcon:
                Toast.makeText(getBaseContext(), R.string.oc_tool , Toast.LENGTH_SHORT ).show();
                return true;

            case R.id.MovieIcon:
                startActivity(new Intent(OCMain.this, MovieActivity.class));
                return true;

            case R.id.help:
                dialogMaker(getString(R.string.Help), getString(R.string.oc_help));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Dialog box showing upon pressing the backbutton, confirms whether user wants to quit the current activity.
     * Special thanks to: Sahnon Mahbub
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OCMain.this);
        builder.setMessage("Return to main hub?"); //Add a dialog message to strings.xml
        builder.setTitle("Exit Prompt");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // User clicked OK button
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(OCMain.this, MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            // User cancelled the dialog
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }

    public void dialogMaker(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OCMain.this);
        builder.setMessage(message); //Add a dialog message to strings.xml
        builder.setTitle(title);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // User clicked OK button
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }

    /**
     * Referenced: https://www.tutorialspoint.com/android/android_json_parser.htm
     */
    class OCDBQuery extends AsyncTask<String, Integer, String> {

        public String doInBackground(String... args) {
            try {
                //connection
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(50000);
                urlConnection.setConnectTimeout(65000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                String ns = null;
                XmlPullParser pp;
                pp = Xml.newPullParser();
                pp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                pp.setInput(urlConnection.getInputStream(), null);
                pp.nextTag();
                while(true) {
                    if (pp.next() == XmlPullParser.START_TAG) {
                        String tag = pp.getName();
                        Log.i("XmlPullParser","at " +tag);
                        if (tag.equals("GetRouteSummaryForStopResult"))
                            break;
                    }
                }
                pp.require(XmlPullParser.START_TAG, ns, "GetRouteSummaryForStopResult");
                while(pp.next() != XmlPullParser.END_DOCUMENT){
                    if(pp.getEventType() != XmlPullParser.START_TAG){
                        continue;
                    }
                    String name = pp.getName();
                    Log.i("ParserLoop", "at "+name);
                    if(name.equals("RouteNo")){
                        if(pp.next() == XmlPullParser.TEXT){
                           ocList.add(pp.getText());
                        }
                    } else {
                        // want to skip
                    }
                }

            } catch (Exception e) {
                Log.i("Exception", e.getMessage());
            }
            return "";
        }


        public void onPostExecute(String result) {
            passInfo = new Bundle();
            passInfo.putString("stopNo", routeName.getText().toString());
            passInfo.putStringArrayList("routeNo", ocList);
            OCMainFragment fragment = new OCMainFragment();
            fragment.setArguments(passInfo);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            getSupportFragmentManager().popBackStack();
            ft.add(R.id.infoFrame, fragment).addToBackStack(null).commit();

            search.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer ... values) {
            // progress bar update
            loadTime.setProgress(values[0]);
        }
    }
        public class OCViewAdapter extends ArrayAdapter<String> {
            public OCViewAdapter(Context ctx) {

                super(ctx, 0);
            }

            public int getCount() {
                return ocList.size();
            }

            public String getItem(int position) {
                return ocList.get(position);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                LayoutInflater inflater = OCMain.this.getLayoutInflater();
                View result = inflater.inflate(R.layout.oc_route, null);//needs to be a list view in a different activity to show all the info
                TextView ocItem = result.findViewById(R.id.oc_item);
                String full = getItem(position);
                String forDisplay = "Stop: " + full.substring(full.indexOf("Item=") + 2);
                ocItem.setText(forDisplay); // get the string at position

                return result;
            }

            public long getItemId(int position){
                return position;
            }
        }
    }


