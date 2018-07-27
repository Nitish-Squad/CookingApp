package me.ninabernick.cookingapplication.Location;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DataParser {
    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            Log.d("Places", "parse");
            jsonObject = new JSONObject((String) jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            Log.d("Places", "parse error");
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        int placesCount = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;
        Log.d("Places", "getPlaces");

        for (int i = 0; i < placesCount; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
                Log.d("Places", "Adding places");

            } catch (JSONException e) {
                Log.d("Places", "Error in Adding places");
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        String rating = "0";
        String formatted_phone_number = "-NA-";
        String price_level = "-NA-";


        Log.d("getPlace", "Entered");

        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
                Log.d("does this ever get called?", "does this ever get called?");
            }
            if (!googlePlaceJson.isNull("rating")) {
                rating = googlePlaceJson.getString("rating");
            }
            if (!googlePlaceJson.isNull("formatted_phone_number")) {
                formatted_phone_number = googlePlaceJson.getString("formatted_phone_number");
                Log.d("From the Data Parser", "From Data Parser, phone number: " + googlePlaceJson.getString("formatted_phone_number"));
            }
            if (!googlePlaceJson.isNull("price_level")) {
                price_level = googlePlaceJson.getString("price_level");
            }

            /*if(!googlePlaceJson.isNull("photos")){
                JSONArray photos = googlePlaceJson.getJSONArray("photos");
                try {
                    mWidth = ((JSONObject)photos.get(1)).getInt("width");
                    mHeight = ((JSONObject)photos.get(1)).getInt("height");
                    mPhotoReference = ((JSONObject)photos.get(1)).getString("photo_reference");

                    Log.d("Print photo", "Print photo attributes: " + mWidth + mHeight + mPhotoReference);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }*/

            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");
            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("vicinity", vicinity);

            googlePlaceMap.put("rating", rating);
            googlePlaceMap.put("formatted_phone_number", formatted_phone_number);
            googlePlaceMap.put("price_level", price_level);
            /*googlePlaceMap.put("mWidth", mWidth.toString());
            googlePlaceMap.put("mHeight", mHeight.toString());
            googlePlaceMap.put("mPhotoReference", mPhotoReference);*/

            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);
            Log.d("getPlace", "Putting Places");

        } catch (JSONException e) {
            Log.d("getPlace", "Error");
            e.printStackTrace();
        }
        return googlePlaceMap;
    }
}
