package me.ninabernick.cookingapplication.ShareRecipe;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.models.SharedRecipe;

public class NotifyShareDialog extends DialogFragment {
    private ParseUser user;
    private RecyclerView rvSharedRecipes;
    private SharedRecipeAdapter adapter;
    private List<SharedRecipe> retrievedsharedrecipes = new ArrayList<>();



    public NotifyShareDialog(){}

    public static NotifyShareDialog newInstance(List<SharedRecipe> retrieved_shared_recipes) {
        NotifyShareDialog f = new NotifyShareDialog();

        f.retrievedsharedrecipes = retrieved_shared_recipes;

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_share_recipe_dialog, container);
        // Not exactly sure why, but this code is required to get the dialog to have rounded corners
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        user = ParseUser.getCurrentUser();

        rvSharedRecipes = (RecyclerView) view.findViewById(R.id.rvSharedRecipes);



        adapter = new SharedRecipeAdapter(retrievedsharedrecipes);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rvSharedRecipes.setAdapter(adapter);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvSharedRecipes.setLayoutManager(layoutManager);


    }
}
