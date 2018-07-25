package me.ninabernick.cookingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private ArrayList<String> permissions;
    private String name;
    private String profilePictureUrl;
    private String fbId;
    private ArrayList<String> friendsList = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("user_friends");

        if (ParseUser.getCurrentUser() != null) {
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
        else {
            ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException err) {
                    if (user == null) {
                        Log.d("Login", "The user cancelled the Facebook login.");
                    }
                    else {
                        getUserDetailsFromFB();
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserDetailsFromFB() {


        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,picture,friends,id");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        try {

                            name = response.getJSONObject().getString("name");
                            fbId = response.getJSONObject().getString("id");

                            ParseUser user = ParseUser.getCurrentUser();
                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");
                            profilePictureUrl = data.getString("url");


                            JSONObject friends = response.getJSONObject().getJSONObject("friends");
                            JSONArray friendsData = friends.getJSONArray("data");
                            for (int i = 0; i < friendsData.length(); i++) {
                                String friend = friendsData.getJSONObject(i).getString("name");
                                friendsList.add(friend);
                            }

                            saveNewUser();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

    private void saveNewUser() {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("name", name);
        user.put("fbId", fbId);
        user.put("friends", friendsList);
        user.put("profileImageURL", profilePictureUrl);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /*
     * The code below sets up the notifications channel, it is necessary for any version of Android
     * above 8.0. It essentially allows any notifications to be send externally from the application,
     * it should be called when the app starts up so it is here so that the launcher activity (this one)
     * can call it immediately.
     */

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

    }



}