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
import android.view.View;

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
    BasicInfoFragment createfragment1 = new BasicInfoFragment();
    Fragment savedRecipeFragment = FeedFragment.newInstance(SAVED_RECIPES, ParseUser.getCurrentUser());
    Fragment createdRecipeFragment = FeedFragment.newInstance(CREATED_RECIPES, ParseUser.getCurrentUser());
    public static BottomNavigationView bottomNavigationView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // getting an error here, cant recognize Fade
        //getWindow().setEnterTransition(new Fade());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomNavigationView.getSelectedItemId() != R.id.miFeed) {
                    bottomNavigationView.setSelectedItemId(R.id.miFeed);
                }
            }
        });

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
                                switchToFragment(feedFragment);
                                return true;

                            case R.id.miCreate:
                                /*
                                 * Pretty interesting bug fixed here, the HomeActivity is never destroyed
                                 * during the create flow, so essentially the initialized createfragment
                                 * is always retained so after the user makes a new recipe and navigates
                                 * back to the create flow the old instance is retained and causes super
                                 * strange interactions.
                                 */
                                createfragment1 = new BasicInfoFragment();
                                switchToFragment(createfragment1);
                                return true;

                            case R.id.miProfile:
                                switchToFragment(profileFragment);
                                return true;
                            case R.id.miSearch:
                                switchToFragment(searchFragment);
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

    public void switchToFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragmentContainer, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction.commit();
    }




}
