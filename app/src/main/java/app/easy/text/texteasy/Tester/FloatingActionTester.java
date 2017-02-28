package app.easy.text.texteasy.Tester;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListener;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import app.easy.text.texteasy.R;

public class FloatingActionTester extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_action_tester);







        Rect r = new Rect();
        r.set(0,0,0,0);

        Drawable d = getResources().getDrawable(R.drawable.sendbutton);

        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.testCheck), "Check", "Button"),
                        TapTarget.forView(findViewById(R.id.testRating), "Rating", "Bar"),
                        TapTarget.forView(findViewById(R.id.testButton), "You", "Up")
                                .dimColor(R.color.amber_500)
                                .outerCircleColor(R.color.teal_800)
                                .targetCircleColor(R.color.deep_orange_50)
                                .textColor(R.color.black))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        // Yay
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget) {
                        // Perfom action for the current target
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                }).start();

    }
}
