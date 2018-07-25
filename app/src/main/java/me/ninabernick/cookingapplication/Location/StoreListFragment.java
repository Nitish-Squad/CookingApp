package me.ninabernick.cookingapplication.Location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
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

import java.util.HashMap;
import java.util.List;

import me.ninabernick.cookingapplication.R;

public class StoreListFragment extends Fragment implements OnMapReadyCallback{

    List<HashMap<String, String>> nearbyPlacesList;
    GoogleMap mMap;
    double latitude;
    double longitude;

    public static StoreListFragment newInstance(GoogleMap mMap, Double latitude, Double longitude) {
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(getActivity()!=null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
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



        /*rvMyStores = view.findViewById(R.id.rvMyStores);
        storeAdapter = new StoreAdapter(nearbyPlacesList);
        rvMyStores.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyStores.setAdapter(storeAdapter);*/

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + R.string.google_maps_api_key);
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        String url = getUrl(latitude, longitude, "supermarket");
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);

        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);

        GetNearbyPlacesList getNearbyPlacesList = new GetNearbyPlacesList();
        getNearbyPlacesList.execute(DataTransfer);
        nearbyPlacesList = GetNearbyPlacesList.passList();

        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.i("Test", "Place: " + nearbyPlacesList.get(i).get("place_name"));
        }
    }
}
