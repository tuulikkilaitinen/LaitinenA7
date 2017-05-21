package sdccd.edu.laitinena7.MainApplication;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;

import sdccd.edu.laitinena7.BookViews.BookListFragment;
import sdccd.edu.laitinena7.BookViews.BookViewFragment;
import sdccd.edu.laitinena7.BookViews.OnFragmentInteractionListener;
import sdccd.edu.laitinena7.BookViews.OnListFragmentInteractionListener;
import sdccd.edu.laitinena7.Chat.ChatMessageFragment;
import sdccd.edu.laitinena7.Chat.OnChatMessageFragmentInteractionListener;
import sdccd.edu.laitinena7.Database.DatabaseHandler;
import sdccd.edu.laitinena7.Database.DatabaseHandlerListener;
import sdccd.edu.laitinena7.R;
import sdccd.edu.laitinena7.Settings.SettingsActivity;
import sdccd.edu.laitinena7.Utils.Book;
import sdccd.edu.laitinena7.Utils.MessageEnum;
import sdccd.edu.laitinena7.Utils.MyMessage;
import sdccd.edu.laitinena7.Utils.StatusEnum;
import sdccd.edu.laitinena7.Utils.User;

public class AfterLoginActivity extends AppCompatActivity
    implements DatabaseHandlerListener, OnListFragmentInteractionListener,
        OnChatMessageFragmentInteractionListener,
        OnFragmentInteractionListener {

    private boolean isUserFound;
    private String userId;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private User user;

    // keys for reading data from SharedPreferences
    public static final String USER_NAME = "username";
    public static final String LOCATION = "location";
    private boolean preferencesChanged;
    private DatabaseHandler databaseHandler;
    private BookListFragment bookListFragment;
    private static final String TAG = "AfterLoginActivity";

    private StatusEnum status;
    private Book selectedBook;
    private ChatMessageFragment chatMessageFragment;
    private String receiverId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAfterLogin);
        //setSupportActionBar(toolbar);


        // set default values in the app's SharedPreferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // register listener for SharedPreferences changes
        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(
                        preferencesChangeListener);

        //getSupportFragmentManager().beginTransaction().add(R.id.fragment_book_list,
                //new BookListFragment(), "booklist").commit();

        //create user
        user = new User();
        //create databasehandler
        databaseHandler = new DatabaseHandler((DatabaseHandlerListener)this);

        //check if user is found from application firebase database
        //Note: A Google account's email address can change,
        // //so don't use it to identify a user. Instead,
        // //use the account's ID, which you can get on the client
        // //with GoogleSignInAccount.getId, and on the backend from
        // //the sub claim of the ID token.

        //first get user id
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            this.userId = extras.getString(MainActivity.USER_ID);
            user.setUserId (this.userId);
        }

       // PreferenceScreen screen = getPreferenceScreen();
        //Preference pref = getPreferenceManager().findPreference("userid");
        //screen.removePreference(pref);

        //then check if user is found based on id
        //reply will come with databaseCallback
        databaseHandler.findUser(this.userId);

    }

    // show menu if app is running on a phone or a portrait-oriented tablet
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        // display the app's menu only in portrait orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // inflate the menu
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        else
            return false;
    }

    private void startMySettingsActivity() {

        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
    }

    // displays the SettingsActivity when running on a phone
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startMySettingsActivity();
        return super.onOptionsItemSelected(item);
    }

    // listener for changes to the app's SharedPreferences
    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                // called when the user changes the app's preferences
                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                    preferencesChanged = true; // user changed app setting

                    if (key.equals(USER_NAME)) {
                        //update user name
                        user.setName(sharedPreferences.getString(USER_NAME, ""));
                        //update user name to firebase database
                        databaseHandler.updateUserData(user.getUserId(), user.getUserName(),
                                MessageEnum.UPDATE_USER_NAME);

                    } else if (key.equals(LOCATION)) {
                        //update user location
                        user.setLocation(sharedPreferences.getString(LOCATION, ""));
                        databaseHandler.updateUserData(user.getUserId(), user.getUserLocation(),
                                MessageEnum.UPDATE_USER_LOCATION);
                    }
                }
            };

    @Override
    public void databaseCallback(MessageEnum message, Object result) {

        if (message == MessageEnum.FIND_USER) {
            //check if user found
            if (result != null) {
                this.user.setName(((User) result).getUserName());
                this.user.setLocation(((User) result).getUserLocation());
                //user found
                //get book list
                databaseHandler.getBookList();
            }
            else { //user not found

                //if user is not found from application firebase database,
                // show settings page to set up
                //user name (which is shown to other users) and location
                //user id is the google account id received from google account
                //TODO LISTEN THAT IF BACK KEY IS PRESSED
                startMySettingsActivity();
            }
        } else if (message == MessageEnum.GET_BOOKS ) {
            if(status != StatusEnum.STARTED_BOOK_LIST) {
                status = StatusEnum.STARTED_BOOK_LIST;
                startBookListFragment((ArrayList<Book>) result);
            }
            else {
                getBookListFragment().setBooks((ArrayList<Book>) result);
            }
        } else if (message == MessageEnum.GET_MESSAGES) {
            //check status
            if (status != StatusEnum.STARTED_CHAT_MESSAGE) {
                status = StatusEnum.STARTED_CHAT_MESSAGE;
                startChatMessageFragment((ArrayList<MyMessage>) result);
            }
            else {
                //else set up messages to view
                chatMessageFragment.setMessages((ArrayList<MyMessage>)result);
            }
           // getChatMessageFragment().setMessages((ArrayList<MyMessage>)result);
        }



        //TEST
        Calendar calendar = Calendar.getInstance();
        long timeInMillis = calendar.getTimeInMillis();
        Log.i(TAG, "timeInMillis: "+ Long.valueOf(timeInMillis));

    }

    private void startBookListFragment(ArrayList<Book> books) {

        //update application status
        status = StatusEnum.STARTED_BOOK_LIST;

        //open book view fragment
        //create bundle for fragment
        Bundle data = new Bundle();
        data.putSerializable("Books", books);
        // Create new fragment and transaction
        BookListFragment bookListFragment = new BookListFragment();
        //set arguments/bundle to fragment
        bookListFragment.setArguments(data);
        // consider using Java coding conventions (upper first char class names!!!)
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.activityAfterLoginId, bookListFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private void startChatMessageFragment(ArrayList<MyMessage> messages) {

        //first set up RECEIVER
        //        ((MyMessage)result).setReceiverId(this.selectedBook.getOwnerId());
        //if owner id is the same as user id, receiver will be get from messages
        //if messages not null, otherwise receiver is the same is book owner id
        if (this.selectedBook.getOwnerId().equals(this.userId)) {
            for (MyMessage message : messages) {
                if (!message.getReceiverId().equals(this.userId)) {
                    this.receiverId = message.getReceiverId();
                    break;
                }
                else if (!message.getSenderId().equals(this.userId)) {
                    this.receiverId = message.getSenderId();
                    break;
                }
            }
        }
        else {
            this.receiverId = this.selectedBook.getOwnerId();
        }

        //update application status
        status = StatusEnum.STARTED_CHAT_MESSAGE;

        //open book view fragment
        //create bundle for fragment
        Bundle data = new Bundle();
        data.putSerializable("Messages", messages);
        // Create new fragment and transaction
        chatMessageFragment = new ChatMessageFragment();
        //set arguments/bundle to fragment
        chatMessageFragment.setArguments(data);
        // consider using Java coding conventions (upper first char class names!!!)
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.activityAfterLoginId, chatMessageFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(MessageEnum message, Book book) {
        this.selectedBook = book; //save information
        startBookViewFragment(book);
    }

    private void startBookViewFragment(Book book) {

        status = StatusEnum.STARTED_BOOK_VIEW;

        //open book view fragment
        //create bundle for fragment
        Bundle data = new Bundle();
        data.putSerializable("Book", book);
        // Create new fragment and transaction
        BookViewFragment bookViewFragment = new BookViewFragment();
        //set arguments/bundle to fragment
        bookViewFragment.setArguments(data);
        // consider using Java coding conventions (upper first char class names!!!)
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.activityAfterLoginId, bookViewFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    // gets a reference to the BookListFragment
    private BookListFragment getBookListFragment() {

        BookListFragment bookListFragment = (BookListFragment)
                getFragmentManager().findFragmentById(R.id.bookListFragment);

        //return (BookListFragment) getSupportFragmentManager().findFragmentById(
               // R.id.fragment_book_list);
        return bookListFragment;
    }

    private ChatMessageFragment getChatMessageFragment() {

        ChatMessageFragment chatMessageFragment = (ChatMessageFragment)
                getFragmentManager().findFragmentById(R.id.chatMessageRecyclerView);

        //return (BookListFragment) getSupportFragmentManager().findFragmentById(
        // R.id.fragment_book_list);
        return chatMessageFragment;
    }

    private static String getFragmentName(int viewId, int id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    public void onFragmentInteraction(MessageEnum message, Object result) {
        //Log.i(TAG, " must implement OnFragmentInteractionListener");

        //if buyer, open chat view with owner
        //TODO else if owner, open chat view list with list of buyers
        if (!((Book)result).getOwnerId().equals(user.getUserId())) {

            //so not owner, open chat view
            //actually first get previous messages from Firebase database
            //regarding this bookid
            databaseHandler.getBookMessages((Book)result, this.user);
        }
        else {
            //TODO open with list of buyers.....
            databaseHandler.getBookMessages((Book)result, this.user);
        }

    }

    @Override
    public void onChatMessageFragmentInteraction(MessageEnum message, Object result) {
        //fill up the rest of the message
        ((MyMessage)result).setSenderId(this.user.getUserId());
        ((MyMessage)result).setReceiverId(this.receiverId);
        //send message to message list
        databaseHandler.sendMessageToDatabase((MyMessage)result);
        //message should come back with reference change?
    }
    //public Object getBookList() {
      //  return bookList;
    //}
}
