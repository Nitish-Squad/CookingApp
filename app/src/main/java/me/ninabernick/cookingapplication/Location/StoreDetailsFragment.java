package me.ninabernick.cookingapplication.Location;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import me.ninabernick.cookingapplication.R;

public class StoreDetailsFragment extends Fragment{

    String name;
    String vicinity;
    String phonenumber;
    String pricelevel;
    String rating;

    public static StoreDetailsFragment newInstance(String name, String vicinity, String phonenumber, String pricelevel, String rating) {
        StoreDetailsFragment storeDetailsFragment = new StoreDetailsFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("vicinity", vicinity);
        args.putString("phonenumber", phonenumber);
        args.putString("pricelevel", pricelevel);
        args.putString("rating", rating);
        storeDetailsFragment.setArguments(args);
        return storeDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        name = getArguments().getString("name");
        vicinity = getArguments().getString("vicinity");
        phonenumber = getArguments().getString("phonenumber");
        pricelevel = getArguments().getString("pricelevel");
        rating = getArguments().getString("rating");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_storedetails, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvStoreName = view.findViewById(R.id.tvStoreName);
        tvStoreName.setText(name);

        TextView tvStoreLocation = view.findViewById(R.id.tvStoreLocation);
        tvStoreLocation.setText(vicinity);

        RatingBar ratingBar = view.findViewById(R.id.rbDisplayRating) ;

        ratingBar.setRating(Float.parseFloat(rating));

    }
}
