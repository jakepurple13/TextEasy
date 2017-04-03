package app.easy.text.texteasy.Tester;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import app.easy.text.texteasy.About.AboutScreen;
import app.easy.text.texteasy.R;
import me.drakeet.materialdialog.MaterialDialog;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONArray;
import org.json.JSONException;

public class BlankTestingActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    com.afollestad.materialdialogs.MaterialDialog dialog;

    MaterialStyledDialog mSDialog;

    MaterialDialog mMaterialDialog;

    Button one;
    Button two;
    Button three;
    TextView tv;
    Button fb;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String TAG = "BlankTestingActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    CallbackManager mCallbackManager;

    String fbID = "";

    boolean firstTime = false;


    private static final String EXTRA_PROTOCOL_VERSION = "com.facebook.orca.extra.PROTOCOL_VERSION";
    private static final String EXTRA_APP_ID = "com.facebook.orca.extra.APPLICATION_ID";
    private static final int PROTOCOL_VERSION = 20150314;
    private static final String YOUR_APP_ID = "[YOUR_FACEBOOK_APP_ID]";
    private static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank_testing);






        // ...
        mAuth = FirebaseAuth.getInstance();



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
                .setStyle(Style.HEADER_WITH_ICON)
                //.setStyle(Style.HEADER_WITH_TITLE)
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




        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(BlankTestingActivity.this);




        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                firstTime = true;
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        LoginButton loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });



        fb = (Button) findViewById(R.id.facebookLogin);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstTime = true;

                if(!firstTime) {
                    // Initialize Facebook Login button
                    LoginManager.getInstance().logInWithReadPermissions(BlankTestingActivity.this,
                            Arrays.asList("email", "public_profile", "user_friends"));
                    firstTime = true;
                } else {
                    /* make the API call */
                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/" + fbID + "/friends",
                            //"/" + fbID + "/taggable_friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                /* handle the result */
                                    addToText(response.toString());

                                    try {
                                        Log.i(TAG, "onCompleted: " + response.getJSONObject().getJSONArray("data").getString(0));
                                        final JSONArray arr = response.getJSONObject().getJSONArray("data");
                                        List<String> list = new ArrayList<String>();
                                        for(int i = 0; i < arr.length(); i++){
                                            list.add(arr.getJSONObject(i).getString("name"));
                                        }
                                        for (int i = 0; i < list.size(); i++) {
                                            Log.i(TAG, "onCompleted: " + list.get(i));
                                            addToText(list.get(i));
                                        }

                                        tv.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                                //Intent intent = new Intent(Intent.ACTION_SEND);

                                                //startActivityForResult(shareIntent, SHARE_TO_MESSENGER_REQUEST_CODE);

                                                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://messaging/" + fbID + "")));

                                                Uri uri = Uri.parse("fb-messenger://user/");

                                                try {
                                                    uri = ContentUris.withAppendedId(uri,Long.parseLong(arr.getJSONObject(0).getString("id")));
                                                    //uri = ContentUris.withAppendedId(uri,Long.parseLong("100006699257230"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);

                                            }
                                        });



                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    ).executeAsync();
                }

            }
        });


        tv = (TextView) findViewById(R.id.logInfo);

        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);

        one.setText("Login");
        two.setText("Sign In");
        three.setText("Sign Out");

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mMaterialDialog.show();
                //revokeAccess();
                Intent i = new Intent(BlankTestingActivity.this, LoginAcitivities.class);
                startActivity(i);
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure Google Sign In
                //Intent i = new Intent(BlankTestingActivity.this, GoogleSignInActivity.class);
                //startActivity(i);
                signIn();

            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.show();
                signOut();
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1019953559222-a5vrv1fts1t0gr979b911lts8la72em9.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    two.setText("Signed In!");
                    addToText(user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    two.setText("Signed Out!");
                }
                // ...
            }
        };


        fbID = AccessToken.getCurrentAccessToken().getUserId();
        addToText(fbID);

        Log.d("Line 356", AccessToken.USER_ID_KEY);

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        fbID = token.getUserId();
        addToText(fbID);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(BlankTestingActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

        Log.d("Line 386", mAuth.getCurrentUser().getProviderId());
    }

    public void addToText(String text) {
        tv.setText(tv.getText() + "\n" + text);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                two.setText("Signed In!");
                addToText(account.getEmail());
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                two.setText("Signed Out!");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(BlankTestingActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed: " + connectionResult.toString());
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }
}
