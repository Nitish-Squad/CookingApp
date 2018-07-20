package me.ninabernick.cookingapplication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.ninabernick.cookingapplication.models.Recipe;

//adaptable to your profile or your friend's
public class ProfileFragment extends Fragment {


    ProfileListener listener;
    ParseUser user;

    private String profileImageUrl;
    private String name;
    private ArrayList<String> friends = new ArrayList<>();
    private ArrayList<String> savedRecipes;
    private ArrayList<ParseUser> friendUsers = new ArrayList<>();
    //needed for recycler view of friends

    FriendImageAdapter adapter;
    FriendImageAdapter.FriendProfileListener friendListener = new FriendImageAdapter.FriendProfileListener() {
        @Override
        public void thumbnailClicked(ParseUser friend) {
            ProfileFragment friendProfile = ProfileFragment.newInstance(friend);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.flFragmentContainer, friendProfile).commit();
        }
    };



    //private ArrayList<String> createdRecipes;

    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvSavedRecipes;
    private TextView tvCreatedRecipes;
    private RecyclerView rvFriends;



    public interface ProfileListener {
        void savedRecipesClicked(ParseUser user);
        void createdRecipesClicked(ParseUser user);
    }



    public static ProfileFragment newInstance(ParseUser user) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);

        profileFragment.setArguments(args);
        return profileFragment;
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
        user = getArguments().getParcelable("user");
        friends.clear();
        friends.addAll(user.<String>getList("friends"));

        adapter = new FriendImageAdapter(friendUsers, friendListener);







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
        rvFriends = (RecyclerView) view.findViewById(R.id.rvFriends);
        rvFriends.setAdapter(adapter);
        rvFriends.setLayoutManager(new GridLayoutManager(view.getContext(),3 ));
        Log.d("friends", friends.toString());
        findFriends();




        savedRecipes = new ArrayList<>();
        if (user.<String>getList("savedRecipes") != null) {
            savedRecipes.addAll(user.<String>getList("savedRecipes"));
        }

        name = user.getString("name");
        tvName.setText(name);

        profileImageUrl = user.getString("profileImageURL");
        Glide.with(view).load(profileImageUrl).into(ivProfileImage);
        tvSavedRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.savedRecipesClicked(user);
                Log.d("saved recipes button", "saved recipes clicked");
            }
        });
        tvCreatedRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.createdRecipesClicked(user);
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

    public void findFriends() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("name", friends);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    friendUsers.clear();
                    friendUsers.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("friends", "query failed");
                }
            }
        });
    }
}
