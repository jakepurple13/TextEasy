package app.easy.text.texteasy.test;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;

import app.easy.text.texteasy.Messages.MainActivity;
import app.easy.text.texteasy.R;
import in.myinnos.alphabetsindexfastscrollrecycler.utilities_fs.StringMatcher;

/**
 * Created by Jacob on 2/13/17.
 */

public class ScrollingAdapter extends RecyclerView.Adapter<ScrollingAdapter.ViewHolder> implements SectionIndexer {
    private ArrayList<String> mDataset;
    private String mSections = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public int getPositionForSection(int section) {
        // If there is no item for current section, previous section will be selected
        for (int i = section; i >= 0; i--) {
            for (int j = 0; j < getItemCount(); j++) {
                if (i == 0) {
                    // For numeric section
                    for (int k = 0; k <= 9; k++) {
                        if (StringMatcher.match(String.valueOf(mDataset.get(j).charAt(0)), String.valueOf(k)))
                            return j;
                    }
                } else {
                    if (StringMatcher.match(String.valueOf(mDataset.get(j).charAt(0)), String.valueOf(mSections.charAt(i))))
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
     */
    public ScrollingAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mytextview, parent, false);
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

        /**
         *
         */
        GradientDrawable bgShape = (GradientDrawable) holder.mTextView.getBackground();
        bgShape.setColor(Color.rgb(187, 187, 187)); //gray

        holder.mTextView.setText(mDataset.get(position).toString());
        holder.mTextView.setTextColor(Color.BLACK);
        //holder.mTextView.setGravity(Gravity.CENTER);
        //holder.mTextView.setTextColor(R.color.textColors);

        /**
         *
         * @param pos
         */
        View.OnClickListener von = new View.OnClickListener() {
            /**
             *
             * @param v
             */
            @Override
            public void onClick(View v) {

            }
        };

        holder.mTextView.setOnClickListener(von);


    }

    // Return the size of your dataset (invoked by the layout manager)
    /**
     *
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}