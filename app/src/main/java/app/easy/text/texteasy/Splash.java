package app.easy.text.texteasy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ftinc.scoop.Scoop;
import com.google.firebase.messaging.FirebaseMessaging;

import net.alhazmy13.gota.Gota;
import net.alhazmy13.gota.GotaResponse;

import app.easy.text.texteasy.ContactList.Contacts;
import app.easy.text.texteasy.Tester.testingtwo;
import tyrantgit.explosionfield.ExplosionField;
import xyz.hanks.library.SmallBang;
import xyz.hanks.library.SmallBangListener;

/**
 * 
 */
/**
 * 
 */
public class Splash extends AppCompatActivity implements Gota.OnRequestPermissionsBack {

    SmallBang mSmallBang;
    /**
     * 
     * @param savedInstanceState 
     */
    ImageView iv;

    String[] perms = {"android.permission.RECEIVE_SMS",
            "android.permission.CALL_PHONE",
            "android.permission.WRITE_CONTACTS",
            "android.permission.READ_CONTACTS",
            "android.permission.INTERNET",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.SEND_SMS",
            "android.permission.READ_SMS"};

    /**
     * 
     * @param savedInstanceState 
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Scoop.getInstance().apply(this);

        setContentView(R.layout.activity_splash);

        mSmallBang = SmallBang.attach2Window(this);

        iv = (ImageView) findViewById(R.id.imageView);

        //TODO: GET RID OF THIS BEFORE PUBLISHING
        //FirebaseMessaging.getInstance().subscribeToTopic("tests");

        //mExplosionField = ExplosionField.attach2Window(Splash.this);
        //addListener(findViewById(R.id.imageView));

        final String myPackageName = getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {

            askForDefault(myPackageName);
            // App is not default.
            // Show the "not currently set as the default SMS app" interface

        } else {
            // App is the default.
            // Hide the "not currently set as the default SMS app" interface
            askForDefault(myPackageName);
        }

        iv.setVisibility(View.GONE);
        //Have some fun and BANG!
        mSmallBang.bang(iv, 50, new SmallBangListener() {
            @Override
            public void onAnimationStart() {
                iv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd() {

            }
        });


    }
    //After the asking to be the main texting app
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 201:
                //Ask for permission
                AskPermission();
            default:

                break;
        }

    }
    //Ask for permissions!
    public void AskPermission() {
        //Gota! THE BEAUTIFUL!
        new Gota.Builder(Splash.this)
                .withPermissions(perms)
                .requestId(1)
                .setListener(Splash.this)
                .check();

        //Keeping this because it's a good reference for how to do permissions without Gota
        /*int permsRequestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    //AskPermission();
                    //requestPermissions(perms, permsRequestCode);
                    ActivityCompat.requestPermissions(this, perms, permsRequestCode);

                } else {
                    *//**If the app does have their Permission  dont ask again**//*
                    //requestPermissions(perms, permsRequestCode);
                    ActivityCompat.requestPermissions(this, perms, permsRequestCode);
                    next();
                }

            }

            //next();

        }*/

    }

    //Asks to be the default texting app
    public void askForDefault(String myPackageName) {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                myPackageName);
        startActivityForResult(intent, 201);
    }

    //To go to the Contacts activity
    public void next() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(Splash.this, Contacts.class);
                startActivity(i);
                overridePendingTransition(R.anim.back_to_contacts, R.anim.from_contacts);
                finish();

            }
        }, 1000);
    }

    //Dealing with Gota response
    @Override
    public void onRequestBack(int requestId, @NonNull GotaResponse gotaResponse) {
        if(requestId==1) {
            if (gotaResponse.hasDeniedPermission()) {
                Toast.makeText(this, "For full app functions these permissions are needed", Toast.LENGTH_LONG).show();
                new Gota.Builder(Splash.this)
                        .withPermissions(gotaResponse.deniedPermissions())
                        .requestId(1)
                        .setListener(Splash.this)
                        .check();
            } else if (gotaResponse.isAllGranted()) {
                next();
            }
        }
    }
}


