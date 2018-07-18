package me.ninabernick.cookingapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

        TextView tvStep = (TextView) view.findViewById(R.id.tvInstruct);
        tvStep.setText(step);

        return view;
    }




}
