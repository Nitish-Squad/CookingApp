package me.ninabernick.cookingapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepsFragment extends Fragment {

    private int stepnumber;
    private String time;
    private String icon;
    private String step;

    public static StepsFragment newInstance(int stepnumber, String time, String url, String stepdetails) {
        StepsFragment step = new StepsFragment();
        Bundle args = new Bundle();
        args.putInt("stepnumber", stepnumber);
        args.putString("time", time);
        args.putString("url", url);
        args.putString("stepdetails", stepdetails);
        step.setArguments(args);
        return step;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stepnumber = getArguments().getInt("stepnumber", 0);
        time = getArguments().getString("time");
        icon = getArguments().getParcelable("url");
        step = getArguments().getString("stepdetails");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        TextView tvStepNumber = (TextView) view.findViewById(R.id.tvStepNumber);
        tvStepNumber.setText("Step " + stepnumber);

        TextView tvET = (TextView) view.findViewById(R.id.tvETime);
        tvET.setText("Estimated Time: " + time);

        ImageView ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
        Glide.with(this).load(icon).into(ivIcon);

        /*
         * Below is the code that sets up the variable timer for each step, it attempts to go
         * through the step string and then recover any potential units of time and set up
         * the necessary variables to launch a timer. Otherwise, it will display the text as normal.
         */

        // attempts to find any numbers in a string using pattern and match objects
        String regex = "(.)*(\\d)(.)*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(step);
        boolean isMatched = matcher.matches();


        if (isMatched) {
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
            } else if (step.indexOf("second") != -1) {
                unit_length = 6;
                if (step.indexOf("seconds") != -1) {
                    unit_length++;
                }
                millisseconds = integer * 1000;
            } else {
                // should never get here, only reachable if there is no time period in the text
                millisseconds = 0;
                unit_of_time = false;
            }

            SpannableString ss = new SpannableString(step);


            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    CountDownDialog countDownDialog = CountDownDialog.newInstance(millisseconds, interval);
                    countDownDialog.show(getActivity().getSupportFragmentManager(), "fragment_countdownTimer");
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
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

        return view;
    }




}
