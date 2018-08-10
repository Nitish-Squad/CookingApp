package me.ninabernick.cookingapplication.YoutubeDialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import me.ninabernick.cookingapplication.R;

public class YoutubePopUp extends DialogFragment implements YouTubePlayer.Provider{

    private YouTubePlayerView youtube_player;

    public YoutubePopUp(){};

    public static YoutubePopUp newInstance(String youtube_url){
        YoutubePopUp f = new YoutubePopUp();

        Bundle args = new Bundle();
        args.putString("youtube_url", youtube_url);
        f.setArguments(args);

        return f;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.youtube_dialog, container);
        youtube_player = (YouTubePlayerView) view.findViewById(R.id.player);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final String videoId = getArguments().getString("youtube_url");

        youtube_player.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.cueVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                // do nothing for now
            }
        });
    }

    @Override
    public void initialize(String s, YouTubePlayer.OnInitializedListener onInitializedListener) {

    }
}
