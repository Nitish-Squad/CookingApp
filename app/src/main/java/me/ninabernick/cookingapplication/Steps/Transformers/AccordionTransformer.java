package me.ninabernick.cookingapplication.Steps.Transformers;

import android.view.View;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;

public class AccordionTransformer extends ABaseTransformer {

    @Override
    protected void onTransform(View view, float position) {
        view.setPivotX(position < 0 ? 0 : view.getWidth());
        view.setScaleX(position < 0 ? 1f + position : 1f - position);
    }

}