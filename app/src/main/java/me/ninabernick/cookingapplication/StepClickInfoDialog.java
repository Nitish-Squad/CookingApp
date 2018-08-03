package me.ninabernick.cookingapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StepClickInfoDialog extends DialogFragment {

    private TextView tvIngredient;
    private TextView tvQuantity;

    public static StepClickInfoDialog newInstance(String ingredient, String quantity) {

        Bundle args = new Bundle();
        args.putString("ingredient/term", ingredient);
        args.putString("quantity/definition", quantity);
        StepClickInfoDialog fragment = new StepClickInfoDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_ingredient_quantity, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(true);
        tvIngredient = (TextView) view.findViewById(R.id.tvIngredient);
        String firstElt = getArguments().getString("ingredient/term") + ": ";
        tvIngredient.setText(firstElt);
        tvQuantity = (TextView) view.findViewById(R.id.tvIngredientQuantity);
        tvQuantity.setText(getArguments().getString("quantity/definition"));

    }
}
