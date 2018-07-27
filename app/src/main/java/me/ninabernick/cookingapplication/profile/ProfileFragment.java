package me.ninabernick.cookingapplication.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import me.ninabernick.cookingapplication.R;

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
            transaction.replace(R.id.flFragmentContainer, friendProfile);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    };


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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (ProfileListener) context;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable("user");
        friends.clear();
        friends.addAll(user.<String>getList("friends"));
        Log.d("onCreate", "onCreate called");
        findFriends();
        adapter = new FriendImageAdapter(friendUsers, friendListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

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
            }
        });
        tvCreatedRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.createdRecipesClicked(user);
            }
        });
    }


    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }


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
                    adapter.clear();
                    adapter.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("friends", "query failed");
                }
            }
        });
    }
}
