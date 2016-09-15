package app.easy.text.texteasy;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import app.easy.text.texteasy.Receiver.SmsReceiver;

/**
 * Created by Jacob on 9/13/16.
 */
public class HeadlessSmsSendService extends Service {


    private SmsReceiver smsReceiver;
    final IntentFilter smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

    @Override
    public void onCreate()
    {
        smsFilter.setPriority(1000);
        this.smsReceiver = new SmsReceiver();
        this.registerReceiver(this.smsReceiver, smsFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
