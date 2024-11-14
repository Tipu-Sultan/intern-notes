package com.example.internnotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.internnotes.database.DatabaseHelper;

public class AddNoteFragment extends Fragment {

    private EditText editNote;
    private Button btnSave;
    private DatabaseHelper db;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);

        editNote = view.findViewById(R.id.edit_note);
        btnSave = view.findViewById(R.id.btn_save_note);

        // Initialize database helper
        db = new DatabaseHelper(getContext());

        // Retrieve the user ID from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", null);

        btnSave.setOnClickListener(v -> {
            String noteText = editNote.getText().toString();
            if (!noteText.isEmpty()) {
                if (userId != null) {
                    db.addNote(userId, noteText);
                    Toast.makeText(getContext(), "Note added successfully", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Note cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
