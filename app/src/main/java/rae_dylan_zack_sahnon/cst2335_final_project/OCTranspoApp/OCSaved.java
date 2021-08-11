package rae_dylan_zack_sahnon.cst2335_final_project.OCTranspoApp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import rae_dylan_zack_sahnon.cst2335_final_project.FoodNutritionDatabase.FoodNutritionDatabase;
import rae_dylan_zack_sahnon.cst2335_final_project.Movies.MovieActivity;
import rae_dylan_zack_sahnon.cst2335_final_project.R;
import rae_dylan_zack_sahnon.cst2335_final_project.newsreader.NewsMain;

public class OCSaved extends AppCompatActivity {

    ListView records;
    ArrayList<OCSaveInfo> items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oc_saved_results);
        Toolbar toolbar = findViewById(R.id.oc_toolbar);
        toolbar.setTitle(R.string.oc_main_text);
        setSupportActionBar(toolbar);

        records = findViewById(R.id.oc_saved_results);

        items = new ArrayList<>();

        OCDBHelper dbHelper = new OCDBHelper(OCSaved.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(true, OCDBHelper.TABLE_NAME, new String[]{OCDBHelper.KEY_STOP, OCDBHelper.KEY_BUS}, null, null, null, null, null, null);
        items.clear();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            OCSaveInfo si = new OCSaveInfo(cursor.getString(cursor.getColumnIndex(OCDBHelper.KEY_STOP)), cursor.getString(cursor.getColumnIndex(OCDBHelper.KEY_BUS)));
            items.add(si);
            Log.i("delta", "at "+cursor.getString(cursor.getColumnIndex(OCDBHelper.KEY_STOP)));
            Log.i("delta", "at "+cursor.getString(cursor.getColumnIndex(OCDBHelper.KEY_BUS)));
            cursor.moveToNext();
        }

        OCViewAdapter ocViewAdapter = new OCViewAdapter(OCSaved.this);
        records.setAdapter(ocViewAdapter);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }


    /**
     * Inflates the menu layout
     *
     * @param menu The menu object related to the layout inflating
     * @return true upon success.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Provides functionality for icons on the tool bar
     *
     * @param item - pass MenuItem clicked
     * @return true if successful
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.NewsReaderIcon:
                startActivity(new Intent(OCSaved.this, NewsMain.class));
                return true;

            case R.id.FoodIcon:
                startActivity(new Intent(OCSaved.this, FoodNutritionDatabase.class));
                return true;

            case R.id.OCTranspoIcon:
                Toast.makeText(getBaseContext(), R.string.oc_tool, Toast.LENGTH_SHORT).show();
                return true;

            case R.id.MovieIcon:
                startActivity(new Intent(OCSaved.this, MovieActivity.class));
                return true;

            case R.id.help:
                dialogMaker(getString(R.string.Help), getString(R.string.oc_help));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void dialogMaker(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OCSaved.this);
        builder.setMessage(message); //Add a dialog message to strings.xml
        builder.setTitle(title);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // User clicked OK button
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }

    public class OCViewAdapter extends ArrayAdapter<OCSaveInfo> {
        public OCViewAdapter(Context ctx) {

            super(ctx, 0);
        }

        public int getCount() {
            return items.size();
        }

        public OCSaveInfo getItem(int position) {
            return items.get(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = OCSaved.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.oc_result, null);//needs to be a list view in a different activity to show all the info
            TextView ocItem = result.findViewById(R.id.oc_item_result);
            TextView ocItem2 = result.findViewById(R.id.oc_item_result2);
            final String full = getItem(position).routeNo;
            final String full2 = getItem(position).busNo;
            String forDisplay = "Stop: " + full;
            String forDisplay2 = "Bus: " + full2;
            ocItem.setText(forDisplay); // get the string at position
            ocItem2.setText(forDisplay2);

            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OCSaved.this, OCInfo.class);
                    intent.putExtra("stopNo", getItem(position).routeNo);
                    intent.putExtra("busInfo", getItem(position).busNo);

                    startActivity(intent);
                }
            });

            return result;
        }

        public long getItemId(int position) {
            return position;
        }
    }

}