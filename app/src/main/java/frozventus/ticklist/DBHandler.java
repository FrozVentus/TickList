package frozventus.ticklist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.Toast;

import java.util.*;


public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "tasksInfo";
    // Tasks table name
    private static final String TASK_TABLE_NAME = "tasks";
    // Tasks Table Columns names
    private static final String TASK_COLUMN_ID = "_id";
    private static final String TASK_COLUMN_TITLE = "title";
    private static final String TASK_COLUMN_DETAILS = "details";
    private static final String TASK_COLUMN_DATE = "date";
    private static final String TASK_COLUMN_DAILY ="isDaily";
    private static final String TASK_COLUMN_TIME = "time"; //unused as of now

    private List<String> _titleList;
    private HashMap<Integer, ArrayList<String>> _detailList; // _id of task is used as Key
    private List<Integer> _titleOrder; // hold _id of the task in order
    private Context _context;


    public DBHandler(Context context, List<String> titleList,
                     HashMap<Integer, ArrayList<String>> detailList, List<Integer> titleOrder) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        _titleList = titleList;
        _detailList = detailList;
        _titleOrder = titleOrder;
        _context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TASK_TABLE_NAME + "(" +
                TASK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TASK_COLUMN_TITLE + " TEXT, " +
                TASK_COLUMN_DETAILS + " TEXT, " +
                TASK_COLUMN_DATE + " TEXT, " +
                TASK_COLUMN_DAILY + " TEXT" +
                //TASK_COLUMN_TIME + "TEXT " +
        ")";
        //debug
        // Toast.makeText(_context, CREATE_CONTACTS_TABLE, Toast.LENGTH_LONG).show();
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
        // Creating tables again
        onCreate(db);
    }

    // Adding new Task
    public void addTask(String title, ArrayList<String> details) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASK_COLUMN_TITLE, title); // TaskName
        values.put(TASK_COLUMN_DETAILS, details.get(0)); // Details
        values.put(TASK_COLUMN_DATE, details.get(1)); // Date
        values.put(TASK_COLUMN_DAILY, details.get(2)); // Daily
        //values.put(TASK_COLUMN_TIME, details.get(3)); // Time

        // Inserting Row
        long newRowID = db.insert(TASK_TABLE_NAME, null, values);
        db.close(); // Closing database connection
        //debug
        // Toast.makeText(_context, "The new Row Id is " + newRowID, Toast.LENGTH_LONG).show();
        getAllTasks();
    }

/*    // Getting one Task
    public ToDoList getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TASK_TABLE_NAME, new String[]{TASK_COLUMN_TITLE,
                TASK_COLUMN_DETAILS, TASK_COLUMN_DATE, TASK_COLUMN_DAILY},
                TASK_COLUMN_ID + "=?",
        new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ToDoList conTask = new ToDoList(cursor.getString(0), cursor.getString(1));
        // return Task

        cursor.close();
        return conTask;
    }*/

    // Getting All Tasks
    // Also update what is shown on screen
    public boolean getAllTasks() {
        List<String> newTitleList = new LinkedList<String>();
        HashMap<Integer, ArrayList<String>> newDetailList =
                new HashMap<Integer, ArrayList<String>>();
        List<Integer> newOrder = new LinkedList<Integer>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TASK_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Integer id = cursor.getInt(0);
                newOrder.add(id);
                newTitleList.add(cursor.getString(1));
                ArrayList<String> currDetails = new ArrayList<String>();
                currDetails.add(cursor.getString(2));
                currDetails.add(cursor.getString(3));
                currDetails.add(cursor.getString(4));
                newDetailList.put(id, currDetails);
            } while (cursor.moveToNext());
        }
        // update _titleOrder
        _titleOrder.clear();
        _titleOrder.addAll(newOrder);
        // update _titleList
        _titleList.clear();
        _titleList.addAll(newTitleList);
        // update _detailList
        _detailList.clear();
        _detailList.putAll(newDetailList);

        cursor.close();
        // return contact list
        return true;
    }
    // Getting tasks Count
    public int getTasksCount() {
        String countQuery = "SELECT * FROM " + TASK_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    // Updating a task
    public int updateTask(int id, int position, String newString) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        switch(position) { //which detail to edit
            case 0:
                values.put(TASK_COLUMN_DETAILS, newString);
                values.put(TASK_COLUMN_DATE, _detailList.get(id).get(1));
                values.put(TASK_COLUMN_DAILY, _detailList.get(id).get(2));
                break;
            case 1:
                values.put(TASK_COLUMN_DAILY, _detailList.get(id).get(0));
                values.put(TASK_COLUMN_DATE, newString);
                values.put(TASK_COLUMN_DAILY, _detailList.get(id).get(2));
                break;
            case 2:
                values.put(TASK_COLUMN_DAILY, _detailList.get(id).get(0));
                values.put(TASK_COLUMN_DAILY, _detailList.get(id).get(1));
                values.put(TASK_COLUMN_DAILY, newString);
                break;
        }
        // updating row
        db.update(TASK_TABLE_NAME, values,TASK_COLUMN_ID + " = ?",
        new String[]{String.valueOf(id)});
        getAllTasks();
        return id;
    }

    // Deleting a Task
    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TASK_TABLE_NAME, TASK_COLUMN_ID + " = ?",
        new String[] { String.valueOf(id) });
        getAllTasks();
        //db.close();
    }

    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        // drop current table
        db.execSQL("DROP TABLE " + TASK_TABLE_NAME);
        // create new table
        this.onCreate(db);
        getAllTasks();
    }
}