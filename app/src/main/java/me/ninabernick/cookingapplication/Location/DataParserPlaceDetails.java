package me.ninabernick.cookingapplication.Location;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParserPlaceDetails {

    public HashMap<String,String> parse(String jsonData){

        JSONObject jPlaceDetails = null;
        JSONObject jsonObject;

        try {
            /** Retrieves all the elements in the 'places' array */
            jsonObject = new JSONObject((String) jsonData);
            jPlaceDetails = jsonObject.getJSONObject("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getPlaces with the array of json object
         * where each json object represent a place
         */
        return getPlaceDetails(jPlaceDetails);
    }

    private HashMap<String, String> getPlaceDetails(JSONObject jPlaceDetails){

        HashMap<String, String> hPlaceDetails = new HashMap<String, String>();

        String name = "-NA-";
        String vicinity = "-NA-";
        String rating="-NA-";
        String formatted_phone="-NA-";
        String formatted_address="-NA-";
        String website="-NA-";
        String gmurl="-NA-";
        String weekday_text = "-NA-";
        String open_now = "true";
        String pricelevel = "0";
        String firstphotoreference = "-NA-";
        String secondphotoreference = "-NA-";
        String thirdphotoreference = "-NA-";
        String fourthphotoreference = "-NA-";

        try {
            if(!jPlaceDetails.isNull("name")){
                name = jPlaceDetails.getString("name");
            }
            if(!jPlaceDetails.isNull("vicinity")){
                vicinity = jPlaceDetails.getString("vicinity");
            }
            if(!jPlaceDetails.isNull("rating")){
                rating = jPlaceDetails.getString("rating");
            }
            if(!jPlaceDetails.isNull("formatted_address")) {
                formatted_address = jPlaceDetails.getString("formatted_address");
            }
            if(!jPlaceDetails.isNull("formatted_phone_number")) {
                formatted_phone = jPlaceDetails.getString("formatted_phone_number");
            }
            if(!jPlaceDetails.isNull("website")) {
                website = jPlaceDetails.getString("website");
            }
            if(!jPlaceDetails.isNull("url")) {
                gmurl = jPlaceDetails.getString("url");
            }
            if(!jPlaceDetails.isNull("open_now")){
                open_now = String.valueOf(jPlaceDetails.getString("open_now"));
            }
            if(!jPlaceDetails.isNull("price_level")){
                pricelevel = String.valueOf(jPlaceDetails.getString("price_level"));
            }

            if(!jPlaceDetails.isNull("photos")){
                JSONArray photos = jPlaceDetails.getJSONArray("photos");
                firstphotoreference = ((JSONObject)photos.get(1)).getString("photo_reference");
                secondphotoreference = ((JSONObject)photos.get(2)).getString("photo_reference");
                thirdphotoreference = ((JSONObject)photos.get(3)).getString("photo_reference");
                fourthphotoreference = ((JSONObject)photos.get(4)).getString("photo_reference");

                /*JSONArray mPhotos = new Photo[photos.length()];
                for(int i=0;i<photos.length();i++){
                    mPhotos[i] = new Photo();
                    place.mPhotos[i].mWidth = ((JSONObject)photos.get(i)).getInt("width");
                    place.mPhotos[i].mHeight = ((JSONObject)photos.get(i)).getInt("height");
                    place.mPhotos[i].mPhotoReference = ((JSONObject)photos.get(i)).getString("photo_reference");
                    JSONArray attributions = ((JSONObject)photos.get(i)).getJSONArray("html_attributions");
                    place.mPhotos[i].mAttributions = new Attribution[attributions.length()];
                    for(int j=0;j<attributions.length();j++){
                        place.mPhotos[i].mAttributions[j] = new Attribution();
                        place.mPhotos[i].mAttributions[j].mHtmlAttribution = attributions.getString(j);
                    }
                }*/
            }

            hPlaceDetails.put("firstphotoreference", firstphotoreference);
            hPlaceDetails.put("secondphotoreference", secondphotoreference);
            hPlaceDetails.put("thirdphotoreference", thirdphotoreference);
            hPlaceDetails.put("fourthphotoreference", fourthphotoreference);

            hPlaceDetails.put("name", name);
            hPlaceDetails.put("pricelevel", pricelevel);
            hPlaceDetails.put("vicinity", vicinity);
            hPlaceDetails.put("rating", rating);
            hPlaceDetails.put("formatted_address", formatted_address);
            hPlaceDetails.put("formatted_phone", formatted_phone);
            hPlaceDetails.put("website", website);
            hPlaceDetails.put("weekday_text", weekday_text);
            hPlaceDetails.put("gmurl", gmurl);
            hPlaceDetails.put("open_now", open_now);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hPlaceDetails;
    }
}
