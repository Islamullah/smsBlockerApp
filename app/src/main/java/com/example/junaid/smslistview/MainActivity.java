package com.example.junaid.smslistview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
//import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.text.*;
import java.util.Calendar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // GUI Widget
    Button btnSent, btnInbox, btnDraft, btnBlocked;
    TextView lblMsg, lblNo, lblBlocked;
    ListView lvMsg, lvblk;
    int flag = 0;
    Intent i;


    String[] address = {""};

    // Cursor Adapter
    SimpleCursorAdapter adapter;
    myDBHandler myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        btnInbox = (Button) findViewById(R.id.btnInbox);
        btnInbox.setOnClickListener(this);

        btnSent = (Button) findViewById(R.id.btnSentBox);
        btnSent.setOnClickListener(this);

        btnDraft = (Button) findViewById(R.id.btnDraft);
        btnDraft.setOnClickListener(this);

        btnBlocked = (Button) findViewById(R.id.btnBlocked);
        btnBlocked.setOnClickListener(this);

        lblBlocked = (TextView) findViewById(R.id.btnBlocked);

        registerForContextMenu(btnBlocked);

        lvMsg = (ListView) findViewById(android.R.id.list);
        lvMsg.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        i = new Intent();
                        i.setClass(MainActivity.this, ReadActivity.class);
                        i.putExtra("_id", String.valueOf(id));
                        i.putExtra("flag", "inbox");
                        startActivity(i);
                    }
                }
        );
        lvblk = (ListView) findViewById(R.id.list1);
        lvblk.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        i = new Intent();
                        i.setClass(MainActivity.this, ReadActivity.class);
                        i.putExtra("_id", String.valueOf(id));
                        i.putExtra("flag", "blkList");
                        startActivity(i);
                    }
                }
        );

        registerForContextMenu(lvMsg);

        myDB = new myDBHandler(this, "sms1.db", null, 1);
        myDB = new myDBHandler(this, "sms1.db", null, 1);

        inbox();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(getApplicationContext(), "main activity", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), createNew.class);
                startActivity(i);


            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);


        if(flag == 0)
        {
            menu.setHeaderTitle("Inbox Message");
            menu.add(0, 1, 0, "Block");
            menu.add(0, 2, 0, "Delete");
        }
        else if(flag == 1)
        {
//          AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Blocked Message");
            menu.add(0, 3, 0, "Move to Inbox");
            menu.add(0, 4, 0, "Unblock");
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int choice = item.getItemId();
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        if (choice == 1) {

//            Toast.makeText(this, "Block selected", Toast.LENGTH_SHORT).show();
            fetchMsgFromInbox(String.valueOf(info.id));
            deleteSMS(MainActivity.this, String.valueOf(info.id));
            //fetchInbox();
            inbox();
            Toast.makeText(this, "Message has Blocked and deleted from Inbox", Toast.LENGTH_LONG).show();

            return true;
        }
        else if (choice == 2)
        {

//            Toast.makeText(this, String.valueOf(info.id), Toast.LENGTH_SHORT).show();


            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setMessage(" Are you sure you want to delete this message?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteSMS(MainActivity.this, String.valueOf(info.id));
                    //fetchInbox();
                    inbox();
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.create();
            alert.show();


            return true;
        }
        else if(choice == 3)
        {
            String id = String.valueOf(info.id);
            Toast.makeText(MainActivity.this, id, Toast.LENGTH_LONG).show();
            moveToInbox(id);
            //update BlockList
            showBlockList();
//            Toast.makeText(MainActivity.this, "Message has Moved To Inbox", Toast.LENGTH_SHORT).show();



            return true;
        }
        else
        {
            String id = String.valueOf(info.id);
            unBlockNumber(id);

            Toast.makeText(MainActivity.this, "Message Number is Unblocked", Toast.LENGTH_SHORT).show();
            return true;
        }

    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        i = new Intent();
//        i.setClass(this, ReadActivity.class);
//        i.putExtra("_id", String.valueOf(id));
//        i.putExtra("flag", "inbox");
//        startActivity(i);
//    }

//    AdapterView.OnItemLongClickListener longClick = new AdapterView.OnItemLongClickListener() {
//        @Override
//        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//            Toast.makeText(getApplicationContext(), String.valueOf(id), Toast.LENGTH_LONG).show();
//
//            return true;
//        }
//    };

//    View.OnLongClickListener longClick = new View.OnLongClickListener() {
//        @Override
//        public boolean onLongClick(View v) {
//
//            int i = v.getId();
//            Toast.makeText(getApplicationContext(), String.valueOf(i), Toast.LENGTH_LONG).show();
//
//            return true;
//        }
//    };

    public void moveToInbox(String id)
    {
        String address = "";
        String body = "";
        String time = "";
        String[] selection = {"_id", "address", "body", "time"};
        String[] args = {id};
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("data/data/com.example.junaid.smslistview/databases/sms1.db", null);
        Cursor c = db.query("Blocked", selection, "_id = ?", args, null, null, null);
        c.moveToFirst();
            address = c.getString(c.getColumnIndex("address"));
            body = c.getString(c.getColumnIndex("body"));
            time = c.getString(c.getColumnIndex("time"));

        ContentValues values = new ContentValues();
        values.put("address", address);
        values.put("body", body);
        values.put("date", time);
        Uri uri = Uri.parse("content://sms/inbox");
        getContentResolver().insert(uri, values);

       if (db.delete("Blocked", "_id = ?", args)> 0)
       {
           Toast.makeText(MainActivity.this, "Message has Moved To Inbox", Toast.LENGTH_LONG).show();
       }

    }

    public void unBlockNumber(String id)
    {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("data/data/com.example.junaid.smslistview/databases/sms1.db", null);

        Cursor c = db.query("Blocked", new String[]{"_id", "address"}, "_id = ?", new String[]{id}, null, null, null);
        c.moveToFirst();

        String cell = c.getString(c.getColumnIndex("address"));

        db.delete("BlockingNumber", "cellno = ?", new String[]{cell});

//        Toast.makeText(MainActivity.this, "Message Successfully Unblocked", Toast.LENGTH_LONG).show();

    }



    @Override
    protected void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (!Telephony.Sms.getDefaultSmsPackage(this).equals("com.example.junaid.smslistview")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("This app is not set as your default messaging app. Do you want to set it as default?")
                        .setCancelable(false)
                        .setTitle("Alert!")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @TargetApi(19)
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btnInbox) {

            inbox();

        }

        if (v == btnSent) {

            myDB.deleteTable(this);

            // Create Sent box URI
            Uri sentURI = Uri.parse("content://sms/sent");

            // List required columns
            String[] reqCols = new String[]{"_id", "address", "body"};

            // Get Content Resolver object, which will deal with Content
            // Provider
            ContentResolver cr = getContentResolver();

            // Fetch Sent SMS Message from Built-in Content Provider
            Cursor c = cr.query(sentURI, reqCols, null, null, null);

            // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[]{"body", "address"}, new int[]{
                    R.id.lblMsg, R.id.lblNumber});
            lvMsg.setAdapter(adapter);

        }

        if (v == btnDraft) {
            // Create Sent box URI
            Uri draftURI = Uri.parse("content://sms/draft");

            // List required columns
            String[] reqCols = new String[]{"_id", "address", "body"};

            // Get Content Resolver object, which will deal with Content
            // Provider
            ContentResolver cr = getContentResolver();

            // Fetch Sent SMS Message from Built-in Content Provider
            Cursor c = cr.query(draftURI, reqCols, null, null, null);

            // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[]{"body", "address"}, new int[]{
                    R.id.lblMsg, R.id.lblNumber});
            lvMsg.setAdapter(adapter);

        }
        if (v == btnBlocked) {
            flag = 1;

//            String blockedSms = "";
//
//            myDB = new myDBHandler(this);
//
//            ArrayList al = myDB.getAllNumbers();
//            Toast.makeText(this, al.toString(), Toast.LENGTH_LONG).show();

//            myDB = new myDBHandler(getBaseContext());

            showBlockList();



//               c.moveToFirst();
////
//                while (!c.isAfterLast())
//                {
//                    int i = 0;
//
//                    if (c.getString(c.getColumnIndex("address")) != null)
//                    {
//                        blockedSms += "Cell: " + c.getString(c.getColumnIndex("address"));
//                        blockedSms += "\nBody: " + c.getString(c.getColumnIndex("body"));
//                        blockedSms += "\n\n";
////
//                        address[i] = c.getString(c.getColumnIndex("address"));
//////                        body[i] = c.getString(c.getColumnIndex("body"));
////
//                        i++;
//                    }
//                    c.moveToNext();
//                }
//                Toast.makeText(this, blockedSms, Toast.LENGTH_SHORT).show();
//                lblBlocked.setText(blockedSms);
        }

    }

    public void showBlockList(){
        try
        {
            SQLiteDatabase db;

            db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.junaid.smslistview/databases/sms1.db", null);
//              Toast.makeText(this, "database is opened", Toast.LENGTH_LONG).show();

            Cursor c = db.query("Blocked", new String[]{"_id", "address", "body", "time"}, null, null, null, null, "time desc");

//                adapter = new SimpleCursorAdapter(this, R.layout.row, c, new String[]{"address", "body"}, new int[] {R.id.lblNumber, R.id.lblMsg});

            startManagingCursor(c);

            lvMsg.setVisibility(View.GONE);
            lvblk.setVisibility(View.VISIBLE);
            lvblk.setAdapter(new MyBlockListAdapter(this, c));

            registerForContextMenu(lvblk);

        }catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    public void fetchMsgFromInbox(String idd)
    {
        Toast.makeText(this, idd, Toast.LENGTH_SHORT).show();
        Uri uri = Uri.parse("content://sms/inbox");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(uri, new String[]{"_id", "address", "body", "date"}, "_id = ?", new String[]{idd}, null);
        c.moveToFirst();

        String id = c.getString(c.getColumnIndex("_id"));
        String address = c.getString(c.getColumnIndex("address"));
        String body = c.getString(c.getColumnIndex("body"));

        long timestamp = c.getLong(c.getColumnIndex("date"));

//        Date d = new Date(timestamp);
//        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(timestamp);
//        Log.i("time", "time" + formatter.format(calendar.getTime()));
//        String time = String.valueOf(formatter.format(calendar.getTime()));


//        Toast.makeText(this, "ID: "+ id + "\nAddress: " + address + "\nBody: " + body + "\nTime: " + String.valueOf(formatter.format(calendar.getTime())) + "\n" + String.valueOf(d), Toast.LENGTH_LONG).show();

        addSmsToBlockList(id, address, body, timestamp);

    }

    public void addSmsToBlockList(String id, String address, String body, long time)
    {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.junaid.smslistview/databases/sms1.db", null);

        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("address", address);
        cv.put("body", body);
        cv.put("time", time);

        db.insert("Blocked", null, cv);
//        Toast.makeText(this, "Message Successfully added to Blocked List", Toast.LENGTH_LONG).show();

        addCellnoToBlockingNumber(id, address, db);

//        db.close();
    }

    public void addCellnoToBlockingNumber(String id, String address, SQLiteDatabase db)
    {
//        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.junaid.smsListview/databases/sms1.db", null);

        int flag = 0;

        Cursor c = db.query("BlockingNumber", new String[]{"_id", "cellno"}, null, null, null, null, null);
        c.moveToFirst();

        String blocked;

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("cellno")) != null) {
                blocked = c.getString(c.getColumnIndex("cellno"));

                if (address.equals(blocked)) {

                    flag =1;
                    break;
                }
                else{
                    c.moveToNext();
                }
            }

        }

        if(flag ==0) {
            ContentValues values = new ContentValues();

            values.put("id", id);
            values.put("cellno", address);

            db.insert("BlockingNumber", null, values);
//            Toast.makeText(this, "CellNo is also added to BlockingNumber table", Toast.LENGTH_SHORT).show();
            db.close();
        }


    }


    public void inbox()
    {
        flag = 0;
        // Create Inbox box URI
        Uri inboxURI = Uri.parse("content://sms/inbox");


        // List required columns
        String[] reqCols = new String[] { "_id", "address", "body", "date" };

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);

