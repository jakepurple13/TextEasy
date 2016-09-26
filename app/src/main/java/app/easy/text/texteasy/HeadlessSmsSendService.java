package app.easy.text.texteasy;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import app.easy.text.texteasy.Receiver.SmsReceiver;

/**
 * Created by Jacob on 9/13/16.
 */
public class HeadlessSmsSendService extends Service {


    //private SmsReceiver smsReceiver;
    final IntentFilter smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

    //private SmsReceiver mSMSreceiver;
    private IntentFilter mIntentFilter;

    /**
     * 
     */
    /**
     * 
     */
    @Override
    public void onCreate() {


        super.onCreate();

        //SMS event receiver
        /*mSMSreceiver = new SmsReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSMSreceiver, mIntentFilter);
        smsFilter.setPriority(1000);
        this.smsReceiver = new SmsReceiver();
        this.registerReceiver(this.smsReceiver, smsFilter);*/

        Toast.makeText(this, "GOT IT", Toast.LENGTH_LONG).show();

    /**
     * 
     */
    }


    /**
     * 
     */
    @Override
    public void onDestroy() {
    /**
     * 
     * @param intent 
     */
        super.onDestroy();

        // Unregister the SMS receiver
        //unregisterReceiver(mSMSreceiver);
    }

    /**
     * 
     * @param intent 
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


