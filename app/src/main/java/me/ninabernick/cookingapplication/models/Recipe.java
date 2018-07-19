package me.ninabernick.cookingapplication.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@ParseClassName("Recipe")
public class Recipe extends ParseObject {

    public Recipe() {
        super();
    }

    // simple get methods
    public String getTitle(){
        return getString("title");
    }
    public String getDescription(){
        return getString("description");
    }
    public String getTime(){
        return getString("time");
    }
    public ParseFile getrecipeImage(){
        return getParseFile("recipeImage");
    }
    public List<String> getIngredients(){
        return getList("ingredients");
    }
    public ParseUser getCreatedBy() {
        return getParseUser("createdBy");
    }

    /*
     * NOTE: The way that steps are stored here is as a list of strings, each of these strings
     * is a string that was created from a JSONObject. Each of these strings can be used to
     * initialize a new JSONObject and then in turn access all of the specified values.
     * These values are "text", "icon", and "time" each returns the specified data for a step.
     */
    public List<String> getSteps(){
        return getList("steps");
    }

    public List<Integer> getRatings(){
        return getList("ratings");
    }

    public List<String> getComments(){
        return getList("comments");
    }

    /*
     * More complicated get methods, used for accessing specific pieces of data without having to
     * sort through any of the JSONObject data for the steps.
     */

    public Integer getNumberofSteps(){
        return getSteps().size();
    }

    // methods for accessing values of a specific step in a recipe

    public String getStepText(Integer stepNumber){
        List<String> steps = getSteps();

        String text = null;

        try {
            JSONObject json = new JSONObject(steps.get(stepNumber));

            text = json.getString("text");

        } catch (JSONException e4) {
            Log.d("Recipe Method", "Failed to get the JSONObject from steps");
            e4.printStackTrace();
        }

        return text;
    }

    public String getStepImageURL(Integer stepNumber){
        List<String> steps = getSteps();

        String icon = null;

        try {
            JSONObject json = new JSONObject(steps.get(stepNumber));

            icon = json.getString("icon");

        } catch (JSONException e4) {
            Log.d("Recipe Method", "Failed to get the JSONObject from steps");
            e4.printStackTrace();
        }

        return icon;
    }

    public String getStepTime(Integer stepNumber){
        List<String> steps = getSteps();

        String time = null;

        try {
            JSONObject json = new JSONObject(steps.get(stepNumber));

            time = json.getString("time");

        } catch (JSONException e4) {
            Log.d("Recipe Method", "Failed to get the JSONObject from steps");
            e4.printStackTrace();
        }

        return time;
    }





    // set methods, used for adding recipes
    public void setTitle(String title){
        put("title", title);
    }

    public void setDescription(String description){
        put("description",description);
    }

    public void setTime(String time){
        put ("time", time);
    }

    public void setCreatedBy(ParseUser user) {
        put("createdBy", user);
    }

    public void setrecipImage(ParseFile file){
        put("recipeImage", file);
    }

    public void setSteps(List<String> steps){
        put("steps", steps);
    }

    public static class Query extends ParseQuery<Recipe> {
        public Query(){
            super(Recipe.class);
        }

        public Query getTop()
        {
            setLimit(20);
            return this;
        }

    }
}
