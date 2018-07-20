package me.ninabernick.cookingapplication;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import me.ninabernick.cookingapplication.models.Recipe;


public class RecipeDetailsActivity extends AppCompatActivity {

    FragmentAdapter fAdapter;
    ViewPager viewPager;
    Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipie_details_main);

        recipe = (Recipe) getIntent().getParcelableExtra("recipe");

        fAdapter = new FragmentAdapter(getSupportFragmentManager(), recipe, RecipeDetailsActivity.this);
        viewPager = (ViewPager) findViewById(R.id.vpPager);
        viewPager.setAdapter(fAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

}
