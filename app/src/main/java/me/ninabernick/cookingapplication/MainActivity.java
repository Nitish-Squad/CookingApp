package me.ninabernick.cookingapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import me.ninabernick.cookingapplication.models.Recipe;

public class MainActivity extends AppCompatActivity {

    TextView tvTest;
    TextView tvTest2;
    TextView tvTest3;
    private ArrayList<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTest = (TextView) findViewById(R.id.tvTest);
        tvTest2 = (TextView) findViewById(R.id.tvTest2);
        tvTest3 = (TextView) findViewById(R.id.tvTest3);

        final Recipe.Query recipeQuery = new Recipe.Query();

        // place to order the recipes in some manner (ex: rating, when created, etc)
        //postsQuery.orderByDescending("createdAt");

        recipeQuery.getTop();
        recipeQuery.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e == null)
                {
                    Recipe recipe = objects.get(0);


                    tvTest.setText(recipe.getStepText(0));
                    tvTest2.setText(recipe.getStepImageURL(0));
                    tvTest3.setText(recipe.getStepTime(0));

                    Integer test_int = recipe.getNumberofSteps();


                    // code used to add steps to the sample recipe on the Parse Server

                    /*try {
                        json.put("text","Put both slices together");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    try {
                        json.put("icon","TBD");
                    } catch (JSONException e2) {
                        Log.d("MainActivity","Failed to save icon value");
                        e2.printStackTrace();
                    }

                    try {
                        json.put("time","One minute");
                    } catch (JSONException e3) {
                        Log.d("MainActivity","Failed to save time value");
                        e3.printStackTrace();
                    }

                    String step = json.toString();

                    tvTest.setText(step);

                    recipe.add("steps",step);
                    recipe.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            // just do nothing
                        }
                    });*/
                }
                else{
                    Log.d("MainActivity","Failed to get all the recipes.");
                    e.printStackTrace();
                }
            }
        });




    }

}
