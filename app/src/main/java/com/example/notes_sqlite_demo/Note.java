package com.example.notes_sqlite_demo;

public class Note
{
    public static final String TABLE_NAME       = "notes";
    public static final String COLUMN_ID        = "id";
    public static final String COLUMN_NAME      = "note";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String note;
    private String timestamp;

    //CREATE table in SQLite DB
    public static final String CREATE_TABLE =
            "CREATE TABLE "+ TABLE_NAME+"("
            +COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            +COLUMN_NAME +" TEXT,"
            +COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"+")";

    public Note() {}//end default constructor

    public Note(int id, String note, String timestamp)
    {
        this.id = id;
        this.note = note;
        this.timestamp = timestamp;
    }//end parameterized constructor


    public int getId()
    {
        return id;
    }//end getId()

    public void setId(int id)
    {
        this.id = id;
    }//end setId()

    public String getNote()
    {
        return note;
    }//end getNote()

    public void setNote(String note)
    {
        this.note = note;
    }//end setNote()

    public String getTimestamp()
    {
        return timestamp;
    }//end getTimestamp()

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }//end setTimestamp()

}//end class
