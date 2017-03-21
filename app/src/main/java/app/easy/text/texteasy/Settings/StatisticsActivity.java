package app.easy.text.texteasy.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ftinc.scoop.Scoop;
import com.github.anastr.speedviewlib.ProgressiveGauge;
import com.github.anastr.speedviewlib.base.Gauge;
import com.github.jinatonic.confetti.CommonConfetti;
import com.xenione.digit.TabDigit;

import app.easy.text.texteasy.R;

public class StatisticsActivity extends AppCompatActivity implements Runnable {

    String TAG = "FloatingActionTester";

    RelativeLayout cl;

    TabDigit htd;
    TabDigit ttd;
    TabDigit otd;
    TabDigit hd;
    TabDigit td;
    TabDigit od;

    char[] digits = "0123456789".toCharArray();

    private boolean mPause = true;

    String num;

    int ht = 0;
    int tt = 0;
    int ot = 0;
    int h = 0;
    int t = 0;
    int o = 0;

    ProgressiveGauge progressiveGauge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Scoop.getInstance().apply(this);

        setContentView(R.layout.activity_statistics);


        //Texts Sent------------------


        progressiveGauge = (ProgressiveGauge) findViewById(R.id.progressiveGauge);

        SharedPreferences load1 = getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
        int numOfContacts = load1.getInt("numOfContacts", 0);

        progressiveGauge.setMaxSpeed((int) (numOfContacts*1.5));

        // change speed to 50 Km/h
        progressiveGauge.speedTo(numOfContacts);
        progressiveGauge.setTrembleDegree(0);
        progressiveGauge.setUnit("Contacts");
        progressiveGauge.setSpeedTextPosition(Gauge.Position.BOTTOM_RIGHT);


        //Contacts---------------

        cl = (RelativeLayout) findViewById(R.id.activity_floating_action_tester);

        SharedPreferences load = getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
        num = load.getInt("DotNum", 0 ) + "";
        if(num.length()!=6) {
            for(int i=num.length();i<6;i++) {
                num="0" + num;
            }
        }

        htd = (TabDigit) findViewById(R.id.tabDigit1);
        ttd = (TabDigit) findViewById(R.id.tabDigit2);
        otd = (TabDigit) findViewById(R.id.tabDigit3);
        hd = (TabDigit) findViewById(R.id.tabDigit4);
        td = (TabDigit) findViewById(R.id.tabDigit5);
        od = (TabDigit) findViewById(R.id.tabDigit6);

        htd.setChars(digits);
        ttd.setChars(digits);
        otd.setChars(digits);
        hd.setChars(digits);
        td.setChars(digits);
        od.setChars(digits);

        new Handler().postDelayed(this, 1000);



    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    public void pause() {
        mPause = true;
        hd.sync();
        td.sync();
        od.sync();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume();
    }

    public void resume() {
        mPause = false;
    }


    @Override
    public void run() {
        if(mPause) {
            return;
        }

        ht = nextCount(htd, ht, 0);
        tt = nextCount(ttd, tt, 1);
        ot = nextCount(otd, ot, 2);
        h = nextCount(hd, h, 3);
        t = nextCount(td, t, 4);
        o = nextCount(od, o, 5);

        ViewCompat.postOnAnimationDelayed(hd, this, 1000);
    }

    public int nextCount(TabDigit t, int counter, int digit) {
        if(counter!=getNum(num, digit)) {
            t.start();
            counter++;
            CommonConfetti.rainingConfetti(cl, new int[] { Color.BLACK, Color.MAGENTA, Color.BLUE })
                    .oneShot()
                    .setTouchEnabled(true);
        }
        return counter;
    }

    public int getNum(String numb, int digit) {

        String number = numb+"";

        int t = Integer.parseInt(number.substring(digit, digit+1));

        return t;
    }

}
