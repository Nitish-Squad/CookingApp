package me.ninabernick.cookingapplication.feed;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import java.security.cert.CertificateNotYetValidException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.ninabernick.cookingapplication.HomeActivity;
import me.ninabernick.cookingapplication.LoadingRecipeFragment;
import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.ShareRecipe.NotifyShareDialog;
import me.ninabernick.cookingapplication.models.Recipe;
import me.ninabernick.cookingapplication.models.SharedRecipe;


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


    ProgressBar loadingView;
    LinearLayout llTitle;
    LinearLayout llSelectedFilters;

    Spinner spSort;
    public static final String DATE = "Date Created (Recent to Old)";
    public static final String TIME = "Time (Low to High)";
    public static final String RATING = "Rating (High to Low)";
    public static String SORT_METHOD;
    public static ArrayList<String> filters = new ArrayList<>();
    public static ArrayList<String> ingredientFilters = new ArrayList<>();
    public ArrayList<String> sortMethods;

    private static final String FEED_TYPE = "feed type";
    private static final int DIALOG_REQUEST_CODE = 20;

    public List<SharedRecipe> shared_recipes = new ArrayList<>();


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
        loadingView.setVisibility(View.VISIBLE);

        final LoadingRecipeFragment loadingRecipeFragment = new LoadingRecipeFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        loadingRecipeFragment.setTargetFragment(FeedFragment.this, DIALOG_REQUEST_CODE);
        //loadingRecipeFragment.show(ft, "dialog");
        final Recipe.Query recipeQuery = new Recipe.Query();
        // make sure no duplicates
        recipes.clear();
        adapter.clear();

        //check if the user has applied filters to their search
        if (!filters.isEmpty()) {
            recipeQuery.whereContainsAll("tags", filters);
        }
        if (!ingredientFilters.isEmpty()) {
            recipeQuery.whereContainsAll("textIngredients", ingredientFilters);
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
                //loadingRecipeFragment.dismiss();
                loadingView.setVisibility(View.INVISIBLE);


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

    public void getRecipesShared() {

        ParseQuery<SharedRecipe> query = ParseQuery.getQuery(SharedRecipe.class);
        query.include("sharedUser");
        query.include("sharingUser");
        query.include("recipeShared");
        query.whereEqualTo("sharedUser", user);
        query.findInBackground(new FindCallback<SharedRecipe>() {
            @Override
            public void done(List<SharedRecipe> objects, ParseException e) {
                if ((objects.size() != 0) && (e == null)) {
                    // gets rid of previous shared recipes
                    shared_recipes.clear();
                    for (int t = 0; t < objects.size(); t++) {
                        shared_recipes.add(objects.get(t));

                        /*
                         * For testing purposes, the following line is commented out. In actual app
                         * these shared recipes should be deleted after the user is notified that
                         * the recipe has been shared. So, the shared recipes are deleted but since
                         * the shared recipes were retrieved and their data was saved they should
                         * still display in the screen and not be lost. They are only lost if the
                         * dialogue is exited from. Once exited those "shared" recipes are lost
                         * and cannot be retrieved.
                         */
                        
                        objects.get(t).deleteInBackground();
                    }

                    NotifyShareDialog notifyRecipeDialog = NotifyShareDialog.newInstance(shared_recipes);
                    notifyRecipeDialog.show(getFragmentManager(), "fragment_share_notify");

                }

            }
        });
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
        sortMethods = new ArrayList<>();
        sortMethods.addAll(Arrays.asList(getResources().getStringArray(R.array.sorting_methods)));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, parent, false);
    }

    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingView = (ProgressBar) view.findViewById(R.id.pbLoading);
        llTitle = (LinearLayout) view.findViewById(R.id.llTitle);
        llSelectedFilters = (LinearLayout) view.findViewById(R.id.llSelectedTags);
        tvFilterByIngredient = (TextView) view.findViewById(R.id.tvFilterByIngredient);
        tvFilterByIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterIngredientFragment filterIngredients = new FilterIngredientFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                
                filterIngredients.setTargetFragment(FeedFragment.this, DIALOG_REQUEST_CODE);
                filterIngredients.show(ft, "dialog");
            }
        });
        tvFilterRecipes = (TextView) view.findViewById(R.id.tvFilterByTag);


        getRecipesShared();

        tvFilterRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                FilterFragment filter = new FilterFragment();
                filter.setTargetFragment(FeedFragment.this, DIALOG_REQUEST_CODE);
                filter.show(ft, "dialog");
            }
        });

        rvFeed = (RecyclerView) view.findViewById(R.id.rvFeed);
        rvFeed.setAdapter(adapter);
        adapter.clear();
        rvFeed.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));


        // set spinner with dummy last item to display
        final int listsize = sortMethods.size()-1;
        spSort = (Spinner) view.findViewById(R.id.spSort);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, sortMethods) {
            @Override
            public int getCount() {
                return(listsize); // Truncate the list
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSort.setAdapter(dataAdapter);
        spSort.setSelection(listsize);
        spSort.setGravity(View.TEXT_ALIGNMENT_VIEW_END);
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
                        TextView title = new TextView(getContext());
                        title.setText("Created Recipes");
                        title.setTextAppearance(R.style.default_font_large);
                        title.setGravity(Gravity.CENTER);
                        title.setPadding(10,10,10,10);
                        llTitle.addView(title);
                        getRecipesCreated();
                        break;
                    case HomeActivity.SAVED_RECIPES:
                        TextView title2 = new TextView(getContext());
                        title2.setText("Saved Recipes");
                        title2.setTextAppearance(R.style.default_font_large);
                        title2.setGravity(Gravity.CENTER);
                        title2.setPadding(10,10,10,10);
                        llTitle.addView(title2);
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

    //dynamically add selected tags to linear layout
    public void updateSelectedTags() {
        llSelectedFilters.removeAllViews();
        for (int i = 0; i < filters.size(); i++) {
            final CheckBox cb = new CheckBox(getContext());
            final String tag = filters.get(i);
            cb.setChecked(true);
            cb.setText(tag);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filters.remove(tag);
                    llSelectedFilters.removeView(cb);
                    getRecipes();
                }
            });
            llSelectedFilters.addView(cb);
        }
        Log.d("ingredient filters", ingredientFilters.toString());
        for (int i = 0; i < ingredientFilters.size(); i++) {
            final CheckBox cb = new CheckBox(getContext());
            final String ingredient = ingredientFilters.get(i);
            cb.setChecked(true);
            cb.setText(ingredient);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ingredientFilters.remove(ingredient);
                    llSelectedFilters.removeView(cb);
                    getRecipes();
                }
            });
            llSelectedFilters.addView(cb);
        }
        //getRecipes();
    }

}

