package me.ninabernick.cookingapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class CountDownDialog extends DialogFragment {

    private TextView counter;
    private CircularProgressBar progress;

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
        progress = (CircularProgressBar) view.findViewById(R.id.determinateBar);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        final int millis = getArguments().getInt("millis");
        int interval = getArguments().getInt("interval");
        progress.setProgressMax(millis / 1000);
        CountDownTimer gameTimer = new CountDownTimer(millis, interval) {
            @Override
            public void onTick(long l) {
                int total_seconds = ((int)Math.round(l/1000.0)-1);

                int numberOfHours = (total_seconds % 86400 ) / 3600 ;
                int numberOfMinutes = ((total_seconds % 86400 ) % 3600 ) / 60;
                int numberOfSeconds = ((total_seconds % 86400 ) % 3600 ) % 60;

                String hours;
                String minutes;
                String seconds;

                if (numberOfHours == 0){
                    hours = "";
                }
                else{
                    hours = Integer.toString(numberOfHours) + ":";
                    if (hours.length() == 2) {
                        hours = "0" + hours;
                    }
                }

                if (numberOfMinutes == 0){
                    minutes = "";
                }
                else{
                    minutes = Integer.toString(numberOfMinutes) + ":";
                    if (minutes.length() == 2) {
                        minutes = "0" + minutes;
                    }
                }

                if ((numberOfHours != 0) || (numberOfMinutes != 0)){
                    seconds = Integer.toString(numberOfSeconds);
                    if (seconds.length() == 1){
                        seconds = "0" + seconds;
                    }
                }
                else{
                    seconds = Integer.toString(numberOfSeconds);
                }



                counter.setText(hours + minutes + seconds);


                double float_test= (((double) millis) - ((double) l))/(millis);

                int time_elapsed = (millis / 1000) - total_seconds;
                /*
                 * Duration here is set for 1.25 seconds, specifically because each tick is slightly
                 * longer than a second so we want the animation to last longer than each tick so the
                 * animation is always moving or barely stopped.
                 */
                progress.setProgressWithAnimation(time_elapsed, 1250);
            }

            @Override
            public void onFinish() {
                progress.setProgress(millis / 1000);
                counter.setText("Time's Up!\n Click Anywhere to Exit");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        CharSequence name = getString(R.string.channel_name);
                        String description = getString(R.string.channel_description);
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel channel = new NotificationChannel("cookingappid", name, importance);
                        channel.setDescription(description);
                        // Register the channel with the system; you can't change the importance
                        // or other notification behaviors after this
                        NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);
                    }
                }

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), "cookingappid")
                        .setSmallIcon(R.drawable.chicken_icon)
                        .setContentTitle("Timer Complete")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Step timer complete, ready to move onto the next step!"))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setDefaults(Notification.DEFAULT_VIBRATE);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(70987, mBuilder.build());


                // dismiss();
            }
        };
        gameTimer.start();
    }

}