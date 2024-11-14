package com.example.internnotes;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.internnotes.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnLogout, btnAddNote;
    private List<Note> notesList;
    private DatabaseHelper dbHelper;

    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        // Initialize views
        btnLogout = view.findViewById(R.id.btn_logout);
        btnAddNote = view.findViewById(R.id.btn_add_note);
        recyclerView = view.findViewById(R.id.recycler_view_notes);
        TextView noNotesTextView = view.findViewById(R.id.tv_no_notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the DatabaseHelper
        dbHelper = new DatabaseHelper(getContext());

        // Fetch the notes for the current user from the database
        notesList = fetchNotesFromDatabase();

        // Check if notesList is not null and update the adapter
        if (notesList != null && !notesList.isEmpty()) {
            NotesAdapter adapter = new NotesAdapter(notesList);
            recyclerView.setAdapter(adapter);
        } else {
            noNotesTextView.setVisibility(View.VISIBLE);
        }

        btnAddNote.setOnClickListener(v -> {
            loadFragment(new AddNoteFragment());
        });

        btnLogout.setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).signOut();
            }
        });

        return view;
    }

    private List<Note> fetchNotesFromDatabase() {
        List<Note> notes = new ArrayList<>();

        // Retrieve the current user's ID from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId != null) {
            Cursor cursor = dbHelper.getAllNotes(userId);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                    int textIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE);
                    if (idIndex != -1 && textIndex != -1) {
                        int noteId = cursor.getInt(idIndex);
                        String noteText = cursor.getString(textIndex);
                        notes.add(new Note(noteId, noteText));
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        }

        return notes;
    }

    // Helper method to load fragments
    public void loadFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

