package me.ninabernick.cookingapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;


public class FilterFragment extends DialogFragment {
    ArrayList<String> tags;
    ArrayList<String> selectedTags;
    LinearLayout tagsLayout;
    Button filter;





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
        // Get field from view
        filter = (Button) view.findViewById(R.id.btFilter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("filters selected", selectedTags.toString());
                FeedFragment.filters.clear();
                FeedFragment.filters.addAll(selectedTags);
                FeedFragment feed = (FeedFragment) getTargetFragment();
                if (feed != null) {
                    feed.getRecipes();
                }
                dismiss();
                Log.d("filters", FeedFragment.filters.toString());
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


    }



}
