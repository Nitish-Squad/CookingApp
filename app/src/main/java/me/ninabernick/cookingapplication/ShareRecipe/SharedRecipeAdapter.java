package me.ninabernick.cookingapplication.ShareRecipe;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;

import java.util.List;

import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.RecipeDetailViewActivity;
import me.ninabernick.cookingapplication.models.Recipe;
import me.ninabernick.cookingapplication.models.SharedRecipe;

public class SharedRecipeAdapter extends RecyclerView.Adapter<SharedRecipeAdapter.ViewHolder> {

    private List<SharedRecipe> shared_recipes;

    Context context;

    public SharedRecipeAdapter(List<SharedRecipe> sharedrecipes){
        shared_recipes = sharedrecipes;
    }

    @NonNull
    @Override
    public SharedRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View shared_recipe_view = inflater.inflate(R.layout.item_shared_recipe, parent, false);

        ViewHolder viewHolder = new ViewHolder(shared_recipe_view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SharedRecipeAdapter.ViewHolder viewHolder, int i) {
        SharedRecipe sharedRecipe = shared_recipes.get(i);


        ParseUser sharinguser = sharedRecipe.getSharingUser();
        Recipe recipe = sharedRecipe.getRecipe();
        viewHolder.tvTitle.setText(recipe.getTitle());
        viewHolder.tvSharingUser.setText((sharinguser.getString("name") + " shared:"));
        viewHolder.tvDescription.setText(recipe.getDescription());
        viewHolder.tvTime.setText(recipe.getTime());
        viewHolder.ratingBarFeed.setRating(recipe.getAverageRating().floatValue());
        Glide.with(context)
                .load(recipe.getrecipeImage().getUrl())
                .into(viewHolder.ivImageThumbnail);

    }

    @Override
    public int getItemCount() {
        return shared_recipes.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvSharingUser;
        private ImageView ivImageThumbnail;
        private TextView tvTitle;
        private TextView tvDescription;
        private RatingBar ratingBarFeed;
        private TextView tvTime;


        public ViewHolder(View itemView) {
            super(itemView);

            tvSharingUser = (TextView) itemView.findViewById(R.id.tvSharingUser);
            ivImageThumbnail = (ImageView) itemView.findViewById(R.id.ivImageThumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            ratingBarFeed = (RatingBar) itemView.findViewById(R.id.ratingBarFeed);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(this);


        }

        public void onClick(View view) {
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                SharedRecipe sharedRecipe = shared_recipes.get(position);

                // open up a new RecipeDetailsActivity
                Intent intent = new Intent(view.getContext(), RecipeDetailViewActivity.class);
                intent.putExtra("recipe", sharedRecipe.getRecipe());
                view.getContext().startActivity(intent);

            }
        }
    }
}
