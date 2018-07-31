package me.ninabernick.cookingapplication.Location;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ninabernick.cookingapplication.R;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    List<HashMap<String, String>> nearbyPlacesList;
    Context context;
    FragmentManager fragmentManager;

    public StoreAdapter(List<HashMap<String, String>> nearbyPlacesList, FragmentManager fragmentManager) {
        this.nearbyPlacesList = nearbyPlacesList;
        this.fragmentManager = fragmentManager;
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

        viewHolder.tvStoreName.setText((i + 1) + ". " + placeName);
        viewHolder.tvStoreLocation.setText(vicinity);

    }

    @Override
    public int getItemCount() {
        Log.i("Store Adapter Size", "Size:" + nearbyPlacesList.size());
        return nearbyPlacesList.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvStoreName) public TextView tvStoreName;
        @BindView(R.id.tvStoreLocation) public TextView tvStoreLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {

                HashMap<String, String> store = nearbyPlacesList.get(position);
                String id = store.get("id");
                String placeName = store.get("place_name");
                String vicinity = store.get("vicinity");

                StoreDetailsFragment storeDetailsFragment = StoreDetailsFragment.newInstance(id);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.textContainer, storeDetailsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }

        }
    }

    private String getPhotoUrl(Integer mWidth, Integer mHeight, String photoreference) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        googlePlacesUrl.append("maxwidth=" + mWidth);
        googlePlacesUrl.append("&maxheight=" + mHeight);
        googlePlacesUrl.append("&photoreference=" + photoreference);
        googlePlacesUrl.append("&key=" + "AIzaSyCnfJ8Xchn3-XtPkcfbLLRZk8IBLwNkfbA");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }
}
