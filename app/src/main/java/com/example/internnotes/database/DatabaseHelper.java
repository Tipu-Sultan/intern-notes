package com.example.internnotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    // Notes table constants
    private static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_NOTE = "note";

    // Users table constants
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_FAMILY_NAME = "family_name";
    private static final String COLUMN_USER_GIVEN_NAME = "given_name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create notes table
        String createNotesTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " TEXT NOT NULL, " +
                COLUMN_NOTE + " TEXT NOT NULL);";
        db.execSQL(createNotesTable);

        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_USER_EMAIL + " TEXT, " +
                COLUMN_USER_FAMILY_NAME + " TEXT, " +
                COLUMN_USER_GIVEN_NAME + " TEXT);";
        db.execSQL(createUsersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Method to add a new note for a specific user
    public void addNote(String userId, String noteText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_NOTE, noteText);

        long result = db.insert(TABLE_NOTES, null, values);
        if (result == -1) {
            Log.e("Database", "Error inserting note");
        } else {
            Log.d("Database", "Note added successfully");
        }
        db.close();
    }

    // Method to get all notes for a specific user
    public Cursor getAllNotes(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM notes WHERE user_id = ?";
        return db.rawQuery(query, new String[]{userId});
    }

    // Method to delete a note by its ID
    public boolean deleteNoteById(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(noteId)});
        db.close();
        return rowsDeleted > 0;
    }

    // Method to update a note by its ID and user ID
    public boolean updateNoteById(int noteId, String newText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE, newText);
        int rowsAffected = db.update(TABLE_NOTES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(noteId)});
        db.close();
        return rowsAffected > 0; // Return true if at least one row was updated
    }


    // Method to add a new user
    public long addUser(String userId, String email, String familyName, String givenName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_FAMILY_NAME, familyName);
        values.put(COLUMN_USER_GIVEN_NAME, givenName);
        return db.insert(TABLE_USERS, null, values);
    }

    public boolean isUserExists(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID},
                COLUMN_USER_ID + " = ?", new String[]{userId}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Method to retrieve user information by user ID
    public Cursor getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";
        return db.rawQuery(query, new String[]{userId});
    }
}
