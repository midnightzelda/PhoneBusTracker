package rae_dylan_zack_sahnon.cst2335_final_project.newsreader;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import rae_dylan_zack_sahnon.cst2335_final_project.FoodNutritionDatabase.FoodNutritionDatabase;
import rae_dylan_zack_sahnon.cst2335_final_project.Movies.MovieActivity;
import rae_dylan_zack_sahnon.cst2335_final_project.OCTranspoApp.OCMain;
import rae_dylan_zack_sahnon.cst2335_final_project.R;

public class NewsMain extends AppCompatActivity {
    /**
     * The Activity name, mainly for debugging
     */
    public final String ACTIVITY_NAME = "NewsMain";

    /**
     * Value describing the kind of query: all fields
     */
    public final int QUERY_ALL = 0;
    /**
     * Value describing the kind of query: all fields except id
     */
    public final int QUERY_STANDARD = 1;
    /**
     * Value describing the kind of query: title
     */
    public final int QUERY_FINDTITLE = 2;

    /**
     * Value describing the kind of feed loaded: from the web
     */
    public final int LIST_WEB = 0;
    /**
     * Value describing the kind of feed loaded: from the saved list (database)
     */
    public final int LIST_FAV = 1;
    /**
     * This variable indicates the current kind of list the {@link NewsMain#feed} currently is, i.g.: {@link NewsMain#LIST_WEB} or {@link NewsMain#LIST_FAV}
     */
    public int currentList = 0;

    private ProgressBar progressBar;
    private ListView list;
    private Button main;
    private Button load;
    private Button stats;

    private NewsDBHelper dbHelper;
    private Cursor c;
    private SQLiteDatabase db;
    private NewsAdapter newsAdapter;

    /**
     * Used to keep the program from crashing, it locks the methods that load entries into the feed. {@link NewsMain#getEntriesFromDB()} and {@link NewsMain#getEntriesFromWeb()}
     */
    private ReentrantLock loading;

    /**
     * The list containing the NewsEntry objects either loaded from Database or from Web
     */
    private ArrayList<NewsEntry> feed;

