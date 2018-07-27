package me.ninabernick.cookingapplication;

import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import me.ninabernick.cookingapplication.models.Recipe;

public class IngredientsFragment extends Fragment {

    Button btnAddIngredient;
    Button btnNext;
    LinearLayout ingredients;
    TextView tvTitle;
    AutoCompleteTextView etIngredient1;
    EditText etQuantity1;
    AutoCompleteTextView acUnit1;
    ArrayList<AutoCompleteTextView> ingredients_array;
    ArrayList<EditText> ingredients_quantity_array;
    ArrayList<AutoCompleteTextView> ingredient_unit_array;
    String ingredients_list;

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

        // setup first ingredient data fields
        etIngredient1 = (AutoCompleteTextView) view.findViewById(R.id.Ingredients1);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.ingredients));
        etIngredient1.setThreshold(3);
        etIngredient1.setAdapter(adapter);

        etQuantity1 = (EditText) view.findViewById(R.id.etQuantity);

        acUnit1 = (AutoCompleteTextView) view.findViewById(R.id.acUnit);
        final ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.units));
        acUnit1.setAdapter(unitAdapter);
        ingredients_array = new ArrayList<>();
        ingredients_quantity_array = new ArrayList<>();
        ingredient_unit_array = new ArrayList<>();

        ingredients_array.add(etIngredient1);
        ingredients_quantity_array.add(etQuantity1);
        ingredient_unit_array.add(acUnit1);

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout indivIngredient = new LinearLayout(getContext());
                indivIngredient.setOrientation(LinearLayout.HORIZONTAL);



                // logic for adding edit text boxes
                AutoCompleteTextView temp = new AutoCompleteTextView(getContext());
                temp.setThreshold(3);
                temp.setAdapter(adapter);

                temp.setHint(" Ingredient ");

                indivIngredient.addView(temp);
                ingredients_array.add(temp);
                // add edit text quantity
                EditText quantity = new EditText(getContext());
                quantity.setHint(" Quantity ");
                indivIngredient.addView(quantity);
                ingredients_quantity_array.add(quantity);
                // add spinner for unit
                AutoCompleteTextView unit = new AutoCompleteTextView(getContext());
                unit.setAdapter(unitAdapter);
                unit.setHint(" Unit ");
                indivIngredient.addView(unit);
                ingredient_unit_array.add(unit);

                // add horizontal layout to vertical layout
                ingredients.addView(indivIngredient);


            }
        });



        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // logic to save ingredients in the array
                ArrayList<String> textIngredients = new ArrayList<>();
                List<String> ingredients = new ArrayList<String>();
                for(int i = 0; i < ingredients_array.size(); i++){
                    // add to text-only ingredient list used for querying
                    textIngredients.add(ingredients_array.get(i).getText().toString().toLowerCase());
                    JSONObject json = new JSONObject();
                    try {
                        // standardize all ingredients to lowercase for querying
                        json.put("name", ingredients_array.get(i).getText().toString().toLowerCase());
                    } catch (JSONException e) {
                        Log.d("Ingredient Creation", "failed to set ingredient name");
                    }
                    try {
                        json.put("quantity", ingredients_quantity_array.get(i).getText().toString());
                    } catch (JSONException e) {
                        Log.d("Ingredient Creation", "failed to set ingredient quantity");
                    }
                    try {
                        json.put("unit", ingredient_unit_array.get(i).getText().toString());
                    } catch (JSONException e) {
                        Log.d("Ingredient Creation", "failed to set ingredient unit");
                    }

                    String ingredient = json.toString();
                    ingredients.add(ingredient);
                }

                HomeActivity createActivity = (HomeActivity) getActivity();

                Recipe new_recipe = createActivity.recipe_to_add;

                new_recipe.setIngredients(ingredients);
                new_recipe.setTextIngredients(textIngredients);

                createActivity.recipe_to_add = new_recipe;


                // logic for starting the steps fragment
                CreateStepsFragment fragment2 = new CreateStepsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flFragmentContainer, fragment2);
                fragmentTransaction.addToBackStack(null);
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
