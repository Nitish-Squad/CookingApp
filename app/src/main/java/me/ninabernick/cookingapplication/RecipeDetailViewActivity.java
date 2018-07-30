package me.ninabernick.cookingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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


//    public static RecipeDetailFragment newInstance(Recipe recipe) {
//        RecipeDetailFragment detailFragment = new RecipeDetailFragment();
//        Bundle args = new Bundle();
//        args.putParcelable(RECIPE_KEY, recipe);
//
//        detailFragment.setArguments(args);
//        return detailFragment;
//    }

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof Activity){
//            this.listener = (FragmentActivity) context;
//        }
//    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_view);
        //Recipe thisRecipe = getArguments().getParcelable(RECIPE_KEY);
        steps = new ArrayList<>();
        //ingredients = new ArrayList<>();
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
                        return true;
                    case R.id.miStartRecipe:
                        Intent i = new Intent(RecipeDetailViewActivity.this, RecipeDetailsActivity.class);
                        i.putExtra("recipe", recipe);
                        startActivity(i);
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


        Glide.with(RecipeDetailViewActivity.this).load(recipe.getrecipeImage().getUrl()).into(ivImage);
        ivSave = (ImageView) findViewById(R.id.ivSaveRecipe);
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


        //ingredients.addAll(recipe.getIngredients());

    }


    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_recipe_detail, parent, false);
//
//    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        HomeActivity.bottomNavigationView.getMenu().clear();
//        HomeActivity.bottomNavigationView.inflateMenu(R.menu.menu_bottom_navigation_recipe_details);
//        HomeActivity.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.miMaps:
//                        Intent intent = new Intent(getContext(), MapsFragment.class);
//                        startActivity(intent);
//                        return true;
//                    case R.id.miStartRecipe:
//                        Intent i = new Intent(getContext(), RecipeDetailsActivity.class);
//                        i.putExtra("recipe", recipe);
//                        startActivity(i);
//                        return true;
//                    default:
//                        return true;
//                }
//            }
//        });
//
//
//        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
//        tvTime = (TextView) view.findViewById(R.id.tvTime);
//        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
//        ivImage = (ImageView) view.findViewById(R.id.ivRecipeImage);
//        llComments = (LinearLayout) view.findViewById(R.id.llComments);
//        rbDisplayRating = (RatingBar) view.findViewById(R.id.rbDisplayRating);
//        rbDisplayRating.setRating(recipe.getAverageRating().floatValue());
//        ivShare = (ImageView) view.findViewById(R.id.fb_share_button);
//
//
//        Glide.with(view.getContext()).load(recipe.getrecipeImage().getUrl()).into(ivImage);
//        ivSave = (ImageView) view.findViewById(R.id.ivSaveRecipe);
//        if (!hasSaved(ParseUser.getCurrentUser(), recipe)) {
//            ivSave.setImageResource(R.drawable.ic_vector_heart_stroke);
//        }
//        else {
//            ivSave.setImageResource(R.drawable.ic_vector_heart);
//        }
//        ivSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ParseUser user = ParseUser.getCurrentUser();
//                // check if recipe already saved
//                if (!hasSaved(user, recipe)) {
//                    // add this recipe to user's list of saved recipes
//                    user.add("savedRecipes", recipe.getObjectId());
//                    user.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            ivSave.setImageResource(R.drawable.ic_vector_heart);
//                            Toast.makeText(getContext(), "Saved to recipes!", Toast.LENGTH_LONG);
//                        }
//                    });
//                }
//                else {
//                    ivSave.setImageResource(R.drawable.ic_vector_heart_stroke);
//                    ArrayList<String> savedRecipes = new ArrayList<>();
//                    savedRecipes.addAll(user.<String>getList("savedRecipes"));
//                    savedRecipes.remove(recipe.getObjectId());
//                    user.remove("savedRecipes");
//                    user.put("savedRecipes", savedRecipes);
//                }
//                Log.d("saved recipes", user.get("savedRecipes").toString());
//
//
//            }
//        });
//
//        ivShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ParseFile parseFile = recipe.getParseFile("recipeImage");
//                byte[] data = new byte[0];
//                try {
//                    data = parseFile.getData();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
//                SharePhoto photo = new SharePhoto.Builder()
//                        .setBitmap(image)
//                        .build();
//                SharePhotoContent content = new SharePhotoContent.Builder()
//                        .addPhoto(photo)
//                        .build();
//
//                ShareDialog.show(getActivity(), content);
//
//            }
//        });
//
//        tvTitle.setText(recipe.getTitle());
//        tvTime.setText(recipe.getTime());
//        tvDescription.setText(recipe.getDescription());
//
//
//
//
//        lvIngredientList = (ListView) view.findViewById(R.id.lvIngredients);
//        if (lvIngredientList == null) {
//            Log.d("ListView", "ListView null");
//        }
//
//        // loop through ingredient list and parse JSON objects
//        ArrayList<String> parsedIngredients = new ArrayList<>();
//        for (int i = 0; i < ingredientsList.size(); i++) {
//            String text = "";
//            try {
//                JSONObject jsonIngredient = new JSONObject(ingredientsList.get(i));
//                text += jsonIngredient.getString("quantity");
//                text += " ";
//                text += jsonIngredient.getString("unit");
//                text += " ";
//                text += jsonIngredient.getString("name");
//            } catch (JSONException e) {
//                e.printStackTrace();
//                Log.d("ingredient", "json object not parsed");
//            }
//            parsedIngredients.add(text);
//        }
//
//
//        ingredientAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, parsedIngredients);
//        lvIngredientList.setAdapter(ingredientAdapter);
//
//        getTopComments(recipe, 3);
//
//    }
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if(getView() == null){
//            return;
//        }
//
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
//                    HomeActivity.bottomNavigationView.getMenu().clear();
//                    HomeActivity.bottomNavigationView.setOnNavigationItemSelectedListener(
//                            new BottomNavigationView.OnNavigationItemSelectedListener() {
//                                @Override
//                                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                                    FragmentManager fragmentManager = getFragmentManager();
//                                    Fragment feedFragment = FeedFragment.newInstance(HomeActivity.RECIPE_FEED, ParseUser.getCurrentUser());
//                                    Fragment profileFragment = ProfileFragment.newInstance(ParseUser.getCurrentUser());
//                                    switch (item.getItemId()) {
//                                        case R.id.miFeed:
//                                            FragmentTransaction fragmentTransactionFeed = fragmentManager.beginTransaction();
//                                            fragmentTransactionFeed.replace(R.id.flFragmentContainer, feedFragment);
//                                            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                                            fragmentTransactionFeed.commit();
//                                            return true;
//
//                                        case R.id.miCreate:
//                                            BasicInfoFragment createfragment1 = new BasicInfoFragment();
//                                            fragmentTransactionFeed = fragmentManager.beginTransaction();
//                                            fragmentTransactionFeed.replace(R.id.flFragmentContainer, createfragment1);
//                                            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                                            fragmentTransactionFeed.commit();
//                                            return true;
//
//                                        case R.id.miProfile:
//                                            FragmentTransaction fragmentTransactionProfile = fragmentManager.beginTransaction();
//                                            fragmentTransactionProfile.replace(R.id.flFragmentContainer, profileFragment);
//                                            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                                            fragmentTransactionProfile.commit();
//
//                                        default:
//                                            return true;
//                                    }
//                                }
//                            });
//                    // handle back button's click listener
//                    return true;
//                }
//                return false;
//            }
//        });
//    }

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

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        this.listener = null;
//    }



    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }




}

