package com.hzstore.mapproject;

import android.content.SharedPreferences;

import com.hzstore.mapproject.models.AccessToken;
import com.hzstore.mapproject.models.User;

public class AccountManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static AccountManager INSTANCE = null;

    private AccountManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    static synchronized AccountManager getInstance(SharedPreferences prefs){
        if(INSTANCE == null){
            INSTANCE = new AccountManager(prefs);
        }
        return INSTANCE;
    }

    public void saveUserdata(User user){
        editor.putInt("ID", user.getId()).commit();
        editor.putString("EMAIL", user.getEmail()).commit();
        editor.putString("USER_NAME", user.getName()).commit();
    }

    public void deleteUserdata(){
        editor.remove("ID").commit();
        editor.remove("EMAIL").commit();
        editor.remove("USER_NAME").commit();
    }

    public User getUserdata(){
        User user = new User();
        user.setId(prefs.getInt("ID", 0));
        user.setEmail(prefs.getString("EMAIL", null));
        user.setName(prefs.getString("USER_NAME", null));
        return user;
    }

}
