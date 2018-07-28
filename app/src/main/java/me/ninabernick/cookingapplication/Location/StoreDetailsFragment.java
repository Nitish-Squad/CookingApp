package me.ninabernick.cookingapplication.Location;

import android.os.AsyncTask;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import me.ninabernick.cookingapplication.R;

public class StoreDetailsFragment extends Fragment {

    String id;
    StringBuilder sb;

    String name;
    String vicinity;
    String rating;
    String formatted_phone;
    String formatted_address;
    String website;
    String gmurl;
    String weekday_text;
    String open_now;
    String pricelevel;

    public static StoreDetailsFragment newInstance(String id) {
        StoreDetailsFragment storeDetailsFragment = new StoreDetailsFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        storeDetailsFragment.setArguments(args);
        return storeDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("id");
        Log.i("Check id", "Check ID: " + id);

        populateData();
    }

    public void populateData() {
        sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        sb.append("placeid="+ id);
        sb.append("&key=AIzaSyD_gosGg3qBnX2WOj6-fglzL49kTMO-KuY");
        Log.i("This is the Url", "This is the Url" + sb.toString());

        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(sb.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_storedetails, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i("Check Info Two", "Check Info Two " + name + ", " + vicinity + ", " + formatted_phone + ", "
                + formatted_address + ", " + website + ", " + weekday_text + ", " + open_now + ", ");

        // + rating + ", " + Float.parseFloat(rating) + ", " + pricelevel + ", " + Float.parseFloat(pricelevel)

        TextView tvStoreName = view.findViewById(R.id.tvStoreName);
        tvStoreName.setText(name);

        TextView tvStoreLocation = view.findViewById(R.id.tvStoreLocation);
        tvStoreLocation.setText(vicinity);

        TextView tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvPhoneNumber.setText(formatted_phone);

        TextView tvAddress = view.findViewById(R.id.tvAddress);
        tvAddress.setText(formatted_address);

        TextView tvWebsite = view.findViewById(R.id.tvWebsite);
        tvWebsite.setText(website);

        TextView tvWeekdayText = view.findViewById(R.id.tvWeekdayText);
        tvWeekdayText.setText(weekday_text);

        TextView tvOpenNow = view.findViewById(R.id.tvOpenNow);
        tvOpenNow.setText(open_now);

        RatingBar ratingBar = view.findViewById(R.id.rbDisplayRating);
        ratingBar.setRating(Float.parseFloat(rating));

        RatingBar priceBar = view.findViewById(R.id.rbDisplayPrice);
        priceBar.setRating((float) (Float.parseFloat(pricelevel) * 1.25));
    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, HashMap<String, String>> {

        JSONObject jObject;

        @Override
        protected HashMap<String, String> doInBackground(String... jsonData) {

            HashMap<String, String> hPlaceDetails = null;
            DataParserPlaceDetails placeDetailsJsonParser = new DataParserPlaceDetails();

            try {
                jObject = new JSONObject(jsonData[0]);
                hPlaceDetails = placeDetailsJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return hPlaceDetails;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> hPlaceDetails) {

            name = hPlaceDetails.get("name");
            vicinity = hPlaceDetails.get("vicinity");
            rating = hPlaceDetails.get("rating");
            formatted_phone = hPlaceDetails.get("formatted_phone");
            formatted_address = hPlaceDetails.get("formatted_address");
            website = hPlaceDetails.get("website");
            gmurl = hPlaceDetails.get("gmurl");
            weekday_text = hPlaceDetails.get("weekday_text");
            open_now = hPlaceDetails.get("open_now");
            pricelevel = hPlaceDetails.get("pricelevel");

            Log.i("Check Info One", "Check Info One " + name + ", " + vicinity + ", " + formatted_phone + ", "
                    + formatted_address + ", " + website + ", " + weekday_text + ", " + open_now + ", " + rating + ", "
                    + Float.parseFloat(rating) + ", " + pricelevel + ", " + Float.parseFloat(pricelevel));

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        Log.i("Real Url", data);
        return data;
    }
}

