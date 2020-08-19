package com.hit.project.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hit.project.R;
import com.hit.project.model.Utils;

public class ChangePasswordActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText currentPasswordET = findViewById(R.id.currentPassword_changePassword);
        EditText passwordET = findViewById(R.id.password_changePassword);
        EditText repeatPassET = findViewById(R.id.passwordRepeat_changePassword);
        ProgressBar loadingBar = findViewById(R.id.loadingBar_changePassword);
        Button changePasswordB = findViewById(R.id.change_password_button);
        changePasswordB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = passwordET.getText().toString();
                String repeatPass = repeatPassET.getText().toString();
                String currentPassword = currentPasswordET.getText().toString();

                loadingBar.setVisibility(View.VISIBLE);
                ChangePassword(currentPassword, newPassword, repeatPass,() -> loadingBar.setVisibility(View.INVISIBLE));

            }
        });
    }



    private void ChangePassword(String currentPassword, String newPassword, String repeatPass, Runnable onFinish){

        if(!Utils.validateChangePassword(newPassword, repeatPass)) {
            Toast.makeText(this, getString(R.string.change_password_details_error), Toast.LENGTH_LONG).show();
            onFinish.run();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();

        if(user!=null) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_changed), Toast.LENGTH_SHORT).show();
                            onFinish.run();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangePasswordActivity.this,
                                    getResources().getString(R.string.error_password), Toast.LENGTH_SHORT).show();
                            onFinish.run();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChangePasswordActivity.this,
                            getResources().getString(R.string.incorrect_current_password), Toast.LENGTH_SHORT).show();
                    onFinish.run();
                }
            });
        }



    }
}