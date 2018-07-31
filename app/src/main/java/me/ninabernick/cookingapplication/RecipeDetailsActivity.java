package me.ninabernick.cookingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import me.ninabernick.cookingapplication.Steps.Transformers.CubeOutTransformer;
import me.ninabernick.cookingapplication.Steps.FragmentAdapter;
import me.ninabernick.cookingapplication.Steps.Transformers.ScaleInOutTransformer;
import me.ninabernick.cookingapplication.models.Recipe;


public class RecipeDetailsActivity extends AppCompatActivity {

    FragmentAdapter fAdapter;
    ViewPager viewPager;
    Recipe recipe;
    TextView title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipie_details_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_recipe_steps);
        setSupportActionBar(toolbar);
        recipe = (Recipe) getIntent().getParcelableExtra("recipe");
        title = findViewById(R.id.tvRecipeTitle);
        title.setText(recipe.getTitle());


        fAdapter = new FragmentAdapter(getSupportFragmentManager(), recipe, RecipeDetailsActivity.this);
        viewPager = (ViewPager) findViewById(R.id.vpPager);
        viewPager.setAdapter(fAdapter);
        viewPager.setPageTransformer(true, new ScaleInOutTransformer());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        ViewPager.OnPageChangeListener pagerListener = new ViewPager.OnPageChangeListener() {
            boolean lastPageChange = false;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
                int lastIdx = fAdapter.getCount() - 1;
                // starts finished recipe activity
                if(lastPageChange && position == lastIdx) {
                    Intent i = new Intent(RecipeDetailsActivity.this, FinishedRecipeActivity.class);
                    i.putExtra("recipe", recipe);
                    startActivity(i);
                }

            }
            @Override
            public void onPageSelected( int i) {
                //viewPager.setCurrentItem(i/2);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                int lastIdx = fAdapter.getCount() - 1;

                int curItem = viewPager.getCurrentItem();
                if(curItem==lastIdx  && state==1){
                    lastPageChange = true;

                    finish();

                }else  {
                    lastPageChange = false;
                }
            }
        };
        viewPager.setOnPageChangeListener(pagerListener);

    }

}
