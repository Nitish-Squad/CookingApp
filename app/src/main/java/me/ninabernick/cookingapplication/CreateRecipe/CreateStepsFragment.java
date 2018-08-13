package me.ninabernick.cookingapplication.CreateRecipe;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.ninabernick.cookingapplication.HomeActivity;
import me.ninabernick.cookingapplication.R;
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
    ProgressBar loadingView;

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
        loadingView = (ProgressBar) view.findViewById(R.id.pbUploading);
        loadingView.setVisibility(View.INVISIBLE);

        btnAddStep = (Button) view.findViewById(R.id.btnAdd);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        steps = (LinearLayout) view.findViewById(R.id.steps);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);

        HomeActivity homeActivity = (HomeActivity) getActivity();
        Boolean prepopulated = homeActivity.getPrepopulated();

        step_description_array = new ArrayList<>();
        icon_spinner_array = new ArrayList<>();
        step_time_array = new ArrayList<>();

        CustomAdapter adapter = new CustomAdapter(getContext(),
                new int[]{R.drawable.oven_vector,
                        R.drawable.blender_vector,
                        R.drawable.microwave_vector,
                        R.drawable.bowl_vector,
                        R.drawable.fridge_vector,
                        R.drawable.kettle_vector,
                        R.drawable.mixer_vector,
                        R.drawable.pan_vector,
                        R.drawable.roller_vector,
                        R.drawable.toaster_vector,
                        R.drawable.knife_vector});


        ArrayList<String> prepopulated_data = new ArrayList<String>(
                Arrays.asList("{\"text\":\"Heat the milk in a microwave for 80 seconds.\",\"icon\":\"microwave_vector\",\"time\":\"2 minutes\"}",
                "{\"text\":\"Add cocoa powder and sugar and stir for 5 seconds.\",\"icon\":\"blender_vector\",\"time\":\"30 seconds\"}",
                "{\"text\":\"Pour into a cup and add toppings as desired!\",\"icon\":\"kettle_vector\",\"time\":\"1 minute\"}"
        ));

        ArrayList<String> potential_icons = new ArrayList<String>(Arrays.asList(
                "oven_vector",
                "blender_vector",
                "microwave_vector",
                "bowl_vector",
                "fridge_vector",
                "kettle_vector",
                "mixer_vector",
                "pan_vector",
                "roller_vector",
                "toaster_vector",
                "knife_vector"
        ));

        if (prepopulated){
            for (int i = 0; i < prepopulated_data.size(); i++){
                try {
                    JSONObject temp_step = new JSONObject(prepopulated_data.get(i));

                    etStepDescription1 = new EditText(getContext());
                    etStepTime1 = new EditText(getContext());
                    // initial spinner setup
                    spinnerIcon1 = new Spinner(getContext());
                    spinnerIcon1.setAdapter(adapter);
                    spinnerIcon1.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

                    etStepDescription1.setHint("Enter step description");
                    step_description_array.add(etStepDescription1);
                    steps.addView(etStepDescription1);
                    etStepDescription1.setText(temp_step.getString("text"));


                    // TODO: set the spinner position based on the prepopulated data
                    icon_spinner_array.add(spinnerIcon1);
                    steps.addView(spinnerIcon1);
                    int index = 0;

                    for (int j = 0; j < potential_icons.size(); j++){
                        if (potential_icons.get(j).equals(temp_step.getString("icon"))){
                            index = j;
                        }
                    }
                    spinnerIcon1.setSelection(index);

                    etStepTime1.setHint("Enter step time");
                    step_time_array.add(etStepTime1);
                    steps.addView(etStepTime1);
                    etStepTime1.setText(temp_step.getString("time"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
        else{
            etStepDescription1 = new EditText(getContext());
            etStepTime1 = new EditText(getContext());
            // initial spinner setup
            spinnerIcon1 = new Spinner(getContext());
            spinnerIcon1.setAdapter(adapter);
            spinnerIcon1.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

            etStepDescription1.setHint("Enter step description");
            step_description_array.add(etStepDescription1);
            steps.addView(etStepDescription1);

            icon_spinner_array.add(spinnerIcon1);
            steps.addView(spinnerIcon1);
            etStepTime1.setHint("Enter step time");
            step_time_array.add(etStepTime1);
            steps.addView(etStepTime1);
        }



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
                CustomAdapter adapter = new CustomAdapter(getContext(),
                        new int[]{R.drawable.oven_vector,
                                R.drawable.blender_vector,
                                R.drawable.microwave_vector,
                                R.drawable.bowl_vector,
                                R.drawable.fridge_vector,
                                R.drawable.kettle_vector,
                                R.drawable.mixer_vector,
                                R.drawable.pan_vector,
                                R.drawable.roller_vector,
                                R.drawable.toaster_vector,
                                R.drawable.knife_vector});
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

                // naming isn't great here, it was just for ease of linking
                HomeActivity createActivity = (HomeActivity) getActivity();

                Recipe new_recipe = createActivity.recipe_to_add;

                new_recipe.setSteps(steps_list);


                createActivity.recipe_to_add = new Recipe();


                new_recipe.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(getContext(), "Recipe Uploaded!", Toast.LENGTH_SHORT).show();
                        /*
                         * No need to implement callbacks, if you set the item selected it acts as though the
                         * icon has been tapped and runs the code that follows.
                         */

                        for (Fragment fragment:getFragmentManager().getFragments()) {
                            getFragmentManager().beginTransaction().remove(fragment).commit();
                        }
                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.bottomNavigationView.setSelectedItemId(R.id.miFeed);
                    }
                });

                loadingView.setVisibility(View.VISIBLE);
                createActivity.recipe_to_add = new Recipe();
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);






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
                return "oven_vector";
            case(1):
                return "blender_vector";
            case(2):
                return "microwave_vector";
            case(3):
                return "bowl_vector";
            case(4):
                return "fridge_vector";
            case(5):
                return "kettle_vector";
            case(6):
                return "mixer_vector";
            case(7):
                return "pan_vector";
            case(8):
                return "roller_vector";
            case(9):
                return "toaster_vector";
            case(10):
                return "knife_vector";
        }

        // this should never be reached, only here to solve error and indicate errors
        return "Something went wrong!";
    }
}
