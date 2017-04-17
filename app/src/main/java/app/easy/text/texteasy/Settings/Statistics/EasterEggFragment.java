package app.easy.text.texteasy.Settings.Statistics;

/**
 * Created by Jacob on 4/10/17.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.github.lzyzsd.circleprogress.ArcProgress;

import app.easy.text.texteasy.R;
import me.drakeet.materialdialog.MaterialDialog;


/**
 * Activities that contain this fragment must implement the
 * {@link app.easy.text.texteasy.Tester.TestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link app.easy.text.texteasy.Tester.TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EasterEggFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String text;

    public EasterEggFragment() {
        // Required empty public constructor
    }

    public EasterEggFragment(String hello) {
        text = hello;
    }

    // TODO: Rename and change types and number of parameters
    public static EasterEggFragment newInstance() {
        EasterEggFragment fragment = new EasterEggFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    ArcProgress ap;
    ArcProgress ip;
    MaterialDialog mMaterialDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_easter, container, false);



        mMaterialDialog = new MaterialDialog(getActivity())
                .setTitle("Hi")
                .setMessage("Hello")
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


        SharedPreferences load2 = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int g = load2.getInt("GSBug", 0);

        SharedPreferences load = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int num = load.getInt("roles", 0);


        ip = (ArcProgress) root.findViewById(R.id.imagination);
        ip.setTextColor(Color.rgb(0, 62, 124));
        ip.setFinishedStrokeColor(Color.rgb(0, 62, 124));
        ip.setUnfinishedStrokeColor(Color.rgb(0, 62, 124));

        View.OnClickListener imagine;

        if(num>=1) {
            ip.setProgress(100);
            ip.setBottomText("Imagination");
            imagine = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog.setTitle("Imagination!");
                    mMaterialDialog.setMessage("You used the /me and used your imagination!");
                    mMaterialDialog.show();
                }
            };
        } else {
            ip.setProgress(num/100);
            ip.setBottomText("Mind Fun");
            imagine = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog.setTitle("Mind Fun");
                    mMaterialDialog.setMessage("/me");
                    mMaterialDialog.show();
                }
            };
        }

        ip.setOnClickListener(imagine);





        ap = (ArcProgress) root.findViewById(R.id.arc_progress);
        ap.setTextColor(Color.YELLOW);
        ap.setFinishedStrokeColor(Color.YELLOW);

        View.OnClickListener goldTheme;

        if(g>=79) {
            ap.setProgress(100);
            ap.setBottomText("Gold Theme");
            goldTheme = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog.setTitle("Gold Theme");
                    mMaterialDialog.setMessage("You Unlocked the Gold Theme");
                    mMaterialDialog.show();
                }
            };
        } else {
            ap.setProgress(g/79);
            ap.setBottomText("???");
            goldTheme = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog.setTitle("New Theme");
                    mMaterialDialog.setMessage("Location: About Screen");
                    mMaterialDialog.show();
                }
            };
        }

        ap.setOnClickListener(goldTheme);

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
