package app.easy.text.texteasy.ContactList;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.viethoa.RecyclerViewFastScroller;

import java.util.ArrayList;

import app.easy.text.texteasy.Messages.MainActivity;
import app.easy.text.texteasy.R;
import in.myinnos.alphabetsindexfastscrollrecycler.utilities_fs.StringMatcher;

/**
 * Created by Jacob on 9/15/16.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements SectionIndexer {
    private ArrayList<Contacts.ContactInfo> mDataset;

    Contacts in;

    private String mSections = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

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
    public ContactAdapter(ArrayList<Contacts.ContactInfo> myDataset, Contacts in, String list) {
        mDataset = myDataset;
        this.in = in;
        mSections = list;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
        bgShape.setColor(getColored(R.color.pure_gray)); //gray
        //bgShape.setSize(30, 15);

        holder.mTextView.setText(mDataset.get(position).toString());
        //holder.mTextView.setTextColor(Color.BLACK);
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
                Intent intent = new Intent(in, MainActivity.class);
                intent.putExtra("Number", mDataset.get(position).number);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(in.getApplicationContext(), R.anim.back_to_contacts,R.anim.from_contacts).toBundle();
                in.startActivity(intent, bndlanimation);
            }
        };

        holder.mTextView.setOnClickListener(von);


    }

    public int getColored(int resource) {
        //resource - int - an id from the R.color file
        return Color.parseColor("#"+Integer.toHexString(in.getResources().getColor(resource)));
        //"#"+Integer.toHexString(getResources().getColor(R.color.blue))
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


}


