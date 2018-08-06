package me.ninabernick.cookingapplication.profile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.RecipeDetailViewActivity;
import me.ninabernick.cookingapplication.models.Recipe;

public class RecipeImageAdapter extends RecyclerView.Adapter<RecipeImageAdapter.ViewHolder> {



    ArrayList<Recipe> recipes = new ArrayList<>();
    Context context;

//    public interface FriendProfileListener {
//        void thumbnailClicked(ParseUser friend);
//    }

    public RecipeImageAdapter(ArrayList<Recipe> recipeList) {
        recipes = recipeList;



    }

    @NonNull
    @Override
    public RecipeImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View friendView = inflater.inflate(R.layout.item_recipe_image, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(friendView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeImageAdapter.ViewHolder viewHolder, int position) {
        Recipe recipe = recipes.get(position);
        Glide.with(context).load(recipe.getrecipeImage().getUrl()).apply(RequestOptions.bitmapTransform(new CropSquareTransformation())).into(viewHolder.ivRecipeThumb);


    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void addAll(List<Recipe> recipes) {
        recipes.addAll(recipes);
        notifyDataSetChanged();

    }

    public void clear() {
        recipes.clear();
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivRecipeThumb;


        public ViewHolder(View itemView) {
            super(itemView);
            ivRecipeThumb = (ImageView) itemView.findViewById(R.id.ivRecipeThumb);
            itemView.setOnClickListener(this);


        }

        public void onClick(View view) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                Recipe recipe = recipes.get(getAdapterPosition());
                Intent i = new Intent(context, RecipeDetailViewActivity.class);
                i.putExtra("recipe", recipe);
                context.startActivity(i);
            }
        }
    }
}