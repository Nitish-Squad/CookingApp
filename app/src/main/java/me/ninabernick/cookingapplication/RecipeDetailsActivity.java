package me.ninabernick.cookingapplication;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parse.FindCallback;

import java.util.List;

import me.ninabernick.cookingapplication.models.Recipe;


public class RecipeDetailsActivity extends AppCompatActivity {

    FragmentAdapter fAdapter;
    ViewPager viewPager;
    Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipie_details_main);

        // Get Top Recipe
        final Recipe.Query recipeQuery = new Recipe.Query();

        recipeQuery.getTop();
        recipeQuery.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, com.parse.ParseException e) {
                if (e == null)
                {
                    recipe = objects.get(0);

                    fAdapter = new FragmentAdapter(getSupportFragmentManager(), recipe, RecipeDetailsActivity.this);
                    viewPager = (ViewPager) findViewById(R.id.vpPager);
                    viewPager.setAdapter(fAdapter);

                    TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
                    tabLayout.setupWithViewPager(viewPager);

                }
                else{
                    Log.d("RecipeDetailsActivity","Failed to get all the recipes.");
                    e.printStackTrace();
                }
            }

        });





    }

}
