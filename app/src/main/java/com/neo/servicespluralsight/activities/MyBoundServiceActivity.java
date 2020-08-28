package com.neo.servicespluralsight.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.neo.servicespluralsight.services.MyBoundService;
import com.neo.servicespluralsight.R;

public class MyBoundServiceActivity extends AppCompatActivity {

    boolean isBound = false;                                                // true if activity is bound the service
    private MyBoundService myBoundService;

    // to get connection btw calling activity and bound service
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {      // IBinder obj is received when onBind is called in bound service
            isBound = true;

            MyBoundService.MyLocalBinder myLocalBinder = (MyBoundService.MyLocalBinder) service;
            myBoundService = myLocalBinder.getService();                           // gets ref to the MyBoundService class
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound=false;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }


    @Override
    protected void onStart() {                       // called when activity starts
        super.onStart();

        Intent intent = new Intent(this, MyBoundService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);              // triggers onBind in BoundService class
    }

    @Override
    protected void onStop() {                       // when activity not visible to user
        super.onStop();

        if(isBound){
            unbindService(mServiceConnection);
            isBound = false;
        }
    }

    public void onClickEvent(View view) {
        EditText etNumOne = findViewById(R.id.etNumOne);
        EditText etNumTwo = findViewById(R.id.etNumTwo);
        TextView txvResult = findViewById(R.id.txvResult);

        int numOne = Integer.valueOf(etNumOne.getText().toString());
        int numTwo = Integer.valueOf(etNumTwo.getText().toString());

        String resultStr = "";

        if (isBound) {
            switch (view.getId()){
                case R.id.btnAdd:
                    resultStr = String.valueOf(myBoundService.add(numOne, numTwo));
                    break;
                case R.id.btnSub:
                    resultStr = String.valueOf(myBoundService.subtract(numOne, numTwo));
                    break;
                case R.id.btnMul:
                    resultStr = String.valueOf(myBoundService.multiply(numOne, numTwo));
                    break;
                case R.id.btnDiv:
                    resultStr = String.valueOf(myBoundService.divide(numOne, numTwo));
                    break;
            }

            txvResult.setText(resultStr);
        }

    }
}
