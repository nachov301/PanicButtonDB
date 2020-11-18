package app.iggy.panicbutton;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddEditActivityFragment extends Fragment {
    private static final String TAG = "AddEditActivityFragment";

    public enum FragmentEditMode {EDIT, ADD};
    private FragmentEditMode mMode;

    private EditText mNameTextView;
    private EditText mDescriptionTextView;
    private EditText mSortOrderTextView;
    private Button mSaveButton;
    private OnSaveClicked mSaveListener = null;

    interface OnSaveClicked{
        void onSaveClicked();
    }

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    public boolean canClose(){
        return false;
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);

//        activities containing this fragment must implements it's callbacks
        Activity activity = getActivity();
        if (!(activity instanceof OnSaveClicked)){
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement AddEditActivityFragment.OnSaveClicked interface");
        }
        mSaveListener = (OnSaveClicked) activity;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        mSaveListener = (OnSaveClicked) null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");

        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        mNameTextView = (EditText) view.findViewById(R.id.addedit_name);
        mDescriptionTextView = (EditText) view.findViewById(R.id.addedit_description);
        mSortOrderTextView = (EditText) view.findViewById(R.id.addedit_sortorder);
        mSaveButton = (Button) view.findViewById(R.id.addedit_save);


//        Bundle arguments = getActivity().getIntent().getExtras();
        Bundle arguments = getArguments();
        final Contact contact;

        if (arguments != null){
            Log.d(TAG, "onCreateView: retrieving task details.");

            contact = (Contact) arguments.getSerializable(Contact.class.getSimpleName());

            if (contact != null){
                Log.d(TAG, "onCreateView: task details found, editing");
                mNameTextView.setText(contact.getName());
                mDescriptionTextView.setText(contact.getDescription());
                mSortOrderTextView.setText(Integer.toString(contact.getSortOrder()));
                mMode = FragmentEditMode.EDIT;
            }else{
//                no task, so must be adding a new task, not editing an existing one.
                mMode = FragmentEditMode.ADD;
            }
        }else {
            contact = null;
            Log.d(TAG, "onCreateView: no arguments, adding a new record");
            mMode = FragmentEditMode.ADD;
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update the database if at least one field has changed
                //there's not need to hit the database unless this had happened
                int so;
                if (mSortOrderTextView.length()>0){
                    so = Integer.parseInt(mSortOrderTextView.getText().toString());
                }else{
                    so = 0;
                }

                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();

                switch (mMode){
                    case EDIT:
                        if (!mNameTextView.getText().toString().equals(contact.getName())){
                            values.put(ContactContract.Columns.CONTACT_NAME, mNameTextView.getText().toString());
                        }
                        if (!mDescriptionTextView.getText().toString().equals(contact.getDescription())){
                            values.put(ContactContract.Columns.CONTACT_NUMBER, mDescriptionTextView.getText().toString());
                        }
                        if (so != contact.getSortOrder()){
                            values.put(ContactContract.Columns.CONTACT_SORTORDER, so);
                        }
                        if (values.size()!=0){
                            Log.d(TAG, "onClick: updating task");
                            contentResolver.update(ContactContract.buildContactUri(contact.getId()), values, null, null);
                        }
                        break;
                    case ADD:
                        if (mNameTextView.length()>0){
                            Log.d(TAG, "onClick: adding a new task");
                            values.put(ContactContract.Columns.CONTACT_NAME, mNameTextView.getText().toString());
                            values.put(ContactContract.Columns.CONTACT_NUMBER, mDescriptionTextView.getText().toString());
                            values.put(ContactContract.Columns.CONTACT_SORTORDER, so);
                            contentResolver.insert(ContactContract.CONTENT_URI, values);
                        }
                        break;
                }
                Log.d(TAG, "onClick: done editing");

                if (mSaveListener!=null){
                    mSaveListener.onSaveClicked();
                }
            }
        });
        Log.d(TAG, "onCreateView: exiting");

        return view;
    }

}
