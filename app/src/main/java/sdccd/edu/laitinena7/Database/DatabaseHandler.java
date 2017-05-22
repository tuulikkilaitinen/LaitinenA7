package sdccd.edu.laitinena7.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sdccd.edu.laitinena7.MainApplication.AfterLoginActivity;
import sdccd.edu.laitinena7.Utils.Book;
import sdccd.edu.laitinena7.Utils.MessageEnum;
import sdccd.edu.laitinena7.Utils.MyMessage;
import sdccd.edu.laitinena7.Utils.User;

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
    private ArrayList<MyMessage> messages;
    private MyMessage message;
    private boolean checkIsMeSender;
    private MyMessage lastSentMessage;

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
                Log.i (TAG, databaseError.getMessage());
            }
        });
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
        this.id = id;
        this.name = name;
        this.author = author;
        this.year = year;
        this.price = price;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerLocation = ownerLocation;
        this.imagePath = imagePath;
                    * */
                    books.add(new Book
                            (ds.getKey().toString(),
                            ds.child("name").getValue().toString(),
                            ds.child("author").getValue().toString(),
                            ds.child("year").getValue().toString(),
                            ds.child("price").getValue().toString(),
                            ds.child("ownerid").getValue().toString(),
                            ds.child("ownername").getValue().toString(),
                            ds.child("ownerlocation").getValue().toString(),
                            null, //for image path now
                            null //for bitmap for now
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

    public void getBookMessages(final Book book, User user) {

        //final values for checking in inner class
        final Book checkBook = book;
        final User checkUser = user;
        //get always new reference to database in case connection
        //has changed
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //https://laitinena7-55fef.firebaseio.com/

        DatabaseReference myRef = database.getReference().
                                    child("users").child(user.getUserId()).child("messages");
        // In NoSQL you typically model your data for the way your app consumes it
        // (see this article on NoSQL data modeling). So if you app needs a list of
        // all conversations for a user, consider storing a list of all conversations
        // for each user /users/<uid>/conversations/<conversationid>.
        // – Frank van Puffelen Jul 16 '16 at 14:25
        //so stored data in /users/<uid>/messages/<messageid>/bookid, receiverid, senderid, text,
        //timestamp

        //get all changes, so that chat view is updated in real time
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long childrenCount = dataSnapshot.getChildrenCount();
                int dsCount = 0;
                String bookId = "";
                String senderId = "";
                String receiverId = "";
                String text = "";
                String timeStamp = "";
                //if message found
                if (childrenCount != 0) {
                    messages = new ArrayList<MyMessage>();

                    //go through all the user messages
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        dsCount++;
                        //if same book, create message object and get data and add to
                        //messages list

                        //if not the last message
                        Object bObject = ds.child("bookid").getValue();
                        if (bObject != null) {
                            bookId = bObject.toString();
                        }
                        else {
                            bookId = DatabaseHandler.this.lastSentMessage.getBookId();
                        }
                        Object sObject = ds.child("senderid").getValue();
                        if (sObject != null) {
                            senderId = sObject.toString();
                        }
                        else {
                            senderId = DatabaseHandler.this.lastSentMessage.getSenderId();
                        }
                        Object rObject = ds.child("receiverid").getValue();
                        if (rObject != null) {
                            receiverId = rObject.toString();
                        }
                        else {
                            receiverId = DatabaseHandler.this.lastSentMessage.getReceiverId();
                        }
                        Object tObject = ds.child("text").getValue();
                        if (tObject != null) {
                            text = tObject.toString();
                        }
                        else {
                            text = DatabaseHandler.this.lastSentMessage.getText();
                        }
                        Object tSObject = ds.child("timestamp").getValue();
                        if (tSObject != null) {
                            timeStamp = tSObject.toString();
                        }
                        else {
                            timeStamp = DatabaseHandler.this.lastSentMessage.getTimeStamp();
                        }

                            //get message id
                        /*
                        bookId = ds.child("bookid").getValue().toString();
                        senderId = ds.child("senderid").getValue().toString();
                        receiverId = ds.child("receiverid").getValue().toString();
                        text = ds.child("text").getValue().toString();
                        timeStamp = ds.child("timestamp").getValue().toString();
*/

/*
                        //else the last one, HACKKKKKKKK from last sent message
                        else if (dsCount == childrenCount && DatabaseHandler.this.lastSentMessage != null){
                            //get message id
                            bookId = DatabaseHandler.this.lastSentMessage.getBookId();
                            senderId = DatabaseHandler.this.lastSentMessage.getSenderId();
                            receiverId = DatabaseHandler.this.lastSentMessage.getReceiverId();
                            text = DatabaseHandler.this.lastSentMessage.getText();
                            timeStamp = DatabaseHandler.this.lastSentMessage.getTimeStamp();
                        }
                        */
                        if (checkBook != null && checkBook.getId().equals(bookId)) {
                            //check value for is me sender

                            if (checkUser.getUserId().equals(senderId)) {
                                checkIsMeSender = true;
                            }
                            else {
                                checkIsMeSender = false;
                            }
                            message = new MyMessage(
                                    ds.getKey().toString(), //id
                                    timeStamp,
                                    text,
                                    bookId,
                                    senderId,
                                    receiverId,
                                    checkIsMeSender
                                                    );
                            messages.add(message);
                        }
                    }

                    DatabaseHandler.this.sendMessage(MessageEnum.GET_MESSAGES, messages);
                }
                //else send null as Object
                else {
                    DatabaseHandler.this.sendMessage(MessageEnum.GET_MESSAGES, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, databaseError.getMessage());
            }
        });
    }

    public void sendMessageToDatabase(MyMessage message) {
        //get always new reference to database in case connection
        //has changed

        //save last message
        this.lastSentMessage = message;

        //FOR SENDER
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //https://laitinena7-55fef.firebaseio.com/
        DatabaseReference myRef1 = database.getReference().child("users").child(
                message.getSenderId()).child("messages");
        String mGroupId = myRef1.push().getKey();

        final FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database2.getReference().child("users").child(
                message.getSenderId()).child("messages").child(mGroupId);
        myRef.setValue(mGroupId);
        //fill up message information
        //bookid, receiverid, senderid, text, timestamp

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("bookid", message.getBookId());
        map2.put("receiverid", message.getReceiverId());
        map2.put("senderid", message.getSenderId());
        map2.put("text", message.getText());
        map2.put("timestamp", message.getTimeStamp());
        //Firebase fire = new Firebase("********").child(markerName);
        myRef.setValue(map2);
/*
        myRef.child("bookid").setValue(message.getBookId());
        myRef.child("receiverid").setValue(message.getReceiverId());
        myRef.child("senderid").setValue(message.getSenderId());
        myRef.child("text").setValue(message.getText());
        myRef.child("timestamp").setValue(message.getTimeStamp());
*/
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "sendMessageToDatabase, onDataChange");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, databaseError.getMessage());
            }
        });


        //FOR RECEIVER
        final FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        //https://laitinena7-55fef.firebaseio.com/
        DatabaseReference myRef2 = database3.getReference().child("users").child(
                message.getReceiverId()).child("messages");
        String mGroupId2 = myRef2.push().getKey();

        final FirebaseDatabase database4 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database4.getReference().child("users").child(
                message.getReceiverId()).child("messages").child(mGroupId2);
        //myRef.setValue(mGroupId); //TODO change...................
        //fill up message information
        //bookid, receiverid, senderid, text, timestamp

        Map<String, String> map3 = new HashMap<String, String>();
        map3.put("bookid", message.getBookId());
        map3.put("receiverid", message.getReceiverId());
        map3.put("senderid", message.getSenderId());
        map3.put("text", message.getText());
        map3.put("timestamp", message.getTimeStamp());
        //Firebase fire = new Firebase("********").child(markerName);
        myRef3.setValue(map3);
