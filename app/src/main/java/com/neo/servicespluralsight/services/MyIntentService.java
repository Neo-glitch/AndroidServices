package com.neo.servicespluralsight.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;


/**
 * using this service no need to override onStartCommand and onBind()
 */
public class MyIntentService extends IntentService {

    public static final String TAG = MyIntentService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MyIntentService() {
        super("MyWorkerThread");                // name of our background thread
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // background, where work is done
        int sleepTime = intent.getIntExtra("sleepTime", 1);
        ResultReceiver resultReceiver = intent.getParcelableExtra("receiver");

        int ctrl = 1;

        while (ctrl <= sleepTime) {
            Log.i(TAG, "onHandleIntent: counter is now: " + ctrl);
            try {
                Thread.sleep(1000);
                ctrl++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // sends a result back to main activity on received result
        Bundle bundle = new Bundle();
        bundle.putString("resultIntentService", "Counter stopped at: " + ctrl + " seconds");
        resultReceiver.send(18, bundle);
    }



}
