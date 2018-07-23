package me.ninabernick.cookingapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CountDownDialog extends DialogFragment {

    private TextView counter;
    private ProgressBar progress;

    public CountDownDialog(){}

    public static CountDownDialog newInstance(int millisseconds, int interval) {
        CountDownDialog f = new CountDownDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("millis", millisseconds);
        args.putInt("interval", interval);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countdowntimer_dialog, container);
        counter = (TextView) view.findViewById(R.id.tvCount);
        progress = (ProgressBar) view.findViewById(R.id.determinateBar);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final int millis = getArguments().getInt("millis");
        int interval = getArguments().getInt("interval");
        CountDownTimer gameTimer = new CountDownTimer(millis, interval) {
            @Override
            public void onTick(long l) {
                int total_seconds = ((int)Math.round(l/1000.0)-1);

                int numberOfHours = (total_seconds % 86400 ) / 3600 ;
                int numberOfMinutes = ((total_seconds % 86400 ) % 3600 ) / 60;
                int numberOfSeconds = ((total_seconds % 86400 ) % 3600 ) % 60;

                counter.setText("" + numberOfHours+ ":" + numberOfMinutes + ":" + numberOfSeconds);

                // provides total number of seconds (ex: 3600 for an hour of time)
                // counter.setText(""+((int)Math.round(l/1000.0)-1));

                double float_test= (((double) millis) - ((double) l))/(millis);

                int progress_count = ((int)Math.round(float_test * 100)) + 1;
                progress.setProgress(progress_count);
            }

            @Override
            public void onFinish() {
                dismiss();
            }
        };
        gameTimer.start();
    }
}