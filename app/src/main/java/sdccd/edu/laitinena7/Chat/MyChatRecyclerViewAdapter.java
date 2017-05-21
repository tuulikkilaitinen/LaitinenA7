package sdccd.edu.laitinena7.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import sdccd.edu.laitinena7.BookViews.MyBookRecyclerViewAdapter;
import sdccd.edu.laitinena7.BookViews.OnListFragmentInteractionListener;
import sdccd.edu.laitinena7.R;
import sdccd.edu.laitinena7.Utils.MyMessage;

/**
 * Created by Tuulikki Laitinen on 5/19/2017.
 */
public class MyChatRecyclerViewAdapter  extends RecyclerView.Adapter<MyChatRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<MyMessage> mValues;
    private final OnChatMessageFragmentInteractionListener mListener;

    public MyChatRecyclerViewAdapter(ArrayList<MyMessage> items, OnChatMessageFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public MyChatRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_message, parent, false);
        return new MyChatRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        long timeStamp = Long.parseLong(mValues.get(position).getTimeStamp());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        String mYear = Integer.toString(calendar.get(Calendar.YEAR));
        String mMonth = Integer.toString(calendar.get(Calendar.MONTH));
        String mDay = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        String mTime = Integer.toString(calendar.get(Calendar.HOUR));

        holder.mContentDate.setText(mYear+" "+mMonth+" "+mDay);
        holder.mContentMessage.setText(mValues.get(position).getText());
        //holder.mContentTime.setText(mTime);

        //if message not from this user, push message and time
        // to right side of screen with gravity
        if (!mValues.get(position).getIsMeSender()) {
            holder.mContentMessage.setGravity(Gravity.RIGHT);
            //holder.mContentTime.setGravity(Gravity.RIGHT);

        }
        else {
            //else they go left
            holder.mContentMessage.setGravity(Gravity.LEFT);
           // holder.mContentTime.setGravity(Gravity.LEFT);
        }

        /* !!!! You can't click chat messages!!
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //return message about book selected and return the book object.
                    mListener.onChatListFragmentInteraction(
                            null,null);

                            //MessageEnum.BOOK_SELECTED,
                            //MyChatRecyclerViewAdapter.this.mValues.get(position));
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    //TODO change to add only one message to the end
    public void setMessages(ArrayList<MyMessage> myMessages) {

        mValues.clear();
        mValues.addAll(myMessages);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentDate;
        public final TextView mContentMessage;
        //public final TextView mContentTime;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentDate = (TextView) view.findViewById(R.id.contentDate);
            mContentMessage = (TextView) view.findViewById(R.id.contentMessage);
            //mContentTime = (TextView) view.findViewById(R.id.contentTime);

        }

        @Override
        public String toString() {
            return super.toString();
         }
    }
} //end of class
