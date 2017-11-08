package donnu.nikasov.musicnews.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import donnu.nikasov.musicnews.Data.NewsData;
import donnu.nikasov.musicnews.Fragments.FragmentNewsBlock;
import donnu.nikasov.musicnews.R;

/**
 * Created by Миша on 09.10.2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<NewsData> data;
    private int cardItem;

    public CardAdapter(List<NewsData> data, int cardItem) {
        this.data = data;
        this.cardItem = cardItem;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        View view = LayoutInflater.from(parent.getContext()).inflate(cardItem, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.title.setText(data.get(position).getTitle());
        holder.description.setText(data.get(position).getDescription());
        holder.date.setText(data.get(position).getDate());

        Picasso.with(holder.context)
                .load(data.get(position).getImage())
                .fit().centerCrop()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private TextView title;
        private TextView date;
        private TextView description;
        private ImageView image;

        private Context context;

        CardViewHolder(final View itemView) {
            super(itemView);

            context = itemView.getContext();

            Typeface segoeLight = Typeface.createFromAsset(context.getAssets(),
                    "segoe.ttf");

            Typeface segoeNormal = Typeface.createFromAsset(context.getAssets(),
                    "segoeNormal.ttf");

            Typeface segoeBold = Typeface.createFromAsset(context.getAssets(),
                    "segoeBold.ttf");

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            date  = (TextView) itemView.findViewById(R.id.cardDate);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.cardDescription);
            image = (ImageView) itemView.findViewById(R.id.cardImage);
//            checkBox = (CheckBox) itemView.findViewById(R.id.cardCheck);
//
//            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                    int position = getAdapterPosition();
//
//                    if (isChecked){
//                        Snackbar.make(buttonView, "Добавлено в избранное", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
//                        data.get(position).setFavorite(true);
//                        data.get(position).setTitle("!R");
//                        ParseData.setData(data);
//                    }
//                    else {
//                        Snackbar.make(buttonView, "Удалено из избранного", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
//                        data.get(position).setFavorite(false);
//                        ParseData.setData(data);
//
//                    }
//                }
//            });
            date.setTypeface(segoeLight);
            title.setTypeface(segoeBold);
            description.setTypeface(segoeLight);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();

                    FragmentNewsBlock fragmentNewsBlock = new FragmentNewsBlock();
                    Bundle args = new Bundle();

                    args.putString("data_title", data.get(position).getTitle());
                    args.putString("data_description", data.get(position).getDescription());
                    args.putString("data_image", data.get(position).getImage());
                    args.putString("data_date", data.get(position).getDate());
                    args.putBoolean("data_fav", data.get(position).isFavorite());
                    args.putString("data_link", data.get(position).getFullTextLink());
                    args.putString("data_type", data.get(position).getNewsType());

                    fragmentNewsBlock.setArguments(args);

                    ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, fragmentNewsBlock)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }
}
