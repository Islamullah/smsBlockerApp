package com.example.junaid.smslistview;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class createNew extends AppCompatActivity {

    EditText Num, Msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Num = (EditText) findViewById(R.id.toNumber);
        Msg = (EditText) findViewById(R.id.toMessage);
        Button send = (Button) findViewById(R.id.sendBtn);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startNewActivity();
            }
        });
    }

    public void sendMessage(View view)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(Num.getText().toString(), null  , Msg.getText().toString(), null, null);
        Toast.makeText(this, "Message has been sent!", Toast.LENGTH_LONG).show();
    }

    public void startNewActivity()
    {

        Intent i = new Intent(getApplicationContext(), createNew.class);
        startActivity(i);
    }


}
