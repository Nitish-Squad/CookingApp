package me.ninabernick.cookingapplication.feed;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.Arrays;

import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.Steps.Transformers.ZoomOutTransformation;


public class FilterFragment extends DialogFragment {
    ArrayList<String> tags;
    ArrayList<String> selectedTags;
    ArrayList<CheckBox> cbTags;
    LinearLayout tagsLayout;
    Button filter;
    Button btClear;
    ArrayList<AutoCompleteTextView> selectedIngredients;


    FilterAdapter adapterViewPager;
    LinearLayout ingredientsLayout;







    public FilterFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tags = new ArrayList<>();
        selectedTags = new ArrayList<>();
        cbTags = new ArrayList<>();
        tags.addAll(Arrays.asList(getResources().getStringArray(R.array.tags)));
        Log.d("tags", tags.toString());


        selectedIngredients = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter, container);


        ViewPager pager = (ViewPager) view.findViewById(R.id.vpPager);
        adapterViewPager = new FilterAdapter(getChildFragmentManager(), getContext());
        pager.setAdapter(adapterViewPager);
        pager.setPageTransformer(true, new ZoomOutTransformation());

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabsStrip.setViewPager(pager);


        // Not exactly sure why, but this code is required to get the dialog to have rounded corners
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(true);
        selectedTags.clear();
        Log.d("onViewCreated", "called");

        filter = (Button) view.findViewById(R.id.btFilter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeedFragment.filters.clear();
                FeedFragment.filters.addAll(selectedTags);

                FeedFragment.ingredientFilters.clear();
                for (int i = 0; i < selectedIngredients.size(); i++) {
                    if (!selectedIngredients.get(i).getText().toString().equals("")) {
                        FeedFragment.ingredientFilters.add(selectedIngredients.get(i).getText().toString().toLowerCase());
                    }
                    else {
                        FeedFragment.ingredientFilters.clear();
                    }
                }

                FeedFragment feed = (FeedFragment) getTargetFragment();
                if (feed != null) {
                    feed.getRecipes();
                    feed.updateSelectedTags();
                }
                dismiss();
            }

        });
//        tagsLayout = (LinearLayout) view.findViewById(R.id.llTags);
//        // dynamically add checkboxes for each tag in string array
//        for (int i = 0; i < tags.size(); i++) {
//            CheckBox cb = new CheckBox(view.getContext());
//            cb.setText(tags.get(i));
//            if (FeedFragment.filters.contains(tags.get(i))) {
//                cb.setChecked(true);
//                selectedTags.add(cb.getText().toString());
//            }
//            else {
//                cb.setChecked(false);
//            }
//            cb.setId(i);
//            cb.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    boolean checked = ((CheckBox)view).isChecked();
//
//                    if (checked) {
//                        selectedTags.add(((CheckBox)view).getText().toString());
//                        Log.d("filter added", ((CheckBox)view).getText().toString());
//                    }
//                    else {
//                        selectedTags.remove(((CheckBox)view).getText().toString());
//                    }
//                }
//            });
//            cbTags.add(cb);
//            tagsLayout.addView(cb);
//        }

        btClear = (Button) view.findViewById(R.id.btClear);
        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < cbTags.size(); i++) {
                    CheckBox cb = cbTags.get(i);
                    if (cb.isChecked()) {
                        cb.setChecked(false);
                    }
                }

               for (int i = 1; i < selectedIngredients.size(); i++) {
                    ingredientsLayout.removeViewAt(i);
                }

                selectedIngredients.clear();
                FeedFragment feed = (FeedFragment) getTargetFragment();
                if (feed != null) {
                    feed.getRecipes();
                    feed.updateSelectedTags();
                }

                FeedFragment.ingredientFilters.clear();
                dismiss();
            }
        });
    }


    public void updateCBandSelectedTags(ArrayList<CheckBox> cbTags, ArrayList<String> selectedTags) {
        //this.selectedTags.clear();
        this.selectedTags.addAll(selectedTags);
        //this.cbTags.clear();
        this.cbTags.addAll(cbTags);
    }

    public void updateSelectedIngredients(ArrayList<AutoCompleteTextView> selectedIngredients, LinearLayout ingredientsLayout) {
        this.selectedIngredients.clear();
        this.selectedIngredients.addAll(selectedIngredients);
        this.ingredientsLayout = ingredientsLayout;
    }
}
