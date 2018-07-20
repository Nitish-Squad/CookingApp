package me.ninabernick.cookingapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.ninabernick.cookingapplication.models.Recipe;

public class IngredientsFragment extends Fragment {

    Button btnAddIngredient;
    Button btnNext;
    LinearLayout ingredients;
    TextView tvTitle;
    EditText etIngredient1;
    ArrayList<EditText> ingredients_array;

    private OnFragmentInteractionListener mListener;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ingredients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAddIngredient = (Button) view.findViewById(R.id.btnAdd);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        ingredients = (LinearLayout) view.findViewById(R.id.steps);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        etIngredient1 = (EditText) view.findViewById(R.id.etIngredient1);

        ingredients_array = new ArrayList<>();

        ingredients_array.add(etIngredient1);

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logic for adding edit text boxes
                EditText temp = new EditText(getContext());

                temp.setHint("Ingredient");

                ingredients.addView(temp);
                ingredients_array.add(temp);
            }
        });



        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logic to save ingredients in the array

                List<String> ingredients = new ArrayList<String>();
                for(int i = 0; i < ingredients_array.size(); i++){
                    ingredients.add((ingredients_array.get(i)).getText().toString());
                }

                HomeActivity createActivity = (HomeActivity) getActivity();

                Recipe new_recipe = createActivity.recipe_to_add;

                new_recipe.setIngredients(ingredients);

                createActivity.recipe_to_add = new_recipe;


                // logic for starting the steps fragment
                CreateStepsFragment fragment2 = new CreateStepsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flFragmentContainer, fragment2);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
