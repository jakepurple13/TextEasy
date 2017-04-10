package app.easy.text.texteasy.Dictionary;

/**
 * Created by Jacob on 9/27/16.
 */

import android.content.Context;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.mTextView.setText(mDataset.get(position).toString());
        View.OnClickListener von = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //in.changeWord(mDataset.get(position).word, mDataset.get(position).meaning, position);
                readAloud(mDataset.get(position).word + " equals " + mDataset.get(position).meaning);
            }
        };

        if(getThemeId() == R.style.Theme_NightTheme_DayNight_NightMODE) {
            holder.mTextView.setTextColor(Color.WHITE);
        }

        holder.mTextView.setOnClickListener(von);

        holder.mDateText.setVisibility(View.GONE);

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



