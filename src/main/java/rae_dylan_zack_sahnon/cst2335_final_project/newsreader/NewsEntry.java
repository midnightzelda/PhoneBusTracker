package rae_dylan_zack_sahnon.cst2335_final_project.newsreader;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Used to create objects that store information taken from a CBC news feed.
 * see <a href="https://www.techjini.com/blog/passing-objects-via-intent-in-android/">this source</a> that was used to write the passing of NewsEntry object
 * @author Rae Ehret
 */
public class NewsEntry implements Parcelable {

    String title, link, pubDate, author, category, description;

    public NewsEntry() {}

    public NewsEntry(String title, String link, String pubDate, String author, String category, String description) {
        setTitle(title);
        setLink(link);
        setPubDate(pubDate);
        setAuthor(author);
        setCategory(category);
        setDescription(description);
    }

    protected NewsEntry(Parcel in) {
        title = in.readString();
        link = in.readString();
        pubDate = in.readString();
        author = in.readString();
        category = in.readString();
        description = in.readString();
    }

    public String getTitle() {return title;}
    public String getLink() {return link;}
    public String getPubDate() {return pubDate;}
    public String getAuthor() {return author;}
    public String getCategory() {return category;}
    public String getDescription() {return description;}


    public void setTitle(String title) {this.title = title;}
    public void setLink(String link) {this.link = link;}
    public void setPubDate(String pubDate) {this.pubDate = pubDate;}
    public void setAuthor(String author) {this.author = author;}
    public void setCategory(String category) {this.category = category;}
    public void setDescription(String description) {this.description = description;}

    @Override
    public int describeContents() {
        return CONTENTS_FILE_DESCRIPTOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(pubDate);
        dest.writeString(author);
        dest.writeString(category);
        dest.writeString(description);
    }


    public static final Creator<NewsEntry> CREATOR = new Creator<NewsEntry>() {
        @Override
        public NewsEntry createFromParcel(Parcel in) {
            return new NewsEntry(in);
        }

        @Override
        public NewsEntry[] newArray(int size) {
            return new NewsEntry[size];
        }
    };
}
