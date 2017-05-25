package sdccd.edu.laitinena7.BookViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import sdccd.edu.laitinena7.R;
import sdccd.edu.laitinena7.Utils.Book;
import sdccd.edu.laitinena7.Utils.MessageEnum;

import static android.R.attr.path;


public class BookViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Book book;
    private ImageView imageView;
    private TextView textView;
    private Button chatButton;
    private Button deleteButton;


    public BookViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookViewFragment newInstance(String param1, String param2) {
        BookViewFragment fragment = new BookViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Bundle data = getArguments();
        //book = (Book) extras.getSerializable("Book");
        //Bundle data = savedInstanceState.getBundle("Book");
        book = (Book) data.getSerializable("Book");
        //hide search


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_view, container, false);
        /*
        * public Book (String id,
                 String name,
                 String author,
                 String year,
                 String price,
                 String ownerId,
                 String ownerName,
                 String imagePath)*/

        //set up image and text
        imageView = (ImageView)view.findViewById(R.id.imageViewBook);
        //we have the book image, let's set it
        imageView.setImageBitmap(book.getBitmap());
        //get bitmap from local directory
        Bitmap bitmap = getBitmapFromLocalDir();
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }


        textView = (TextView) view.findViewById(R.id.textViewBook);
        String text = "Title: "      +    book.getName()      + "\n" +
                "Author: "     +    book.getAuthor()    + "\n" +
                "Year: "       +    book.getYear()      + "\n" +
                "Price: "      +"$"+book.getPrice()     + "\n" +
                "Owner Name: " +    book.getOwnerName() + "\n" +
                "Owner Location: "+ book.getOwnerLocation() + "\n";
        textView.setText(text
        );


        //get chat button and attach listener

        chatButton = (Button)view.findViewById(R.id.buttonBook);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(MessageEnum.CHAT, book);
            }
        });

        //get delete button and attach listener, if user is owner

        deleteButton = (Button)view.findViewById(R.id.buttonDelete);
        if (book.getIsUserOwner() == true) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonPressed(MessageEnum.DELETE_BOOK, book);
                }
            });
        }
        //else hide button
        else {
            deleteButton.setVisibility(View.GONE);
        }

        return view;
    }

    private void sendMessageToMyListener (MessageEnum message, Book book) {
        if (mListener != null) {
            mListener.onFragmentInteraction(message, book);
        }
    }

    private void onButtonPressed(MessageEnum message, Book book) {
        if (mListener != null) {
            mListener.onFragmentInteraction(message, book);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private Bitmap getBitmapFromLocalDir() {
        Bitmap b = null;

            try {
                File f=new File(Environment.getExternalStorageDirectory()+"/inpaint/", "temporaryPic.png");
                b = BitmapFactory.decodeStream(new FileInputStream(f));
                }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

        return b;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}