    /**
     * onCreate method that loads the views and sets onClickListeners.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_main);
        setTitle(getString(R.string.CBCNewsReaderText));

        Log.i(ACTIVITY_NAME, "in onCreate()");

        // toolbar
        Toolbar toolbar = findViewById(R.id.news_toolbar);
        toolbar.setTitle(R.string.news_title_abbr);
        setSupportActionBar(toolbar);

        // locks
        loading = new ReentrantLock();

        // create NewsDBHelper and SQLiteDatabase objects
//        this.deleteDatabase(NewsDBHelper.DATABASE_NAME); // call this to delete database
        dbHelper = new NewsDBHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        // header
        main = findViewById(R.id.news_main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feed = new ArrayList<>();
                currentList = LIST_WEB;
                new FeedFiller().execute("https://www.cbc.ca/cmlink/rss-world");
            }
        });

        // SAVED ENTRIES - loading entries from Database
        load = findViewById(R.id.news_load);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feed = getEntriesFromDB();
                currentList = LIST_FAV;
                newsAdapter.notifyDataSetChanged();
            }
        });

        // opens STATS dialog
        stats = findViewById(R.id.news_credits);
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NewsEntry> tempList = getEntriesFromDB();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.news_stats)
                        .setMessage(getString(R.string.news_stats_articles)+getArticleCount(tempList)+"\n"
                        +getString(R.string.news_stats_average)+getAvgWordCount(tempList)+"\n"
                        +getString(R.string.news_stats_min)+getMinWordCount(tempList)+"\n"
                        +getString(R.string.news_stats_max)+getMaxWordCount(tempList))
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // does nothing
                            }
                        });
                builder.show();
            }
        });

        // list
        list = findViewById(R.id.news_list);

        // add items to feed
        currentList = LIST_WEB;
        getEntriesFromWeb();

        // progress bar
        progressBar = findViewById(R.id.news_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

    }

    /**
     * Closes resources.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        dbHelper.close();
        if (c != null)
            c.close();
    }

    /**
     * Handles the result from {@link NewsFragment}
     * @param requestCode Not used.
     * @param responseCode The code retrieved from {@link NewsFragment}
     * @param data Data package holding the {@link NewsEntry} article in question
     */
    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        Log.d(ACTIVITY_NAME, "returning from NewsReader... Req: " +requestCode + ", Res: " +responseCode);
        if (responseCode == 50) {
            // check for extra to add to database
            Bundle extras = data.getExtras();
            if (extras != null) {
                NewsEntry toAddEntry = extras.getParcelable("entry");

                // check if title already exists in database
                Log.i(ACTIVITY_NAME, "checking if entry already exists in database");
                c = db.rawQuery(getQuery(QUERY_FINDTITLE, toAddEntry.getTitle()), null);
                if (c.getCount() > 0) {
                    Snackbar.make(list, "Article already exists / L'article est déjà enregistré", Snackbar.LENGTH_LONG).show();
                } else {
                    Log.d(ACTIVITY_NAME, "checking for NewsEntry to add to database: " + toAddEntry.getTitle());
                    // addEntry(entry)
                    addEntry(db, toAddEntry);
                    // snackbar
                    Snackbar.make(list, "Article saved / Article enregistré", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Dialog box showing upon pressing the backbutton, confirms whether user wants to quit the current activity.
     * Special thanks to: Sahnon Mahbub
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewsMain.this);
        builder.setMessage(getString(R.string.news_exit)) //Add a dialog message to strings.xml
                .setTitle(R.string.exit)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    // User clicked OK button
                    public void onClick(DialogInterface dialog, int id) {
                        NewsMain.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                       // nothing happens
                    }
                })
                .create()
                .show();
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(NewsMain.this);
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
                AlertDialog.Builder builder2 = new AlertDialog.Builder(NewsMain.this);
                builder2.setTitle(R.string.warning)
                        .setMessage(R.string.exit_msg)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent foodDatabase = new Intent(NewsMain.this, FoodNutritionDatabase.class);
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
                AlertDialog.Builder builder3 = new AlertDialog.Builder(NewsMain.this);
                builder3.setTitle(R.string.warning)
                        .setMessage(R.string.exit_msg)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent ocTranspo = new Intent(NewsMain.this, OCMain.class);
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
                AlertDialog.Builder builder4 = new AlertDialog.Builder(NewsMain.this);
                builder4.setTitle(R.string.warning)
                        .setMessage(R.string.exit_msg)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent movieInfo = new Intent (NewsMain.this, MovieActivity.class);
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

    /**
     * Gets a raw query as a String to be passed to a cursor object's rawQuery() method.
     * @param i Number indicating the type of query
     * @param arg An optional argument, necessary for certain query types
     * @return String holding the query, or null if no appropriate query selected.
     */
    public String getQuery(int i, String arg) {
        //attributes: title, link, pubDate, author, category, description;
        switch (i) {
            case QUERY_ALL:
                return "select * from " + NewsDBHelper.TABLE_NAME;
            case QUERY_STANDARD:
                return "select " + NewsDBHelper.KEY_TITLE + ", " + NewsDBHelper.KEY_LINK + ", " + NewsDBHelper.KEY_PUBDATE + ", " + NewsDBHelper.KEY_AUTHOR + ", " + NewsDBHelper.KEY_CATEGORY + ", " + NewsDBHelper.KEY_DESCRIPTION + " from " + NewsDBHelper.TABLE_NAME;
            case QUERY_FINDTITLE:
                return "select " + NewsDBHelper.KEY_TITLE + " from " + NewsDBHelper.TABLE_NAME + " where " + NewsDBHelper.KEY_TITLE + " = \"" + arg + "\"";
            default:
                return null;
        }
    }

    /**
     * Adds entry to database
     * @param db the database to add entry to
     * @param entry the news article (NewsEntry object) to add to database
     */
    public void addEntry(SQLiteDatabase db, NewsEntry entry) {
        ContentValues cv = new ContentValues();
        cv.put(NewsDBHelper.KEY_TITLE, entry.getTitle());
        cv.put(NewsDBHelper.KEY_LINK, entry.getLink());
        cv.put(NewsDBHelper.KEY_PUBDATE, entry.getPubDate());
        cv.put(NewsDBHelper.KEY_AUTHOR, entry.getAuthor());
        cv.put(NewsDBHelper.KEY_CATEGORY, entry.getCategory());
        cv.put(NewsDBHelper.KEY_DESCRIPTION, entry.getDescription());
        db.insert(NewsDBHelper.TABLE_NAME, NewsDBHelper.KEY_DESCRIPTION, cv);
    }

    /**
     * Gets entries from the local database, all saved News Entries.
     * @return list of news entries
     */
    public ArrayList<NewsEntry> getEntriesFromDB() {
        loading.lock();

        try {
            ArrayList<NewsEntry> list = new ArrayList<>();

            // load DB
            c = db.rawQuery(getQuery(QUERY_STANDARD, null), null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                // make NewsEntry objects, add to list
                NewsEntry entry = new NewsEntry();
                entry.setTitle(c.getString(c.getColumnIndex(NewsDBHelper.KEY_TITLE)));
                entry.setLink(c.getString(c.getColumnIndex(NewsDBHelper.KEY_LINK)));
                entry.setPubDate(c.getString(c.getColumnIndex(NewsDBHelper.KEY_PUBDATE)));
                entry.setAuthor(c.getString(c.getColumnIndex(NewsDBHelper.KEY_AUTHOR)));
                entry.setCategory(c.getString(c.getColumnIndex(NewsDBHelper.KEY_CATEGORY)));
                entry.setDescription(c.getString(c.getColumnIndex(NewsDBHelper.KEY_DESCRIPTION)));
                list.add(entry);
                c.moveToNext();
            }
        return list;
        } finally {
            loading.unlock();
        }
    }

    /**
     * Gets the entries from the online rss feed
     */
    public void getEntriesFromWeb() {
        loading.lock();
        feed = new ArrayList<>();
        new FeedFiller().execute("https://www.cbc.ca/cmlink/rss-world");
        loading.unlock();
    }

    /**
     * Gets saved article count
     * @param list The list/feed to count
     * @return The count
     */
    public int getArticleCount(ArrayList<NewsEntry> list) {
        return list.size();
    }

    /**
     * Gets the average word count
     * @param list The list to count
     * @return The average word count
     */
    public int getAvgWordCount(ArrayList<NewsEntry> list) {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            count += list.get(i).getDescription().split("\\s").length;
        }
        return count/(list.size());
    }

