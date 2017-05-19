package sdccd.edu.laitinena7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AfterLoginActivity extends AppCompatActivity
    implements DatabaseHandlerListener, OnListFragmentInteractionListener {

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
    private BookFragment bookFragment;



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
                //new BookFragment(), "booklist").commit();

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
                startMySettingsActivity();
            }
        } else if (message == MessageEnum.GET_BOOKS ) {
            getBookFragment().setBooks((ArrayList<Book>)result);
        }


    }

    @Override
    public void onListFragmentInteraction(MessageEnum message, Book mItem) {

        //open book view activity
    }

    // gets a reference to the BookFragment
    private BookFragment getBookFragment() {

        BookFragment bookFragment = (BookFragment)
                getSupportFragmentManager().findFragmentById(R.id.bookListFragment);

        //return (BookFragment) getSupportFragmentManager().findFragmentById(
               // R.id.fragment_book_list);
        return bookFragment;
    }

    private static String getFragmentName(int viewId, int id) {
        return "android:switcher:" + viewId + ":" + id;
    }
    //public Object getBookList() {
      //  return bookList;
    //}
}