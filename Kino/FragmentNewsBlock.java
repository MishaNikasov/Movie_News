package donnu.nikasov.musicnews.Fragments;


import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.mzelzoghbi.zgallery.ZGrid;
import com.mzelzoghbi.zgallery.entities.ZColor;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import donnu.nikasov.musicnews.Data.NewsData;
import donnu.nikasov.musicnews.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNewsBlock extends Fragment {

    private static final int LAYOUT = R.layout.fragment_news_block;
    private static final String LOG_TAG = "MyActivity";

    private String description;
    private String title;
    private String date;
    private String image;
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
                            ,true ,getArguments().getString("data_link")));

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

    class Parse extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getActivity(),"Загрузка...",
                    "С божьей помощью мы все загрузим...", false, false);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Document document;

            try {
                document = Jsoup.connect(link).get();

                Elements newsDetail = document.select("div.news-detail");

                description = newsDetail.select("p").first().text();
                date = newsDetail.select("div.date-time").first().text();
                image = newsDetail.select("img").first().absUrl("src");

                if ( newsDetail.select("p").first().select("strong").first() != null) {
                    title = newsDetail.select("p").first().select("strong").first().text();
                }
                else title = getArguments().getString("data_title");

                if (document.select("div.photos").first()!=null) {

                    Element images = document.select("div.photos").first();
                    Elements photoItems = images.select("img");

                    for (Element photo : photoItems) {
//
                        imageList.add(photo.absUrl("src"));
                        Log.d(LOG_TAG, photo.absUrl("src"));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();


            Typeface segoeLight = Typeface.createFromAsset(getContext().getAssets(),
                    "segoe.ttf");

            Typeface segoeNormal = Typeface.createFromAsset(getContext().getAssets(),
                    "segoeNormal.ttf");

            Typeface segoeBold = Typeface.createFromAsset(getContext().getAssets(),
                    "segoeBold.ttf");

            dateView.setTypeface(segoeLight);
            titleView.setTypeface(segoeBold);
            descriptionView.setTypeface(segoeLight);

            titleView.setText(title);
            descriptionView.setText(description);
            dateView.setText(date);

            if (imageList.isEmpty())
                gallery.setVisibility(View.GONE);
            else
                gallery.setVisibility(View.VISIBLE);

            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ZGallery.with(getActivity(),imageList)
                            .setToolbarTitleColor(ZColor.WHITE) // toolbar title color
                            .setGalleryBackgroundColor(ZColor.BLACK) // activity background color
                            .setToolbarColorResId(R.color.colorPrimary) // toolbar color
                            .setTitle("Галерея") // toolbar title
                            .show();

//                    ZGrid.with( getActivity(),imageList)
//                            .setToolbarColorResId(R.color.colorPrimary) // toolbar color
//                            .setTitle("Zak Gallery") // toolbar title
//                            .setToolbarTitleColor(ZColor.WHITE) // toolbar title color
//                            .setSpanCount(3) // colums count
//                            .setGridImgPlaceHolder(R.color.colorPrimary) // color placeholder for the grid image until it loads
//                            .show();
                }
            });

//            for (String image: imageList) {
//                Picasso.with(getActivity())
//                            .load(image)
//                            .fit().centerCrop()
//                            .into(gallery);
//            }

            Picasso.with(getContext()).load(image).fit().centerCrop().into(imageView);
        }
    }

}
