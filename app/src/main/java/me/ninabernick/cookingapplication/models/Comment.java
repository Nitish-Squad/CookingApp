package me.ninabernick.cookingapplication.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject{
    private static final String TEXT_KEY = "text";
    private static final String USER_KEY = "user";
    private static final String RECIPE_KEY = "recipe";

    public Comment() {
        super();
    }

    public String getText() {
        return getString(TEXT_KEY);
    }

    public void setText(String text) {
        put(TEXT_KEY, text);
    }

    public ParseUser getUser() {
        return getParseUser(USER_KEY);
    }

    public void setUser(ParseUser user) {
        put(USER_KEY, user);
    }

    public ParseObject getRecipe() {
        return getParseObject(RECIPE_KEY);
    }

    public void setRecipe(Recipe recipe) {
        put(RECIPE_KEY, recipe);
    }

}
