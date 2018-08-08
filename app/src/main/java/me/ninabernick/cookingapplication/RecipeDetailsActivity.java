package me.ninabernick.cookingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bennyhuo.swipefinishable.SwipeFinishable;

import me.ninabernick.cookingapplication.Steps.FragmentAdapter;
import me.ninabernick.cookingapplication.Steps.Transformers.ScaleInOutTransformer;
import me.ninabernick.cookingapplication.models.Recipe;


public class RecipeDetailsActivity extends AppCompatActivity implements SwipeFinishable.SwipeFinishableActivity {

    FragmentAdapter fAdapter;
    ViewPager viewPager;
    public static Recipe recipe;
    TextView title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipie_details_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipeDetailsActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

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
                if(lastPageChange && (position == lastIdx)) {
                    Intent i = new Intent(RecipeDetailsActivity.this, FinishedRecipeActivity.class);
                    i.putExtra("recipe", recipe);
                    startActivity(i);

                    /*
                     * Negation below fixes back error, the code appears to run this onPageScrolled section
                     * multiple times upon conclusion so on the last slide it actually creates
                     * multiple FinishedRecipeActivities which was causing the problems with the
                     * back navigation. Setting the lastPageChange as false at the end of this
                     * conditional makes sure it is only added one time.
                     */
                    lastPageChange = false;
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
                if((curItem == lastIdx)  && (state == 1)){
                    lastPageChange = true;

                }else  {
                    lastPageChange = false;
                }
            }
        };
        viewPager.setOnPageChangeListener(pagerListener);

    }

    @Override
    public void finishThisActivity() {
        super.finish();
    }

    @Override
    public void finish() {
        SwipeFinishable.INSTANCE.finishCurrentActivity();
    }

}
