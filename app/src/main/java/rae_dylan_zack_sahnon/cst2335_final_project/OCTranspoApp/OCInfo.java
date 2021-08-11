package rae_dylan_zack_sahnon.cst2335_final_project.OCTranspoApp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import rae_dylan_zack_sahnon.cst2335_final_project.FoodNutritionDatabase.FoodNutritionDatabase;
import rae_dylan_zack_sahnon.cst2335_final_project.Movies.MovieActivity;
import rae_dylan_zack_sahnon.cst2335_final_project.R;
import rae_dylan_zack_sahnon.cst2335_final_project.newsreader.NewsMain;

public class OCInfo extends AppCompatActivity {

    TextView des;
    TextView lat;
    TextView lon;
    TextView gps;
    TextView sta;
    TextView fin;
    TextView sto;
    TextView bus;
    private ProgressBar progressBar;
    private Button add;
    private Button remove;
    ContentValues c = new ContentValues();
    SQLiteDatabase db;
    Cursor cursor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oc_info);
        final String routeNo;
        final String stopNo;

        Toolbar toolbar = findViewById(R.id.oc_toolbar);
        toolbar.setTitle(R.string.oc_main_text);
        setSupportActionBar(toolbar);


        routeNo = getIntent().getExtras().getString("busInfo");
        stopNo = getIntent().getExtras().getString("stopNo");
        progressBar = findViewById(R.id.oc_info_bar);
        add = findViewById(R.id.oc_add_fav);
        remove = findViewById(R.id.oc_remove_fav);

        sto = findViewById(R.id.oc_item_stop);
        bus = findViewById(R.id.oc_item_bus);

        des = findViewById(R.id.oc_item_destination);
        lat = findViewById(R.id.oc_item_latitude);
        lon = findViewById(R.id.oc_item_longitude);
        gps = findViewById(R.id.oc_item_gps);
        sta = findViewById(R.id.oc_item_start);
        fin = findViewById(R.id.oc_item_finish);

        OCDBHelper dbHelper = new OCDBHelper(OCInfo.this);
        db = dbHelper.getWritableDatabase();
        cursor = db.query(true, OCDBHelper.TABLE_NAME, new String[] {OCDBHelper.KEY_STOP, OCDBHelper.KEY_BUS}, null, null, null, null, null, null);




        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                Toast.makeText(getBaseContext(), "Adding to Favourites." , Toast.LENGTH_SHORT ).show();
                c.put(OCDBHelper.KEY_STOP, stopNo);
                c.put(OCDBHelper.KEY_BUS, routeNo);
                db.insert(OCDBHelper.TABLE_NAME, OCDBHelper.KEY_BUS, c);

            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                Toast.makeText(getBaseContext(), "Removing from Favourites." , Toast.LENGTH_SHORT ).show();
                db.delete(OCDBHelper.TABLE_NAME, "? = ? AND ? = ?", new String[]{OCDBHelper.KEY_STOP, stopNo, OCDBHelper.KEY_BUS, routeNo});
                OCDBHelper.VERSION_NUM++;
                Log.i("field", "at "+stopNo);
                Log.i("field", "at "+OCDBHelper.KEY_STOP);
                Log.i("field", "at "+routeNo);
                Log.i("field", "at "+OCDBHelper.KEY_BUS);




            }
        });

        new OCDBQuery().execute("https://api.octranspo1.com/v1.2/GetNextTripsForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo=" + stopNo  + "&routeNo=" + routeNo, stopNo, routeNo);


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
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
                startActivity(new Intent(OCInfo.this, NewsMain.class));
                return true;

            case R.id.FoodIcon:
                startActivity(new Intent(OCInfo.this, FoodNutritionDatabase.class));
                return true;

            case R.id.OCTranspoIcon:
                Toast.makeText(getBaseContext(), R.string.oc_tool , Toast.LENGTH_SHORT ).show();
                return true;

            case R.id.MovieIcon:
                startActivity(new Intent(OCInfo.this, MovieActivity.class));
                return true;

            case R.id.help:
                dialogMaker(getString(R.string.Help), getString(R.string.oc_help));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void dialogMaker(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OCInfo.this);
        builder.setMessage(message); //Add a dialog message to strings.xml
        builder.setTitle(title);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // User clicked OK button
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }


    public void onProgressUpdate(Integer ... args) {
        progressBar.setProgress(args[0]);
        progressBar.setVisibility(View.VISIBLE);
    }

    class OCDBQuery extends AsyncTask<String, Integer, String> {
        String a;
        String b;
        String c;
        String d;
        String e;
        String f;

        String z;
        String x;

        public String doInBackground(String... args) {
            z = args[1];
            x = args[2];


            try {
                //connection

                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();
                publishProgress(25);

                String ns = null;
                XmlPullParser pp;
            /*    StringBuilder sb = new StringBuilder();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
               while((line = reader.readLine()) != null)
                   sb.append(line);*/
                pp = Xml.newPullParser();
                pp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                pp.setInput(urlConnection.getInputStream(), null);
                pp.nextTag();
                while(true) {
                    if (pp.next() == XmlPullParser.START_TAG) {
                        String tag = pp.getName();
                        Log.i("XmlPullParser","at " +tag);
                        if (tag.equals("GetNextTripsForStopResult"))
                            break;
                    }
                }
                pp.require(XmlPullParser.START_TAG, ns, "GetNextTripsForStopResult");
                while(pp.next() != XmlPullParser.END_DOCUMENT){
                    int type = pp.getEventType();
                    if(pp.getEventType() != XmlPullParser.START_TAG){
                        continue;
                    }
                    publishProgress(50);
                    String name = pp.getName();
                    Log.i("ParserLoop", "at "+name);
                    if(name.equals("TripDestination")){
                        if(pp.next() == XmlPullParser.TEXT){
                            a = pp.getText();//make temporary variable
                        }
                    } else if(name.equals("TripStartTime")){
                        if(pp.next() == XmlPullParser.TEXT){
                            b = pp.getText();
                            //b.setText(pp.getText());
                        }
                    } else if(name.equals("AdjustedScheduleTime")){
                        if(pp.next() == XmlPullParser.TEXT){
                            c = pp.getText();
                        }
                    } else if(name.equals("Latitude")) {
                        if (pp.next() == XmlPullParser.TEXT) {
                            d = pp.getText();
                        }
                    }  else if(name.equals("Longitude")){
                        if(pp.next() == XmlPullParser.TEXT){
                            e = pp.getText();
                        }
                    }  else if(name.equals("GPSSpeed")){
                        if(pp.next() == XmlPullParser.TEXT){
                            f = pp.getText();
                        }
                    } else if (name.equals("Error")){
                    }
                }

            } catch (Exception e) {
                Log.i("Exception", e.getMessage());
            }
            publishProgress(75);
            return "";

        }


        public void onPostExecute(String result) {
            sto.setText("Stop: " +z);
            bus.setText("Bus: " +x);
            if(a == null || a.isEmpty()){
                a = "N/A";
            }
            des.setText("Destination: " + a);
            if(b == null || b.isEmpty()){
                b = "N/A";
            }
            sta.setText("Start Time: " + b);
            if(c == null || c.isEmpty()){
                c = "N/A";
            }
            fin.setText("Adjusted Time: "+ c);
            if(d == null || d.isEmpty()){
                d = "N/A";
            }
            lat.setText("Latitude: " + d);
            if(e == null || e.isEmpty()){
                e = "N/A";
            }
            lon.setText("Longitude: " + e);
            if(f == null || f.isEmpty()){
                f = "N/A";
            }
            gps.setText("GPS Speed: " + f);
            publishProgress(100);

        }

    }

    }
