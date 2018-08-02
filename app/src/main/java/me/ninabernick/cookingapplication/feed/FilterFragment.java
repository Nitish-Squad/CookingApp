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
    ArrayList<CheckBox> cbTags;
    LinearLayout tagsLayout;
    Button filter;
    Button btClear;






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

        filter = (Button) view.findViewById(R.id.btFilter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeedFragment.filters.clear();
                FeedFragment.filters.addAll(selectedTags);

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
            cbTags.add(cb);
            tagsLayout.addView(cb);
        }

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
                selectedTags.clear();
                FeedFragment.filters.clear();
                FeedFragment feed = (FeedFragment) getTargetFragment();
                if (feed != null) {
                    feed.getRecipes();
                }
                dismiss();
            }
        });
    }
}
