package donnu.nikasov.musicnews.Fragments;


import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import donnu.nikasov.musicnews.Adapters.CardAdapter;
import donnu.nikasov.musicnews.Data.NewsData;
import donnu.nikasov.musicnews.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFeed extends Fragment {

    private static final int LAYOUT = R.layout.fragment_feed;

    private static final String TAG = "MyActivity";
    private List<NewsData> data;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static String currentNews;

    public FragmentFeed() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.refresh);

        View view = inflater.inflate(LAYOUT, container, false);

        Bundle bundle = this.getArguments();

        if (currentNews == null) {
            currentNews = "News";
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.feed_item);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewFeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                data = new ArrayList<>();
                mp.start();
                new Parse().execute();
            }
        });

        data = new ArrayList<>();

        new Parse().execute();

        return view;
    }
    public  void setData(List<NewsData> data) {
        this.data = data;
    }

//    public List<NewsData> updateNewsDataList() throws ExecutionException, InterruptedException{
//
//        if (parseData == null){
//            return createNewsDataList();
//        }
//
//        else {
//            parse = new Parse();
//            parse.execute();
//
//            rawData = parse.get();
//
//            for (int i = 0; i < rawData.size() ; i++) {
//                if (!parseData.contains(rawData.get(i))){
//                    parseData.add(rawData.get(i));
//                }
//            }
//            return parseData;
//        }
//    }

    public void SetCurrentNews(String news){
        currentNews = news;
    }

    class Parse extends AsyncTask<Void, Void, Void> {

        ArrayList<NewsData> parseData;
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
            Log.d(TAG, "start");


            try {
                parseData = new ArrayList<>();

                Document document;

                switch (currentNews) {

                    case "News":
                        document = Jsoup.connect("http://www.kinomania.ru/news/").get();
                        break;
                    case "Press":
                        document = Jsoup.connect("http://www.kinomania.ru/article/press_review/").get();
                        break;
                    default:
                        document = Jsoup.connect("http://www.kinomania.ru/article/interview/").get();
                        break;
                }

                Elements elements = document.select("div.pagelist-item.clear");

                for (Element element : elements) {
                    Element image = element.select("div.image-shadow").first().select("a").first().select("img").first();
                    Element date = element.select("div.pagelist-info").first().select("span.date__month").first();
                    Element title = element.select("div.pagelist-item-title").first();
                    Element description = element.select("p").first();
                    Element link =  element.select("a").first();

                    parseData.add(new NewsData(
                              description.text()
                            , title.text()
                            , "http:" + image.attr("data-original")
                            , date.text()
                            , false
                            , link.absUrl("href")
                            , currentNews));

                    Log.d(TAG, image.attr("data-original"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void newsDatas) {
            super.onPostExecute(newsDatas);

            data = parseData;
            swipeRefreshLayout.setRefreshing(false);
            recyclerView.setAdapter(new CardAdapter(data, R.layout.card_item));

            progDailog.dismiss();
        }
    }
}
