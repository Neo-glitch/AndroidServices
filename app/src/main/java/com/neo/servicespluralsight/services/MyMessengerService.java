package com.neo.servicespluralsight.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyMessengerService extends Service {

    /**
     * class handles message coming from another process(MyMessengerActivity)
     */
    private class IncomingHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch(msg.what){                   // gets what opp is to be done in the service class
                case 43:
                    Bundle bundle = msg.getData();
                    int numOne = bundle.getInt("numOne", 0);
                    int numTwo = bundle.getInt("numTwo", 0);

                    int res = addNumber(numOne, numTwo);
                    Toast.makeText(getApplicationContext(), "Result: " + res, Toast.LENGTH_LONG).show();

                    // Sends data back to calling activity
                    Messenger incomingMessenger = msg.replyTo;                      // gets the ref of incoming messenger from MessengerActivity passed when sending the message
                    Message msgToActivity = Message.obtain(null, 87);

                    Bundle bundleToActivity = new Bundle();
                    bundleToActivity.putInt("result", res);
                    msgToActivity.setData(bundleToActivity);
                    try {
                        incomingMessenger.send(msgToActivity);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    Messenger mMessenger = new Messenger(new IncomingHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();                                                          // binder helps to comm with activity on another process
    }

    public int addNumber(int a, int b){
        return a + b;
    }
}
