package rae_dylan_zack_sahnon.cst2335_final_project.Movies;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

import rae_dylan_zack_sahnon.cst2335_final_project.FoodNutritionDatabase.FoodNutritionDatabase;
import rae_dylan_zack_sahnon.cst2335_final_project.OCTranspoApp.OCMain;
import rae_dylan_zack_sahnon.cst2335_final_project.R;
import rae_dylan_zack_sahnon.cst2335_final_project.newsreader.NewsMain;

public class MovieActivity extends AppCompatActivity {

    private EditText search;
    private Button toMovies;
    private Fragment fragment;
    public ArrayList<String> movieList;
    private MovieAdapter movieAdapter;
    private ListView movieShow;
    private HttpURLConnection web;
    private String Siteref = "https://www.omdbapi.com/?apikey=6c9862c2&r=xml&t=";
    private String[] movies;
    public TextView movieDesc;
    public TextView moviePost;
    private ProgressBar prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        movieAdapter = new MovieAdapter(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Toolbar toolb = (Toolbar)findViewById(R.id.toolbar2);
        toolb.setTitle(R.string.movie_toolBar);
        setSupportActionBar(toolb);
        movieDesc = (TextView) findViewById(R.id.textView);

        Toast.makeText(this,R.string.movie_toast, Toast.LENGTH_SHORT).show();

        search = (EditText)findViewById(R.id.editText);
        movieShow = (ListView) findViewById(R.id.listId);

        prog = (ProgressBar) findViewById(R.id.progressBar);
        prog.setMax(6);
        moviePost = (TextView) findViewById(R.id.moviePoster);

        toMovies = findViewById(R.id.button2);
        movieList= new ArrayList<String>();

        toMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(toMovies,R.string.movie_snack,Snackbar.LENGTH_SHORT).show();}});


        Button searchbut = findViewById(R.id.searchbut);
        searchbut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                prog.setProgress(1);
                movies = new String[8];
                movies[0]="";
                if(search.getText().length()!=0){
                webReader reader = new webReader();
                reader.execute(Siteref+search.getText().toString());
                Log.i("inButton",movies[0]);
                movieList.clear();
                if(!movies[0].equals(null)){
                    movieList.add(movies[0]);
                movieAdapter.notifyDataSetChanged();}
               }
    }});

        fragment = new MovieFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.FrameFrag,fragment).addToBackStack(null).commit();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuinf = getMenuInflater();
        menuinf.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.NewsReaderIcon:
                startActivity(new Intent(MovieActivity.this, NewsMain.class));
                return true;

            case R.id.FoodIcon:
                startActivity(new Intent(MovieActivity.this, FoodNutritionDatabase.class));
                return true;

            case R.id.OCTranspoIcon:
                startActivity(new Intent(MovieActivity.this, OCMain.class));
                return true;

            case R.id.MovieIcon:
                Toast.makeText(this, "You are in the movies activity", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.help:
                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(MovieActivity.this)
                        .setTitle(R.string.Help)
                        .setMessage(getString(R.string.movie_info))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                helpBuilder.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class MovieAdapter extends ArrayAdapter<String> {
        public MovieAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @Override
        public int getCount() {
            return movieList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inff = MovieActivity.this.getLayoutInflater();
            View r =  inff.inflate(R.layout.movieitem, null);

            r.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    movieDesc.setText(movies[0]+"      "+movies[4]+"\n"+
                    movies[1]+"\n\n"+"Categories:"+movies[6]+"\n"+"Rated:"+movies[3]+"\n\n"+movies[5]);

                    moviePost.setText(movies[7]);
                }
            });

            TextView movname = (TextView) r.findViewById(R.id.movieName);
            movname.setText((CharSequence) movies[0]+"      "+movies[4]+"\n"+movies[1]);

            return r;
        }

        @Override
        public String getItem(int p){
            return movieList.get(p);
        }

    }

    class webReader extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String ...urls) {
            String ser = (String) search.getText().toString();


            try{
                URL site = new URL ((Siteref) + URLEncoder.encode(ser,"UTF-8"));
                web = (HttpURLConnection) site.openConnection();
                web.setRequestMethod("GET");
                web.setDoInput(true);
                web.connect();

            }catch(Exception t){
                Log.i("", "error:"+t.getMessage());}

            XmlPullParser parse = Xml.newPullParser();
            try{
                parse.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
                parse.setInput(web.getInputStream(),null);
                parse.nextTag();
                while(parse.next() != XmlPullParser.END_DOCUMENT){
                    if(parse.getEventType()==XmlPullParser.START_TAG){
                        movies[0] = parse.getAttributeValue(null,"title");
                        if(movies[0].equals("")){movies[0]=null;break;}
                        Log.i("name",movies[0]);
                        movies[1] = parse.getAttributeValue(null,"plot");

                        movies[2] = parse.getAttributeValue(null,"year");
                        prog.setProgress(2);
                        movies[3] = parse.getAttributeValue(null,"rated");
                        prog.setProgress(3);
                        movies[4] = parse.getAttributeValue(null,"runtime");
                        prog.setProgress(4);
                        movies[5] = parse.getAttributeValue(null,"actors");
                        prog.setProgress(5);
                        movies[6] = parse.getAttributeValue(null,"genre");
                        prog.setProgress(6);
                        movies[7] = parse.getAttributeValue(null,"poster");
                        prog.setProgress(7);
                    }
                }

            }catch(Exception t){Log.i("Error",t.getMessage());}

            return movies[0];
        }

        @Override
        protected void onPostExecute(String result) {
            movieShow = (ListView)findViewById(R.id.listId);
            movieShow.setAdapter(movieAdapter);
            prog.setProgress(0);
        }
    }
    // download IMG, code based upon:
    // https://stackoverflow.com/questions/18210700/best-method-to-download-image-from-url-in-android

    // I made several attempts to display the poster using this and other methods, but with each one I got
    // the same issue: android.os.StrictMode$AndroidBlockGuardPolicy.onNetwork.
    // I brought this to Eric who recommended an emulator running an older version of android, but I tried that
    // and received the same issue. I apologize for the incomplete state of the assignment, but I spent so long trying
    // to work with this that I didn't have time for the database.
    // Thank you for the part marks on what I have done, and thank you for the course.

    public Bitmap downloadIMG (String url) {
        Log.i("bitmap","called");
     try{
         URL imagesite = new URL(url);
         HttpURLConnection imgcon = (HttpURLConnection) imagesite.openConnection();
         imgcon.setRequestMethod("GET");
         imgcon.setDoInput(true);
         imgcon.connect();
         InputStream in = imgcon.getInputStream();
         Bitmap poster = BitmapFactory.decodeStream(in);
         int y = poster.getHeight();
         int scalew = 184/poster.getWidth();
         int scaley = 184/poster.getHeight();
         Matrix matrix = new Matrix();
         matrix.postScale(scalew,scaley);
         Bitmap rePoster = Bitmap.createBitmap(poster,0,0,poster.getWidth(),poster.getHeight(),matrix,false);
         return rePoster;
     }catch(IOException e){
         Log.i("img error",e.getMessage());
         return null;
     }

    }
}
