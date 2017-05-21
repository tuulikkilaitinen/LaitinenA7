package sdccd.edu.laitinena7.Chat;

import android.graphics.Color;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import sdccd.edu.laitinena7.BookViews.MyBookRecyclerViewAdapter;
import sdccd.edu.laitinena7.BookViews.OnListFragmentInteractionListener;
import sdccd.edu.laitinena7.R;
import sdccd.edu.laitinena7.Utils.MessageEnum;
import sdccd.edu.laitinena7.Utils.MyMessage;

/**
 * Created by Tuulikki Laitinen on 5/19/2017.
 */
public class MyChatRecyclerViewAdapter  extends RecyclerView.Adapter<MyChatRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<MyMessage> mValues;
    private final OnChatMessageFragmentInteractionListener mListener;
    private boolean haveDate = false;
    private String sendMessage = "";
    private MyCustomEditTextListener myCustomEditTextListener;
    //information for the last message:
    //set all the information to holder to be saved
    //since it is needed for doing new MyMessage
    private  String id = "";
    private  String bookId = "";
    private  String senderId = "";
    private  String receiverId = "";
    private String previousDate = "";
    private boolean isDataChanged = false;

    public MyChatRecyclerViewAdapter(ArrayList<MyMessage> items, OnChatMessageFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        myCustomEditTextListener = new MyCustomEditTextListener();
    }

    @Override
    public MyChatRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_message, parent, false);

        // pass MyCustomEditTextListener to viewholder in onCreateViewHolder
        // so that we don't have to do this expensive allocation in onBindViewHolder
        return new MyChatRecyclerViewAdapter.ViewHolder(view,
                myCustomEditTextListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        long timeStamp = Long.parseLong(mValues.get(position).getTimeStamp());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        String mYear = Integer.toString(calendar.get(Calendar.YEAR));
        //String mMonth = Integer.toString(calendar.get(Calendar.MONTH));
        String mMonth = getMonthForInt(Calendar.MONTH);
        String mDay = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        String mTime = Integer.toString(calendar.get(Calendar.HOUR)) +
                       ":" +
                       Integer.toString(calendar.get(Calendar.SECOND));


        //holder.mContentDate.setText(mYear+" "+mMonth+" "+mDay);
        //check if chats have this, if they have it, just hide it
        if (!haveDate) {
            holder.mContentDate.setText(mMonth + " " + mDay + ", " + mYear);
            haveDate = true;
            previousDate = mMonth + " " + mDay + ", " + mYear;
            holder.mContentDate.setVisibility(View.VISIBLE);
        }
        else if (!previousDate.equals(mMonth + " " + mDay + ", " + mYear)) {
            holder.mContentDate.setText(mMonth + " " + mDay + ", " + mYear);
            previousDate = mMonth + " " + mDay + ", " + mYear;
            holder.mContentDate.setVisibility(View.VISIBLE);
        }
        else {
            holder.mContentDate.setVisibility(View.GONE);
        }
        holder.mContentMessage.setText(mValues.get(position).getText());
        holder.mContentTime.setText(mTime);

        //if this is not the last, remove textedit and buttons
        if (position != mValues.size()-1) {
            holder.mEditText.setVisibility(View.GONE);
            holder.mSendButton.setVisibility(View.GONE);
        }
        //else show
        else {
            holder.mEditText.setVisibility(View.VISIBLE);
            holder.mSendButton.setVisibility(View.VISIBLE);
        }

        //if message not from this user, push message and time
        // to right side of screen with gravity
        if (!mValues.get(position).getIsMeSender()) {

            holder.mContentMessage.setTextColor(Color.BLUE);
            holder.mContentTime.setTextColor(Color.BLUE);

        }
        else {
            //else they are black
            holder.mContentMessage.setTextColor(Color.BLACK);
            holder.mContentTime.setTextColor(Color.BLACK);
            //holder.mContentMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
           // holder.mContentTime.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
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
        // update MyCustomEditTextListener every time we bind a new item
        // so that it knows what item in mDataset to update
        int adapterPosition = holder.getAdapterPosition();
        //holder.myCustomEditTextListener.updatePosition(adapterPosition);
        myCustomEditTextListener.updatePosition(adapterPosition);
        holder.mEditText.setText(sendMessage);
    }

    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            //mDataset[position] = charSequence.toString();
            sendMessage = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    private String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    //TODO change to add only one message to the end
    public void setMessages(ArrayList<MyMessage> myMessages) {

        mValues.clear();
        mValues.addAll(myMessages);

        dataSetChanged();
    }

    @UiThread
    protected void dataSetChanged() {
        //reset values
        haveDate = false;
        previousDate = "";
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentDate;
        public final TextView mContentMessage;
        public final TextView mContentTime;
        public final EditText mEditText;
        public final Button mSendButton;
        //set all the information to holder to be saved
        //since it is needed for doing new MyMessage
        /*
        public final String id;
        public final String bookId;
        public final String senderId;
        public final String receiverId;
        public final boolean isMeSender;
        */

        public MyCustomEditTextListener myCustomEditTextListener;

        public ViewHolder(View view, MyCustomEditTextListener myCustomEditTextListener) {
            super(view);
            mView = view;
            mContentDate = (TextView) view.findViewById(R.id.contentDate);
            mContentMessage = (TextView) view.findViewById(R.id.contentMessage);
            mContentTime = (TextView) view.findViewById(R.id.contentTime);
            mEditText = (EditText) view.findViewById(R.id.editChatText);
            mSendButton = (Button) view.findViewById(R.id.buttonSend);

            //default values
            /*
            id = ""; //message id, received later.....
            bookId = "";
            senderId = "";
            receiverId = "";
            isMeSender = true; //default value*/

            mEditText.addTextChangedListener(myCustomEditTextListener);

            //add onclicklistener
            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //check if message not empty
                    if (sendMessage != null && !sendMessage.isEmpty()
                            && !sendMessage.equals("")
                            && !sendMessage.equals(" ")) {

                        Calendar calendar = Calendar.getInstance();
                        long timeInMillis = calendar.getTimeInMillis();
                        MyMessage sendChatMessage = new MyMessage(
                            "",
                            String.valueOf(timeInMillis),
                            sendMessage,
                            mValues.get(mValues.size() - 1).getBookId(),
                            senderId, //"", so fill up later
                            receiverId, //"", so fill up later
                            true //isMeSender is true
                        );
                        onSendButtonPressed(MessageEnum.SEND_CHAT_MESSAGE, sendChatMessage);
                        mEditText.setText("");
                    }
                }
            });
/*    public MyMessage(
            String id,
            String timestamp,
            String text,
            String bookId,
            String senderId,
            String receiverId,
            boolean isMeSender) {*/

        }

        @Override
        public String toString() {
            return super.toString();
         }
    }

    private void onSendButtonPressed(MessageEnum chat, MyMessage sendMessage) {

        if (mListener != null) {
            mListener.onChatMessageFragmentInteraction(chat, sendMessage);
        }
    }
} //end of class
