package me.ninabernick.cookingapplication.CreateRecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import me.ninabernick.cookingapplication.R;

public class CustomAdapter extends BaseAdapter {
    Context context;
    int icons[];
    LayoutInflater inflater;

    public CustomAdapter(Context applicationContext, int[] icons) {
        this.context = applicationContext;
        this.icons = icons;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.step_icon_item, null);
        ImageView icon = (ImageView) view.findViewById(R.id.ivStepIcon);
        icon.setImageResource(icons[i]);
        return view;
    }
}