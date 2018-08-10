package me.ninabernick.cookingapplication;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.bennyhuo.swipefinishable.SwipeFinishable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
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

    private LinearLayout llIngredientList;
    private ArrayList<String> steps;
    private ArrayList<String> ingredients;
    private List<String> ingredientsList;
    private ArrayAdapter<String> ingredientAdapter;
    private ArrayAdapter<String> stepAdapter;
    private ArrayList<Comment> comments;
    private LinearLayout.LayoutParams layoutParams;
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
                Intent intent = new Intent(RecipeDetailViewActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        View view = findViewById(android.R.id.content);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10,10,10,10);


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

        // save button animation
        Drawable[] layers = new Drawable[2];
        layers[0] = getResources().getDrawable(R.drawable.heart_outline, getTheme());
        layers[1] = getResources().getDrawable(R.drawable.heart, getTheme());
        final TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
        ivSave.setImageDrawable(transitionDrawable);

        if (!hasSaved(ParseUser.getCurrentUser(), recipe)) {
            transitionDrawable.resetTransition();
        }
        else {
            transitionDrawable.startTransition(300);
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

                            transitionDrawable.startTransition(300);
                            Toast.makeText(RecipeDetailViewActivity.this, "Saved to recipes!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    transitionDrawable.reverseTransition(300);;
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




        llIngredientList = (LinearLayout) findViewById(R.id.llIngredients);

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
            TextView ingredient = new TextView(this);
            ingredient.setText(text);
            ingredient.setLayoutParams(layoutParams);
            llIngredientList.addView(ingredient);


        }




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
                        Comment comment = objects.get(i);
                        comments.add(comment);
                        View newComment = LayoutInflater.from(RecipeDetailViewActivity.this).inflate(R.layout.comment, null);
                        ImageView ivProfile = (ImageView) newComment.findViewById(R.id.ivProfile);
                        TextView tvName = (TextView) newComment.findViewById(R.id.tvName);
                        TextView tvComment = (TextView) newComment.findViewById(R.id.tvCommentText);
                        String profileImageUrl = "https://graph.facebook.com/" + comment.getUser().getString("fbId") + "/picture?type=large";
                        Glide.with(RecipeDetailViewActivity.this)
                                .load(profileImageUrl)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(ivProfile);
                        String name = comment.getUser().getString("name");
                        //name = name.substring(0, name.indexOf(" "));
                        tvName.setText(name);
                        tvComment.setText(comment.getText());
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
        List<String> sRecipes = user.getList("savedRecipes");
        Log.d("this recipe", recipe.getObjectId());
        if (sRecipes != null) {
            savedRecipes.addAll(user.<String>getList("savedRecipes"));
            for (int i = 0; i < savedRecipes.size(); i++) {
                Log.d("saved Recipes", savedRecipes.get(i));
            }
        }

        if (savedRecipes.contains(recipe.getObjectId())) {
            Log.d("has saved", "true");
            return true;
        }
        else {
            Log.d("has saved", "false");
            return false;
        }
    }

}