//        long timestamp = c.getLong(c.getColumnIndex("date"));
//        Date d = new Date(timestamp);
//        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(timestamp);
//        Log.i("time", "time" + formatter.format(calendar.getTime()));
//        String time = String.valueOf(formatter.format(calendar.getTime()));




        // Attached Cursor with adapter and display in listview
//        adapter = new SimpleCursorAdapter(this, R.layout.row, c,  new String[] { "body", "address", "date" }, new int[] { R.id.lblMsg, R.id.lblNumber, R.id.lblTime });

        startManagingCursor(c);


        lvblk.setVisibility(View.GONE);
        lvMsg.setVisibility(View.VISIBLE);
        lvMsg.setAdapter(new MyAdapter(this, c));
//        lvMsg.setOnItemLongClickListener(longClick);
//        lvMsg.setOnItemClickListener(this);
        registerForContextMenu(lvMsg);
    }


    public void deleteSMS(Context context, String id)
    {
        try {
            //mLogger.logInfo("Deleting SMS from inbox");
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(uriSms,
                    new String[] { "_id", "thread_id", "address",
                            "person", "date", "body" }, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    String idd = c.getString(c.getColumnIndex("_id"));


                    if (idd.equals(id)) {
                        //mLogger.logInfo("Deleting SMS with id: " + threadId);
                        context.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), null, null);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            //mLogger.logError("Could not delete SMS from inbox: " + e.getMessage());
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    final String[] options = {"Add New", "Update", "Remove"};


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action_Manage:
                AlertDialog.Builder ManageBlockList = new AlertDialog.Builder(this);
                ManageBlockList.setTitle("Select Option For BlockList");
                ManageBlockList.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if(item == 0){
                            addNewNumber();
                            Toast.makeText(getApplicationContext(),"Prompt to Add New ", Toast.LENGTH_LONG).show();
                        }
                        else if(item == 1){
                            updateOldNumber();
                        }
                        else if(item == 2){

                        }

                        Toast.makeText(getApplicationContext(), options[item], Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), String.valueOf(item), Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alert = ManageBlockList.create();
                alert.show();
                break;
        }

        return true;
    }

    public void addNewNumber(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // get prompts.xml view
        LayoutInflater li = this.getLayoutInflater();
        final View promptsView = li.inflate(R.layout.addnew, null);
        //        (ViewGroup) findViewById(R.id.layout_root);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("ADD NEW TO BLOCK LIST");


        final EditText userInput = (EditText) promptsView.findViewById(R.id.PromptText);

        // set dialog message
        alertDialogBuilder
                .setPositiveButton("ADD",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                // Put New Number to BlockingNumber Database.
                                SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.junaid.smslistview/databases/sms1.db", null);
                                ContentValues values = new ContentValues();
                                values.put("cellno", userInput.getText().toString());
                                db.insert("BlockingNumber", null, values);
                                db.close();

                                Toast.makeText(getApplicationContext(),"New Number Added Successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void updateOldNumber(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // get prompts.xml view
        LayoutInflater li = this.getLayoutInflater();
        final View promptsView = li.inflate(R.layout.update, null);
        //        (ViewGroup) findViewById(R.id.layout_root);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("UPDATE BLOCK LIST");


        final EditText oldNum = (EditText) promptsView.findViewById(R.id.oldNumber);
        final EditText newNum = (EditText) promptsView.findViewById(R.id.newNumber);

        // set dialog message
        alertDialogBuilder
                .setPositiveButton("UPDATE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.junaid.smslistview/databases/sms1.db", null);
                        ContentValues values = new ContentValues();

                        Cursor c = db.query("BlockingNumber", new String[]{ "cellno"}, null, null, null, null, null);
                        c.moveToFirst();
                        while(!c.moveToLast()){
                            if(c.getString(c.getColumnIndex("cellno")) != null){
                                String dbcellno = c.getString(c.getColumnIndex("cellno"));
                                if(oldNum.equals(dbcellno)){
                                    // update the cellNumber
                                }
                                else{ c.moveToNext(); }

                            }

                        }

//                        values.put("cellno", userInput.getText().toString());
//                        db.insert("BlockingNumber", null, values);
//                        db.close();
//
//                        Toast.makeText(getApplicationContext(), "New Number Added Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

}

