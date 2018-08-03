package me.ninabernick.cookingapplication.ShareRecipe;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
