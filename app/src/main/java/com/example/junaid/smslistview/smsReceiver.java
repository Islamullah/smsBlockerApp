package com.example.junaid.smslistview;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class smsReceiver extends BroadcastReceiver {
    public smsReceiver() {
    }

    private String cellNo, msgBody;
    long timestamp;
    Context context;
    int flag = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
//—get the SMS message passed in—
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        SmsMessage sms;
        String messages = "";
        if (bundle != null)
        {
//—retrieve the SMS message received—
            Object[] smsExtra = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[smsExtra.length];

            for (int i=0; i<msgs.length; i++) {
                sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
//take out content from sms
                msgBody = sms.getMessageBody().toString();
                cellNo = sms.getOriginatingAddress();
                timestamp = System.currentTimeMillis();
//                Toast.makeText(context, String.valueOf(timestamp), Toast.LENGTH_LONG).show();
//                Date d = new Date(timestamp);
//                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(timestamp);
//                Log.i("time", "time" + formatter.format(calendar.getTime()));
//                String time = String.valueOf(formatter.format(calendar.getTime()));
//                Toast.makeText(context, String.valueOf(time), Toast.LENGTH_LONG).show();
//                time = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
//                time += " " + java.text.DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
            }
//============================check whether the number is blocked or not



                myDBHandler myDB = new myDBHandler(context);
                String[] addresses =  myDB.getAllNumbers();

                for (int i = 1; i <addresses.length; i++)
                {
                    messages += addresses[i];
                    messages += "\n";
                }
                Toast.makeText(context, messages, Toast.LENGTH_LONG).show();

                SQLiteDatabase db;

                db = SQLiteDatabase.openDatabase("/data/data/com.example.junaid.smslistview/databases/sms1.db", null, SQLiteDatabase.OPEN_READONLY);
            Toast.makeText(context, "database is opened", Toast.LENGTH_LONG).show();

                Cursor c = db.query("BlockingNumber", new String[]{"_id", "cellno"}, null, null, null, null, null);
                c.moveToFirst();

                String blocked;

                while (!c.isAfterLast()) {
                    if (c.getString(c.getColumnIndex("cellno")) != null) {
                        blocked = c.getString(c.getColumnIndex("cellno"));

                        if (cellNo.equals(blocked)) {
                            putSmsToDatabase(context);
//                            Toast.makeText(context, "sms added to Blocked List", Toast.LENGTH_LONG).show();
                            flag = 1;
                            break;
                        }

                    }
                    c.moveToNext();
                }

                db.close();
//--------------------------


            if(flag == 0)
            {
//                Toast.makeText(context, "flag called", Toast.LENGTH_SHORT).show();

                //save incoming message into inbox if not blocked

                ContentValues values = new ContentValues();
                values.put("address", cellNo);
                values.put("date", timestamp);
//        values.put("read", "1");
//        values.put("type", "1");
                values.put("body", msgBody);
                Uri uri = Uri.parse("content://sms/inbox");
                context.getContentResolver().insert(uri, values);

                abortBroadcast();
                //updatingInbox

            }
        }

    }

    private void putSmsToDatabase(Context context)
    {
        myDBHandler dataBaseHelper = new myDBHandler(context);

        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
// Create SMS row
        ContentValues values = new ContentValues();

        values.put("address", cellNo);
        values.put("time", timestamp);
        values.put("body", msgBody);
// values.put( READ, MESSAGE_IS_NOT_READ );
// values.put( STATUS, sms.getStatus() );
// values.put( TYPE, MESSAGE_TYPE_INBOX );
// values.put( SEEN, MESSAGE_IS_NOT_SEEN );

        db.insert("Blocked", null, values);

        db.close();

        abortBroadcast();

    }

















//    /*@Override
//    public void onReceive(Context context, Intent intent) {
//
//        // TODO: This method is called when the BroadcastReceiver is receiving
//        // an Intent broadcast.
//        Toast t =  Toast.makeText(context, "onReceive called", Toast.LENGTH_SHORT);
//        t.show();
//
//        Bundle myBundle = intent.getExtras();
//        SmsMessage[] messages = null;
//        String strMessage = "";
//
//        if (myBundle != null)
//        {
//            Object [] pdus = (Object[]) myBundle.get("pdus");
//
//            messages = new SmsMessage[pdus.length];
//
//            for (int i = 0; i < messages.length; i++)
//            {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    String format = myBundle.getString("format");
//                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
//                }
//                else {
//                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                }
//
//                smsDBOC sm = new smsDBOC();
//                sm.setThings(messages[i].getOriginatingAddress().toString(), messages[i].getMessageBody().toString(),
//                                java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime()) + " " +
//                                java.text.DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));
//
//                //putSmsToDatabase(messages[i], context);
//
//
//                Toast.makeText(context, "SMS is saved in database", Toast.LENGTH_LONG);
//
//                strMessage += "SMS From: " + cellNo;
//                strMessage += " : ";
//                strMessage += msgBody;
//                strMessage += "\n";
//                strMessage += time;
//
//            }
//
//            Log.e("SMS", strMessage);
//            Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
//        }
//
//        abortBroadcast();
//        Toast.makeText(context, "Broadcast Aborted!", Toast.LENGTH_LONG).show();
//    }
//
//    private void putSmsToDatabase(SmsMessage sms, Context context)
//    {
//        myDBHandler dataBaseHelper = new myDBHandler(context, null, null, 1);
//
//        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
//
//        cellNo = sms.getOriginatingAddress().toString();
//        msgBody = sms.getMessageBody().toString();
//        time = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
//        time += " " + java.text.DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
//
//// Create SMS row
//        ContentValues values = new ContentValues();
//
//        values.put("Cell", cellNo);
//        values.put("Body", msgBody);
//        values.put("Time", time);
//
//// values.put( READ, MESSAGE_IS_NOT_READ );
//// values.put( STATUS, sms.getStatus() );
//// values.put( TYPE, MESSAGE_TYPE_INBOX );
//// values.put( SEEN, MESSAGE_IS_NOT_SEEN );
//
//        db.insert("blockedSms", null, values);
//
//        db.close();
//
//    }*/

}
