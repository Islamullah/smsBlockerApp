package com.example.junaid.smslistview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;

/**
 * @author appsrox.com
 *
 */
public class ReadActivity extends Activity implements AppCompatCallback {

//	private static final String TAG = "ReadActivity";

    private TextView tv;
    private AppCompatDelegate delegate;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        delegate.onCreate(savedInstanceState);
        setContentView(R.layout.content_read);

        delegate = AppCompatDelegate.create(this, this);

        delegate.setContentView(R.layout.activity_read);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        delegate.setSupportActionBar(toolbar);

        Toast.makeText(this, "activity is working fine", Toast.LENGTH_SHORT).show();

        tv = (TextView) findViewById(R.id.textView1);
        tv.setTextSize(12);

        String flag = getIntent().getStringExtra("flag");
        String id = getIntent().getStringExtra("_id");
        String[] projection = {"_id", "address", "date", "body"};
        String selection = "_id = ?";
        String[] selectionArgs = {id};

        if (flag.equals("inbox"))
        {
            Uri inboxURI = Uri.parse("content://sms/inbox");

            Cursor c = getContentResolver().query(inboxURI, projection, selection, selectionArgs, null);
            if (c.moveToFirst()) {
                toolbar.setTitle(c.getString(c.getColumnIndex("address")));
                tv.setText(c.getString(c.getColumnIndex("body")));
            }
        }
        else if (flag.equals("blkList"))
        {
            String[] projection1 = {"_id", "address", "time", "body"};
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.junaid.smslistview/databases/sms1.db", null);
            Cursor c = db.query("Blocked", projection1, selection, selectionArgs,null, null, null);
            if (c.moveToFirst())
            {
                toolbar.setTitle(c.getString(c.getColumnIndex("address")));
                tv.setText(c.getString(c.getColumnIndex("body")));
            }
        }




    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }
}
