package app.easy.text.texteasy.Settings.Statistics;

/**
 * Created by Jacob on 4/10/17.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.ftinc.scoop.Scoop;
import com.github.anastr.speedviewlib.ProgressiveGauge;
import com.github.anastr.speedviewlib.base.Gauge;
import com.github.jinatonic.confetti.CommonConfetti;
import com.xenione.digit.TabDigit;

import app.easy.text.texteasy.R;


/**
 * Activities that contain this fragment must implement the
 * {@link app.easy.text.texteasy.Tester.TestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link app.easy.text.texteasy.Tester.TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatFragment extends Fragment implements Runnable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String text;

    public StatFragment() {
        // Required empty public constructor
    }

    public StatFragment(Statistic s, int con, String texts) {
        this.s = s;

        /*SharedPreferences load1 = getActivity().getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
        int numOfC = load1.getInt("numOfContacts", 0);

        SharedPreferences load = getActivity().getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
        String num = load.getInt("DotNum", 0) + "";

        numOfTexts = num;
        numOfContacts = numOfC;*/

        numOfTexts = texts;
        numOfContacts = con;

    }

    // TODO: Rename and change types and number of parameters
    public static StatFragment newInstance(Statistic s, int c, String t) {

        SharedPreferences load1 = s.getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
        int numOfContacts = load1.getInt("numOfContacts", 0);

        SharedPreferences load = s.getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
        String num = load.getInt("DotNum", 0) + "";

        //StatFragment fragment = new StatFragment(s, numOfContacts, num);
        StatFragment fragment = new StatFragment(s, c, t);

        //Log.i("VARIABLES", "newInstance: " + num  + ":" + numOfContacts);

        Log.i("VARIABLES", "newInstance: " + c  + ":" + t);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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


    @Override
    public void onPause() {
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
    public void onResume() {
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
        if (counter != getNum(num, digit)) {
            t.start();
            counter++;
            CommonConfetti.rainingConfetti(cl, new int[]{Color.BLACK, Color.MAGENTA, Color.BLUE})
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

    Statistic s;
    int numOfContacts;
    String numOfTexts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_stat, container, false);
        //Texts Sent------------------

        Log.i("VARIABLES OF STAT", "onCreateView: " + num + ":" + numOfTexts + ":" + numOfContacts);

        progressiveGauge = (ProgressiveGauge) root.findViewById(R.id.progressiveGauge);

        progressiveGauge.setMaxSpeed((int) (numOfContacts*1.5));

        // changes speed to contacts
        progressiveGauge.speedTo(numOfContacts);
        progressiveGauge.setUnit("Contacts");
        progressiveGauge.setSpeedTextPosition(Gauge.Position.BOTTOM_RIGHT);
        progressiveGauge.setWithTremble(false);
        progressiveGauge.setSpeedometerColor(R.color.lavender_indigo);


        //Contacts---------------

        cl = (RelativeLayout) root.findViewById(R.id.activity_floating_action_tester);

        num = numOfTexts + "";
        if(num.length()!=6) {
            for(int i=num.length();i<6;i++) {
                num="0" + num;
            }
        }

        htd = (TabDigit) root.findViewById(R.id.tabDigit1);
        ttd = (TabDigit) root.findViewById(R.id.tabDigit2);
        otd = (TabDigit) root.findViewById(R.id.tabDigit3);
        hd = (TabDigit) root.findViewById(R.id.tabDigit4);
        td = (TabDigit) root.findViewById(R.id.tabDigit5);
        od = (TabDigit) root.findViewById(R.id.tabDigit6);

        htd.setChars(digits);
        ttd.setChars(digits);
        otd.setChars(digits);
        hd.setChars(digits);
        td.setChars(digits);
        od.setChars(digits);

        new Handler().postDelayed(this, 1000);
        return root;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
