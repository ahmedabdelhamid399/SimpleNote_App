package com.example.notes_sqlite_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView noNotesView;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        noNotesView = findViewById(R.id.empty_notes_view);

        db = new DatabaseHandler(this);
        notesList.addAll(db.getAllNotes());

        mAdapter = new NotesAdapter(this, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

        Button addNote =  findViewById(R.id.addNote_btn);
        addNote.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showNoteDialog(false, null, -1);
            }//end onClick()
        });

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener()
        {
            @Override
            public void onClick(View view, final int position)
            {
            }//end onClick()

            @Override
            public void onLongClick(View view, int position)
            {
                showActionsDialog(position);
            }//end onLongClick()
        }));
    }//end onCreate()



    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String note)
    {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(note);

        // get the newly inserted note from db
        Note n = db.getNote(id);

        if (n != null)
        {
            // adding new note to array list at 0 position
            notesList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }//end if{}
    }//end createNote()

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String note, int position)
    {
        Note n = notesList.get(position);
        // updating note text
        n.setNote(note);

        // updating note in db
        db.updateNote(n);

        // refreshing the list
        notesList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }//end updateNote()

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position)
    {
        // deleting the note from db
        db.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }//end deleteNote()

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position)
    {
        CharSequence[] colors = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    showNoteDialog(true, notesList.get(position), position);
                }//end if{}
                else
                {
                    deleteNote(position);
                }//end else{}
            }//end onClick()
        });
        builder.show();
    }//end showActionsDialog()

    /**
     * Shows alert dialog with EditText options to enter / edit  a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position)
    {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText
                (!shouldUpdate ? getString(R.string.lbl_new_note) : getString(R.string.lbl_edit_note));

        if (shouldUpdate && note != null)
        {
            inputNote.setText(note.getNote());
        }//end if{}
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialogBox, int id)
                    {

                    }//end onClick()
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id)
                            {
                                dialogBox.cancel();
                            }//end onClick()
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString()))
                {
                    Toast.makeText(MainActivity.this, "Plz write your note!", Toast.LENGTH_SHORT).show();
                    return;
                }//end if{}
                else
                {
                    alertDialog.dismiss();
                }//end else{}

                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    updateNote(inputNote.getText().toString(), position);
                } //end if{}
                else
                {
                    // create new note
                    createNote(inputNote.getText().toString());
                }//end else{}
            }//end onClick()
        });
    }//end showNoteDialog()
    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes()
    {
        // you can check notesList.size() > 0

        if (db.getNotesCount() > 0)
        {
            noNotesView.setVisibility(View.GONE);
        }//end if{}
        else
        {
            noNotesView.setVisibility(View.VISIBLE);
        }//end else{}
    }//end toggleEmptyNotes()
}//end class
