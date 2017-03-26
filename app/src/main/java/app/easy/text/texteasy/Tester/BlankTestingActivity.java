package app.easy.text.texteasy.Tester;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import app.easy.text.texteasy.About.AboutScreen;
import app.easy.text.texteasy.R;
import me.drakeet.materialdialog.MaterialDialog;

public class BlankTestingActivity extends AppCompatActivity {

    com.afollestad.materialdialogs.MaterialDialog dialog;

    MaterialStyledDialog mSDialog;

    MaterialDialog mMaterialDialog;

    Button one;
    Button two;
    Button three;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank_testing);

        dialog = new com.afollestad.materialdialogs.MaterialDialog.Builder(this)
                .title("Tada")
                .content("Tada")
                .positiveText("Yup")
                .negativeText("Nope")
                .onAny(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull com.afollestad.materialdialogs.MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build();



        mSDialog = new MaterialStyledDialog.Builder(this)
                .setTitle("Awesome!")
                .setDescription("What can we improve? Your feedback is always welcome.")
                //.setStyle(Style.HEADER_WITH_ICON)
                .setStyle(Style.HEADER_WITH_TITLE)
                .withDialogAnimation(true)
                .setIcon(R.drawable.texteasyicon)
                .setPositiveText("Yup")
                .setNegativeText("Nope")
                .build();



        mMaterialDialog = new MaterialDialog(BlankTestingActivity.this)
                .setTitle("MaterialDialog")
                .setMessage("Hello world!")
                .setCanceledOnTouchOutside(true)
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });


        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.show();
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSDialog.show();
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });


    }
}
