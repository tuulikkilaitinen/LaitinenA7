package sdccd.edu.laitinena7;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Tuulikki Laitinen on 5/18/2017.
 *
 * Handles read from database and write to database.
 */

public class DatabaseHandler {

    private static final String TAG = "DatabaseHandler";
    private DatabaseHandlerListener listener;
    private User user;
    private ArrayList<Book> books;

    public DatabaseHandler (DatabaseHandlerListener listener) {
        this.listener = listener;
    }

    public void writeNewUserInformation(User user) {

        //mDatabase.child("users").child(user.getUserId()).setValue(user);
    }

    public void findUser(String userId) {

        //get always new reference to database in case connection
        //has changed
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //https://laitinena7-55fef.firebaseio.com/
        DatabaseReference myRef = database.getReference().child("users").child(userId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if user found
                if (dataSnapshot.getChildrenCount() != 0) {
                    user = new User();
                    user.setName((String) dataSnapshot.child("name").getValue());
                    user.setLocation((String) dataSnapshot.child("location").getValue().toString());

                    DatabaseHandler.this.sendMessage(MessageEnum.FIND_USER, user);
                }
                //else send null as Object
                else {
                    DatabaseHandler.this.sendMessage(MessageEnum.FIND_USER, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println (databaseError.getMessage());
            }
        });
        //mDatabase= FirebaseDatabase.getInstance().getReference().child("your_no‌​de_name").child("use‌​rID");
        /*
        myRef.orderByChild().equalTo(userId).addListenerForSingleValueEvent
                (new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            ///Log.d(TAG, "PARENT: "+ childDataSnapshot.getKey());
                            //Log.d(TAG,""+ childDataSnapshot.child("name").getValue());
                            String name = (String)childDataSnapshot.child("name").getValue();
                            if (name != null) {
                                DatabaseHandler.this.sendMessage(MessageEnum.FIND_USER, name);
                                break; //break from for loop
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage());
                    }
                });
                */
    }

    private void sendMessage(MessageEnum message, Object result) {
        listener.databaseCallback(message, result);
    }

    public void updateUserData(String userId, String userField, MessageEnum message) {

        //get always new reference to database in case connection
        //has changed
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //https://laitinena7-55fef.firebaseio.com/
        DatabaseReference myRef = database.getReference().child("users").child(userId);

        if (message == MessageEnum.UPDATE_USER_ID) {
            myRef.setValue(userId);
        }
        else if (message == MessageEnum.UPDATE_USER_NAME) {
            myRef.child("name").setValue(userField);
        }
        else if (message == MessageEnum.UPDATE_USER_LOCATION) {
            myRef.child("location").setValue(userField);
        }

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "updateUserData, onDataChange");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, databaseError.getMessage());
            }
        });
    }

    public void getBookList() {
        //get always new reference to database in case connection
        //has changed
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //https://laitinena7-55fef.firebaseio.com/
        DatabaseReference myRef = database.getReference().child("books");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "getBookList, onDataChange");
                books = new ArrayList<Book>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //fill up book list
                    /*
                    *
                public Book (String id,
                 String name,
                 String author,
                 String year,
                 String price,
                 String ownerId)
                    * */
                    books.add(new Book (ds.getKey().toString(),
                            ds.child("name").getValue().toString(),
                            ds.child("author").getValue().toString(),
                            ds.child("year").getValue().toString(),
                            ds.child("price").getValue().toString(),
                            ds.child("ownerid").getValue().toString()
                    ));

                }

                DatabaseHandler.this.sendMessage(MessageEnum.GET_BOOKS, books);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, databaseError.getMessage());
            }
        });
    }
}

/*
*
* USERS IN FIREBASE DATABSE:
* {
  "users": {
    "alovelace": {
      "name": "Ada Lovelace",
      "location": "92128",
    },
    "tuulaiti": {
      "name": "Tuulikki Laitinen",
      "location": "92127",
    },
    "james": {
      "name": "James Gappy",
      "location": "92128",
    }
  }
}
* */
