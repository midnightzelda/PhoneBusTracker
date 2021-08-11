package rae_dylan_zack_sahnon.cst2335_final_project.newsreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import rae_dylan_zack_sahnon.cst2335_final_project.R;

public class NewsFragment extends Fragment {
    public final String ACTIVITY_NAME = "NewsReader";

    private NewsEntry entry;
    ImageView image;

    /**
     * Inflates the header's view and creates onClickListeners for buttons
     * @param inflater The inflater
     * @param container The container for the view
     * @param savedInstanceState A bundle containing info for saved state
     * @return The view for the header's info
     */
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View feedview = inflater.inflate(R.layout.news_frag, container, false);

        Bundle data = getArguments();
        entry = (NewsEntry) data.getParcelable("entry");

        TextView category = feedview.findViewById(R.id.news_item_category);
        TextView title = feedview.findViewById(R.id.news_item_title);
        TextView author = feedview.findViewById(R.id.news_item_author);
        TextView date = feedview.findViewById(R.id.news_item_date);
        TextView description = feedview.findViewById(R.id.news_item_description);
        image = feedview.findViewById(R.id.news_item_image);
        Button gotobutton = feedview.findViewById(R.id.news_goto);
        Button savebutton = feedview.findViewById(R.id.news_save);

        if (entry != null) {
            // set layout fields
            category.setText(entry.getCategory());
            title.setText(entry.getTitle());
            author.setText(entry.getAuthor());
            date.setText(entry.getPubDate());
            parseDescription(description, entry.getDescription());

            gotobutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // SOURCE : https://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application
                    Intent toBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getLink()));
                    startActivity(toBrowser);
                }
            });

            savebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent saveEntry = new Intent(getActivity(), NewsMain.class);
                    Bundle saved = new Bundle();
                    saved.putParcelable("entry", entry);
                    saveEntry.putExtras(saved);
                    getActivity().setResult(50,saveEntry);
                    getActivity().finish();
                }
            });
        }
        return feedview;
    }

    /**
     * Parses the description, taking and downloading the image to display on the app, and updates views
     * @param text The {@link View} to load the text taken from the description
     * @param description The plain text description, including html tags
     */
    private void parseDescription(TextView text, String description) {
        String imgURL = description.substring(description.indexOf("http"), description.indexOf("' alt"));
        Log.i(ACTIVITY_NAME, "in parseDescription(). imgURL="+imgURL);
        String mainText = description.substring(description.indexOf("title"), description.indexOf("\' height")).substring(7);
        mainText += "\n\n";
        mainText += description.substring(description.indexOf("<p>")+3, description.indexOf("</p>"));

        // download image, show image
        new DownloadImage().execute(imgURL);

        // set views
        text.setText(mainText);
    }

    /**
     * Saves the bitmap image passed to it, under the name specified by imageName
     * @param context The application context
     * @param bm The bitmap to save
     * @param imageName The name of the image to save as
     */
    public void saveImage(Context context, Bitmap bm, String imageName) {
        FileOutputStream outstream;
        try {
            outstream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, outstream);
            outstream.flush();
            outstream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads and returns an image
     * @param context The application context
     * @param img the image's name
     * @return A bitmap containing the image
     */
    public Bitmap loadImage(Context context, String img) {
        Bitmap bm = null;
        FileInputStream instream;
        try {
            instream = context.openFileInput(img);
            bm = BitmapFactory.decodeStream(instream);
            instream.close();
            Log.i(ACTIVITY_NAME, "File exists and updating!");
            image.setImageBitmap(bm);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bm;
    }

    /**
     * Source code learned from <a href="https://www.codexpedia.com/android/android-download-and-save-image-internally/">this link</a>
     */
    class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        public final String CLASS_NAME = "DownloadImage";

        @Override
        protected Bitmap doInBackground(String ... args) {
            return downloadImage(args[0]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(getContext(), result, "temp.jpg");
            Toast.makeText(getContext(), "Image saved as temp.jpg", Toast.LENGTH_SHORT).show();
            loadImage(getContext(), "temp.jpg");
        }

        private Bitmap downloadImage(String surl) {
            Bitmap bm = null;
            try {
                InputStream input = new URL(surl).openStream();
                bm = BitmapFactory.decodeStream(input);
                input.close();
                if (bm != null) {
                    Log.i(CLASS_NAME, "Download successful!");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bm;
        }
    }
}
