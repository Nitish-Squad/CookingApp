package me.ninabernick.cookingapplication.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.models.Recipe;

//adaptable to your profile or your friend's
public class ProfileFragment extends Fragment {


    ProfileListener listener;
    ParseUser user;

    private String profileImageUrl;
    private String name;
    private ArrayList<String> friends = new ArrayList<>();
    private ArrayList<String> savedRecipesText;
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
    private RecyclerView rvSavedRecipes;
    private ArrayList<Recipe> savedRecipes;

    private RecyclerView rvCreatedRecipes;
    private ArrayList<Recipe> createdRecipes;
    private RecipeImageAdapter savedRecipeAdapter;
    private RecipeImageAdapter createdRecipeAdapter;
    private ImageView savedLeftArrow;
    private ImageView savedRightArrow;
    private ImageView createdLeftArrow;
    private ImageView createdRightArrow;
    private LinearLayoutManager savedRecipeManager;
    private LinearLayoutManager createdRecipeManager;




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
        savedRecipes = new ArrayList<>();
        savedRecipeAdapter = new RecipeImageAdapter(savedRecipes);
        createdRecipes = new ArrayList<>();
        createdRecipeAdapter = new RecipeImageAdapter(createdRecipes);

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
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        savedLeftArrow = (ImageView) view.findViewById(R.id.ivSavedLeftArrow);
        savedRightArrow = (ImageView) view.findViewById(R.id.ivSavedRightArrow);
        createdLeftArrow = (ImageView) view.findViewById(R.id.ivCreatedLeftArrow);
        createdRightArrow = (ImageView) view.findViewById(R.id.ivCreatedRightArrow);
        savedLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savedRecipeManager.findFirstVisibleItemPosition() > 0) {
                    rvSavedRecipes.smoothScrollToPosition(savedRecipeManager.findFirstVisibleItemPosition() - 1);
                } else {
                    rvSavedRecipes.smoothScrollToPosition(0);
                }

            }
        });

        savedRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvSavedRecipes.smoothScrollToPosition(savedRecipeManager.findLastVisibleItemPosition() + 1);
            }
        });

        createdLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (createdRecipeManager.findFirstVisibleItemPosition() > 0) {
                    rvCreatedRecipes.smoothScrollToPosition(createdRecipeManager.findFirstVisibleItemPosition() - 1);
                } else {
                    rvCreatedRecipes.smoothScrollToPosition(0);
                }

            }
        });

        createdRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvCreatedRecipes.smoothScrollToPosition(createdRecipeManager.findLastVisibleItemPosition() + 1);
            }
        });


        savedRecipesText = new ArrayList<>();
        if (user.<String>getList("savedRecipes") != null) {
            savedRecipesText.addAll(user.<String>getList("savedRecipes"));
        }

        name = user.getString("name");

        tvName.setText(name);

        profileImageUrl = "https://graph.facebook.com/" + user.getString("fbId") + "/picture?type=large";
        Glide.with(view)
                .load(profileImageUrl)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(ivProfileImage);

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

        rvSavedRecipes = (RecyclerView) view.findViewById(R.id.rvSavedRecipes);
        rvSavedRecipes.setAdapter(savedRecipeAdapter);
        savedRecipeManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvSavedRecipes.setLayoutManager(savedRecipeManager);

//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(rvSavedRecipes);
        rvSavedRecipes.setItemAnimator(new SlideInRightAnimator());
        getSavedRecipes();
        rvCreatedRecipes = (RecyclerView) view.findViewById(R.id.rvCreatedRecipes);
        rvCreatedRecipes.setAdapter(createdRecipeAdapter);
        createdRecipeManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvCreatedRecipes.setLayoutManager(createdRecipeManager);
        //snapHelper.attachToRecyclerView(rvCreatedRecipes);
        getRecipesCreated();
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

    public void getSavedRecipes() {

        if (user.getList("savedRecipes") != null) {
            final ArrayList<String> mySavedRecipes = new ArrayList<>();
            mySavedRecipes.addAll(user.<String>getList("savedRecipes"));

            ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
            query.whereContainedIn("objectId", mySavedRecipes);
            query.findInBackground(new FindCallback<Recipe>() {
                @Override
                public void done(List<Recipe> objects, ParseException e) {
                    Log.d("Saved Recipes", String.format("%d",objects.size()));
                    savedRecipes.clear();
                    savedRecipes.addAll(objects);
                    savedRecipeAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void getRecipesCreated() {

        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.whereEqualTo("createdBy", user.getObjectId());
        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                createdRecipes.clear();
                createdRecipes.addAll(objects);
                createdRecipeAdapter.notifyDataSetChanged();

            }
        });

    }
}
