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
 * Created by Миша on 15.10.2017.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesAdapterView> {

    private List<NewsData> data;

    public FavoritesAdapter(List<NewsData> data) {
        this.data = data;
    }

    @Override
    public FavoritesAdapterView onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_card_item, parent, false);
        return new FavoritesAdapterView(view);
    }

    @Override
    public void onBindViewHolder(FavoritesAdapterView holder, int position) {
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

    class FavoritesAdapterView extends RecyclerView.ViewHolder{

        private CardView cardView;
        private TextView title;
        private TextView date;
        private TextView description;
        private ImageView image;

        private Context context;

        FavoritesAdapterView(final View itemView) {
            super(itemView);

            context = itemView.getContext();


            Typeface segoeBold = Typeface.createFromAsset(context.getAssets(),
                    "segoe.ttf");

            cardView = (CardView) itemView.findViewById(R.id.favCardView);
            date  = (TextView) itemView.findViewById(R.id.favCardDate);
            title = (TextView) itemView.findViewById(R.id.favCardTitle);
            description = (TextView) itemView.findViewById(R.id.favCardDescription);
            image = (ImageView) itemView.findViewById(R.id.favCardImage);

            title.setTypeface(segoeBold);

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
