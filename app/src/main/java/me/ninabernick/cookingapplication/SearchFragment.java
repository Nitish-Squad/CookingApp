package me.ninabernick.cookingapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import me.ninabernick.cookingapplication.feed.RecipeAdapter;
import me.ninabernick.cookingapplication.models.Recipe;

public class SearchFragment extends Fragment {


    private FragmentActivity mListener;
    private EditText etSearch;
    private RecyclerView rvTrending;
    ArrayList<Recipe> filteredRecipes;
    ArrayList<Recipe> recipes;
    private RecipeAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipes = new ArrayList<>();
        adapter = new RecipeAdapter(recipes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvTrending = (RecyclerView) view.findViewById(R.id.rvTrendingRecipes);
        rvTrending.setAdapter(adapter);
        rvTrending.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        getRecipes();
        etSearch = (EditText) view.findViewById(R.id.etSearchRecipes);
        // implementation of search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filteredRecipes = new ArrayList<>();
                ArrayList<Recipe> temp = recipes;

                String search = editable.toString();
                if (search != "") {
                    for (int i = 0; i < recipes.size(); i++) {
                        Log.d("text ingredients", recipes.get(i).getTextIngredients().toString());
                        if (recipes.get(i).getTitle().contains(search) || recipes.get(i).getDescription().contains(search)) {
                            filteredRecipes.add(recipes.get(i));
                        }
                        // have to test indiv. ingredients in arraylist for contains
                        for (String s : recipes.get(i).getTextIngredients()) {
                            if (s.contains(search) && !filteredRecipes.contains(recipes.get(i))) {
                                filteredRecipes.add(recipes.get(i));
                            }
                        }
                    }
                    adapter.clear();
                    adapter.addAll(filteredRecipes);
                }
                else {
                    adapter.clear();
                    adapter.addAll(recipes);
                }



            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.mListener = (FragmentActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void getRecipes(){
        final Recipe.Query recipeQuery = new Recipe.Query();
        // make sure no duplicates
        recipes.clear();
        adapter.clear();
        recipeQuery.getTop();
        recipeQuery.orderByDescending("averageRating");
        recipeQuery.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e == null)
                {

                    for (int i = 0; i < objects.size(); i ++){
                        Recipe recipe = objects.get(i);
                        recipes.add(recipe);
                    }


                    adapter.clear();
                    adapter.addAll(recipes);
                    adapter.notifyDataSetChanged();

                }
                else{

                    e.printStackTrace();
                }


            }
        });


    }


}
