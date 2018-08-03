package me.ninabernick.cookingapplication.models;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("SharedRecipe")
public class SharedRecipe extends ParseObject {

    public ParseUser getSharedUser(){return getParseUser("sharedUser"); }
    public void setSharedUser(ParseUser user){ put ("sharedUser", user); }

    public ParseUser getSharingUser(){return getParseUser("sharingUser"); }
    public void setSharingUser(ParseUser user){ put ("sharingUser", user); }

    // currently no way to grab the recipe, will look into
    public Recipe getRecipe(){return ((Recipe) getParseObject("recipeShared")); }
    public void setRecipe(Recipe recipe){ put("recipeShared", recipe); }


    public static class Query extends ParseQuery<SharedRecipe> {
        public Query(){
            super(SharedRecipe.class);
        }

        public SharedRecipe.Query getTop()
        {
            setLimit(20);
            return this;
        }

    }
}
