package me.ninabernick.cookingapplication.Location;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesList extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    List<HashMap<String, String>> nearbyPlacesList;


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
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(result);
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }


    public static List<HashMap<String, String>> passList() {
        List<HashMap<String, String>> nearbyPlaceListFinal = nearbyPlacesList;
        return nearbyPlaceListFinal;
    }

}
