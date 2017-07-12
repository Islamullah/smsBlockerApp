package com.example.junaid.smslistview;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by DILAWAR on 16-Jun-16.
 */
public class MyBlockListAdapter extends CursorAdapter {

    private final LayoutInflater inflater;
    String address, body;
    long time;

    public MyBlockListAdapter(Context context, Cursor c) {
        super(context, c, false);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        time = cursor.getLong(cursor.getColumnIndex("time"));

        address = cursor.getString(cursor.getColumnIndex("address"));
        body = cursor.getString(cursor.getColumnIndex("body"));

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        String format = "M/dd h:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateString = sdf.format(cal.getTime());

        String ctName = getContactName(address,context);
        if(ctName==null)
        {
            ((TextView) view.findViewById(R.id.lblTime)).setText(dateString);
            ((TextView) view.findViewById(R.id.lblMsg)).setText(body);
            ((TextView) view.findViewById(R.id.lblNumber)).setText(address);
        }
        else
        {
            ((TextView) view.findViewById(R.id.lblTime)).setText(dateString);
            ((TextView) view.findViewById(R.id.lblMsg)).setText(body);
            ((TextView) view.findViewById(R.id.lblNumber)).setText(ctName);
        }

    }

    public String getContactName(String number, Context context ) {
        String cName = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String nameColumn[] = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor c =  context.getContentResolver().query(uri, nameColumn, null, null, null);
        if(c == null || c.getCount() == 0)
            return cName;
        c.moveToFirst();
        cName = c.getString(0);
        return cName;

    }

}
