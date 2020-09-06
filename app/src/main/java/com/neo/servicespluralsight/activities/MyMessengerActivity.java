package com.neo.servicespluralsight.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.neo.servicespluralsight.R;
import com.neo.servicespluralsight.services.MyMessengerService;


/**
 * Activity for IPC implementation
 */
public class MyMessengerActivity extends AppCompatActivity {

    private boolean mIsBound;
    private TextView txvResult;

    private Messenger mService = null;

    /**
     * handles response from MyMessengerService in another thread or process
     */
    private class IncomingResponseHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msgFromService) {
            switch (msgFromService.what){
                case 87:
                    Bundle bundle = msgFromService.getData();
                    int result = bundle.getInt("result");
                    txvResult.setText("Result: " + result);
                    break;
                default:
                    super.handleMessage(msgFromService);
            }
        }
    }


    // messenger to receive msg sent back from the Service on another process
    private Messenger incomingMessenger = new Messenger(new IncomingResponseHandler());


    // for comm btw MessengerBoundService on another process and this Activity
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // gets ref to messenger present in MyMessengerService class using binder ret
            // msg obj can be used to send msg to remote process and handled by handler assoc with msg obj
            mService = new Messenger(service);
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        txvResult = findViewById(R.id.txvResult);
    }


    public void performAddOperation(View view) {
        EditText etNumOne = findViewById(R.id.etNumOne);
        EditText etNumTwo = findViewById(R.id.etNumTwo);

        // gets values from what user enters in editText
        int num1 = Integer.valueOf(etNumOne.getText().toString());
        int num2 = Integer.valueOf(etNumTwo.getText().toString());

        // sends message to the remote process having the messengerService
        Message msgToService = Message.obtain(null, 43);                 // returns inst of messenger obj
        Bundle bundle = new Bundle();
        bundle.putInt("numOne", num1);
        bundle.putInt("numTwo", num2);
        msgToService.setData(bundle);
        msgToService.replyTo = incomingMessenger;                               // passes messenger obj to reply to in the msg object

        try {
            mService.send(msgToService);                                        // sends message to messengerService and msg handled by handler of this messenger
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void bindService(View view) {
        Intent intent = new Intent(this, MyMessengerService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public void unbindService(View view) {
        if(mIsBound){
            unbindService(mServiceConnection);
            mIsBound = false;
        }
    }
}
