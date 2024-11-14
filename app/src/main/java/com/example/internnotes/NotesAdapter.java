package com.example.internnotes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.internnotes.database.DatabaseHelper;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes; // List of Note objects
    private Context context;

    public NotesAdapter(List<Note> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        context = parent.getContext();
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);

        // Set note text
        holder.noteTextView.setText(note.getText());

        // Handle Edit button click
        holder.editButton.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putInt("noteId", note.getId());
            bundle.putString("noteText", note.getText());

            EditNoteFragment editNoteFragment = new EditNoteFragment();
            editNoteFragment.setArguments(bundle);

            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, editNoteFragment)
                    .addToBackStack(null)
                    .commit();
        });


        // Handle Delete button click
        holder.deleteButton.setOnClickListener(v -> {
            deleteNoteFromDatabase(note.getId());
            notes.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTextView;
        Button editButton, deleteButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTextView = itemView.findViewById(R.id.note_text);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }

    // Method to delete a note from the database
    private void deleteNoteFromDatabase(int noteId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        boolean isDeleted = dbHelper.deleteNoteById(noteId);

        if (isDeleted) {
            // Show success message
            Toast.makeText(context, "Note deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Show error message
            Toast.makeText(context, "Failed to delete note", Toast.LENGTH_SHORT).show();
        }
    }

}
