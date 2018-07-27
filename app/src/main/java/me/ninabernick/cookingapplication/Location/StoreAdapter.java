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
        String rating = store.get("rating");
        String phonenumber = store.get("formatted_phone_number");
        String price_level =  store.get("price_level");

        Log.i("Store Adapter Test", "Store Rating: " + rating);
        Log.i("Store Adapter Test", "Store Phonenumber: " + phonenumber);
        Log.i("Store Adapter Test", "Store price_level: " + price_level);


        viewHolder.tvStoreName.setText(placeName);
        viewHolder.tvStoreLocation.setText(vicinity);
        viewHolder.tvStoreRating.setText(rating);
        //Glide.with(context).load(getPhotoUrl(mWidth, mHeight, mPhotoReference)).into(viewHolder.ivStoreImage);


    }

    @Override
    public int getItemCount() {
        Log.i("Store Adapter Size", "Size:" + nearbyPlacesList.size());
        return nearbyPlacesList.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivStoreImage) public ImageView ivStoreImage;
        @BindView(R.id.tvStoreName) public TextView tvStoreName;
        @BindView(R.id.tvStoreRating) public TextView tvStoreRating;
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
                String placeName = store.get("place_name");
                String vicinity = store.get("vicinity");
                String rating = store.get("rating");
                String phonenumber = store.get("formatted_phone_number");
                String price_level =  store.get("price_level");

                StoreDetailsFragment storeDetailsFragment = StoreDetailsFragment.newInstance(placeName, vicinity, phonenumber, price_level, rating);
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
