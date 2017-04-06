package app.easy.text.texteasy.Settings.Statistics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.ftinc.scoop.Scoop;
import com.github.anastr.speedviewlib.ProgressiveGauge;
import com.github.anastr.speedviewlib.base.Gauge;
import com.github.jinatonic.confetti.CommonConfetti;
import com.xenione.digit.TabDigit;

import app.easy.text.texteasy.R;

public class StatisticsActivity extends AppCompatActivity implements Runnable {

    //The relative layout
    RelativeLayout cl;
    //The tabs for a counter
    //hundred thousand
    TabDigit htd;
    //ten thousand
    TabDigit ttd;
    //one thousand
    TabDigit otd;
    //hundred
    TabDigit hd;
    //tens
    TabDigit td;
    //ones
    TabDigit od;
    //the characters to put on the tabs
    char[] digits = "0123456789".toCharArray();

    private boolean mPause = true;

    String num;
    //digits for the tabs
    int ht = 0;
    int tt = 0;
    int ot = 0;
    int h = 0;
    int t = 0;
    int o = 0;

    //speedometer
    ProgressiveGauge progressiveGauge;

    private MaterialMenuDrawable materialMenu;
    //next page >.> probably should change this to a fragment and sliding activity <.<
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SCOOP! Deals with the theme
        Scoop.getInstance().apply(this);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.statTool);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle your drawable state here
                materialMenu.animateIconState(MaterialMenuDrawable.IconState.CHECK);
                onBackPressed();
            }
        });

        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
        toolbar.setNavigationIcon(materialMenu);

        next = (Button) findViewById(R.id.nextStat);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StatisticsActivity.this, StatisticsPage2.class);
                startActivity(i);
                overridePendingTransition(R.anim.back_to_contacts, R.anim.from_contacts);
                finish();
            }
        });


        //Texts Sent------------------


        progressiveGauge = (ProgressiveGauge) findViewById(R.id.progressiveGauge);

        SharedPreferences load1 = getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
        int numOfContacts = load1.getInt("numOfContacts", 0);

        progressiveGauge.setMaxSpeed((int) (numOfContacts*1.5));

        // changes speed to contacts
        progressiveGauge.speedTo(numOfContacts);
        progressiveGauge.setUnit("Contacts");
        progressiveGauge.setSpeedTextPosition(Gauge.Position.BOTTOM_RIGHT);
        progressiveGauge.setWithTremble(false);
        progressiveGauge.setSpeedometerColor(R.color.lavender_indigo);


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
