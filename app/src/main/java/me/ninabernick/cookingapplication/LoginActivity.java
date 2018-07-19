package me.ninabernick.cookingapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private ArrayList<String> permissions;
    private String name;
    private String profilePictureUrl;
    private String fbId;
    private ArrayList<String> friendsList = new ArrayList<>();
    //private String email;
    //private String mUsername;
    //CallbackManager callbackManager = CallbackManager.Factory.create();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("user_friends");

//        loginButton = (LoginButton) findViewById(R.id.login_button);
//
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        // App code
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        // App code
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        // App code
//                        Log.d("Facebook Login", "Login Failed!");
//                    }
//                });
        if (ParseUser.getCurrentUser() != null) {
            Log.d("login", "user already logged in");
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
        }
        else {
            ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException err) {
                    if (user == null) {
                        Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                    } else if (user.isNew()) {
                        Log.d("MyApp", "User signed up and logged in through Facebook!");

                        getUserDetailsFromFB();

                    } else {
                        Log.d("MyApp", "User logged in through Facebook!");
                        //getUserDetailsFromParse();
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

        // Suggested by https://disqus.com/by/dominiquecanlas/
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

                           // email = response.getJSONObject().getString("email");
                            //Log.d("email", email);

                            //mEmailID.setText(email);

                            name = response.getJSONObject().getString("name");
                           // mUsername.setText(name);
                            Log.d("name", name);

                            fbId = response.getJSONObject().getString("id");
                            Log.d("id", String.format("facebook id: %s", fbId));
                            ParseUser user = ParseUser.getCurrentUser();
                            Log.d("id", String.format("Parse userId: %s", user.getObjectId()));

                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");

                            //  Returns a 50x50 profile picture
                            profilePictureUrl = data.getString("url");
                            Log.d("profile picture", profilePictureUrl);

                            JSONObject friends = response.getJSONObject().getJSONObject("friends");
                            JSONArray friendsData = friends.getJSONArray("data");
                            for (int i = 0; i < friendsData.length(); i++) {
                                String friend = friendsData.getJSONObject(i).getString("name");
                                friendsList.add(friend);
                            }
                            Log.d("friends", friendsData.toString());
                            saveNewUser();

                            //new ProfilePhotoAsync(pictureUrl).execute();

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
            }
        });
    }



//    private void getUserDetailsFromParse() {
//        ParseUser parseUser = ParseUser.getCurrentUser();
//
////Fetch profile photo
//        try {
//            ParseFile parseFile = parseUser.getParseFile("profileThumb");
//            byte[] data = parseFile.getData();
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            //mProfileImage.setImageBitmap(bitmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //mEmailID.setText(parseUser.getEmail());
//        //mUsername.setText(parseUser.getUsername());
//
//        Toast.makeText(LoginActivity.this, "Welcome back " + mUsername.getText().toString(), Toast.LENGTH_SHORT).show();
//
//    }
//
//    private void saveNewUser() {
//        final ParseUser parseUser = ParseUser.getCurrentUser();
//        parseUser.setUsername(name);
//        //parseUser.setEmail(email);
//
////        Saving profile photo as a ParseFile
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
//        byte[] data = stream.toByteArray();
//        String thumbName = parseUser.getUsername().replaceAll("\\s+", "");
//        final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", data);
//
//        parseFile.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                parseUser.put("profileThumb", parseFile);
//
//                //Finally save all the user details
//                parseUser.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        Toast.makeText(LoginActivity.this, "New user:" + name + " Signed up", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }
//        });
//
//    }
}
