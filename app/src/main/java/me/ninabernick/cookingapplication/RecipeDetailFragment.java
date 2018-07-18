package me.ninabernick.cookingapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import me.ninabernick.cookingapplication.models.Recipe;

public class RecipeDetailFragment extends Fragment {

    FragmentActivity listener;

    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvDescription;
    private ImageView ivImage;
    private Recipe recipe;
    private Button btStartRecipe;

    private ListView lvIngredientList;
    private ArrayList<String> steps;
    private ArrayList<String> ingredients;
    private ArrayAdapter<String> ingredientAdapter;
    private ArrayAdapter<String> stepAdapter;

    public static final String RECIPE_KEY = "recipe";


    public static RecipeDetailFragment newInstance(Recipe recipe) {
        RecipeDetailFragment detailFragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(RECIPE_KEY, recipe);

        detailFragment.setArguments(args);
        return detailFragment;
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
        //Recipe thisRecipe = getArguments().getParcelable(RECIPE_KEY);
        steps = new ArrayList<>();
        ingredients = new ArrayList<>();
        recipe = getArguments().getParcelable(RECIPE_KEY);
        //TODO- populate list of steps with data from recipe object
        //see guide on creating/using fragments for dynamically loading fragment in home activity
        //incorrect implementation of steps
        //steps.addAll(recipe.getSteps());
        ingredients.addAll(recipe.getIngredients());




    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_detail, parent, false);

    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        ivImage = (ImageView) view.findViewById(R.id.ivRecipeImage);
        btStartRecipe = (Button) view.findViewById(R.id.btStartRecipe);
        btStartRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), RecipeDetailsActivity.class);
                i.putExtra("recipe", recipe);
                startActivity(i);
            }
        });

        tvTitle.setText(recipe.getTitle());
        tvTime.setText(recipe.getTime());
        tvDescription.setText(recipe.getDescription());


        lvIngredientList = (ListView) view.findViewById(R.id.lvIngredients);
        if (lvIngredientList == null) {
            Log.d("ListView", "ListView null");
        }

        //stepAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, steps);
        ingredientAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, ingredients);
        lvIngredientList.setAdapter(ingredientAdapter);








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

