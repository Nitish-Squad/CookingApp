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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.ninabernick.cookingapplication.Location.MapsFragment;
import me.ninabernick.cookingapplication.models.Comment;
import me.ninabernick.cookingapplication.models.Recipe;
import me.ninabernick.cookingapplication.models.User;

public class RecipeDetailFragment extends Fragment {

    FragmentActivity listener;

    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvDescription;
    private ImageView ivImage;
    private Recipe recipe;
    private Button btStartRecipe;
    private Button btStartMaps;
    private ImageView ivSave;
    private RatingBar rbDisplayRating;
    private LinearLayout llComments;

    private ListView lvIngredientList;
    private ArrayList<String> steps;
    private ArrayList<String> ingredients;
    private List<String> ingredientsList;
    private ArrayAdapter<String> ingredientAdapter;
    private ArrayAdapter<String> stepAdapter;
    private ArrayList<Comment> comments;

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
        //ingredients = new ArrayList<>();
        recipe = getArguments().getParcelable(RECIPE_KEY);
        comments = new ArrayList<>();

        ingredientsList = recipe.getIngredients();


        //ingredients.addAll(recipe.getIngredients());

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
        llComments = (LinearLayout) view.findViewById(R.id.llComments);
        rbDisplayRating = (RatingBar) view.findViewById(R.id.rbDisplayRating);
        rbDisplayRating.setRating(recipe.getAverageRating());


        btStartRecipe = (Button) view.findViewById(R.id.btStartRecipe);
        btStartRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), RecipeDetailsActivity.class);
                i.putExtra("recipe", recipe);
                startActivity(i);
            }
        });

        btStartMaps = (Button) view.findViewById(R.id.btStartMaps);
        btStartMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsFragment.class);
                startActivity(intent);
            }
        });

        Glide.with(view.getContext()).load(recipe.getrecipeImage().getUrl()).into(ivImage);
        ivSave = (ImageView) view.findViewById(R.id.ivSaveRecipe);
        if (!hasSaved(ParseUser.getCurrentUser(), recipe)) {
            ivSave.setImageResource(R.drawable.ic_vector_heart_stroke);
        }
        else {
            ivSave.setImageResource(R.drawable.ic_vector_heart);
        }
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser user = ParseUser.getCurrentUser();
                // check if recipe already saved
                if (!hasSaved(user, recipe)) {
                    // add this recipe to user's list of saved recipes
                    user.add("savedRecipes", recipe.getObjectId());
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            ivSave.setImageResource(R.drawable.ic_vector_heart);
                            Toast.makeText(getContext(), "Saved to recipes!", Toast.LENGTH_LONG);
                        }
                    });
                }
                else {
                    ivSave.setImageResource(R.drawable.ic_vector_heart_stroke);
                    ArrayList<String> savedRecipes = new ArrayList<>();
                    savedRecipes.addAll(user.<String>getList("savedRecipes"));
                    savedRecipes.remove(recipe.getObjectId());
                    user.remove("savedRecipes");
                    user.put("savedRecipes", savedRecipes);
                }
                Log.d("saved recipes", user.get("savedRecipes").toString());


            }
        });

        tvTitle.setText(recipe.getTitle());
        tvTime.setText(recipe.getTime());
        tvDescription.setText(recipe.getDescription());




        lvIngredientList = (ListView) view.findViewById(R.id.lvIngredients);
        if (lvIngredientList == null) {
            Log.d("ListView", "ListView null");
        }

        // loop through ingredient list and parse JSON objects
        ArrayList<String> parsedIngredients = new ArrayList<>();
        for (int i = 0; i < ingredientsList.size(); i++) {
            String text = "";
            try {
                JSONObject jsonIngredient = new JSONObject(ingredientsList.get(i));
                text += jsonIngredient.getString("quantity");
                text += " ";
                text += jsonIngredient.getString("unit");
                text += " ";
                text += jsonIngredient.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("ingredient", "json object not parsed");
            }
            parsedIngredients.add(text);
        }


        ingredientAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, parsedIngredients);
        lvIngredientList.setAdapter(ingredientAdapter);

        getTopComments(recipe, 3);

    }

    private void getTopComments(Recipe recipe, int numComments) {
        List<String> commentIds = recipe.getComments();
        if (commentIds == null) {
            return;
        }
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include("user");
        query.whereContainedIn("objectId", commentIds);
        query.orderByDescending("createdAt");
        query.setLimit(numComments);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {

                        comments.add(objects.get(i));
                        TextView newComment = new TextView(getContext());
                        newComment.setText(String.format("%s: %s", objects.get(i).getUser().getString("name"), objects.get(i).getText()));
                        llComments.addView(newComment);
                    }

                }
            }
        });

    }

    private boolean hasSaved(ParseUser user, Recipe recipe) {
        ArrayList<String> savedRecipes = new ArrayList<>();
        if (user.<String>getList("savedRecipes") != null) {
            savedRecipes.addAll(user.<String>getList("savedRecipes"));
        }

        if (savedRecipes.contains(recipe.getObjectId())) {
            return true;
        }
        else return false;

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

