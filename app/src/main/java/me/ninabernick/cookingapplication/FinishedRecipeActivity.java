package me.ninabernick.cookingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import me.ninabernick.cookingapplication.Location.RestaurantActivity;
import me.ninabernick.cookingapplication.models.Comment;
import me.ninabernick.cookingapplication.models.Recipe;

public class FinishedRecipeActivity extends AppCompatActivity{

    Recipe recipe;
    private RatingBar ratingBar;
    private EditText etLeaveComment;
    private Button btFinishRecipe;
    private TextView tvLowRating;
    private Button btFindRestaurants;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_recipe);

        recipe = getIntent().getParcelableExtra("recipe");

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        etLeaveComment = (EditText) findViewById(R.id.etComment);
        btFinishRecipe = (Button) findViewById(R.id.btFinish);
        tvLowRating = (TextView) findViewById(R.id.tvLowRating);
        btFindRestaurants = (Button) findViewById(R.id.btFindRestaurants);

        btFindRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FinishedRecipeActivity.this, RestaurantActivity.class);
                startActivity(i);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating <= 2) {
                    tvLowRating.setVisibility(View.VISIBLE);
                    btFindRestaurants.setVisibility(View.VISIBLE);
                }
                else {
                    tvLowRating.setVisibility(View.INVISIBLE);
                    btFindRestaurants.setVisibility(View.INVISIBLE);
                }
            }
        });

        btFinishRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rating = (int)ratingBar.getRating();
                if (rating != 0) {
                    recipe.addRating(rating);
                    recipe.updateAverageRating();
                    Toast.makeText(FinishedRecipeActivity.this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                }
                if (!etLeaveComment.getText().toString().equals("")) {
                    final Comment comment = new Comment();
                    comment.setText(etLeaveComment.getText().toString());
                    comment.setUser(ParseUser.getCurrentUser());
                    comment.setRecipe(recipe);

                    comment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            recipe.addComment(comment);
                            recipe.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(FinishedRecipeActivity.this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(FinishedRecipeActivity.this, HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    });
                }
                else {
                    recipe.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Intent intent = new Intent(FinishedRecipeActivity.this, HomeActivity.class);
                            // these flags clear the back stack of activities to solve potential back navigation issues
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                }


            }
        });
    }
}
