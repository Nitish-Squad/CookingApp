package me.ninabernick.cookingapplication.Location;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import me.ninabernick.cookingapplication.R;

public class StoreListFragment extends Fragment implements StoreAdapter.MapListener{

    GoogleMap mMap;
    double latitude;
    double longitude;
    List<HashMap<String, String>> nearbyPlacesList;

    StoreAdapter storeAdapter;
    RecyclerView rvMyStores;
    BottomNavigationView bottomNavigationView;

    private StoreListFragmentListener listener;

    public interface StoreListFragmentListener {
        void oneStoreMap(String latitude, String longitude);
    }

    public static StoreListFragment newInstance(Double latitude, Double longitude) {
        StoreListFragment storeListFragment = new StoreListFragment();
        Bundle args = new Bundle();
        args.putDouble("Latitude", latitude);
        args.putDouble("Longitude", longitude);
        storeListFragment.setArguments(args);
        return storeListFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StoreListFragmentListener) {
            this.listener = (StoreListFragmentListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        longitude = getArguments().getDouble("Longitude", 0);
        latitude = getArguments().getDouble("Latitude", 0);

        nearbyPlacesList = new ArrayList<>();
        storeAdapter = new StoreAdapter(nearbyPlacesList, getFragmentManager(), this);

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&key=" + "AIzaSyD_gosGg3qBnX2WOj6-fglzL49kTMO-KuY");
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
                Log.i("This is url", "Google Places url: " + url);
                googlePlacesData = downloadUrl.readUrl(url);
                Log.i("This is googlePlacesData", "Google Places Data: " + googlePlacesData);
                Log.d("GooglePlacesReadTask", "doInBackground Exit");

            } catch (Exception e) {
                Log.d("GooglePlacesReadTask", e.toString());
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("Checking URL in StoreListFragment", url);

            Log.d("GooglePlacesReadTask", "onPostExecute Entered");
            DataParser dataParser = new DataParser();
            List<HashMap<String, String>> nearbyPlaces = dataParser.parse(result);

            nearbyPlacesList.clear();
            nearbyPlacesList.addAll(nearbyPlaces);
            Log.d("GooglePlacesReadTask", "onPostExecute Exit");

            storeAdapter.notifyDataSetChanged();

            Log.d("Adapter", "Not Different Data Set");

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

        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.top_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.distance:
                        calltoNearbyList();
                        bottomNavigationView.getMenu().getItem(1).setIcon(R.drawable.rating);
                        return true;
                    case R.id.rating:
                        bottomNavigationView.getMenu().getItem(1).setIcon(R.drawable.rating_filled);
                        rvMyStores.smoothScrollToPosition(0);
                        calltoProminenceList();
                        return true;
                    case R.id.price:
                        bottomNavigationView.getMenu().getItem(1).setIcon(R.drawable.rating);
                        rvMyStores.smoothScrollToPosition(0);
                        sortListPrice();
                        return true;
                }
                return false;
            }
        });
    }

    public void calltoProminenceList() {
        String url = getUrl(latitude, longitude, "supermarket");
        Log.i("This is url prom", "Url: " + url);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        getStoreList getStorelistData = new getStoreList();
        getStorelistData.execute(DataTransfer);
    }

    public void calltoNearbyList() {
        String url = getDistanceUrl(latitude, longitude, "supermarket");
        Log.i("This is url dist", "Url: " + url);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        getStoreList getStorelistData = new getStoreList();
        getStorelistData.execute(DataTransfer);
    }


    public void sortListPrice() {
        Collections.sort(nearbyPlacesList, new Comparator<HashMap<String, String>>() {
            public int compare(HashMap<String, String> obj1, HashMap<String, String> obj2) {
                return obj2.get("price_level").compareTo(obj1.get("price_level"));
            }
        });
        storeAdapter.notifyDataSetChanged();
    }

    private String getDistanceUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&rankby=distance");
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&key=" + "AIzaSyD_gosGg3qBnX2WOj6-fglzL49kTMO-KuY");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onMapDetailsClicked(String longitude, String latitude) {
        listener.oneStoreMap(longitude, latitude);
    }

}

