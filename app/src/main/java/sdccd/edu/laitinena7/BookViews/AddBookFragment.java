package sdccd.edu.laitinena7.BookViews;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sdccd.edu.laitinena7.R;
import sdccd.edu.laitinena7.Utils.Book;
import sdccd.edu.laitinena7.Utils.MessageEnum;

/**
 * Created by Tuulikki Laitinen on 5/21/2017.
 */
public class AddBookFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Book book;
    private Button addButton;
    private EditText name;
    private EditText author;
    private EditText price;
    private EditText year;
    private View view;


    public AddBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddBookFragment newInstance(String param1, String param2) {
        AddBookFragment fragment = new AddBookFragment();
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

        //Bundle data = getArguments();
        //book = (Book) extras.getSerializable("Book");
        //Bundle data = savedInstanceState.getBundle("Book");
        //book = (Book) data.getSerializable("Book");
        //hide search


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_book, container, false);

        name = (EditText) view.findViewById(R.id.editTextTitle);
        author = (EditText) view.findViewById(R.id.editTextAuthor);
        price = (EditText) view.findViewById(R.id.editTextPrice);
        year = (EditText) view.findViewById(R.id.editTextYear);

        //get button and attach listener
        addButton = (Button)view.findViewById(R.id.buttonAddBook);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSendButton = (Button) view.findViewById(R.id.buttonSend);


                //check for empty fields
                if (name.getText().toString().isEmpty() ||
                        author.getText().toString().isEmpty() ||
                        price.getText().toString().isEmpty() ||
                        year.getText().toString().isEmpty() ) {
                    //show toast and don't continue
                    //Toast.makeText(getContext(), "Please fill up all the fields.", Toast.LENGTH_SHORT).show();
                        AddBookFragment.this.showMyToast("Please fill up all the fields.");

                }


            else {
                            /*
        * public Book (String id,
                 String name,
                 String author,
                 String year,
                 String price,
                 String ownerId,
                 String ownerName,
                 String imagePath)*/
                    //get data from fields and send forward as a book
                    Book book = new Book (
                            null, //id null for now
                            name.getText().toString(),
                            author.getText().toString(),
                            year.getText().toString(),
                            price.getText().toString(),
                            null, //ownderid null for now
                            null, //owndername null for now
                            null //image path null for now
                    );
                    onButtonPressed(MessageEnum.ADD_BOOK, book);

                }

            }
        });

        return view;
    }


    @UiThread
    private void showMyToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
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