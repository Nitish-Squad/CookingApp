package me.ninabernick.cookingapplication.Location;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ninabernick.cookingapplication.R;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    List<HashMap<String, String>> nearbyPlacesList;
    Context context;

    public StoreAdapter(List<HashMap<String, String>> nearbyPlacesList) {
        this.nearbyPlacesList = nearbyPlacesList;
    }

    @NonNull
    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View storeView = inflater.inflate(R.layout.item_store_maps, parent, false);
        final ViewHolder viewHolder = new ViewHolder(storeView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        HashMap<String, String> store = nearbyPlacesList.get(i);

        String placeName = store.get("place_name");
        String vicinity = store.get("vicinity");
        Log.i("Store Adapter Test", "hi hi:" + placeName);
        Log.i("Store Adapter Test", "blah blah: " + vicinity);

        viewHolder.tvName.setText(placeName);
        viewHolder.tvLocation.setText(vicinity);

    }

    @Override
    public int getItemCount() {
        return nearbyPlacesList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivStoreImage) public ImageView ivStoreImage;
        @BindView(R.id.tvName) public TextView tvName;
        @BindView(R.id.tvRating) public TextView tvRating;
        @BindView(R.id.tvLocation) public TextView tvLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
