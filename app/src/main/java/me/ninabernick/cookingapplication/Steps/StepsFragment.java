package me.ninabernick.cookingapplication.Steps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.ninabernick.cookingapplication.CountDownDialog;
import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.RecipeDetailsActivity;
import me.ninabernick.cookingapplication.StepClickInfoDialog;
import me.ninabernick.cookingapplication.YoutubeVideoActivity;
import me.ninabernick.cookingapplication.models.Recipe;

import static com.parse.Parse.getApplicationContext;

public class StepsFragment extends Fragment {

    private int stepnumber;
    private String time;
    private String icon;
    private String step;
    private Recipe recipe;
    private ArrayList<JSONObject> jsonIngredients;
    private ArrayList<String> cookingTerms;
    private ArrayList<String> termDefinitions;
    private static final int DIALOG_REQUEST_CODE = 20;

    public static StepsFragment newInstance(int stepnumber, String time, String url, String stepdetails) {
        StepsFragment step = new StepsFragment();
        Bundle args = new Bundle();
        args.putInt("stepnumber", stepnumber);
        args.putString("time", time);
        args.putString("icon", url);
        args.putString("stepdetails", stepdetails);
        step.setArguments(args);
        return step;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stepnumber = getArguments().getInt("stepnumber", 0);
        time = getArguments().getString("time");
        icon = getArguments().getString("icon");
        step = getArguments().getString("stepdetails");
        recipe = RecipeDetailsActivity.recipe;
        jsonIngredients = new ArrayList<>();
        cookingTerms = new ArrayList<>();
        termDefinitions = new ArrayList<>();
        cookingTerms.addAll(Arrays.asList(getResources().getStringArray(R.array.cooking_terms)));
        termDefinitions.addAll(Arrays.asList(getResources().getStringArray(R.array.definitions)));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_steps, container, false);

        TextView tvStepNumber = (TextView) view.findViewById(R.id.tvStepNumber);
        tvStepNumber.setText("Step " + stepnumber);

        TextView tvET = (TextView) view.findViewById(R.id.tvETime);
        tvET.setText("Estimated Time: " + time);

        ImageButton ivIcon = (ImageButton) view.findViewById(R.id.ivIcon);

        int myID = getResourceID(icon, "drawable", getApplicationContext());

        ivIcon.setImageResource(myID);


        String temp_video_extension = null;

        Boolean valid_video_extension = true;


        /*
         * This is the area where icons and links should be set up, any time an icon is added
         * that should have its own accompanying video, the proper case and video extension
         * need to be added to the switch statement below so that the image is clickable and
         * the video will open on click.
         */
        switch (icon){
            case ("stove_top_icon"):
                temp_video_extension = "UYhKDweME3A";
                break;
            case("glove_icon"):
                temp_video_extension = "gMK5Via4f6c";
                break;
            case("chicken_icon"):
                temp_video_extension = "abWDuIXcggc";
                break;
            default:
                // icons without links will do nothing when clicked
                valid_video_extension = false;
                break;
        }

