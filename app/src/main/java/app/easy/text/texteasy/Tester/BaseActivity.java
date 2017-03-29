package app.easy.text.texteasy.Tester;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Jacob on 3/27/17.
 */

public class BaseActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Wait!");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
