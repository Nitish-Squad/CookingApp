package me.ninabernick.cookingapplication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;

import java.util.ArrayList;

import me.ninabernick.cookingapplication.models.Recipe;


public class ProfileFragment extends Fragment {


    ProfileListener listener;
    ParseUser user;

    private String profileImageUrl;
    private String name;
    private ArrayList<String> friends;
    private ArrayList<String> savedRecipes;

    private ArrayList<String> createdRecipes;

    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvSavedRecipes;
    private TextView tvCreatedRecipes;

    public interface ProfileListener {
        void savedRecipesClicked();
        void createdRecipesClicked();
    }


    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (ProfileListener) context;
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ParseUser.getCurrentUser();






    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvSavedRecipes = (TextView) view.findViewById(R.id.tvSavedRecipes);
        tvCreatedRecipes = (TextView) view.findViewById(R.id.tvCreatedRecipes);
        name = user.getString("name");
        friends = new ArrayList<>();
        if (user.<String>getList("friends") != null) {
            friends.addAll(user.<String>getList("friends"));
        }

        savedRecipes = new ArrayList<>();
        if (user.<String>getList("savedRecipes") != null) {
            savedRecipes.addAll(user.<String>getList("savedRecipes"));
        }

        profileImageUrl = user.getString("profileImageURL");

        // TODO- query for recipes where createdBy = user id

        tvName.setText(name);
        Glide.with(view).load(profileImageUrl).into(ivProfileImage);
        tvSavedRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.savedRecipesClicked();
            }
        });







    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }



    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
