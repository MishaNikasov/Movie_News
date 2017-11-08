package donnu.nikasov.musicnews.Data;

import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Миша on 09.10.2017.
 */

public class NewsData {

    private String description;
    private String title;
    private String image;
    private String date;
    private boolean favorite;
    private String fullTextLink;
    private String newsType;

    public NewsData(String description, String title, String image, String date, boolean favorite, String fullTextLink, String newsType) {
        this.description = description;
        this.title = title;
        this.image = image;
        this.date = date;
        this.favorite = favorite;
        this.fullTextLink = fullTextLink;
        this.newsType = newsType;
    }

    public String getNewsType() {
        return newsType;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getFullTextLink() {
        return fullTextLink;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
