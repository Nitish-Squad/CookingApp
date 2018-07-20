package me.ninabernick.cookingapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

import me.ninabernick.cookingapplication.models.Recipe;

public class HomeActivity extends AppCompatActivity implements ProfileFragment.ProfileListener{

    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment feedFragment = new FeedFragment();
    Fragment RecipeDetailFragment;
    Fragment profileFragment = new ProfileFragment();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        final Recipe.Query recipeQuery = new Recipe.Query();

        // place to order the recipes in some manner (ex: rating, when created, etc)
        //postsQuery.orderByDescending("createdAt");

        recipeQuery.getTop();
        recipeQuery.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e == null)
                {
                    Recipe recipe = objects.get(0);




                    Integer test_int = recipe.getNumberofSteps();


                    // code used to add steps to the sample recipe on the Parse Server

                    /*try {
                        json.put("text","Put both slices together");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    try {
                        json.put("icon","TBD");
                    } catch (JSONException e2) {
                        Log.d("MainActivity","Failed to save icon value");
                        e2.printStackTrace();
                    }

                    try {
                        json.put("time","One minute");
                    } catch (JSONException e3) {
                        Log.d("MainActivity","Failed to save time value");
                        e3.printStackTrace();
                    }

                    String step = json.toString();

                    tvTest.setText(step);

                    recipe.add("steps",step);
                    recipe.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            // just do nothing
                        }
                    });*/
                }
                else{
                    Log.d("MainActivity","Failed to get all the recipes.");
                    e.printStackTrace();
                }
            }
        });

        FragmentTransaction fragmentTransactionFeed = fragmentManager.beginTransaction();
        fragmentTransactionFeed.replace(R.id.flFragmentContainer, feedFragment).commit();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.miFeed:
                                FragmentTransaction fragmentTransactionFeed = fragmentManager.beginTransaction();
                                fragmentTransactionFeed.replace(R.id.flFragmentContainer, feedFragment).commit();
                                return true;
<<<<<<< Updated upstream
                            case R.id.miCreate:
                                BasicInfoFragment createfragment1 = new BasicInfoFragment();
                                fragmentTransactionFeed = fragmentManager.beginTransaction();
                                fragmentTransactionFeed.replace(R.id.flFragmentContainer, createfragment1).commit();
                                return true;
                            //TODO-create profile fragment

=======
>>>>>>> Stashed changes
                            case R.id.miProfile:
                                FragmentTransaction fragmentTransactionProfile = fragmentManager.beginTransaction();
                                fragmentTransactionProfile.replace(R.id.flFragmentContainer, profileFragment).commit();

                            default:
                                return true;
                        }
                    }
                });

    }


    @Override
    public void savedRecipesClicked() {
        FragmentTransaction fragmentTransactionSavedRecipes = fragmentManager.beginTransaction();
    }

    @Override
    public void createdRecipesClicked() {

    }
}
