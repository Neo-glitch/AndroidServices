package com.neo.servicespluralsight.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyStartedService extends Service {

    public static final String TAG = MyStartedService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: Thread name: " + Thread.currentThread().getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: Thread name: " + Thread.currentThread().getName());
        int sleepTime = intent.getIntExtra("sleepTime", 1);
        new MyAsyncTask().execute(sleepTime);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: Thread name: " + Thread.currentThread().getName());
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: Thread name: " + Thread.currentThread().getName());
        return null;                        // always return null to make a started service
    }

    /**
     * for running long Service Tasks in background thread
     */
    class MyAsyncTask extends AsyncTask<Integer, String, String> {

        final String TAG = MyAsyncTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            // mainThread
            super.onPreExecute();
            Log.i(TAG, "onPreExecute: Thread name : " + Thread.currentThread().getName());
        }

        @Override
        protected String doInBackground(Integer... ints) {
            // background
            Log.i(TAG, "doInBackground: Thread name: " + Thread.currentThread().getName());

            int sleepTime = ints[0];
            int ctrl = 1;

            while (ctrl <= sleepTime) {
                publishProgress("counter is now: " + ctrl);
                try {
                    Thread.sleep(1000);
                    ctrl++;
                } catch(InterruptedException e){
                    e.printStackTrace();
                }

            }

            return "Counter stopped at: " + ctrl + " seconds";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // mainThread
            super.onProgressUpdate(values);
            Toast.makeText(MyStartedService.this, values[0], Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Counter Value: " + values[0] + "onProgressUpdate: Thread name: " + Thread.currentThread().getName());
        }

        @Override
        protected void onPostExecute(String str) {
            // mainThread
            super.onPostExecute(str);
            stopSelf();                                 // destroys service

            Log.i(TAG, "onPostExecute: Thread name: " + Thread.currentThread().getName());


            Intent intent = new Intent("action.service.to.activity");            // the string action acts as identifier
            intent.putExtra("startServiceResult", str);
            sendBroadcast(intent);                                                      // broadcast and intent that we will receive in mainActivity bReceiver
        }
    }
}
