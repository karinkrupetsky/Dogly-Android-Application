package com.hit.project.model;

import android.util.Patterns;

import java.util.regex.Pattern;

public class Utils
{

    public static boolean validateCredentials(String email, String password) {
        if(email == null || password == null)
            return false;

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return false;

        if(password.length() < 6)
            return false;

        return true;
    }

    public static boolean validateCredentials(String name, String email, String password, String repeatPass) {
        return Pattern.compile("\\w+( \\w+)*").matcher(name).matches() &&
                validateCredentials(email, password) && password.equals(repeatPass);
    }

    public static boolean validateChangePassword(String password, String repeatPass) {
        if(password == null || repeatPass == null)
            return false;

        if(password.length() < 6){
            return false;
        }

        if(!password.equals(repeatPass)){
            return false;
        }

        return true;
    }
}
