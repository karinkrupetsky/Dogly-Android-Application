package com.hit.project.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hit.project.R;
import com.hit.project.model.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



public class LoginFragment extends Fragment
{
    public static final String TAG = "LOGIN_FRG";
    private EditText emailET;
    private EditText passwordET;
    private ProgressBar loadingBar;
    private LoginFragmentClickListener clickListener;


    public LoginFragment() {
        // Default Constructor for Android system..
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        emailET = view.findViewById(R.id.email);
        passwordET = view.findViewById(R.id.password);
        loadingBar = view.findViewById(R.id.loadingBar);
        Button loginButton = view.findViewById(R.id.login);
        TextView signupButton = view.findViewById(R.id.signupTV);
        loginButton.setOnClickListener(this::onLoginClick);
        signupButton.setOnClickListener(this::onSignupClick);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            clickListener = (LoginFragmentClickListener)context;
        } catch(ClassCastException ex) {
            throw new ClassCastException("NOTE! The activity must implement the fragment's listener" +
                    " interface!");
        }
    }

    public void onLoginClick(View view) {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        if(!Utils.validateCredentials(email, password)) {
            Toast.makeText(getContext(), getString(R.string.wrong_credentials), Toast.LENGTH_SHORT).show();
            return;
        }
        loadingBar.setVisibility(View.VISIBLE);
        clickListener.onLoginButtonClick(email, password, () -> loadingBar.setVisibility(View.GONE));
    }

    public void onSignupClick(View view) {
        clickListener.onSignUpButtonClicked();
    }


    public static interface LoginFragmentClickListener {
        public void onLoginButtonClick(String email, String password, Runnable onResult);
        public void onSignUpButtonClicked();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getResources().getString(R.string.login));
    }
}
