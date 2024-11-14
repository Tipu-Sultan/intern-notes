package com.example.internnotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        Button btnSignIn = view.findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).signIn(progressBar);
        });

        return view;
    }
}


