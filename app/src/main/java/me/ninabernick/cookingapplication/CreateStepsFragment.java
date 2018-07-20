package me.ninabernick.cookingapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.ninabernick.cookingapplication.models.Recipe;


public class CreateStepsFragment extends Fragment {


    Button btnAddStep;
    Button btnNext;
    LinearLayout steps;
    TextView tvTitle;
    EditText etStepDescription1;
    EditText etStepTime1;
    ArrayList<EditText> step_description_array;
    ArrayList<EditText> step_time_array;
    ArrayList<Spinner> icon_spinner_array;
    Spinner spinnerIcon1;

    List<String> steps_list;

    private OnFragmentInteractionListener mListener;

    public CreateStepsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_steps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAddStep = (Button) view.findViewById(R.id.btnAdd);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        steps = (LinearLayout) view.findViewById(R.id.steps);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        etStepDescription1 = (EditText) view.findViewById(R.id.etStepDescription1);
        etStepTime1 = (EditText) view.findViewById(R.id.etStepTime1);

        // initial spinner setup
        spinnerIcon1 = (Spinner) view.findViewById(R.id.spinnerIcon1);

        // TODO: put the images in this spinner
        SimpleImageArrayAdapter adapter = new SimpleImageArrayAdapter(getContext(),
                new Integer[]{R.drawable.stove_top_icon, R.drawable.chicken_icon, R.drawable.glove_icon});
        spinnerIcon1.setAdapter(adapter);

        step_description_array = new ArrayList<>();
        step_description_array.add(etStepDescription1);

        step_time_array = new ArrayList<>();
        step_time_array.add(etStepTime1);

        icon_spinner_array = new ArrayList<>();
        icon_spinner_array.add(spinnerIcon1);

        steps_list = new ArrayList<>();

        btnAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logic for adding edit text box for step description
                EditText temp = new EditText(getContext());
                temp.setHint("Enter step description");
                steps.addView(temp);
                step_description_array.add(temp);

                // logic for adding new spinners for icons
                Spinner tempspinner = new Spinner(getContext());
                SimpleImageArrayAdapter adapter = new SimpleImageArrayAdapter(getContext(),
                        new Integer[]{R.drawable.stove_top_icon, R.drawable.chicken_icon, R.drawable.glove_icon});
                tempspinner.setAdapter(adapter);
                tempspinner.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                steps.addView(tempspinner);
                icon_spinner_array.add(tempspinner);


                // logic for adding new edit text box for step time
                EditText temp2 = new EditText(getContext());
                temp2.setHint("Enter step time");
                steps.addView(temp2);
                step_time_array.add(temp2);
            }
        });



        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logic to save ingredients in the array


                for (int i = 0; i < step_description_array.size(); i++)
                {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("text", step_description_array.get(i).getText().toString());
                    } catch (JSONException e1) {
                        Log.d("CreateStepsFragment","Failed to save step text value");
                        e1.printStackTrace();
                    }

                    try {
                        json.put("icon",getResourceURL(i));
                    } catch (JSONException e2) {
                        Log.d("CreateStepsFragment","Failed to save icon value");
                        e2.printStackTrace();
                    }

                    try {
                        json.put("time",step_time_array.get(i).getText().toString());
                    } catch (JSONException e3) {
                        Log.d("CreateStepsFragment","Failed to save time value");
                        e3.printStackTrace();
                    }

                    String step = json.toString();

                    steps_list.add(step);


                }

                HomeActivity createActivity = (HomeActivity) getActivity();

                Recipe new_recipe = createActivity.recipe_to_add;

                new_recipe.setSteps(steps_list);

                createActivity.recipe_to_add = new_recipe;

                new_recipe.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        // just do nothing
                    }
                });

                BasicInfoFragment fragment2 = new BasicInfoFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flFragmentContainer, fragment2);
                fragmentTransaction.commit();


            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String getResourceURL(Integer position){

        Integer spinner_position = icon_spinner_array.get(position).getSelectedItemPosition();
        switch (spinner_position){
            case (0):
                return "R.drawable.stove_top_icon";
            case(1):
                return "R.drawable.chicken_icon";
            case(2):
                return"R.drawable.glove_icon";
        }

        // this should never be reached, only here to solve error and indicate errors
        return "Something went wrong!";
    }
}
