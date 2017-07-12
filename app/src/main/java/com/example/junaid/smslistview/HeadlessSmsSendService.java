package com.example.junaid.smslistview;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Junaid on 5/15/2016.
 */
public class HeadlessSmsSendService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
