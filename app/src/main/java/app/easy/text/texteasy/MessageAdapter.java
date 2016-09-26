package app.easy.text.texteasy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jacob on 9/12/16.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private ArrayList<MainActivity.TextInfo> mDataset;

    MainActivity in;

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
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mytextview, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    /**
     * 
     * @param holder 
     * @param position 
    /**
     * 
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).toString());
    /**
     * 
     * @param viewToAnimate 
     * @param position 
     * @param type 
     */

        if(mDataset.get(position).fromTo==1) { //from
            GradientDrawable bgShape = (GradientDrawable) holder.mTextView.getBackground();
            bgShape.setColor(Color.rgb(175, 210, 246)); //blue
            holder.mTextView.setGravity(Gravity.LEFT);
        } else { //to
            GradientDrawable bgShape = (GradientDrawable) holder.mTextView.getBackground();
            bgShape.setColor(Color.rgb(187, 187, 187)); //gray
            holder.mTextView.setGravity(Gravity.RIGHT);
        }

        setAnimation(holder.mTextView, position, mDataset.get(position).fromTo);

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
        if (position > in.lastPosition) {
            Animation animation;
            if(type==1) {
                animation = AnimationUtils.loadAnimation(in, R.anim.push_left_in);
            } else {
                animation = AnimationUtils.loadAnimation(in, R.anim.push_right_in);
            }
            viewToAnimate.startAnimation(animation);
            in.lastPosition = position;
        }
    }

}


