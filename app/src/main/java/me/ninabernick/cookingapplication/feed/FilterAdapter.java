package me.ninabernick.cookingapplication.feed;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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

    FilterFragment dialog;

    public FilterAdapter(FragmentManager fragmentManager, Context context, FilterFragment dialog) {
        super(fragmentManager);
        this.context = context;
        this.dialog = dialog;
        dietary_info_tags.addAll(Arrays.asList(context.getResources().getStringArray(R.array.dietary_info_tags)));
        meal_tags.addAll(Arrays.asList(context.getResources().getStringArray(R.array.meal_tags)));
        dish_tags.addAll(Arrays.asList(context.getResources().getStringArray(R.array.dish_tags)));
        tags.addAll(Arrays.asList(context.getResources().getStringArray(R.array.tags)));
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FilterDetailsFragment.newInstance(meal_tags, context);
            case 1:
                return FilterDetailsFragment.newInstance(dietary_info_tags, context);
            case 2:
                return FilterDetailsFragment.newInstance(dish_tags, context);
            case 3:
                return FilterDetailsFragment.newInstance(tags, context);
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }


    public void onClose () {
        dialog.dismiss();
    }
}

