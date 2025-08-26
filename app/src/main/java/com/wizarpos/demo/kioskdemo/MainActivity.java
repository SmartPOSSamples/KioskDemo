package com.wizarpos.demo.kioskdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OptionalDataException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SETTING = 0X11;
    private static final int DEVICECOMPONENTCODE = 0x12;
    private static final String TAG = "MainActivity";
    private ComponentName deviceAdminName;
    private DevicePolicyManager dpm;
    private StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Enter full-screen mode
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        deviceAdminName = new ComponentName(getApplicationContext(),MyDeviceAdminReceiver.class);
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if(dpm.isAdminActive(deviceAdminName)){
            startMain();
        }else {
            satrtOpenDevicer();
        }

//        getMacAddress();

    }

    /**
     * Closing the kiosk to exit the LockTaskMode.
     * @param view
     */
    public void stopKiosk(View view){
        this.stopLockTask();
    }

    public void exit(View view) {
        stopKiosk(view);
        finish();
    }

    public void systemSetting(View view) {
        stopKiosk(view);
        finish();
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivityForResult(intent, REQUEST_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_SETTING == requestCode){
//            this.startLockTask();
        }else if(requestCode == DEVICECOMPONENTCODE){
            startMain();
        }
    }

    private void startMain() {

        try {
            // Allow the current program to enable LockTaskMode. If the current program has not yet obtained the application management permission, this line of code will throw an error and cause the app to crash. Therefore, it is also possible to nest a try statement block or something like that.
            dpm.setLockTaskPackages(deviceAdminName,new String[]{getPackageName()});
        }catch (Exception e){
            e.printStackTrace();
        }

        // 启动 LockTaskMode
        this.startLockTask();
    }

    private void satrtOpenDevicer() {
        ComponentName componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Request device management permission...");
        /*
         * You cannot directly call startActivity because it is possible that the user clicks the cancel button during activation. At this point, the CheckBox is checked, but in fact, the activation has not occurred.
         */
        startActivityForResult(intent, DEVICECOMPONENTCODE);
    }
//    private void getMacAddress() {
//        String mac1 = getMac();
//        Log.d(TAG, "mac1 = " + mac1);
//        Log.d(TAG, "mac2 = " + getMacAddressFromWifiManager() + ", mac3 = " + getMacAddressFromNetworkInterface());
//    }

//    String getMac() {
//        String macSerial = null;
//        String str = "";
//        try {
//            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
//            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
//            LineNumberReader input = new LineNumberReader(ir);
//
//            for (; null != str;) {
//                str = input.readLine();
//                if (str != null) {
//                    macSerial = str.trim();
//                    break;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return macSerial;
//    }
//
//    private String getMacAddressFromWifiManager() {
//        try {
//            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//            if (wifiManager != null) {
//                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                return wifiInfo.getMacAddress();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "Can't get";
//    }
//    private String getMacAddressFromNetworkInterface() {
//        try {
//            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
//            for (NetworkInterface nif : all) {
//                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
//                byte[] macBytes = nif.getHardwareAddress();
//                if (macBytes == null) {
//                    return "02:00:00:00:00:00";
//                }
//                StringBuilder res = new StringBuilder();
//                for (byte b : macBytes) {
//                    res.append(String.format("%02X:", b));
//                }
//                if (res.length() > 0) {
//                    res.deleteCharAt(res.length() - 1);
//                }
//                return res.toString();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "02:00:00:00:00:00";
//    }

}