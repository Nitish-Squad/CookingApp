package me.ninabernick.cookingapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.parse.ParseUser;

import me.ninabernick.cookingapplication.CreateRecipe.BasicInfoFragment;
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
    Fragment searchFragment = new SearchFragment();
    Fragment savedRecipeFragment = FeedFragment.newInstance(SAVED_RECIPES, ParseUser.getCurrentUser());
    Fragment createdRecipeFragment = FeedFragment.newInstance(CREATED_RECIPES, ParseUser.getCurrentUser());
    public static BottomNavigationView bottomNavigationView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        recipe_to_add = new Recipe();


        FragmentTransaction fragmentTransactionFeed = fragmentManager.beginTransaction();
        fragmentTransactionFeed.replace(R.id.flFragmentContainer, feedFragment);
        fragmentTransactionFeed.commit();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.menu_bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.miFeed:
                                //supportFinishAfterTransition();
                                FragmentTransaction fragmentTransactionFeed = fragmentManager.beginTransaction();
                                fragmentTransactionFeed.replace(R.id.flFragmentContainer, feedFragment);
                                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                fragmentTransactionFeed.commit();
                                return true;

                            case R.id.miCreate:
                                BasicInfoFragment createfragment1 = new BasicInfoFragment();
                                fragmentTransactionFeed = fragmentManager.beginTransaction();
                                fragmentTransactionFeed.replace(R.id.flFragmentContainer, createfragment1);
                                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                fragmentTransactionFeed.commit();
                                return true;

                            case R.id.miProfile:
                                FragmentTransaction fragmentTransactionProfile = fragmentManager.beginTransaction();
                                fragmentTransactionProfile.replace(R.id.flFragmentContainer, profileFragment);
                                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                fragmentTransactionProfile.commit();
                                return true;
                            case R.id.miSearch:
                                FragmentTransaction fragmentTransactionSearch = fragmentManager.beginTransaction();
                                fragmentTransactionSearch.replace(R.id.flFragmentContainer, searchFragment);
                                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                fragmentTransactionSearch.commit();
                                return true;

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
        fragmentTransactionSavedRecipes.replace(R.id.flFragmentContainer, savedFragment);
        fragmentTransactionSavedRecipes.addToBackStack(null);
        fragmentTransactionSavedRecipes.commit();
    }

    @Override
    public void createdRecipesClicked(ParseUser user) {
        Fragment createdFragment = FeedFragment.newInstance(CREATED_RECIPES, user);
        FragmentTransaction fragmentTransactionSavedRecipes = fragmentManager.beginTransaction();
        fragmentTransactionSavedRecipes.replace(R.id.flFragmentContainer, createdFragment);
        fragmentTransactionSavedRecipes.addToBackStack(null);
        fragmentTransactionSavedRecipes.commit();
    }




}
