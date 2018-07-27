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


public class FilterFragment extends DialogFragment {
    ArrayList<String> tags;
    ArrayList<String> selectedTags;
    LinearLayout tagsLayout;
    Button filter;
    AutoCompleteTextView acIngredientFilter;
    ImageView ivAddIngredient;
    LinearLayout ingredientsLayout;
    ArrayList<AutoCompleteTextView> selectedIngredients;
    Spinner spSort;





    public FilterFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tags = new ArrayList<>();
        selectedTags = new ArrayList<>();
        tags.addAll(Arrays.asList(getResources().getStringArray(R.array.tags)));
        Log.d("tags", tags.toString());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(true);
        // set drop down list of ingredients
//        Spinner spinner = (Spinner) view.findViewById(R.id.spIngredients);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
//                R.array.ingredients, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);




        filter = (Button) view.findViewById(R.id.btFilter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("filters selected", selectedTags.toString());
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
                }
                dismiss();
            }
        });
        tagsLayout = (LinearLayout) view.findViewById(R.id.llTags);
        // dynamically add checkboxes for each tag in string array
        for (int i = 0; i < tags.size(); i++) {
            CheckBox cb = new CheckBox(view.getContext());
            cb.setText(tags.get(i));
            cb.setId(i);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean checked = ((CheckBox)view).isChecked();

                    if (checked) {
                        selectedTags.add(((CheckBox)view).getText().toString());
                        Log.d("filter added", ((CheckBox)view).getText().toString());
                    }
                    else {
                        selectedTags.remove(((CheckBox)view).getText().toString());
                    }
                }
            });
            tagsLayout.addView(cb);
        }

        selectedIngredients = new ArrayList<>();

        // adapter for autocomplete text views
        final ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.ingredients));
        acIngredientFilter = view.findViewById(R.id.acIngredient);
        acIngredientFilter.setAdapter(ingredientAdapter);
        selectedIngredients.add(acIngredientFilter);
        ingredientsLayout = (LinearLayout) view.findViewById(R.id.llIngredientFilters);

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
