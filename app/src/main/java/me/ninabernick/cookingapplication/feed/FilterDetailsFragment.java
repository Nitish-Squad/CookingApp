package me.ninabernick.cookingapplication.feed;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;

import me.ninabernick.cookingapplication.R;

public class FilterDetailsFragment extends Fragment {

    LinearLayout tagsLayout;
    ArrayList<String> tags;
    ArrayList<String> selectedTags;
    ArrayList<CheckBox> cbTags;

    public static FilterDetailsFragment newInstance(ArrayList<String> choiceTags) {
        FilterDetailsFragment fragmentFirst = new FilterDetailsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("choiceTags", choiceTags);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tags = getArguments().getStringArrayList("choiceTags");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_filter_details, container);
        tagsLayout = (LinearLayout) view.findViewById(R.id.llTags);

        for (int i = 0; i < tags.size(); i++) {
            CheckBox cb = new CheckBox(view.getContext());
            cb.setText(tags.get(i));
            if (FeedFragment.filters.contains(tags.get(i))) {
                cb.setChecked(true);
                selectedTags.add(cb.getText().toString());
            }
            else {
                cb.setChecked(false);
            }
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

        ((FilterFragment) getParentFragment()).updateCBandSelectedTags(cbTags, selectedTags);
        return view;
    }


}

