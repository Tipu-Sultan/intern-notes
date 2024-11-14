package com.example.internnotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.internnotes.database.DatabaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Initialize launcher for result of sign-in
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInResult(task);
                    }
                }
        );

        if (isUserLoggedIn()) {
            loadFragment(new NotesFragment());
        } else {
            loadFragment(new LoginFragment());
        }
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false);
    }

    public void signIn(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);

        // Start the sign-in process
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        ProgressBar progressBar = findViewById(R.id.progressBar);

        try {
            // Hide the ProgressBar once the sign-in result is received
            progressBar.setVisibility(View.GONE);

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String userId = account.getId();
                String email = account.getEmail();
                String familyName = account.getFamilyName();
                String givenName = account.getGivenName();

                // Save user details to the database
                if (!databaseHelper.isUserExists(userId)) {
                    databaseHelper.addUser(userId, email, familyName, givenName);
                }

                // Update SharedPreferences to mark user as logged in
                SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
                editor.putBoolean("is_logged_in", true);
                editor.putString("user_id", userId);
                editor.apply();

                loadFragment(new NotesFragment());
            }
        } catch (ApiException e) {
            progressBar.setVisibility(View.GONE);
            Log.w("SignIn", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
            editor.putBoolean("is_logged_in", false);
            editor.apply();
            loadFragment(new LoginFragment());
        });
    }
}


