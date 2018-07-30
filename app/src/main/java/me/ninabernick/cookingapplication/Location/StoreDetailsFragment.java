package me.ninabernick.cookingapplication.Location;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.ninabernick.cookingapplication.R;

public class StoreDetailsFragment extends Fragment {

    String id;
    HashMap<String, String> nearbyPlace;

    String name;
    String vicinity;
    String rating;
    String formatted_phone;
    String formatted_address;
    String website;
    String gmurl;
    String open_now;
    String pricelevel;
    String firstphotoreference;
    String secondphotoreference;
    String thirdphotoreference;
    String fourthphotoreference;

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
    }


    private String getUrl(String id) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        googlePlacesUrl.append("placeid="+ id);
        googlePlacesUrl.append("&key=" + "AIzaSyD_gosGg3qBnX2WOj6-fglzL49kTMO-KuY");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


    private class PlacesTask extends AsyncTask<Object, Integer, String> {

        String googlePlacesData;
        View view;
        String url;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(Object... params) {
            try{
                Log.i("Places Tag Launched", "Places Tag Launched");
                DownloadUrl downloadUrl = new DownloadUrl();

                view = (View) params[0];
                url = (String) params[1];

                googlePlacesData = downloadUrl.readUrl(url);
                Log.i("This is googlePlacesData", "Google Places Data: " + googlePlacesData);

                Log.d("GooglePlacesReadTask", "doInBackground Exit");
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result){

            Log.i("Got to onPostExecute", "Got to onPostExecute");
            DataParserPlaceDetails placeDetailsJsonParser = new DataParserPlaceDetails();

            Log.i("This is nearbyPlace", "nearbyPlace: " + nearbyPlace);
            nearbyPlace = placeDetailsJsonParser.parse(result);
            Log.i("This is nearbyPlace", "nearbyPlace: " + nearbyPlace);

            name = nearbyPlace.get("name");
            vicinity = nearbyPlace.get("vicinity");
            rating = nearbyPlace.get("rating");
            formatted_phone = nearbyPlace.get("formatted_phone");
            formatted_address = nearbyPlace.get("formatted_address");
            website = nearbyPlace.get("website");
            gmurl = nearbyPlace.get("gmurl");
            open_now = nearbyPlace.get("open_now");
            pricelevel = nearbyPlace.get("pricelevel");
            firstphotoreference = nearbyPlace.get("firstphotoreference");
            secondphotoreference = nearbyPlace.get("secondphotoreference");
            thirdphotoreference = nearbyPlace.get("thirdphotoreference");
            fourthphotoreference = nearbyPlace.get("fourthphotoreference");

            Log.i("Check Info One", "Check Info One " + name + ", " + vicinity + ", " + formatted_phone + ", "
                    + formatted_address + ", " + website + ", " + open_now + ", " + rating + ", " + Float.parseFloat(rating) + ", " + pricelevel + ", " + Float.parseFloat(pricelevel));

            setViewHolders(view);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_storedetails, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String url = getUrl(id);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = view;
        DataTransfer[1] = url;
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(DataTransfer);

    }

    public void setViewHolders(View view) {
        TextView tvStoreName = view.findViewById(R.id.tvStoreName);
        tvStoreName.setText(name);

        TextView tvStoreLocation = view.findViewById(R.id.tvStoreLocation);
        tvStoreLocation.setText(formatted_address);

        TextView tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvPhoneNumber.setText(formatted_phone);

        TextView tvWebsite = view.findViewById(R.id.tvWebsite);
        tvWebsite.setText(Html.fromHtml(website));

        RatingBar ratingBar = view.findViewById(R.id.rbDisplayRating);
        ratingBar.setRating(Float.parseFloat(rating));

        RatingBar priceBar = view.findViewById(R.id.rbDisplayPrice);
        priceBar.setRating((float) (Float.parseFloat(pricelevel) * 1.25));

        new DownloadImageTask((ImageView) view.findViewById(R.id.ivStorePicture)).execute(photourl(firstphotoreference));
        new DownloadImageTask((ImageView) view.findViewById(R.id.ivStorePicture2)).execute(photourl(secondphotoreference));
        new DownloadImageTask((ImageView) view.findViewById(R.id.ivStorePicture3)).execute(photourl(thirdphotoreference));
        //new DownloadImageTask((ImageView) view.findViewById(R.id.ivStorePicture4)).execute(photourl(fourthphotoreference));
    }

    public String photourl(String photoreference) {
        StringBuilder photosb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400");
        photosb.append("&photoreference=" + photoreference);
        photosb.append("&key=AIzaSyD_gosGg3qBnX2WOj6-fglzL49kTMO-KuY");
        Log.i("checking photo url", "checking photo url: " + photosb.toString());
        return photosb.toString();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

