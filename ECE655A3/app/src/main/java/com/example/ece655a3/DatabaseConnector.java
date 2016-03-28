package com.example.ece655a3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;


public class DatabaseConnector 
{
// database name
private static final String DATABASE_NAME = "Questions";
   
private SQLiteDatabase database; // for interacting with the database
private DatabaseOpenHelper databaseOpenHelper; // creates the database

// public constructor for DatabaseConnector
public DatabaseConnector(Context context) 
{
   // create a new DatabaseOpenHelper
   databaseOpenHelper = 
      new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
}

// open the database connection
public void open() throws SQLException 
{
   // create or open a database for reading/writing
   database = databaseOpenHelper.getWritableDatabase();
}

// close the database connection
public void close() 
{
   if (database != null)
      database.close(); // close the database connection
} 

// inserts a new question in the database
public long insertQuestion(String question, String answer, String first,  
   String second, String third, String timer) 
{
   ContentValues newQuestion = new ContentValues();
   newQuestion.put("question", question);
   newQuestion.put("answer", answer);
   newQuestion.put("first", first);
   newQuestion.put("second", second);
   newQuestion.put("third", third);
   newQuestion.put("timer", timer);
   

   open(); // open the database
   long rowID = database.insert("questions", null, newQuestion);
   close(); // close the database
   return rowID;
} 

// updates an existing question in the database
public void updateQuestion(long id, String question, String answer, 
   String first, String second, String third, String timer) 
{
   ContentValues editQuestion = new ContentValues();
   editQuestion.put("question", question);
   editQuestion.put("answer", answer);
   editQuestion.put("first", first);
   editQuestion.put("second", second);
   editQuestion.put("third", third);
   editQuestion.put("timer", timer);
   

   open(); // open the database
   database.update("questions", editQuestion, "_id=" + id, null);
   close(); // close the database
} // end method updateQuestion

// return a Cursor with all question names in the database
public Cursor getAllQuestions() 
{
   return database.query("questions", new String[] {"_id", "question"}, 
      null, null, null, null, "question");
} 

public Cursor returnAll()
{
	return database.query("questions", new String[] {"_id", "question", "answer", "first", "second", "third","timer"},
		      null, null, null, null, "question");
}
// return a Cursor containing specified question's information 
public Cursor getOneQuestion(long id) 
{
   return database.query(
      "questions", null, "_id=" + id, null, null, null, null);
} 

// delete the question specified by the given String name
public void deleteQuestion(long id) 
{
   open(); // open the database
   database.delete("questions", "_id=" + id, null);
   close(); // close the database
} 

private class DatabaseOpenHelper extends SQLiteOpenHelper 
{
   // constructor
   public DatabaseOpenHelper(Context context, String question,
      CursorFactory factory, int version) 
   {
      super(context, question, factory, version);
   }

   // creates the questions table when the database is created
   @Override
   public void onCreate(SQLiteDatabase db) 
   {
      // query to create a new table named questions
      String createQuery = "CREATE TABLE questions" +
         "(_id integer primary key autoincrement," +
         "question TEXT, answer TEXT, first TEXT, " +
         "second TEXT, third TEXT, timer TEXT);";
               
      db.execSQL(createQuery); // execute query to create the database
   } 

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, 
       int newVersion) 
   {
   }
} // end class DatabaseOpenHelper
} // end class DatabaseConnector