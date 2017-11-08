package donnu.nikasov.musicnews.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mukesh.tinydb.TinyDB;
import com.mzelzoghbi.zgallery.ZGrid;
import com.mzelzoghbi.zgallery.entities.ZColor;

import java.util.ArrayList;

import donnu.nikasov.musicnews.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGallery extends Fragment {

    private static ArrayList<String> imageList;
    private static TinyDB tinyDB;

    public FragmentGallery() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_fragment_gallery, container, false);
    }

    public static void AddImage(String image){
        imageList.add(image);
        Save();
    }


    public static void InitGallery(Context context){

        if (imageList == null){
            imageList = new ArrayList<>();
        }

        if (tinyDB == null){
            tinyDB = new TinyDB(context);
        }
    }

    public static void Save(){
        tinyDB.putListString("Gallery", imageList);
    }

    public static void Load(){
        imageList = tinyDB.getListString("Gallery");
    }

    public static ArrayList<String> getImageList() {
        return imageList;
    }
}
