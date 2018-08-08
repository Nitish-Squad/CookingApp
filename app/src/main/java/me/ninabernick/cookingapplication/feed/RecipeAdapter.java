package me.ninabernick.cookingapplication.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.RecipeDetailViewActivity;
import me.ninabernick.cookingapplication.models.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>{
    Context context;
    private ArrayList<Recipe> recipes;
//    RecipeListener recipeListener;

    public interface RecipeListener {
        void respond(Recipe recipe, View view);
    }

    public RecipeAdapter(ArrayList<Recipe> recipeList) {
        recipes = new ArrayList<>();
        recipes.addAll(recipeList);
//        recipeListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View recipeView = inflater.inflate(R.layout.item_recipe, parent, false);
        ViewHolder viewHolder = new ViewHolder(recipeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.tvTitle.setText(recipe.getTitle());
        holder.tvDescription.setText(recipe.getDescription());
        holder.tvTime.setText(recipe.getTime());
        holder.rating.setRating(recipe.getAverageRating().floatValue());
        Glide.with(context)
                .load(recipe.getrecipeImage().getUrl())
                .apply(new RequestOptions().transforms(new CropSquareTransformation()))
                .into(holder.ivThumbnail);

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivThumbnail;
        public TextView tvTitle;
        public TextView tvTime;
        public TextView tvDescription;
        public RatingBar rating;
        private final Context context;

        RecipeListener listener;


        public ViewHolder(View itemView) {
            super(itemView);
//            listener = rListener;
            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivImageThumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            rating = (RatingBar) itemView.findViewById(R.id.ratingBarFeed);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                Intent i = new Intent(view.getContext(), RecipeDetailViewActivity.class);
                i.putExtra("recipe", recipes.get(getAdapterPosition()));
                Pair<View, String> p1 = Pair.create((View)ivThumbnail, context.getResources().getString(R.string.TRANS_RECIPEIMAGE));
                //Pair<View, String> p3 = Pair.create((View)tvTitle, context.getResources().getString(R.string.TRANS_TITLE));
                //Pair<View, String> p4 = Pair.create((View)tvDescription, context.getResources().getString(R.string.TRANS_DESCRIP));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity)context, p1);
                context.startActivity(i, options.toBundle());
            }

        }
    }
}

