package me.ninabernick.cookingapplication.feed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.models.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>{
    Context context;
    private ArrayList<Recipe> recipes;
    RecipeListener recipeListener;

    public interface RecipeListener {
        void respond(Recipe recipe);
    }

    public RecipeAdapter(ArrayList<Recipe> recipeList, RecipeListener listener) {
        recipes = new ArrayList<>();
        recipes.addAll(recipeList);
        recipeListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View recipeView = inflater.inflate(R.layout.item_recipe, parent, false);
        ViewHolder viewHolder = new ViewHolder(recipeView, recipeListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.tvTitle.setText(recipe.getTitle());
        holder.tvDescription.setText(recipe.getDescription());
        //holder.tvTime.setText(recipe.getTime());
        Glide.with(context).load(recipe.getrecipeImage().getUrl()).into(holder.ivThumbnail);

    }

    public int getItemCount() {
        return recipes.size();
    }

    public void clear() {
        recipes.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Recipe> newRecipes) {
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    public void add(Recipe recipe) {
        recipes.add(recipe);
        notifyDataSetChanged();
    }

    public void filter(String search) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivThumbnail;
        public TextView tvTitle;
        //public TextView tvTime;
        public TextView tvDescription;
        RecipeListener listener;


        public ViewHolder(View itemView, RecipeListener rListener) {
            super(itemView);
            listener = rListener;
            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivImageThumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            //tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.respond(recipes.get(getAdapterPosition()));
        }
    }
}

