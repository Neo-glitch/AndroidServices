package com.neo.servicespluralsight.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.neo.servicespluralsight.services.MyIntentService;
import com.neo.servicespluralsight.services.MyStartedService;
import com.neo.servicespluralsight.R;




/**
 * Author: Sriyank Siddhartha
 * <p>
 * Module 3: "Working with Started Service"
 * <p>
 * "BEFORE" project
 */
public class MainActivity extends AppCompatActivity {
    private TextView tvIntentServiceResult, tvStartedServiceResult;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvIntentServiceResult = findViewById(R.id.txvIntentServiceResult);
        tvStartedServiceResult = findViewById(R.id.txvStartedServiceResult);
    }

    public void startStartedService(View view) {

        Intent intent = new Intent(this, MyStartedService.class);
        intent.putExtra("sleepTime", 10);
        startService(intent);
    }

    public void stopStartedService(View view) {
        Intent intent = new Intent(this, MyStartedService.class);
        stopService(intent);
    }

    public void startIntentService(View view) {
        ResultReceiver myResultReceiver = new MyResultReceiver(null);

        Intent intent = new Intent(this, MyIntentService.class);
        intent.putExtra("sleepTime", 10);
        intent.putExtra("receiver", myResultReceiver);                              // passes the result receiver(parceable) to the intent starting service
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // intent filter opp to reg the broadcastReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.service.to.activity");
        registerReceiver(myStartedServiceReceiver, intentFilter);
    }

    // creates broadcastReceiver dynamically, and receives intent broadCasted that meets the criteria action
    private BroadcastReceiver myStartedServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // on UI thread or main thread
            String result = intent.getStringExtra("startServiceResult");
            tvStartedServiceResult.setText(result);

        }
    };

    @Override
    protected void onPause() {
        super.onPause();

        // unreg the broadcastReceiver( to avoid memLeaks)
        unregisterReceiver(myStartedServiceReceiver);
    }

    public void moveToSecondActivity(View view) {
        startActivity(new Intent(this, MyBoundServiceActivity.class));
    }

    public void moveToMessengerActivity(View view) {
        startActivity(new Intent(this, MyMessengerActivity.class));
    }

    // receives data back from MyIntentService class using ResultReceiver( can also be used with startedService)
    private class MyResultReceiver extends ResultReceiver {


        public MyResultReceiver(Handler handler) {
            super(handler);
        }


        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // receives data from intent service class and in background thread
            super.onReceiveResult(resultCode, resultData);

            if (resultCode == 18 && resultData != null) {
                final String result = resultData.getString("resultIntentService");

                mHandler.post(new Runnable() {
                    // to post data back to UI thread, works in main thread since handler is of the mainThread
                    @Override
                    public void run() {
                        tvIntentServiceResult.setText(result);
                    }
                });
            }
        }


    }
}
