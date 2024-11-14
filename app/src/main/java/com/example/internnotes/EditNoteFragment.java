package com.example.internnotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.internnotes.database.DatabaseHelper;

public class EditNoteFragment extends Fragment {

    private EditText editNoteText;
    private Button btnSaveNote;
    private int noteId;
    private String userId;
    private DatabaseHelper dbHelper;

    public EditNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        editNoteText = view.findViewById(R.id.edit_note);
        btnSaveNote = view.findViewById(R.id.btn_save_note);

        dbHelper = new DatabaseHelper(getContext());

        // Retrieve the note ID and text passed in the arguments
        if (getArguments() != null) {
            noteId = getArguments().getInt("noteId");  // Note ID
            String noteText = getArguments().getString("noteText");  // Note Text
            if (noteText != null) {
                editNoteText.setText(noteText);  // Set the current note text in the EditText
            }
        }

        // Handle Save button click
        btnSaveNote.setOnClickListener(v -> {
            String updatedText = editNoteText.getText().toString().trim();

            if (updatedText.isEmpty()) {
                Toast.makeText(getContext(), "Note cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the note in the database using the note ID
            boolean isUpdated = dbHelper.updateNoteById(noteId, updatedText);

            if (isUpdated) {
                Toast.makeText(getContext(), "Note updated successfully", Toast.LENGTH_SHORT).show();

                getParentFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "Failed to update note", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
