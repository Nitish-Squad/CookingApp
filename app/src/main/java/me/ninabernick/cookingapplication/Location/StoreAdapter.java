package me.ninabernick.cookingapplication.Location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeImageTransform;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;

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
    private MapListener listener;

    public interface MapListener {
        void onMapDetailsClicked(String longitude, String latitude);
    }

    public StoreAdapter(List<HashMap<String, String>> nearbyPlacesList, FragmentManager fragmentManager, MapListener listener) {
        this.nearbyPlacesList = nearbyPlacesList;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
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
        return nearbyPlacesList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvStoreName)
        public TextView tvStoreName;
        @BindView(R.id.tvStoreLocation)
        public TextView tvStoreLocation;
        Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {

                HashMap<String, String> store = nearbyPlacesList.get(position);
                String id = store.get("id");

                StoreDetailsFragment storeDetailsFragment = StoreDetailsFragment.newInstance(id);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Inflate transitions to apply
                    Transition changeTransform = TransitionInflater.from(context).
                            inflateTransition(R.transition.change_image_transform);
                    Transition explodeTransform = TransitionInflater.from(context).
                            inflateTransition(android.R.transition.explode);

                    // Setup enter transition on second fragment
                    storeDetailsFragment.setSharedElementEnterTransition(changeTransform);
                    storeDetailsFragment.setEnterTransition(explodeTransform);
                }

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addSharedElement(tvStoreName, tvStoreName.getTransitionName());
                fragmentTransaction.addSharedElement(tvStoreLocation, tvStoreLocation.getTransitionName());
                fragmentTransaction.addToBackStack("transaction");
                fragmentTransaction.replace(R.id.textContainer, storeDetailsFragment);
                fragmentTransaction.commit();
                listener.onMapDetailsClicked(store.get("lat"), store.get("lng"));

            }

        }
    }
}
