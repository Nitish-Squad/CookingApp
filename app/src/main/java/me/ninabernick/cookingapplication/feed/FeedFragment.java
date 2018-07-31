package me.ninabernick.cookingapplication.feed;

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
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.ninabernick.cookingapplication.HomeActivity;
import me.ninabernick.cookingapplication.LoadingRecipeFragment;
import me.ninabernick.cookingapplication.R;

import me.ninabernick.cookingapplication.models.Recipe;


public class FeedFragment extends Fragment {
    FragmentActivity listener;
    // note--adapter makes a copy of recipes, does not change it
    ArrayList<Recipe> recipes;
    // make a new list of recipes so you don't have to query again when you get rid of the search
    ArrayList<Recipe> filteredRecipes;
    RecipeAdapter adapter;
    RecyclerView rvFeed;
    TextView tvFilterRecipes;
    TextView tvFilterByIngredient;
    ParseUser user;
    FilterFragment filter = new FilterFragment();
    FilterIngredientFragment filterIngredients = new FilterIngredientFragment();

    Spinner spSort;
    public static final String DATE = "Date Created (Recent to Old)";
    public static final String TIME = "Time (Low to High)";
    public static final String RATING = "Rating (High to Low)";
    public static String SORT_METHOD;
    public static ArrayList<String> filters = new ArrayList<>();
    public static ArrayList<String> ingredientFilters = new ArrayList<>();

    private static final String FEED_TYPE = "feed type";
    private static final int DIALOG_REQUEST_CODE = 20;


    // key determines whether this is a recipe feed or a list of saved recipes, created recipes
    public static FeedFragment newInstance(String key, ParseUser user) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(FEED_TYPE, key);
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }



    public void getRecipes(){

        final LoadingRecipeFragment loadingRecipeFragment = new LoadingRecipeFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        loadingRecipeFragment.setTargetFragment(FeedFragment.this, DIALOG_REQUEST_CODE);
        loadingRecipeFragment.show(ft, "dialog");
        final Recipe.Query recipeQuery = new Recipe.Query();
        // make sure no duplicates
        recipes.clear();
        adapter.clear();

        //check if the user has applied filters to their search
        if (!filters.isEmpty()) {
            recipeQuery.whereContainedIn("tags", filters);
        }
        if (!ingredientFilters.isEmpty()) {
            recipeQuery.whereContainedIn("textIngredients", ingredientFilters);
        }

         //order by sorting method
        switch(SORT_METHOD) {
            case DATE:
                recipeQuery.orderByDescending("createdAt");
                break;
            case TIME:
                recipeQuery.orderByAscending("recipe_time_standard");
                break;
            case RATING:
                recipeQuery.orderByDescending("averageRating");
                break;
            default:
                recipeQuery.orderByDescending("createdAt");
                break;
        }

        recipeQuery.getTop();
        recipeQuery.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e == null)
                {

                    for (int i = 0; i < objects.size(); i ++){
                        Recipe recipe = objects.get(i);
                        recipes.add(recipe);
                    }

                    
                    adapter.clear();
                    adapter.addAll(recipes);
                    adapter.notifyDataSetChanged();

                }
                else{

                    e.printStackTrace();
                }
                loadingRecipeFragment.dismiss();


            }
        });


    }



    // always change adapter data set, not local "recipes" list
    public void getSavedRecipes() {

        if (user.getList("savedRecipes") != null) {
            ArrayList<String> savedRecipes = new ArrayList<>();
            savedRecipes.addAll(user.<String>getList("savedRecipes"));

            ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
            if (!filters.isEmpty()) {
                query.whereContainedIn("tags", filters);
            }
            if (!ingredientFilters.isEmpty()) {
                query.whereContainedIn("textIngredients", ingredientFilters);
            }
            query.whereContainedIn("objectId", savedRecipes);
            query.findInBackground(new FindCallback<Recipe>() {
                @Override
                public void done(List<Recipe> objects, ParseException e) {
                    adapter.clear();
                    adapter.addAll(objects);
                    adapter.notifyDataSetChanged();
                }
            });




        }
    }
    // always change adapter data set, not local "recipes" list
    public void getRecipesCreated() {

        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        if (!filters.isEmpty()) {
            query.whereContainedIn("tags", filters);
        }
        if (!ingredientFilters.isEmpty()) {
            query.whereContainedIn("textIngredients", ingredientFilters);
        }
        query.whereEqualTo("createdBy", user.getObjectId());
        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                Log.d("created Recipes", objects.toString());
                adapter.clear();
                adapter.addAll(objects);
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

        SORT_METHOD = getResources().getString(R.string.DATE);
        adapter = new RecipeAdapter(recipes);
        user = getArguments().getParcelable("user");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, parent, false);
    }

    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvFilterByIngredient = (TextView) view.findViewById(R.id.tvFilterByIngredient);
        tvFilterByIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                filterIngredients.setTargetFragment(FeedFragment.this, DIALOG_REQUEST_CODE);
                filterIngredients.show(ft, "dialog");
            }
        });
        tvFilterRecipes = (TextView) view.findViewById(R.id.tvFilterByTag);
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

        adapter.clear();
        rvFeed.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));

        spSort = (Spinner) view.findViewById(R.id.spSort);
        spSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedItem = adapterView.getItemAtPosition(position).toString();
                FeedFragment.SORT_METHOD = selectedItem;
                Log.d("spinner", "item selected");
                // allows adaptation for home feed, created recipes, and saved recipes
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

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}