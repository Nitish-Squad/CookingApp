package me.ninabernick.cookingapplication.feed;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import me.ninabernick.cookingapplication.R;

public class FilterAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 4;

    ArrayList<String> tags = new ArrayList<>();
    private Context context;
    ArrayList<String> meal_tags = new ArrayList<>();
    ArrayList<String> dietary_info_tags = new ArrayList<>();
    ArrayList<String> dish_tags = new ArrayList<>();

    public FilterAdapter(FragmentManager fm, Context context) {
        super(fm);
        dietary_info_tags.addAll(Arrays.asList(context.getResources().getStringArray(R.array.dietary_info_tags)));
        meal_tags.addAll(Arrays.asList(context.getResources().getStringArray(R.array.meal_tags)));
        dish_tags.addAll(Arrays.asList(context.getResources().getStringArray(R.array.dish_tags)));
        tags.addAll(Arrays.asList(context.getResources().getStringArray(R.array.tags)));

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FilterDetailsFragment.newInstance(meal_tags);
            case 1:
                return FilterDetailsFragment.newInstance(dietary_info_tags);
            case 2:
                return FilterDetailsFragment.newInstance(dish_tags);
            case 3:
                FilterIngredientFragment ingredientFragment = new FilterIngredientFragment();
                return ingredientFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    private String[] tabTitles = new String[]{"Meal Tags", "Dietary Info Tags", "Dish Tags", "Ingredients"};

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}
