package me.ninabernick.cookingapplication.feed;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import me.ninabernick.cookingapplication.R;

public class FilterDetailsFragment extends Fragment {

    LinearLayout tagsLayout;
    ArrayList<CheckBox> cbTags;
    ArrayList<String> choiceTags;
    ArrayList<String> selectedTags;

    Context context;

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        public void updateCheckList(ArrayList<CheckBox> cbTags, ArrayList<String> selectedTags);
    }

    public void onAttachToParentFragment(Fragment fragment)
    {
        try
        {
            listener = (OnItemSelectedListener) fragment;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(
                    fragment.toString() + " must implement OnPlayerSelectionSetListener");
        }
    }


    // newInstance constructor for creating fragment with arguments
    public static FilterDetailsFragment newInstance(ArrayList<String> choiceTags, Context context) {
        FilterDetailsFragment fragmentFirst = new FilterDetailsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("choiceTags", choiceTags);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        choiceTags = getArguments().getStringArrayList("choiceTags");
        selectedTags = new ArrayList<>();
        cbTags = new ArrayList<>();
        onAttachToParentFragment((FilterFragment) getParentFragment());


    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_filter_details, container);

        tagsLayout = (LinearLayout) view.findViewById(R.id.llTags);
        // dynamically add checkboxes for each tag in string array
        for (int i = 0; i < choiceTags.size(); i++) {
            CheckBox cb = new CheckBox(view.getContext());
            cb.setText(choiceTags.get(i));
            if (FeedFragment.filters.contains(choiceTags.get(i))) {
                cb.setChecked(true);
                selectedTags.add(cb.getText().toString());
                listener.updateCheckList(cbTags, selectedTags);
                notifyAll();
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
                        listener.updateCheckList(cbTags, selectedTags);
                        Log.d("filter added", ((CheckBox)view).getText().toString());
                    }
                    else {
                        selectedTags.remove(((CheckBox)view).getText().toString());
                        listener.updateCheckList(cbTags, selectedTags);

                    }
                }
            });
            cbTags.add(cb);
            tagsLayout.addView(cb);
        }
        listener.updateCheckList(cbTags, selectedTags);
        return view;
    }

}

