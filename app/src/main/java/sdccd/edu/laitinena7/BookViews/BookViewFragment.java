package sdccd.edu.laitinena7.BookViews;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import sdccd.edu.laitinena7.R;
import sdccd.edu.laitinena7.Utils.Book;
import sdccd.edu.laitinena7.Utils.MessageEnum;


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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        textView = (TextView) view.findViewById(R.id.textViewBook);
        textView.setText("Name: "       +    book.getName()      + "\n" +
                         "Author: "     +    book.getAuthor()    + "\n" +
                         "Year: "       +    book.getYear()      + "\n" +
                         "Price: "      +"$"+book.getPrice()     + "\n"
                        );
        //get button and attach listener
        chatButton = (Button)view.findViewById(R.id.buttonBook);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(MessageEnum.CHAT, book);
            }
        });

        return view;
    }

    public void onButtonPressed(MessageEnum message, Book book) {
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

