package com.example.notes_sqlite_demo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notes_db";

    public DatabaseHandler(Context context)
    {
        super(context,DATABASE_NAME ,null ,DATABASE_VERSION);
    }//end constructor()

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(Note.CREATE_TABLE);
    }//End onCreate()

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        db.execSQL("DROP TABLE IF EXISTS "+ Note.TABLE_NAME);
        onCreate(db);
    }//end onUpgrade()

    //add new note
    public long insertNote(String note)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NAME,note);

        long id = db.insert(Note.TABLE_NAME, null,values);

        db.close();
        return id;
    }//end insertNote()

    //get a single note
    public Note getNote(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Note.TABLE_NAME, new String[]{Note.COLUMN_ID, Note.COLUMN_NAME, Note.COLUMN_TIMESTAMP},
        Note.COLUMN_ID + "=?",new String[] {String.valueOf(id)}, null,null,null,null);

        if(cursor != null)
        {
            cursor.moveToFirst();
        }//end if()

        @SuppressLint("Range")
        Note note = new Note (
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

        cursor.close();
        return note;
    }//end getNote()

    //get all notes
    @SuppressLint("Range")
    public List<Note> getAllNotes()
    {
        List<Note> noteList = new ArrayList<>();

        //Select all Query
        String selectQuery= "SELECT * FROM "+ Note.TABLE_NAME + " ORDER BY "+ Note.COLUMN_TIMESTAMP+" DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        //loop on cursor
        if (cursor.moveToFirst())
        {
            do
            {
                Note noteObject = new Note();
                noteObject.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                noteObject.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NAME)));
                noteObject.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));
            }while (cursor.moveToNext());//end Do-While
        }//end if()

        db.close();
        return noteList;
    }//end getAllNotes()

    //Update Note
    public int updateNote(Note note)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NAME,note.getNote());

        //update query
        //int update= db.update(Note.TABLE_NAME, values, Note.COLUMN_ID+" =?" ,new String[]{String.valueOf(note.getId())});

      return  db.update(Note.TABLE_NAME, values, Note.COLUMN_ID+" =?" ,new String[]{String.valueOf(note.getId())});
    }//end updateNote()

    //delete Note
    public void deleteNote(Note note)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID+" =?", new String[]{String.valueOf(note.getId())});
        db.close();
    }//end deleteNote()

    public long getNotesCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, Note.TABLE_NAME);
        db.close();
        return count;
    }//end getNotesCount()

}//end class
