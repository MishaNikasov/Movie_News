package donnu.nikasov.musicnews.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mukesh.tinydb.TinyDB;

import java.util.ArrayList;

import donnu.nikasov.musicnews.Data.NewsData;
import donnu.nikasov.musicnews.Adapters.FavoritesAdapter;
import donnu.nikasov.musicnews.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFavorites extends Fragment {

    private RecyclerView recyclerView;
    private static Context context;
    private static final int LAYOUT = R.layout.fragment_favorites;
    private static TinyDB tinyDB;
    private static ArrayList<NewsData> newsDatas;
    private static ArrayList<Object> newsDatasObjects;

    private static final String LOG_TAG = "FragmentFav";

    public FragmentFavorites() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(LAYOUT, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.favorites_item);

        FragmentFavorites.context = getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewFavorites);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(new FavoritesAdapter(newsDatas));

        return view;
    }

    public static void AddToFavList(NewsData newsData){

        newsDatas.add(newsData);
        Save();
    }

    public  static  void DelFromFavList(String link){

        for (NewsData news : newsDatas) {
            if (link.equals(news.getFullTextLink())) {
                newsDatas.remove(news);
                Save();
                return;
            }
        }
    }

    public static void InitDatabase(Context context){

        if (newsDatas == null){
            newsDatas = new ArrayList<>();
        }
        if (newsDatasObjects == null){
            newsDatasObjects = new ArrayList<>();
        }
        if (tinyDB == null){
            tinyDB = new TinyDB(context);
        }
    }

    public static void  Save(){

        newsDatasObjects.clear();

        for(NewsData a : newsDatas){
            newsDatasObjects.add(a);
        }

        tinyDB.putListObject("Data_News", newsDatasObjects);
    }

    public static void Load(){

        newsDatasObjects.clear();

        newsDatasObjects = tinyDB.getListObject("Data_News", NewsData.class);
        newsDatas.clear();

        for(Object objs : newsDatasObjects){
            newsDatas.add((NewsData) objs);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            newsDatas.clear();
            Save();
        }

        return super.onOptionsItemSelected(item);
    }

    public static ArrayList<NewsData> GetFavList(){
        return newsDatas;
    }

    public static Context getAppContext() {
        return FragmentFavorites.context;
    }
}
