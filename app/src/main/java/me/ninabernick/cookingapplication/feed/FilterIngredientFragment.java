package me.ninabernick.cookingapplication.feed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.common.images.WebImageCreator;

import java.util.ArrayList;
import java.util.Arrays;

import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.feed.FeedFragment;


public class FilterIngredientFragment extends DialogFragment {

    AutoCompleteTextView acIngredientFilter;
    ImageView ivAddIngredient;
    LinearLayout ingredientsLayout;
    ArrayList<AutoCompleteTextView> selectedIngredients;
    Button filter;
    Button btClear;

    public FilterIngredientFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedIngredients = new ArrayList<>();


    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_ingredients, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(true);

        filter = (Button) view.findViewById(R.id.btFilter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        btClear = (Button) view.findViewById(R.id.btClear);
        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeedFragment.ingredientFilters.clear();
                for (int i = 1; i < selectedIngredients.size(); i++) {
                    ingredientsLayout.removeViewAt(i);
                }
                selectedIngredients.clear();
                FeedFragment feed = (FeedFragment) getTargetFragment();
                if (feed != null) {
                    feed.getRecipes();
                    feed.updateSelectedTags();
                }
                dismiss();
            }
        });



        // adapter for autocomplete text views
        final ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.ingredients));
        acIngredientFilter = view.findViewById(R.id.acIngredient);
        acIngredientFilter.setAdapter(ingredientAdapter);
        if (FeedFragment.ingredientFilters.size() != 0) {
            acIngredientFilter.setText(FeedFragment.ingredientFilters.get(0));
        }
        selectedIngredients.add(acIngredientFilter);
        ingredientsLayout = (LinearLayout) view.findViewById(R.id.llIngredientFilters);


        for (int i = 1; i < FeedFragment.ingredientFilters.size(); i++) {
            AutoCompleteTextView newIngredient = new AutoCompleteTextView(getContext());
            newIngredient.setGravity(View.TEXT_ALIGNMENT_CENTER);
            newIngredient.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            newIngredient.setAdapter(ingredientAdapter);
            newIngredient.setText(FeedFragment.ingredientFilters.get(i));
            ingredientsLayout.addView(newIngredient);
            selectedIngredients.add(newIngredient);
        }

        ivAddIngredient = view.findViewById(R.id.ivAddIngredient);
        ivAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutoCompleteTextView newIngredient = new AutoCompleteTextView(getContext());
                newIngredient.setGravity(View.TEXT_ALIGNMENT_CENTER);
                newIngredient.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                newIngredient.setAdapter(ingredientAdapter);
                ingredientsLayout.addView(newIngredient);
                selectedIngredients.add(newIngredient);
            }
        });

    }

}
