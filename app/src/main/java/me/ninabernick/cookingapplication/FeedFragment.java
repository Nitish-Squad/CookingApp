package me.ninabernick.cookingapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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


public class FeedFragment extends Fragment {
    FragmentActivity listener;
    ArrayList<Recipe> recipes;
    RecipeAdapter adapter;
    RecyclerView rvFeed;
    TextView tvFilterRecipes;
    ParseUser user;
    FilterFragment filter = new FilterFragment();
    public static ArrayList<String> filters = new ArrayList<>();
    private static final String FEED_TYPE = "feed type";
    private static final int DIALOG_REQUEST_CODE = 20;



    RecipeAdapter.RecipeListener recipeListener = new RecipeAdapter.RecipeListener() {
        @Override
        public void respond(Recipe recipe) {
            RecipeDetailFragment detailFragment = RecipeDetailFragment.newInstance(recipe);
            final FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.flFragmentContainer, detailFragment).commit();
        }
    };



    // key determines whether this is a recipe feed or a list of
    public static FeedFragment newInstance(String key, ParseUser user) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(FEED_TYPE, key);
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    public void getRecipes(){
        final Recipe.Query recipeQuery = new Recipe.Query();
        // make sure no duplicates
        recipes.clear();

        // place to order the recipes in some manner (ex: rating, when created, etc)
        //postsQuery.orderByDescending("createdAt");
        //check if the user has applied filters to their search
        if (!filters.isEmpty()) {
            recipeQuery.whereContainedIn("tags", filters);
        }

        recipeQuery.getTop();
        recipeQuery.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e == null)
                {
                    Log.d("Number of recipes", Integer.toString(objects.size()));
                    for (int i = 0; i < objects.size(); i ++){
                        Recipe recipe = objects.get(i);
                        recipes.add(recipe);
                        adapter.notifyDataSetChanged();

                        // code to update the adapter for the recycler view
                        // recipeAdapter.notifyItemInserted(recipes.size() - 1);
                    }

                }
                else{
                    Log.d("MainActivity","Failed to get all the recipes.");
                    e.printStackTrace();
                }


            }
        });


    }


    public void getSavedRecipes() {

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

    public void getRecipesCreated() {


        //ArrayList<String> createdRecipes = new ArrayList<>();
        //savedRecipes.addAll(user.<String>getList("savedRecipes"));

        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.whereEqualTo("createdBy", user.getObjectId());
        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                recipes.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });

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
        user = getArguments().getParcelable("user");


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
        tvFilterRecipes = (TextView) view.findViewById(R.id.tvFilterResults);
        tvFilterRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                filter.setTargetFragment(FeedFragment.this, DIALOG_REQUEST_CODE);
                filter.show(ft, "dialog");
            }
        });
        rvFeed = (RecyclerView) view.findViewById(R.id.rvFeed);
        rvFeed.setAdapter(adapter);
        rvFeed.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        String feedType = getArguments().getString(FEED_TYPE);
        switch (feedType) {
            case HomeActivity.RECIPE_FEED:
                getRecipes();
                break;
            case HomeActivity.CREATED_RECIPES:
                getRecipesCreated();
                break;
            case HomeActivity.SAVED_RECIPES:
                getSavedRecipes();
                break;
            default:
                break;
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