package frozventus.ticklist;

/**
 * Created by Darren Chin on 18/7/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.*;


public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "tasksInfo";
    // Tasks table name
    private static final String TABLE_TASKS = "tasks";
    // Tasks Table Columns names
    private static final String KEY_ID = "title";
    private static final String KEY_DETAILS = "details";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DETAILS + " TEXT,"
        + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        // Creating tables again
        onCreate(db);
    }

    // Adding new Task
    public void addTask(ToDoList task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, task.getTitle()); // TaskName
        values.put(KEY_DETAILS, task.getDeatils()); // Details

        // Inserting Row
        db.insert(TABLE_TASKS, null, values);
        db.close(); // Closing database connection
    }

    // Getting one Task
    public ToDoList getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS, new String[]{KEY_ID,
                KEY_DETAILS}, KEY_ID + "=?",
        new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ToDoList conTask = new ToDoList(cursor.getString(0), cursor.getString(1));
        // return Task

        cursor.close();
        return conTask;
    }

    // Getting All Tasks
    public List<ToDoList> getAllTasks() {
        List<ToDoList> taskList = new ArrayList<ToDoList>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_TASKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ToDoList newTask = new ToDoList();
                newTask.setTitle(cursor.getString(0));
                newTask.setDetails(cursor.getString(1));
                // Adding contact to list
                taskList.add(newTask);
            } while (cursor.moveToNext());
        }

        // return contact list
        return taskList;
    }
    // Getting tasks Count
    public int getTasksCount() {
        String countQuery = "SELECT * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    // Updating a task
    public int updateTask(ToDoList task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, task.getTitle());
        values.put(KEY_DETAILS, task.getDeatils());

        // updating row
        return db.update(TABLE_TASKS, values, KEY_ID + " = ?",
        new String[]{String.valueOf(task.getTitle())});
    }

    // Deleting a Task
    public void deleteTask(ToDoList task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_ID + " = ?",
        new String[] { String.valueOf(task.getTitle()) });
        db.close();
    }
}
