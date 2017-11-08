package donnu.nikasov.musicnews.Fragments;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mzelzoghbi.zgallery.ZGallery;
import com.mzelzoghbi.zgallery.entities.ZColor;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import donnu.nikasov.musicnews.Data.NewsData;
import donnu.nikasov.musicnews.Main.PicassoImageGetter;
import donnu.nikasov.musicnews.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNewsBlock extends Fragment {

    private static final int LAYOUT = R.layout.fragment_news_block;

    private static final String LOG_TAG = "MyActivity";
    private final static String TAG = "TestImageGetter";

    private String description;
    private String title;
    private String date;
    private String image;
    private String newsType;
    private String link;
    private ArrayList<String> imageList;
    private boolean isFavorite;

    private TextView titleView;
    private TextView dateView;
    private TextView descriptionView;
    private ImageView imageView;
    private ImageView gallery;
    private Button buttonShare;
    private ToggleButton toggleButton;

    public FragmentNewsBlock() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        newsType = getArguments().getString("data_type");
        link = getArguments().getString("data_link");
        isFavorite = getArguments().getBoolean("data_fav");

        if (!isFavorite){
            for (NewsData newsData : FragmentFavorites.GetFavList()){

                if (newsData.getFullTextLink().equals(link)){
                    isFavorite = true;
                    break;
                }
            }
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        if (isFavorite)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.favorites_item);
        else
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.feed_item);

        final View view = inflater.inflate(LAYOUT, container, false);
        imageList = new ArrayList<>();

