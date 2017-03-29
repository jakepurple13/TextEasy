package app.easy.text.texteasy.Messages;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import app.easy.text.texteasy.R;
import app.easy.text.texteasy.Tester.BlankTestingActivity;

/**
 * Created by Jacob on 9/12/16.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final String TAG = "MessageAdapter";
    private ArrayList<MainActivity.TextInfo> mDataset;

    MainActivity in;
    int lastPos = -1;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    /**
     * 
     */
    /**
     * 
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView mTextView;
        public TextView dateView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.textView);
            dateView = (TextView) v.findViewById(R.id.dateOfText);

        }


    }


    // Provide a suitable constructor (depends on the kind of dataset)
    /**
     * 
     * @param myDataset 
     * @param in 
     */
    public MessageAdapter(ArrayList<MainActivity.TextInfo> myDataset, MainActivity in) {
        mDataset = myDataset;
        this.in = in;
    }

    // Create new views (invoked by the layout manager)

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        View v;
        if(viewType==1) {
            // create a new view
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chatmessagethem, parent, false);
            // set the view's size, margins, paddings and layout parameters
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chatmessageyou, parent, false);
        }

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        int fromTo = mDataset.get(position).fromTo;
        //from == 1
        //to == everything else

        return fromTo;
    }


    boolean isHidden = true;

    // Replace the contents of a view (invoked by the layout manager)
    /**
     * 
     * @param holder 
     * @param position 
    /**
     * 
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.mTextView.setText(mDataset.get(position).toString());
        holder.mTextView.setLinksClickable(true);

        String pattern = "hh:mm:ss a MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        Date date;

        date = mDataset.get(position).dateOfText;
        //In case it becomes null, which only happens for a sent text
        if(date==null) {
            date = new Date(System.currentTimeMillis());
        }

        holder.dateView.setText(format.format(date));

        holder.dateView.setVisibility(View.GONE);
        SharedPreferences load = PreferenceManager.getDefaultSharedPreferences(in);
        String num = load.getString("stroke_width_choice", "1");
        if(num.equals("Default value"))
            num = "1";


        if(mDataset.get(position).fromTo==1) { //from
            GradientDrawable bgShape = (GradientDrawable) holder.mTextView.getBackground();
            //bgShape.setColor(getColored(R.color.pure_gray)); //blue
            //bgShape.setColor(R.color.dark_color); //blue
            bgShape.setStroke(Integer.parseInt(num), Color.BLACK);
            holder.mTextView.setGravity(Gravity.LEFT);

        } else { //to
            GradientDrawable bgShape = (GradientDrawable) holder.mTextView.getBackground();
            //bgShape.setColor(getColored(R.color.pure_gray)); //gray
            bgShape.setStroke(Integer.parseInt(num), Color.BLACK);
            holder.mTextView.setGravity(Gravity.RIGHT);
        }

        if(getThemeId() == R.style.Theme_NightTheme_DayNight_NightMODE) {
            holder.mTextView.setTextColor(Color.WHITE);
        }

        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isHidden) {
                    holder.dateView.setVisibility(View.VISIBLE);
                    isHidden = false;
                } else {
                    holder.dateView.setVisibility(View.GONE);
                    isHidden = true;
                }
            }
        });


        holder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Vibrator vibrator = (Vibrator) in.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);

                new MaterialDialog.Builder(in)
                        .title("Details")
                        .items(R.array.message_details)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if(text.equals("Copy Text")) {
                                    copyText(mDataset.get(position).defaultText);
                                } else if(text.equals("Read Aloud")) {
                                    readAloud(mDataset.get(position).defaultText);
                                } else if(text.equals("Share with FaceBook Messenger")) {
                                    shareWithFBMessenger(mDataset.get(position).defaultText);
                                }
                                dialog.hide();
                            }
                        })
                        .show();
                return false;
            }
        });

        setAnimation(holder.mTextView, position, mDataset.get(position).fromTo);


    }
    TextToSpeech tts;
    public void readAloud(final String text) {
        tts = new TextToSpeech(in, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak(text);

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

    }

    private void speak(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void copyText(String text) {
        //the clipboard manager
        ClipboardManager clipboard = (ClipboardManager)
                in.getSystemService(Context.CLIPBOARD_SERVICE);
        //the data to clip
        ClipData clip = ClipData.newPlainText("simple text", text);
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
        Vibrator vibrator = (Vibrator) in.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);

        Toast.makeText(in, "Text Copied", Toast.LENGTH_SHORT).show();
    }

    public void shareWithFBMessenger(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.facebook.orca");

        try {
            in.startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(in, "Please install Facebook Messenger", Toast.LENGTH_SHORT).show();
        }
    }


    public int getColored(int resource) {
        //resource - int - an id from the R.color file
        return Color.parseColor("#"+Integer.toHexString(in.getResources().getColor(resource)));
        //"#"+Integer.toHexString(getResources().getColor(R.color.blue))
    }


    public int getThemeId() {
        try {
            Class<?> wrapper = Context.class;
            Method method = wrapper.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(in);
        } catch (Exception e) {
            Log.w("themeid", e.toString());
        }
        return 0;
    }

    // Return the size of your dataset (invoked by the layout manager)
    /**
     * 
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    /**
     * 
     * @param viewToAnimate 
     * @param position 
     * @param type 
     */
    private void setAnimation(View viewToAnimate, int position, int type) {
        //right is you
        //left is friend
        // If the bound view wasn't previously displayed on screen, it's animated
        //if (position > in.lastPosition) {
        if (position > lastPos) {
            Animation animation;
            if(type==1) {
                animation = AnimationUtils.loadAnimation(in, R.anim.push_left_in);
            } else {
                animation = AnimationUtils.loadAnimation(in, R.anim.push_right_in);
            }
            viewToAnimate.startAnimation(animation);
            in.lastPosition = position;
            lastPos = position;
        }
    }



}


