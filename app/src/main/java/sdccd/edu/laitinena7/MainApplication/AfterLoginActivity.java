package sdccd.edu.laitinena7.MainApplication;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;

import sdccd.edu.laitinena7.BookViews.AddBookFragment;
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

    private StatusEnum status = StatusEnum.STARTED_APP;
    private Book selectedBook;
    private ChatMessageFragment chatMessageFragment;
    private String receiverId = "";
    private Menu menu;
    private ArrayList<Book> latestBookList;
    private ArrayList<Book> searchBookList;


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

    @Override
    public void onResume(){
        super.onResume();
        if (this.menu != null) {
            this.menu.findItem(R.id.menu_search).setVisible(true);
        }

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
            this.menu = menu;
            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

            // Assumes current activity is the searchable activity
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
            searchView.setOnQueryTextListener(queryTextListener);
            return true;
        }
        else
            return false;
    }

    final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String newText) {
            // Do something
            //check if empty
            if (newText.isEmpty()) {
                //show the whole list again
                //set book list status
                AfterLoginActivity.this.status = StatusEnum.STARTED_BOOK_LIST;
                AfterLoginActivity.this.databaseHandler.getBookList();
            }
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            // Do something
            //search book list and return where string is find
            if (AfterLoginActivity.this.latestBookList != null) {
                searchBookList = new ArrayList<>();

                for (Book book : AfterLoginActivity.this.latestBookList) {
                    //str1.toLowerCase().contains(str2.toLowerCase())
                    if (book.getName().toLowerCase().contains(query.toLowerCase())) {
                        //add to search book list
                        searchBookList.add(book);
                    }
                }
                //set books to book list view
                AfterLoginActivity.this.bookListFragment.setBooks(searchBookList);
            }
            return true;
        }
    };


    private void startMySettingsActivity() {

        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //check which id
        // switch based on the MenuItem id
        switch (item.getItemId()) {
            case R.id.action_settings:
                // displays the SettingsActivity when running on a phone
                startMySettingsActivity();
                // consume the menu event
                return true;

            case R.id.add_book:
                startAddBookFragment();
            // consume the menu event
                return true;

            default:
                break;
        }

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
            Log.i(TAG, status.toString());
            Log.i(TAG, StatusEnum.STARTED_BOOK_LIST.toString());

            if(!status.equals(StatusEnum.STARTED_BOOK_LIST)) {
                status = StatusEnum.STARTED_BOOK_LIST;
                latestBookList = (ArrayList<Book>) result;
                startBookListFragment(latestBookList);
            }
            else {
                latestBookList = (ArrayList<Book>) result;
                this.bookListFragment.setBooks((ArrayList<Book>) result);
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
        //set visible search from menu
        this.menu.findItem(R.id.menu_search).setVisible(true);

        //open book view fragment
        //create bundle for fragment
        Bundle data = new Bundle();
        data.putSerializable("Books", books);
        // Create new fragment and transaction
        this.bookListFragment = new BookListFragment();
        //set arguments/bundle to fragment
        this.bookListFragment.setArguments(data);
        // consider using Java coding conventions (upper first char class names!!!)
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.activityAfterLoginId, this.bookListFragment);
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
        //hide search from menu
        this.menu.findItem(R.id.menu_search).setVisible(false);

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
        //hide search from menu
        this.menu.findItem(R.id.menu_search).setVisible(false);

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

    private void startAddBookFragment() {

        status = StatusEnum.STARTED_ADD_BOOK;
        //hide search from menu
        this.menu.findItem(R.id.menu_search).setVisible(false);

        // Create new fragment and transaction
        AddBookFragment addBookFragment = new AddBookFragment();

        // consider using Java coding conventions (upper first char class names!!!)
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.activityAfterLoginId, addBookFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    // gets a reference to the BookListFragment
    private BookListFragment getBookListFragment() {

        this.bookListFragment = (BookListFragment)
                getFragmentManager().findFragmentById(R.id.bookListFragment);

        //return (BookListFragment) getSupportFragmentManager().findFragmentById(
               // R.id.fragment_book_list);
        return this.bookListFragment;
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

        if (message == MessageEnum.ADD_BOOK) {
            ((Book)result).setOwnderId(user.getUserId());
            databaseHandler.sendBookToDatabase((Book)result);
        }

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
