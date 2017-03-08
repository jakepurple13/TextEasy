package app.easy.text.texteasy.Receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;

import app.easy.text.texteasy.R;

public class NotificationActivity extends AppCompatActivity {

    int mRequestCode = 1000;
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        TextView textView = (TextView) findViewById(R.id.replyMessage);
        textView.setText(getMessageText(getIntent()));

        String returnMessage = getMessageText(getIntent()).toString();
        phoneNumber = getIntent().getStringExtra("Number");

        sendSMS(phoneNumber, returnMessage);

        finish();

    }

    private void sendSMS(String phoneNumber, String message) {

        String SENT = "SENT_SMS_ACTION";
        String DELIVERED = "DELIVERED_SMS_ACTION";

        Intent sentIntent = new Intent(SENT);
        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, sentIntent, 0);

        Intent deliveryIntent = new Intent(DELIVERED);
        PendingIntent deliver = PendingIntent.getBroadcast(getApplicationContext(), 0, deliveryIntent, 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliver);

    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }


}
