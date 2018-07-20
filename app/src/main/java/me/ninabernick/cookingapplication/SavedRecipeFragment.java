package me.ninabernick.cookingapplication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.ninabernick.cookingapplication.models.Recipe;


public class SavedRecipeFragment extends Fragment {
    FragmentActivity listener;
    private ArrayList<Recipe> recipes;
    private RecipeAdapter adapter;
    private RecyclerView rvFeed;


    RecipeAdapter.RecipeListener recipeListener = new RecipeAdapter.RecipeListener() {
        @Override
        public void respond(Recipe recipe) {
            RecipeDetailFragment detailFragment = RecipeDetailFragment.newInstance(recipe);
            final FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.flFragmentContainer, detailFragment).commit();
        }
    };
    //determines whether it's saved recipes or created recipes
    public static SavedRecipeFragment newInstance(ArrayList<Recipe> recipes) {
        SavedRecipeFragment fragment = new SavedRecipeFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("recipes", recipes);
        fragment.setArguments(args);
        return fragment;
    }
    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipes = new ArrayList<>();


        adapter = new RecipeAdapter(recipes, recipeListener);


    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFeed = (RecyclerView) view.findViewById(R.id.rvFeed);
        rvFeed.setAdapter(adapter);
        rvFeed.setLayoutManager(new LinearLayoutManager(view.getContext()));
        getRecipes();






    }

    public void getRecipes() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user.getList("savedRecipes") != null) {
            ArrayList<String> savedRecipes = new ArrayList<>();
            savedRecipes.addAll(user.<String>getList("savedRecipes"));

            ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
            query.whereContainedIn("objectId", savedRecipes);
            query.findInBackground(new FindCallback<Recipe>() {
                @Override
                public void done(List<Recipe> objects, ParseException e) {
                    recipes.addAll(objects);
                    adapter.notifyDataSetChanged();
                }
            });




        }
    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }



    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
