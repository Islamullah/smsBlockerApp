package com.example.junaid.smslistview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.SmsMessage;
import android.util.Log;
//import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by Junaid on 5/15/2016.
 */
public class myDBHandler extends SQLiteOpenHelper {

    //public static final String SMS_URI = “/data/data/org.secure.sms/databases/”;
    public static final String db_name = "sms1.db";
    public static final int version =1;
    Context context;
    SimpleCursorAdapter adapter;
    ListView lvMsg;
    public myDBHandler(Context context) {
        super(context, db_name, null, version);
// TODO Auto-generated constructor stub
        this.context =context;
    }


    public myDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int v) {
        super(context, db_name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
// TODO Auto-generated method stub

        db.execSQL("create table Blocked(_id integer primary key autoincrement, id integer, address text, body text, time integer)");
        db.execSQL("create table BlockingNumber ( _id integer primary key autoincrement, id integer, cellno text)");
        Toast.makeText(context, "database created with two tables", Toast.LENGTH_LONG).show();
        Log.i("dbcreate", "DATABASE IS CREATED");
    }

    public void deleteTable(Context c)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists Blocked");
        db.execSQL("drop table if exists BlockingNumber");
        onCreate(db);
        Toast.makeText(c, "New(Empty) Data Base Created", Toast.LENGTH_LONG).show();
    }



    public boolean checkDataBase(String db) {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = "data/data/"+ context.getPackageName() +"/databases/" + db;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

// database does’t exist yet.

        } catch (Exception e) {

        }

        if (checkDB != null) {

            //checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// TODO Auto-generated method stub
        if (oldVersion >= newVersion)
            return;

        if (oldVersion == 1) {
            Log.d("New Version", "Data can be upgraded");
        }

        Log.d("Sample Data", "onUpgrade : " + newVersion);
    }

    public void addSmsToBlockList(String id, String address, String body, String time)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("address", address);
        cv.put("body", body);
        cv.put("time", time);

        db.insert("Blocked", null, cv);

        db.close();
    }

    public String[] getAllNumbers()
    {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("data/data/com.example.junaid.smslistview/databases/sms1.db", null);
                Cursor c = db.rawQuery("select address from Blocked", null );
        c.moveToFirst();
        String[] address = new String[c.getCount()];

        int i = 0;

        while(!c.isAfterLast()){

            address[i] = c.getString(c.getColumnIndex("address"));
            c.moveToNext();
            i++;
        }
        db.close();
        return address;
    }


//    public SimpleCursorAdapter view() {
//
//        String query = "select * from datatable where 1";
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor c = db.rawQuery(query, null);
//
//        if (c != null && c.moveToFirst())
//        {
//            adapter = new SimpleCursorAdapter(context, R.layout.row, c, new String[]{"address", "body"}, new int[]{R.id.lblNumber, R.id.lblMsg});
//            return adapter;
//            //db.close();
//        }
//        return adapter;
        //return adapter;
       // db.close();
//        c.moveToFirst();
//
//        while (!c.isAfterLast()) {
//            if (c.getString(c.getColumnIndex("address")) != null) {
//                displaySms += "\nCELL: " + c.getString(c.getColumnIndex("address"));
//                displaySms += "\nBody: " + c.getString(c.getColumnIndex("body"));
//                displaySms += "\nTime: " + c.getString(c.getColumnIndex("time"));
//            }
//            c.moveToNext();
//        }
    }



    /*private static final int DB_VERSION = 1;
    private static final String DB_NAME = "messagesDB.db";
    private static final String TABLE_NAME = "blockedSms";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_CELL = "Cell";
    private static final String COLUMN_BODY = "Body";
    private static final String COLUMN_TIME = "Time";

    public myDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "create table " + TABLE_NAME + " ( " +
                COLUMN_ID + "integer PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CELL + " text ," +
                COLUMN_BODY + " text, " +
                COLUMN_TIME + " text;";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addSms(Messages sms)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CELL, sms.getCellNo());
        cv.put(COLUMN_BODY, sms.getMessageBody());
        cv.put(COLUMN_TIME, sms.getTime());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public void view(Context context)
    {
        String displaySms = "";

        String query = "select * from " + TABLE_NAME + " where 1";
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        while (!c.isAfterLast())
        {
            if (c.getString(c.getColumnIndex(COLUMN_CELL)) != null)
            {
                displaySms += "ID: " + c.getString(c.getColumnIndex(COLUMN_ID));
                displaySms += "\nCELL: " + c.getString(c.getColumnIndex(COLUMN_CELL));
                displaySms += "\nBody: " + c.getString(c.getColumnIndex(COLUMN_BODY));
                displaySms += "\nTime: " + c.getString(c.getColumnIndex(COLUMN_TIME));
            }
            c.moveToNext();
        }
        Toast.makeText(context, displaySms, Toast.LENGTH_LONG).show();
    }*/

