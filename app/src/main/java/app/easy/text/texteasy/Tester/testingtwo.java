package app.easy.text.texteasy.Tester;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.Space;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.mooveit.library.Fakeit;

import java.util.ArrayList;
import java.util.List;

import app.easy.text.texteasy.R;
import eu.long1.spacetablayout.SpaceTabLayout;

public class testingtwo extends AppCompatActivity {

    SpaceTabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testingtwo);


        // Default locale is en
        Fakeit.init(this);

        ArrayList<ContactInfoTest> alc = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            alc.add(new ContactInfoTest(Fakeit.name().name(), Fakeit.phone().formats(), Fakeit.chuckNorris().fact()));
        }

        String q = "";

        for (int i = 0; i < alc.size(); i++) {
            q+=alc.get(i).toString() + "\n";
        }

        TextView test = (TextView) findViewById(R.id.textView5);

        //test.setText(q);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set(v);
            }
        });


        /*TapTargetSequence tts = new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.never), "Gonna"),
                        TapTarget.forView(findViewById(R.id.give), "You", "Up")
                                .dimColor(android.R.color.never)
                                .outerCircleColor(R.color.gonna)
                                .targetCircleColor(R.color.let)
                                .textColor(android.R.color.you),
                        TapTarget.forBounds(rickTarget, "Down", ":^)")
                                .cancelable(false)
                                .icon(R.mipmap.android))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        // Yay
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                });*/

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new TestFragment("Hello"));
        fragmentList.add(new TestFragment("Good Bye"));
        fragmentList.add(new TestFragment("Yes"));
        fragmentList.add(new TestFragment("No"));
        fragmentList.add(new TestFragment("Maybe"));


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (SpaceTabLayout) findViewById(R.id.spaceTabLayout);

        //we need the savedInstanceState to get the position
        tabLayout.initialize(viewPager, getSupportFragmentManager(),
                fragmentList, savedInstanceState);



    }

    //we need the outState to save the position
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        tabLayout.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    public void set(View v) {
        TapTargetView.showFor(this,
                TapTarget.forView(v, "asdf", "asdf" + "\nPress and hold to bring up again")
                        .cancelable(true)
                        .drawShadow(true)
                        .tintTarget(true)
                        .transparentTarget(false)
                        .outerCircleColor(R.color.primary)
                        .targetCircleColor(R.color.primary_dark),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        view.dismiss(true);
                    }

                    @Override
                    public void onTargetLongClick(TapTargetView view) {

                    }
                });
    }
}
