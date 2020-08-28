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

    private Messenger incomingMessenger = new Messenger(new IncomingResponseHandler());



    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);                  // gets ref to messenger present in MyMessengerService class
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
        msgToService.replyTo = incomingMessenger;                               // reply to messenger, and expects a messenger ref

        try {
            mService.send(msgToService);                                          // triggers handler present in MyMessengerService class
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