        final Boolean finalValid_video_extension = valid_video_extension;
        final String video_extension = temp_video_extension;

        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finalValid_video_extension) {
                    Intent intent = new Intent(getContext(), YoutubeVideoActivity.class);
                    intent.putExtra("video_extension", video_extension);
                    startActivity(intent);

                }

                // if not a valid video extension then nothing happens

            }
        });

        // create list of json object ingredients that will be used to set clickables
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            try {
                JSONObject json = new JSONObject(recipe.getIngredients().get(i));
                jsonIngredients.add(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /*
         * Below is the code that sets up the variable timer for each step, it attempts to go
         * through the step string and then recover any potential units of time and set up
         * the necessary variables to launch a timer. Otherwise, it will display the text as normal.
         */

        // attempts to find any numbers in a string using pattern and match objects
        String regex = "(.)*(\\d)(.)*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(step);
        Boolean match_found = matcher.matches();

        SpannableString ss = new SpannableString(step);


        if (match_found) {
            Scanner in = new Scanner(step).useDelimiter("[^0-9]+");
            int integer = in.nextInt();

            final int millisseconds;
            final int interval = 1000;

            String integer_string = Integer.toString(integer);
            Boolean unit_of_time = true;


            int position = step.indexOf(integer_string);
            int unit_length = 0;

            if (step.indexOf("hour") != -1) {
                unit_length = 4;
                if (step.indexOf("hours") != -1) {
                    unit_length++;
                }
                millisseconds = integer * 60 * 60 * 1000;
            } else if (step.indexOf("minute") != -1) {
                unit_length = 6;
                if (step.indexOf("minutes") != -1) {
                    unit_length++;
                }
                millisseconds = integer * 60 * 1000;
            } else if (step.indexOf("seconds") != -1) {
                unit_length = 7;
                // should never be a timer of 1 second, doesn't make sense assume >1 second of time
                millisseconds = integer * 1000;
            } else {
                // should never get here, only reachable if there is no time period in the text
                millisseconds = 0;
                unit_of_time = false;
            }



            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    CountDownDialog countDownDialog = CountDownDialog.newInstance(millisseconds, interval);
                    countDownDialog.show(getActivity().getSupportFragmentManager(), "fragment_countdownTimer");
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.BLUE);
                    ds.setUnderlineText(false);
                }
            };

            if (unit_of_time){
                ss.setSpan(clickableSpan, position, position + 1 + integer_string.length() + unit_length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                TextView textView = (TextView) view.findViewById(R.id.tvInstruct);
                textView.setText(ss);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setHighlightColor(Color.TRANSPARENT);
            }
            else{
                TextView tvStep = (TextView) view.findViewById(R.id.tvInstruct);
                tvStep.setText(step);
            }



        }
        else{
            TextView tvStep = (TextView) view.findViewById(R.id.tvInstruct);
            tvStep.setText(step);
        }

        // edited this out after integrating the clickable countdown timer
        // TextView tvStep = (TextView) view.findViewById(R.id.tvInstruct);
        // tvStep.setText(step);

        // search through step text and find instances of ingredient to create a popup
        for (int i = 0; i < recipe.getTextIngredients().size(); i++) {
            final String ingredient = recipe.getTextIngredients().get(i);
            if (step.contains(ingredient)) {
                Log.d("ingredient", ingredient);


                ClickableSpan span = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        try {
                            StepClickInfoDialog dialog = StepClickInfoDialog.newInstance(ingredient, getAmountOfIngredient(ingredient));
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            dialog.setTargetFragment(StepsFragment.this, DIALOG_REQUEST_CODE);
                            dialog.setEnterTransition(new Fade());
                            dialog.setExitTransition(new Fade());
                            dialog.show(ft, "dialog");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                int index = step.indexOf(ingredient);
                Log.d("index", Integer.toString(index));
                ss.setSpan(span, index, index+ingredient.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                TextView textView = (TextView) view.findViewById(R.id.tvInstruct);
                textView.setText(ss);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setHighlightColor(Color.TRANSPARENT);
            }
        }

        // search through step text for cooking terms
        for (int i = 0; i < cookingTerms.size(); i++) {
            final String term = cookingTerms.get(i);
            final String definition = termDefinitions.get(i);
            if (step.contains(term)) {
                Log.d("term", term);
                ClickableSpan span = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        StepClickInfoDialog dialog = StepClickInfoDialog.newInstance(term, definition);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        dialog.setTargetFragment(StepsFragment.this, DIALOG_REQUEST_CODE);
                        dialog.show(ft, "dialog");
                    }
                };
                int index = step.indexOf(term);
                ss.setSpan(span, index, index + term.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                TextView textView = (TextView) view.findViewById(R.id.tvInstruct);
                textView.setText(ss);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setHighlightColor(Color.TRANSPARENT);
            }
        }

        return view;
    }

    public String getAmountOfIngredient(String ingredient) throws JSONException {
        for (int i = 0; i < jsonIngredients.size(); i++) {
            JSONObject jsonIngredient = jsonIngredients.get(i);
            if (jsonIngredient.getString("name").equals(ingredient)) {
                return (jsonIngredient.getString("quantity") + " " + jsonIngredient.getString("unit"));
            }

        }
        return null;
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    protected final static int getResourceID (final String resName, final String resType, final Context ctx)
    {
        final int ResourceID =
                ctx.getResources().getIdentifier(resName, resType,
                        ctx.getApplicationInfo().packageName);
        if (ResourceID == 0)
        {
            throw new IllegalArgumentException
                    (
                            "No resource string found with name " + resName
                    );
        }
        else
        {
            return ResourceID;
        }
    }




}
