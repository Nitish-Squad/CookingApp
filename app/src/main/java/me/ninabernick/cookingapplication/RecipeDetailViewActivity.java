package me.ninabernick.cookingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bennyhuo.swipefinishable.SwipeFinishable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.ninabernick.cookingapplication.Location.MapsFragment;
import me.ninabernick.cookingapplication.ShareRecipe.ShareRecipeDialog;
import me.ninabernick.cookingapplication.models.Comment;
import me.ninabernick.cookingapplication.models.Recipe;


public class RecipeDetailViewActivity extends AppCompatActivity {

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
    private ImageView ivShare;

    private ListView lvIngredientList;
    private ArrayList<String> steps;
    private ArrayList<String> ingredients;
    private List<String> ingredientsList;
    private ArrayAdapter<String> ingredientAdapter;
    private ArrayAdapter<String> stepAdapter;
    private ArrayList<Comment> comments;
    BottomNavigationView bottomNavigationView;

    public static final String RECIPE_KEY = "recipe";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecipeDetailViewActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });
        View view = findViewById(android.R.id.content);


        steps = new ArrayList<>();
        recipe = getIntent().getParcelableExtra("recipe");

        comments = new ArrayList<>();

        ingredientsList = recipe.getIngredients();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //HomeActivity.bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.menu_bottom_navigation_recipe_details);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.miMaps:
                        Intent intent = new Intent(RecipeDetailViewActivity.this, MapsFragment.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_up, 0);
                        return true;
                    case R.id.miStartRecipe:
                        Intent i = new Intent(RecipeDetailViewActivity.this, RecipeDetailsActivity.class);
                        i.putExtra("recipe", recipe);
                        SwipeFinishable.INSTANCE.startActivity(i);

                        return true;
                    default:
                        return true;
                }
            }
        });


        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        ivImage = (ImageView) findViewById(R.id.ivRecipeImage);
        llComments = (LinearLayout) findViewById(R.id.llComments);
        rbDisplayRating = (RatingBar) findViewById(R.id.rbDisplayRating);
        rbDisplayRating.setRating(recipe.getAverageRating().floatValue());
        ivShare = (ImageView) findViewById(R.id.fb_share_button);


        Glide.with(RecipeDetailViewActivity.this)
                .load(recipe.getrecipeImage().getUrl())
                .apply(new RequestOptions().transforms(new CropSquareTransformation()))
                .into(ivImage);
        ivSave = (ImageView) findViewById(R.id.ivSaveRecipe);
        if (!hasSaved(ParseUser.getCurrentUser(), recipe)) {
            ivSave.setImageResource(R.drawable.ic_vector_heart_stroke);
        }
        else {
            ivSave.setColorFilter(R.drawable.ic_vector_heart);
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
                            Toast.makeText(RecipeDetailViewActivity.this, "Saved to recipes!", Toast.LENGTH_LONG);
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

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareRecipeDialog shareRecipeDialog = new ShareRecipeDialog();
                shareRecipeDialog.show(getSupportFragmentManager(), "fragment_share_recipe");

            }
        });



        tvTitle.setText(recipe.getTitle());
        tvTime.setText(recipe.getTime());
        tvDescription.setText(recipe.getDescription());




        lvIngredientList = (ListView) findViewById(R.id.lvIngredients);
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


        ingredientAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, parsedIngredients);
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
                        TextView newComment = new TextView(RecipeDetailViewActivity.this);
                        newComment.setText(String.format("%s: %s", objects.get(i).getUser().getString("name"), objects.get(i).getText()));
                        llComments.addView(newComment);
                    }

                }
            }
        });

    }

    public Recipe getRecipeShown(){
        return recipe;
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

}

