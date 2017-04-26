package app.easy.text.texteasy.ContactList;

import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.Profile;
import com.mooveit.library.Fakeit;
import com.viethoa.RecyclerViewFastScroller;

import org.json.JSONException;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import app.easy.text.texteasy.About.AboutScreen;
import app.easy.text.texteasy.Messages.MainActivity;
import app.easy.text.texteasy.R;
import app.easy.text.texteasy.Translator;
import in.myinnos.alphabetsindexfastscrollrecycler.utilities_fs.StringMatcher;

/**
 * Created by Jacob on 9/15/16.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements SectionIndexer {
    //The list of contacts
    private ArrayList<Contacts.ContactInfo> mDataset;
    //Contact activity
    Contacts in;
    //Last position
    int lastPos = -1;
    //sections for the index bar
    private String mSections = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    Translator t;

    @Override
    public int getPositionForSection(int section) {

        // If there is no item for current section, previous section will be selected
        for (int i = section; i >= 0; i--) {
            for (int j = 0; j < getItemCount(); j++) {
                if (i == 0) {
                    // For numeric section
                    for (int k = 0; k <= 9; k++) {
                        if (StringMatcher.match(String.valueOf(mDataset.get(j).name.charAt(0)), String.valueOf(k)))
                            return j;
                    }
                } else {
                    if (StringMatcher.match(String.valueOf(mDataset.get(j).name.charAt(0)), String.valueOf(mSections.charAt(i))))
                        return j;
                }
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        String[] sections = new String[mSections.length()];
        for (int i = 0; i < mSections.length(); i++)
            sections[i] = String.valueOf(mSections.charAt(i));
        return sections;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.contactViewed);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)

    public ContactAdapter(ArrayList<Contacts.ContactInfo> myDataset, Contacts in, String list, Translator t) {
        mDataset = myDataset;
        this.in = in;
        mSections = list;
        this.t = t;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contactview, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //The stroke width of the border
        SharedPreferences load = PreferenceManager.getDefaultSharedPreferences(in);
        String num = load.getString("stroke_width_choice", "1");

        GradientDrawable bgShape = (GradientDrawable) holder.mTextView.getBackground();
        //bgShape.setColor(getColored(R.color.pure_gray)); //gray
        if(num.equals("Default value"))
            num = "1";
        bgShape.setStroke(Integer.parseInt(num), Color.BLACK);
        //setting the text
        //holder.mTextView.setText(Html.fromHtml(mDataset.get(position).toString()));
        holder.mTextView.setText(Html.fromHtml(mDataset.get(position).toString() + getsms(mDataset.get(position).number)));
        //Change text to white if the theme is a dark theme
        if(getThemeId() == R.style.Theme_NightTheme_DayNight_NightMODE) {
            holder.mTextView.setTextColor(Color.WHITE);
        }

        //set up an OnClickListener
        View.OnClickListener von = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //If the contact is a facebook contact
                if(mDataset.get(position).facebook) {

                    String s = "https://www.facebook.com/app_scoped_user_id/" + mDataset.get(position).number;//264254410443692";

                    Uri uri = Uri.parse("fb-messenger://user/");

                    uri = ContentUris.withAppendedId(uri,Long.parseLong("100006154636052"));
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                    in.startActivity(intent);

                    //Go to their facebook page cause facebook is a dick >.>

                } else {
                    //OTHERWISE, go to the Main Activity with a possible message if its being shared
                    Intent intent = new Intent(in, MainActivity.class);
                    //the contact number
                    intent.putExtra("Number", mDataset.get(position).number);
                    //message if we are sharing
                    intent.putExtra("MessageToPass", in.messageToPass);
                    //animation!
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(in.getApplicationContext(), R.anim.back_to_contacts, R.anim.from_contacts).toBundle();
                    in.startActivity(intent, bndlanimation);
                }
            }
        };

        holder.mTextView.setOnClickListener(von);
        //setting an OnLongClickListener to read a contact aloud
        holder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                readAloud(mDataset.get(position).toString());

                return false;
            }
        });
        //Animations!
        setAnimation(holder.mTextView, position);

    }

    //Text to speech!
    TextToSpeech tts;
    public void readAloud(final String text) {
        tts = new TextToSpeech(in, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
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

    //speaking words
    private void speak(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void setAnimation(View viewToAnimate, int position) {
        //right is you
        //left is friend
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPos) {
            Animation animation;
            animation = AnimationUtils.loadAnimation(in, R.anim.push_right_in);
            viewToAnimate.startAnimation(animation);
            lastPos = position;
        }
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

    /*
    *getsms
    *get texts of a contact
    * phoneNumber - (String) - a contacts phone number
    */
    public String getsms(String phoneNumber) {
        //the text that will be returned
        String text = " ";

        Uri uri = Uri.parse("content://sms/");
        ContentResolver contentResolver = in.getContentResolver();
        //SQL based stuff to single out a contact

        //gets the last text contact sent
        //String sms = "address='"+ "+1" + phoneNumber + "'";
        //gets last contact you sent
        //String sms = "address='" + phoneNumber + "'";
        //Gets the last text sent or received
        String[] smsTest = new String[]{ "+1" + phoneNumber, phoneNumber};
        //Cursor cursor = contentResolver.query(uri, new String[] { "_id", "body", "date", "address" }, sms, null, null);
        Cursor cursor = contentResolver.query(uri, new String[] { "_id", "body", "date", "address" }, "address IN(?,?)", smsTest, null);
        //body of text
        String strbody = "";
        //To get the date
        Calendar cal = Calendar.getInstance();
        //get the most recent data
        assert cursor != null;
        if(cursor.moveToFirst()) {
            strbody = cursor.getString(cursor.getColumnIndex("body"));
            cal.setTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("date")).toString()));
            //Log.i(phoneNumber, "getsms: " + strbody);
        }
        //Change calendar to a date
        Date d = cal.getTime();
        //deal with patterns to conversion
        String pattern = "hh:mm:ss a MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        //Closing the cursor
        cursor.close();
        //moving on to String manipulation
        text = strbody;
        //Possible change in color
        String textColor = "gray";

        //String result = "<br><small><small><font color=\"" + textColor + "\">" + translate.translate(text) +
        //      "<br>" + format.format(d) + "</font></small></small>";


        //This is what will be returned
        String result = "<br><small><small><small>" + t.translate(text) + "<br>" + format.format(d) + "</small></small></small>";

        //if no text was sent, make result do a break line so things still look nice
        if(text.equals("")) {
            result = "<br>";
        }
        //return the text
        return result;
    }

    public int getColored(int resource) {
        //resource - int - an id from the R.color file
        return Color.parseColor("#"+Integer.toHexString(in.getResources().getColor(resource)));
        //"#"+Integer.toHexString(getResources().getColor(R.color.blue))
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}


