package me.ninabernick.cookingapplication.feed;

import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;

import me.ninabernick.cookingapplication.R;


public class FilterFragment extends DialogFragment implements FilterDetailsFragment.OnItemSelectedListener{
    ArrayList<String> selectedTags;
    ArrayList<CheckBox> cbTags;
    Button filter;
    Button btClear;

    FilterAdapter adapterViewPager;


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
        selectedTags = new ArrayList<>();
        cbTags = new ArrayList<>();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_filter, container);

        ViewPager pager = (ViewPager) view.findViewById(R.id.vpPager);
        adapterViewPager = new FilterAdapter(getChildFragmentManager(), this.getContext(), this);
        pager.setAdapter(adapterViewPager);


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
                    feed.updateSelectedTags();
                }
                dismiss();
            }
        });
    }

    @Override
    public void updateCheckList(ArrayList<CheckBox> cbTags, ArrayList<String> selectedTags) {
        this.cbTags.clear();
        this.cbTags.addAll(cbTags);
        this.selectedTags.clear();
        this.selectedTags.addAll(selectedTags);
    }
}
