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
import com.parse.ParseUser;

import java.util.List;

import me.ninabernick.cookingapplication.feed.FeedFragment;
import me.ninabernick.cookingapplication.models.Recipe;
import me.ninabernick.cookingapplication.profile.ProfileFragment;

public class HomeActivity extends AppCompatActivity implements ProfileFragment.ProfileListener{

    public static final String SAVED_RECIPES = "savedRecipes";
    public static final String CREATED_RECIPES = "createdRecipes";
    public static final String RECIPE_FEED = "allRecipes";

    // recipe to be added
    public Recipe recipe_to_add;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment feedFragment = FeedFragment.newInstance(RECIPE_FEED, ParseUser.getCurrentUser());
    Fragment RecipeDetailFragment;
    Fragment profileFragment = ProfileFragment.newInstance(ParseUser.getCurrentUser());
    Fragment savedRecipeFragment = FeedFragment.newInstance(SAVED_RECIPES, ParseUser.getCurrentUser());
    Fragment createdRecipeFragment = FeedFragment.newInstance(CREATED_RECIPES, ParseUser.getCurrentUser());

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recipe_to_add = new Recipe();



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

                            case R.id.miCreate:
                                BasicInfoFragment createfragment1 = new BasicInfoFragment();
                                fragmentTransactionFeed = fragmentManager.beginTransaction();
                                fragmentTransactionFeed.replace(R.id.flFragmentContainer, createfragment1).commit();
                                return true;
                            //TODO-create profile fragment

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
    public void savedRecipesClicked(ParseUser user) {
        Fragment savedFragment = FeedFragment.newInstance(SAVED_RECIPES, user);
        FragmentTransaction fragmentTransactionSavedRecipes = fragmentManager.beginTransaction();
        fragmentTransactionSavedRecipes.replace(R.id.flFragmentContainer, savedFragment).commit();
    }

    @Override
    public void createdRecipesClicked(ParseUser user) {
        Fragment createdFragment = FeedFragment.newInstance(CREATED_RECIPES, user);
        FragmentTransaction fragmentTransactionSavedRecipes = fragmentManager.beginTransaction();
        fragmentTransactionSavedRecipes.replace(R.id.flFragmentContainer, createdFragment).commit();
    }




}
