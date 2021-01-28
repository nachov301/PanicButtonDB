package app.iggy.panicbutton2;


import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;


/**
 * A simple {@link Fragment} subclass.
 */

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivityFragment";

    public static final int LOADER_ID = 0;

    private CursorRecyclerViewAdapter mAdapter; //add adapter reference

    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: starts");
    }
//when initializing the load manager is better to do it in this method
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new CursorRecyclerViewAdapter(null, (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity());
        recyclerView.setAdapter(mAdapter);

        Log.d(TAG, "onCreateView: returning");
        return view;
    }

//   this method query our data in another thread and returns a cursor to onLoadFinished method when it's done
//   we use this so we dont freeze out user interface
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: starts with id " + id);
        String[] projection = {ContactContract.Columns._ID, ContactContract.Columns.CONTACT_NAME,
                                ContactContract.Columns.CONTACT_NUMBER, ContactContract.Columns.CONTACT_SORTORDER};
//      <order by> Task.SortOrder, Task.Name COLLATE NOCASE
        String sortOrder = ContactContract.Columns.CONTACT_SORTORDER + "," + ContactContract.Columns.CONTACT_NAME + " COLLATE NOCASE";

        switch (id){
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        ContactContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);
                default:
                    throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Entering onLoadFinished");
        mAdapter.swapCursor(data);
        int count = mAdapter.getItemCount();

//        old code for logging database
//        if(data != null){
//            while(data.moveToNext()){
//                for (int i=0; i<data.getColumnCount(); i++){
//                    Log.d(TAG, "onLoadFinished: " + data.getColumnName(i) + ": " + data.getString(i));
//                }
//                Log.d(TAG, "onLoadFinished: ================================================");
//            }
//            count = data.getCount();
//        }

        Log.d(TAG, "onLoadFinished: count is " + count);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        mAdapter.swapCursor(null);
    }
}
