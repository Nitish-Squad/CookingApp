package me.ninabernick.cookingapplication.Location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.ninabernick.cookingapplication.R;

public class StoreListFragment extends Fragment{

    GoogleMap mMap;
    double latitude;
    double longitude;
    List<HashMap<String, String>> nearbyPlacesList = new ArrayList<>();

    StoreAdapter storeAdapter;
    RecyclerView rvMyStores;

    public static StoreListFragment newInstance(Double latitude, Double longitude) {
        StoreListFragment storeListFragment = new StoreListFragment();
        Bundle args = new Bundle();
        args.putDouble("Latitude", latitude);
        args.putDouble("Longitude", longitude);
        storeListFragment.setArguments(args);
        return storeListFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        longitude = getArguments().getDouble("Longitude", 0);
        latitude = getArguments().getDouble("Latitude", 0);

        storeAdapter = new StoreAdapter(nearbyPlacesList);
        calltoNearbyList();



    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyCnfJ8Xchn3-XtPkcfbLLRZk8IBLwNkfbA");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


    private class getStoreList extends AsyncTask<Object, String, String> {

        String googlePlacesData;
        GoogleMap mMap;
        String url;

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d("GetNearbyPlacesData", "doInBackground entered");
                mMap = (GoogleMap) params[0];
                url = (String) params[1];
                DownloadUrl downloadUrl = new DownloadUrl();
                googlePlacesData = downloadUrl.readUrl(url);
                Log.d("GooglePlacesReadTask", "doInBackground Exit");

            } catch (Exception e) {
                Log.d("GooglePlacesReadTask", e.toString());
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("susala", url);

            Log.d("GooglePlacesReadTask", "onPostExecute Entered");
            DataParser dataParser = new DataParser();
            nearbyPlacesList = dataParser.parse(result);
            Log.d("GooglePlacesReadTask", "onPostExecute Exit");

            storeAdapter = new StoreAdapter(nearbyPlacesList);
            storeAdapter.notifyDataSetChanged();
            Log.d("Adapter", "Set up Store Adapter");

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_storelist, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMyStores = view.findViewById(R.id.rvMyStores);
        rvMyStores.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyStores.setHasFixedSize(true);
        rvMyStores.setAdapter(storeAdapter);
        calltoNearbyList();
    }

    public void calltoNearbyList() {
        String url = getUrl(latitude, longitude, "supermarket");
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        getStoreList getStorelistData = new getStoreList();
        getStorelistData.execute(DataTransfer);
    }
}
