package app.easy.text.texteasy.Dictionary;

/**
 * Created by Jacob on 9/27/16.
 */

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.viethoa.RecyclerViewFastScroller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

import app.easy.text.texteasy.R;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter  {
    private ArrayList<ListOfWords.WordInfo> mDataset;

    ListOfWords in;

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
        /**
         *
         * @param v
         */
        public TextView mTextView;
        public TextView mDateText;


        /**
         *
         * @param v
         */
        public ViewHolder(View v) {
            super(v);
            /**
             *
             * @param myDataset
             * @param in
             */
            mTextView = (TextView) v.findViewById(R.id.textView);
            mDateText = (TextView) v.findViewById(R.id.dateOfText);


        }
    }

    /**
     *
     * @param parent
     * @param viewType
     */
    // Provide a suitable constructor (depends on the kind of dataset)
    /**
     *
     * @param myDataset
     * @param in
     */
    public WordAdapter(ArrayList<ListOfWords.WordInfo> myDataset, ListOfWords in) {
        mDataset = myDataset;
        this.in = in;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatmessagethem, parent, false);
        // set the view's size, margins, paddings and layout parameters
        /**
         *
         * @param v
         */

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    /**
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.mTextView.setText(mDataset.get(position).toString());

        if(getThemeId() == R.style.Theme_NightTheme_DayNight_NightMODE) {
            holder.mTextView.setTextColor(Color.WHITE);
        }

        holder.mDateText.setVisibility(View.GONE);


        holder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Vibrator vibrator = (Vibrator) in.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);

                new MaterialDialog.Builder(in)
                        .title("Details")
                        .items(R.array.word_details)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if(text.equals("Copy Text")) {
                                    copyText(holder.mTextView.getText().toString());
                                } else if(text.equals("Read Aloud")) {
                                    readAloud(holder.mTextView.getText().toString());
                                } else if(text.equals("Change Word")) {
                                    in.changeWord(mDataset.get(position).word,mDataset.get(position).meaning,position);
                                } else if(text.equals("Delete Word")) {
                                    in.deleteWord(position);
                                }
                                dialog.hide();
                            }
                        })
                        .show();
                return false;
            }
        });

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
     * @param pos
     */
    @Override
    public String getTextToShowInBubble(int pos) {
        if (pos < 0 || pos >= mDataset.size())
            return null;

        String name = mDataset.get(pos).word;
        if (name == null || name.length() < 1)
            return null;

        return mDataset.get(pos).word.substring(0, 1);
    }

}