//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        if (newsType.equals(null)){
            newsType = "News";
        }

        titleView = (TextView) view.findViewById(R.id.blockTitle);
        dateView = (TextView) view.findViewById(R.id.blockDate);
        descriptionView = (TextView) view.findViewById(R.id.blockDescription);
        imageView = (ImageView) view.findViewById(R.id.blockImage);
        gallery = (ImageView) view.findViewById(R.id.gallery);
        buttonShare = (Button) view.findViewById(R.id.buttonShare);
        toggleButton = (ToggleButton) view.findViewById(R.id.toggleButton);

        new Parse().execute();

        SetFav();

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Мы всем рассказали", Toast.LENGTH_SHORT).show();
            }
        });

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite){

                    FragmentFavorites.AddToFavList(new NewsData(
                            getArguments().getString("data_description"),getArguments().getString("data_title")
                            ,getArguments().getString("data_image"),getArguments().getString("data_date")
                            ,true ,getArguments().getString("data_link"), getArguments().getString("data_type")));

                    isFavorite = true;

                    Toast.makeText(getContext(), "Добавленно в избранное", Toast.LENGTH_SHORT).show();
                    SetFav();
                }

                else {
                    FragmentFavorites.DelFromFavList(link);

                    isFavorite = false;

                    Toast.makeText(getContext(), "Удаленно из избранного", Toast.LENGTH_SHORT).show();

//                    Snackbar.make(view, "Удаленно из избранного", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    SetFav();
                }
            }
        });

        return view;
    }

    public void SetFav(){
        if (isFavorite){
            toggleButton.setChecked(true);
        }
        else toggleButton.setChecked(false);
    }


    class Parse extends AsyncTask<Void, Void, Void> implements Html.ImageGetter{

        ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(getActivity());
            progDailog.setMessage("Загрузка...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Document document;

            try {
                document = Jsoup.connect(link).get();

                switch (newsType) {
                    case "News":

                        title = document.select("div.page-item-title").first().text();
                        date = document.select("div.page-item-info__date").first().text();
                        image = "http:" + document.select("div.image-shadow.page-item__image").first().select("img").first().attr("src");
                        description = document.select("div.page-content-text").first().html();

                        Element imageBlock1 = document.select("div.page-content-text").first();
                        Elements photoItems1 = imageBlock1.select("img");

                        for (Element photo : photoItems1) {

                            imageList.add("http:" + photo.attr("src"));
                            Log.d(LOG_TAG, photo.absUrl("src"));
                        }
                        break;

                    case "Interview":

                        title = document.select("h1.pagetitle.pagetitle-interview").first().text();
                        date = document.select("span.date__month").first().text();
                        image = "http:" + document.select("img.image-prewiew").first().attr("src");
                        description = "<i>" + document.select("div.description").first().text() + "</i>";

                        Element interviewContent = document.select("div.content-text").first();

                        description += interviewContent.html();

                        Element imageBlock2 = document.select("div.content-text").first();
                        Elements photoItems2 = imageBlock2.select("img");

                        for (Element photo : photoItems2) {

                            imageList.add("http:" + photo.attr("src"));
                            Log.d(LOG_TAG, photo.absUrl("src"));
                        }
//                    for (Element pElement: interviewText) {
//                        if (pElement.select("strong").first() != null && pElement.ownText().equals("")){
//                            description += "<br></br><br><b>" + pElement.select("strong").first().text()
//                                    + "</b></br><br></br>";
//                            Log.d(LOG_TAG, pElement.ownText());
//                        }
//                        else {
//                            description +="<br>" ;
//                            description += pElement.html();
//
////                            for (Element element: pElement.getAllElements()) {
////
////                                if ("strong".equals(element.nodeName())){
////                                    description += "<b>" + element.ownText() + "</b>";
////                                }
////                                else description += element.ownText();
////                            }
//
//                            description += "</br>";
//
//                        }
////                        else if (pElement.select("img").first() != null){
////                            String image = "http:" + pElement.select("img").first().attr("src");
////                            ImageView iv = new ImageView(getContext());
////                        }
//                    }
                        break;

                    case "Press":

                        description = "";
                        title = document.select("h1.pagetitle.pagetitle-interview").first().text();
                        date = document.select("span.date__month").first().text();
                        image = "http:" + document.select("img.image-prewiew").first().attr("src");
                        description += "<i>" + document.select("div.description").first().text() + "</i>";

                        description += document.select("div.content-text").first().html();

                        Element imageBlock3 = document.select("div.content-text").first();
                        Elements photoItems3 = imageBlock3.select("img");

                        for (Element photo : photoItems3) {

                            imageList.add("http:" + photo.attr("src"));
                            Log.d(LOG_TAG, photo.absUrl("src"));
                        }
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Typeface segoeLight = Typeface.createFromAsset(getContext().getAssets(),
                    "segoe.ttf");
            Typeface segoeBold = Typeface.createFromAsset(getContext().getAssets(),
                    "segoeBold.ttf");

            dateView.setTypeface(segoeLight);
            titleView.setTypeface(segoeBold);
            descriptionView.setTypeface(segoeLight);

            titleView.setText(title);
            dateView.setText(date);
            Picasso.with(getContext()).load(image).fit().centerCrop().into(imageView);

//            PicassoImageGetter imageGetter = new PicassoImageGetter(descriptionView, getContext());
//            Spannable html;
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                html = (Spannable) Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
//            } else {
//                html = (Spannable) Html.fromHtml(description, imageGetter, null);
//            }

            Spanned spanned = Html.fromHtml(description, this, null);

            descriptionView.setText(spanned);

            if (imageList.isEmpty())
                gallery.setVisibility(View.GONE);
            else
                gallery.setVisibility(View.VISIBLE);

            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ZGallery.with(getActivity(),imageList)
                            .setToolbarTitleColor(ZColor.WHITE)
                            .setGalleryBackgroundColor(ZColor.BLACK)
                            .setToolbarColorResId(R.color.colorPrimary)
                            .setTitle("Галерея")
                            .show();
                }
            });
//            descriptionView.refreshDrawableState();

            descriptionView.setMovementMethod(LinkMovementMethod.getInstance());
            progDailog.dismiss();
        }

        @Override
        public Drawable getDrawable(String source) {
            LevelListDrawable d = new LevelListDrawable();
            Drawable empty = getResources().getDrawable(R.drawable.ic_action_navigation_more_vert);

            d.addLevel(0, 0, empty);
            d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

            new LoadImage().execute("http:" + source, d);

            return d;
        }
    }


    private class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            Log.d(TAG, "doInBackground " + source);

            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            Log.d(TAG, "onPostExecute drawable " + mDrawable);
            Log.d(TAG, "onPostExecute bitmap " + bitmap);
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
//
//                if (bitmap.getHeight()>600 && bitmap.getWidth()>1200){
//                    mDrawable.setBounds(0, 0,bitmap.getWidth()/3, bitmap.getHeight()/3);
//                }

                if (bitmap.getHeight()>400 && bitmap.getWidth()>820){
                    mDrawable.setBounds(0, 0,bitmap.getWidth()/2, bitmap.getHeight()/2);
//                    mDrawable.setBounds(0, 0, width, 450);
                }
                else
                    mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                //                mDrawable.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

                mDrawable.setLevel(1);
                // i don't know yet a better way to refresh TextView
                // mTv.invalidate() doesn't work as expected
                CharSequence t = descriptionView.getText();
                descriptionView.setText(t);
            }
        }
    }



}
