package com.wizarpos.demo.kioskdemo;

import android.app.admin.DeviceAdminReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent)     {
        super.onEnabled(context, intent);
        Intent itn = new Intent(context,
                MainActivity.class);
        itn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(itn);
    }

}