    /**
     * Gets the minimum word count
     * @param list The list to count
     * @return The minimum word count
     */
    public int getMinWordCount(ArrayList<NewsEntry> list) {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            int tempcount = list.get(i).getDescription().split("\\s").length;
            if (count == 0 || tempcount < count) {
                count = tempcount;
            }
        }
        return count;
    }

    /**
     * Gets the maximum word count
     * @param list The list to count
     * @return The maximum word count
     */
    public int getMaxWordCount(ArrayList<NewsEntry> list) {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            int tempcount = list.get(i).getDescription().split("\\s").length;
            if (count == 0 || tempcount > count) {
                count = tempcount;
            }
        }
        return count;
    }

    /**
     * ArrayAdapter to set views of each row in {@link ListView}.
     */
    private class NewsAdapter extends ArrayAdapter<NewsEntry> {
        public final String CLASS_NAME = "NewsAdapter";
        private NewsAdapter(Context context) {
            super(context, 0);
        }

        public int getCount(){
            return feed.size();
        }

        public NewsEntry getItem(int position) {
            return feed.get(position);
        }

        /**
         * creates and returns the view that holds one title from the news feed.
         * @param position the position in the list (in the feed)
         * @param convertView the view for view recycling
         * @param parent the parent
         * @return the view holding fully set fields using NewsEntry item
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = NewsMain.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.news_feed, null);
            final Button deleteButton = result.findViewById(R.id.news_deleteButton);
            final Button goButton = result.findViewById(R.id.news_gobutton);

            // get news entry
            final int i = position;
            final NewsEntry entry = getItem(i);

            // set onclicklisteners for buttons
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide buttons again
                    deleteButton.setVisibility(View.GONE);
                    goButton.setVisibility(View.GONE);

                    if (currentList == LIST_FAV) {
                        db.delete(NewsDBHelper.TABLE_NAME, NewsDBHelper.KEY_TITLE + " = \"" + entry.getTitle() + "\"", null);
                        Snackbar.make(deleteButton, "Deleted / Supprimé", Snackbar.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                feed = getEntriesFromDB();
                                newsAdapter.notifyDataSetChanged();
                            }
                        },200);
                    } else {
                        Snackbar.make(deleteButton, "Cannot delete from web feed.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide buttons again
                    deleteButton.setVisibility(View.GONE);
                    goButton.setVisibility(View.GONE);

                    Intent intent = new Intent(NewsMain.this, NewsReader.class);
                    // pass NewsEntry to next activity
                    intent.putExtra("entry", entry);
                    startActivityForResult(intent,100);
                }
            });

            // set text to items in view
             TextView title = result.findViewById(R.id.news_title);
             title.setText(getItem(position).getTitle());

            // when item clicked on, open new activity with more information
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (goButton.getVisibility() == View.VISIBLE) {
                        deleteButton.setVisibility(View.GONE);
                        goButton.setVisibility(View.GONE);
                    } else {
                        if (currentList == LIST_FAV) {
                            deleteButton.setVisibility(View.VISIBLE);
                            goButton.setVisibility(View.VISIBLE);
                        } else {
                            goButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            return result;
        }

        public long getItemId(int position) {
            return position;
        }
    }

    /**
     * Reads lines from Feed, creates new NewsEntry objects, and adds them to {@link NewsMain#feed}.
     */
    private class FeedFiller extends AsyncTask<String, Integer, String> {
        public final String CLASS_NAME = "FeedFiller";
        private final String ns = null;

        /**
         * updates the progress bar
         * @param values the value to set to the progressbar (example: 100 upon finishing xml loading)
         */
        @Override
        protected void onProgressUpdate(Integer ... values) {
            // progress bar update
            progressBar.setProgress(values[0]);
        }

        /**
         * upon finishing, updates necessary components
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            newsAdapter = new NewsAdapter(getApplicationContext());
            list.setAdapter(newsAdapter);
        }

        /**
         * works in the background, Async, reading xml
         * @param urls the url
         * @return message upon completion
         */
        @Override
        protected String doInBackground(String... urls) {
            progressBar.setMax(6);

            Log.i(CLASS_NAME, "in doInBackground()");

            // create URL and HttpURLConnection objects
            URL url = null;
            HttpURLConnection in = null;

            // load URL
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // connect using HttpURLConnection object
            try {
                in = (HttpURLConnection) url.openConnection();
                in.setReadTimeout(10000 /* milliseconds */);
                in.setConnectTimeout(15000 /* milliseconds */);
                in.setRequestMethod("GET");
                in.setDoInput(true);
                in.connect();
                Log.i(CLASS_NAME, "Successfully connected");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // getting input stream and putting it in parser
            XmlPullParser parser = Xml.newPullParser();
            try {
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in.getInputStream(), null);
                parser.nextTag();
                while (true) {
                    if (parser.next() == XmlPullParser.START_TAG) {
                        String tagname = parser.getName();
                        if (tagname.equals("item"))
                            break;
                    } else
                        Log.i(CLASS_NAME, "Skipping tags: currently at " + parser.getName());
                }
                parser.require(XmlPullParser.START_TAG, ns, "item");
                // loop through tags and pull information from "item" blocks
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    String name = null;
                    if (parser.getEventType() == XmlPullParser.START_TAG) {
                        name = parser.getName();
                        Log.i(CLASS_NAME, "in parser loop, at " + name);
                    }
                    // Looks for item tag and adds new NewsEntry
                    if (name != null) {
                        if (name.equals("item")) {
                            publishProgress(0);
                            NewsEntry entry = new NewsEntry();
                            entry.setTitle(readTitle(parser));
                            entry.setLink(readLink(parser));
                            while (true) { // skip to end tag of next line
                                if (parser.next() == XmlPullParser.END_TAG) {
                                    break;
                                }
                            }
                            entry.setPubDate(readPubDate(parser));
                            entry.setAuthor(readAuthor(parser));
                            entry.setCategory(readCategory(parser));
                            entry.setDescription(readDescription(parser));
                            feed.add(entry);
                            publishProgress(6);
                            parser.nextTag();
                            parser.require(XmlPullParser.END_TAG, ns, "item");
                        }
                    }
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Done";
        }

        /**
         * Reads the title of the article from the feed
         * @param parser the parser / feed
         * @return the title of the article
         * @throws IOException
         * @throws XmlPullParserException
         */
        private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
            // skip to "title" tag
            while (parser.next() != XmlPullParser.START_TAG) {
                Log.i(CLASS_NAME, "at " + parser.getName() + " and skipping...");
            }
            // if next tag is text from title tag, the
            String value = " ";
            parser.require(XmlPullParser.START_TAG, ns, "title");
            if (parser.next() == XmlPullParser.TEXT) {
                Log.i(CLASS_NAME, "reading \"title\"");
                value = parser.getText();
            }
            // skip until closing tag
            while (parser.next() != XmlPullParser.END_TAG) {
                Log.i(CLASS_NAME, "at " + parser.getName() + " and skipping...");
            }

            publishProgress(1);
            parser.require(XmlPullParser.END_TAG, ns, "title");
            
            return value;
        }

        /**
         * Reads link from feed / xml pull parser
         * @param parser the parser / feed
         * @return The link to the article
         * @throws IOException
         * @throws XmlPullParserException
         */
        private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
            // skip to "link" tag
            while (parser.next() != XmlPullParser.START_TAG) {
                Log.i(CLASS_NAME, "at " + parser.getName() + " and skipping...");
            }
            // if next tag is text from title tag, the
            String value = " ";
            parser.require(XmlPullParser.START_TAG, ns, "link");
            if (parser.next() == XmlPullParser.TEXT) {
                Log.i(CLASS_NAME, "reading \"link\"");
                value = parser.getText();
            }
            // skip until closing tag
            while (parser.next() != XmlPullParser.END_TAG) {
                Log.i(CLASS_NAME, "at " + parser.getName() + " and skipping...");
            }

            publishProgress(2);
            parser.require(XmlPullParser.END_TAG, ns, "link");

            return value;
        }

        /**
         * Reads publish date from xml feed / parser
         * @param parser
         * @return publish date
         * @throws IOException
         * @throws XmlPullParserException
         */
        private String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
            // skip to "publish date" tag
            while (parser.next() != XmlPullParser.START_TAG) {
                Log.i(CLASS_NAME, "at " + parser.getName() + " and skipping...");
            }
            // if next tag is text from title tag, the
            String value = " ";
            parser.require(XmlPullParser.START_TAG, ns, "pubDate");
            if (parser.next() == XmlPullParser.TEXT) {
                Log.i(CLASS_NAME, "reading \"pubDate\"");
                value = parser.getText();
            }
            // skip until closing tag
            while (parser.next() != XmlPullParser.END_TAG) {
                Log.i(CLASS_NAME, "at " + parser.getName() + " and skipping...");
            }

            publishProgress(3);
            parser.require(XmlPullParser.END_TAG, ns, "pubDate");

            return value;
        }

        /**
         * Reads author from xml pull parser / feed
         * @param parser xml pull parser or the feed
         * @return the author
         * @throws IOException
         * @throws XmlPullParserException
         */
        private String readAuthor(XmlPullParser parser) throws IOException, XmlPullParserException {
            // skip to "author" tag
            while (parser.next() != XmlPullParser.START_TAG) {
                Log.i(CLASS_NAME, "at " + parser.getName() + " and skipping...");
            }
            // if next tag is text from title tag, the
            String value = " ";
            parser.require(XmlPullParser.START_TAG, ns, "author");
            if (parser.next() == XmlPullParser.TEXT) {
                Log.i(CLASS_NAME, "reading \"author\"");
                value = parser.getText();
            }
            // skip until closing tag
            while (parser.next() != XmlPullParser.END_TAG) {
                Log.i(CLASS_NAME, "at " + parser.getName() + " and skipping...");
            }

            publishProgress(4);
            parser.require(XmlPullParser.END_TAG, ns, "author");

            return value;
        }

        /**
         * Reads category from xml pull parser / feed
         * @param parser the parser / feed
         * @return the category
         * @throws IOException
         * @throws XmlPullParserException
         */
        private String readCategory(XmlPullParser parser) throws IOException, XmlPullParserException {
            // skip to "category" tag
            while (parser.next() != XmlPullParser.START_TAG) {
                Log.i(CLASS_NAME, "at " + parser.getName() + " and skipping...");
            }
            // if next tag is text from title tag, the
            String value = " ";
            parser.require(XmlPullParser.START_TAG, ns, "category");
            if (parser.next() == XmlPullParser.TEXT) {
                Log.i(CLASS_NAME, "reading \"category\"");
                value = parser.getText();
            }
            // skip until closing tag
            while (parser.next() != XmlPullParser.END_TAG) {
                Log.i(CLASS_NAME, "at " + parser.getName() + " and skipping...");
            }

            publishProgress(5);
            parser.require(XmlPullParser.END_TAG, ns, "category");

            return value;
        }

        /**
         * Reads description from feed / xml pull parser
         * @param parser the parser / feed
         * @return description text, including <img> and other tags
         * @throws IOException
         * @throws XmlPullParserException
         */
        private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
            // skip to "description" tag
            while (parser.next() != XmlPullParser.START_TAG) {
                Log.i(CLASS_NAME, "at " + parser.getName() + " and skipping...");
            }
            // if next tag is text from title tag, the
            String value = " ";
            parser.require(XmlPullParser.START_TAG, ns, "description");
            if (parser.next() == XmlPullParser.TEXT) {
                Log.i(CLASS_NAME, "reading \"description\"");
                value = parser.getText();
            }
            // skip until closing tag
            while (parser.next() != XmlPullParser.END_TAG) {
                Log.i(CLASS_NAME, "at " + parser.getName() + " and skipping...");
            }

            parser.require(XmlPullParser.END_TAG, ns, "description");

            return value;
        }

        /**
         * skips past tags including sub-tags within it. throws exception when not at a start tag
         * @param parser the parser / feed
         * @throws XmlPullParserException
         * @throws IOException
         */
        private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }
    }
}