/*
        myRef3.child("bookid").setValue(message.getBookId());
        myRef3.child("receiverid").setValue(message.getReceiverId());
        myRef3.child("senderid").setValue(message.getSenderId());
        myRef3.child("text").setValue(message.getText());
        myRef3.child("timestamp").setValue(message.getTimeStamp());
*/
        myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "sendMessageToDatabase, onDataChange");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, databaseError.getMessage());
            }
        });

    }

    public void sendBookToDatabase(Book book) {

        final Book sentBook = book;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //https://laitinena7-55fef.firebaseio.com/
        DatabaseReference myRef1 = database.getReference().child("books");
        String mGroupId = myRef1.push().getKey();

        final FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database2.getReference().child("books").child(mGroupId);
        myRef.setValue(mGroupId);
        //fill up message information
        //bookid, receiverid, senderid, text, timestamp

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("author", book.getAuthor());
        map2.put("name", book.getName());
        map2.put("ownerid", book.getOwnerId());
        map2.put("price", book.getPrice());
        map2.put("year", book.getYear());
        map2.put("ownername", book.getOwnerName());
        map2.put("ownerlocation", book.getOwnerLocation());
        //Firebase fire = new Firebase("********").child(markerName);
        myRef.setValue(map2);
/*
        myRef.child("bookid").setValue(message.getBookId());
        myRef.child("receiverid").setValue(message.getReceiverId());
        myRef.child("senderid").setValue(message.getSenderId());
        myRef.child("text").setValue(message.getText());
        myRef.child("timestamp").setValue(message.getTimeStamp());
*/
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "sendBookToDatabase, onDataChange");
                //send back book that was sent to database
                sendMessage(MessageEnum.BOOK_ADDED, sentBook);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, databaseError.getMessage());
            }
        });
    }

    public void sendBookImage(Book book) {

        final Book usedBook = book;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageReference = storage.getReference();

        // Create a reference to image file
        //StorageReference imageFileReference = storageReference.child(getFileNameFromUri(book.getUri()));
        StorageReference imageFileReference = storageReference.child(book.getName()+".png");
        // Create a reference to 'images/mountains.jpg'
       //StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");

// While the file names are the same, the references point to different files
        //mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        //mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false

        Bitmap bitmap = book.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageFileReference.putBytes(data);

        //uploadTask = storageRef.child("images/mountains.jpg").putFile(file, metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //send information to afterloginactivity
                sendMessage(MessageEnum.IMAGE_LOADED, usedBook);
            }
        });

    }

    public void getBookImage(Book book) {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        //StorageReference imageRef = storageRef.child(getFileNameFromUri(book.getUri()));
        StorageReference imageRef = storageRef.child(book.getName()+".png");

        File localFile = null;
        try {
            localFile = File.createTempFile(book.getName(), "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File useThisFile = localFile;
        final Book useThisBook = book;
        imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                //send it forward
                String filePath = useThisFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                useThisBook.setBitmap(bitmap);
                sendMessage(MessageEnum.IMAGE_DOWNLOADED, useThisBook);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    //helper function
    private String getFileNameFromUri (Uri uri) {

        String fileName = "";
        if (uri.getScheme().equals("file")) {
            fileName = uri.getLastPathSegment();
        } else {
            Cursor cursor = null;
            try {
                cursor = ((AfterLoginActivity)listener).getContentResolver().query(uri, new String[]{
                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                }, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    Log.d(TAG, "name is " + fileName);
                }
            } finally {

                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return fileName;
    }


} //end of class
