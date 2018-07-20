package me.ninabernick.cookingapplication.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("User")
public class User extends ParseUser{

    public User() {
        super();
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name){
        put("name", name);
    }

    public List<String> getFriends() {
        return getList("friends");
    }

    public void addFriend(String name) {
        add("friends", name);
    }

    public ParseFile getProfileImage() {
        return getParseFile("profileImage");
    }

    public void setProfileImage(ParseFile image) {
        put("profileImage", image);
    }
}
