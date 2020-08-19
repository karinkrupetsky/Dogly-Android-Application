package com.hit.project.model;


import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserData
{
    private String id;  // Auth uid + Firestore document id (Excluded)
    private String name;
    private String email;


    public UserData(String name, String email, String imageURL) {
        this.name = name;
        this.email = email;
    }

    public UserData(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserData withId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getId() {
        return id;
    }
}
