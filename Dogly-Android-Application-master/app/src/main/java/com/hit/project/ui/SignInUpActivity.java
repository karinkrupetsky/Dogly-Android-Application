package com.hit.project.ui;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hit.project.R;
import com.hit.project.model.UserData;
import com.hit.project.model.Utils;


public class SignInUpActivity extends AppCompatActivity
        implements LoginFragment.LoginFragmentClickListener, SignUpFragment.SignUpFragmentClickListener
{
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinup);

        // initialize Toolbar..
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.container, new LoginFragment(), LoginFragment.TAG)
        .commit();
    }

    @Override
    public void onLoginButtonClick(String email, String password, Runnable onResult) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> { //OnComplete() method
                    if (task.isSuccessful()) {
                        // Login succeed..
                        moveToMainActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignInUpActivity.this, getString(R.string.wrong_credentials), Toast.LENGTH_SHORT).show();
                    }
                    onResult.run();
                });
    }

    @Override
    public void onSignUpButtonClicked() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SignUpFragment(), SignUpFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSignUp(String name, String email, String password, String repeatPass, Runnable onFinish) {
        if(!Utils.validateCredentials(name, email, password, repeatPass)) {
            Toast.makeText(this, getString(R.string.signup_details_error), Toast.LENGTH_LONG).show();
            onFinish.run();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        addUserToDB(new UserData(name, email).withId(auth.getCurrentUser().getUid()));
                        auth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name).build());
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignInUpActivity.this, getString(R.string.signup_failed)
                                + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    // hide progress bar..
                    onFinish.run();
                });
    }

    /**
     *  Create and add  user's details to Firestore DB,
     *  With same document id as user auth uid.
     */
    private void addUserToDB(UserData userData) {

        assert userData.getId() != null;

        db.collection("users")
                .document(userData.getId())
                .set(userData)
                .addOnSuccessListener(documentReference -> {
                    moveToMainActivity();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(SignInUpActivity.this, "ERROR: "+exception.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void moveToMainActivity() {
        startActivity(new Intent(SignInUpActivity.this, MainActivity.class));
        finish();
    }
}

