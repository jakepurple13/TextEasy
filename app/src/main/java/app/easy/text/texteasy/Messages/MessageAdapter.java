package app.easy.text.texteasy.Messages;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;

import app.easy.text.texteasy.R;

/**
 * Created by Jacob on 9/12/16.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
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

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.textView);


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
                    .inflate(R.layout.chatmessageyou, parent, false);
            // set the view's size, margins, paddings and layout parameters
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chatmessagethem, parent, false);
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

        //ScaleDrawable background = (ScaleDrawable) holder.mTextView.getBackground();

        if(mDataset.get(position).fromTo==1) { //from
            GradientDrawable bgShape = (GradientDrawable) holder.mTextView.getBackground();
            //bgShape.setColor(getColored(R.color.pure_gray)); //blue
            //bgShape.setColor(R.color.dark_color); //blue
            holder.mTextView.setGravity(Gravity.LEFT);

        } else { //to
            GradientDrawable bgShape = (GradientDrawable) holder.mTextView.getBackground();
            //bgShape.setColor(getColored(R.color.pure_gray)); //gray
            holder.mTextView.setGravity(Gravity.RIGHT);
        }

        if(getThemeId() == R.style.Theme_NightTheme_DayNight_NightMODE) {
            holder.mTextView.setTextColor(Color.WHITE);
        }

        setAnimation(holder.mTextView, position, mDataset.get(position).fromTo);

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


