package me.ninabernick.cookingapplication.CreateRecipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.models.Recipe;

public class CreateActivity extends AppCompatActivity {

    public Recipe recipe_to_add;
    File photofile;
    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        recipe_to_add = new Recipe();



        final FragmentManager fragmentManager = getSupportFragmentManager();

        // actually creating the fragments
        fragment1 = new BasicInfoFragment();
        //fragment2 = new IngredientsFragment();
        fragment3 = new CreateStepsFragment();


        /*
         * This is the default fragment, the basic info fragment starts up on launch (because it
         * is the first fragment you navigate to) and all other transitions are handled within the
         * actual fragments as they call various methods to replace themselves and modify the
         * recipe variable which eventually gets submitted.
         */
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, fragment1).commit();




    }
}
