package me.ninabernick.cookingapplication.ShareRecipe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.RecipeDetailViewActivity;
import me.ninabernick.cookingapplication.models.Recipe;
import me.ninabernick.cookingapplication.models.SharedRecipe;
import me.ninabernick.cookingapplication.profile.FriendImageAdapter;

public class ShareRecipeDialog extends DialogFragment {

    private static final int SHARE_TO_FACEBOOK = 3201;

    private ParseUser user;
    private RecyclerView recycler_friends;
    private ImageButton fb_share;
    private ArrayList<String> friends = new ArrayList<>();
    private ArrayList<String> savedRecipes;
    private ArrayList<ParseUser> friendUsers = new ArrayList<>();
    private FriendImageAdapter adapter;

    FriendImageAdapter.FriendProfileListener friendListener = new FriendImageAdapter.FriendProfileListener() {
        @Override
        public void thumbnailClicked(ParseUser friend) {
            SharedRecipe recipe_to_be_shared = new SharedRecipe();
            recipe_to_be_shared.setSharedUser(friend);
            recipe_to_be_shared.setSharingUser(ParseUser.getCurrentUser());
            RecipeDetailViewActivity recipe_details = (RecipeDetailViewActivity) getActivity();
            recipe_to_be_shared.setRecipe(recipe_details.getRecipeShown());

            recipe_to_be_shared.saveInBackground();

            dismiss();
            Toast.makeText(getContext(), "Recipe Shared with " + friend.getString("name"), Toast.LENGTH_SHORT).show();
        }
    };

    public ShareRecipeDialog(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_share_dialog, container);

        user = ParseUser.getCurrentUser();

        recycler_friends = (RecyclerView) view.findViewById(R.id.RecyclerView);
        fb_share = (ImageButton) view.findViewById(R.id.fb_share);
        friends.clear();
        friends.addAll(user.<String>getList("friends"));


        findFriends();
        adapter = new FriendImageAdapter(friendUsers, friendListener);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recycler_friends.setAdapter(adapter);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recycler_friends.setLayoutManager(layoutManager);

        fb_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RecipeDetailViewActivity recipeDetails = (RecipeDetailViewActivity) getActivity();

                Recipe recipe = recipeDetails.getRecipeShown();

                ParseFile parseFile = recipe.getParseFile("recipeImage");
                byte[] parse_data = new byte[0];
                try {
                    parse_data = parseFile.getData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Bitmap image = BitmapFactory.decodeByteArray(parse_data, 0, parse_data.length);
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(image)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                ShareDialog.show(getActivity(), content);

                dismiss();
            }
        });


    }



    // copied from profile fragment since retrieving friends is necessary here as well
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
