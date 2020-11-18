package app.iggy.panicbutton;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor mCursor;
    private OnTaskClickListener mListener;

    interface OnTaskClickListener{
        void onEditClick(Contact contact);
        void onDeleteClick(Contact contact);
    }

    public CursorRecyclerViewAdapter(Cursor cursor, OnTaskClickListener listener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: constructor called " + cursor);
        mCursor = cursor;
        mListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_items, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts");

        if ((mCursor == null) || (mCursor.getCount() == 0)){
            Log.d(TAG, "onBindViewHolder: providing instructions");
            holder.name.setText(R.string.instructions_heading);
            holder.description.setText(R.string.instructions);
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }else{
            if (!mCursor.moveToPosition(position)){
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final Contact contact = new Contact(mCursor.getLong(mCursor.getColumnIndex(ContactContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(ContactContract.Columns.CONTACT_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(ContactContract.Columns.CONTACT_NUMBER)),
                    mCursor.getInt(mCursor.getColumnIndex(ContactContract.Columns.CONTACT_SORTORDER)));

            holder.name.setText(contact.getName());
            holder.description.setText(contact.getDescription());
            holder.editButton.setVisibility(View.VISIBLE); // TODO add onClick listener
            holder.deleteButton.setVisibility(View.VISIBLE); // TODO add onClick listener

            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: starts");
                    switch (v.getId()){
                        case R.id.tli_edit:
                            if (mListener!=null){
                                mListener.onEditClick(contact);
                            }
                            break;
                        case R.id.tli_delete:
                            if (mListener!=null){
                                mListener.onDeleteClick(contact);
                            }
                            break;
                            default:
                                Log.d(TAG, "onClick: found unexpected button id");
                    }
                    Log.d(TAG, "onClick: button with id " + v.getId() + " clicked");
                    Log.d(TAG, "onClick: task name is " + contact.getName());
                }
            };

            holder.deleteButton.setOnClickListener(buttonListener);
            holder.editButton.setOnClickListener(buttonListener);
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");
        if ((mCursor == null) || (mCursor.getCount() == 0)){
            return 1; //fib, because we populate a single view with instructions
        }else{
            Log.d(TAG, "getItemCount: count is " + mCursor.getCount());
            return mCursor.getCount();
        }
    }

    /**
     * swap in a new cursor, returning the old Cursor
     * the returned old cursor is <em>not</em> closed
     *
     * @param newCursor The new cursor to be used
     * @return  Returns the previously set Cursor, or null if there wasn't one.
     * if the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned
     */
    Cursor swapCursor(Cursor newCursor){
        Log.d(TAG, "swapCursor: cursor is " + newCursor);
        if (newCursor == mCursor){
            return null;
        }

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null){
            //notify the observers about the new cursor
            notifyDataSetChanged();
        }else{
            //notify the observers about the lack of data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "TaskViewHolder";

        TextView name = null;
        TextView description = null;
        ImageButton editButton = null;
        ImageButton deleteButton = null;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "TaskViewHolder: starts");

            this.name = (TextView) itemView.findViewById(R.id.tli_name);
            this.description = (TextView) itemView.findViewById(R.id.tli_description);
            this.editButton = (ImageButton) itemView.findViewById(R.id.tli_edit);
            this.deleteButton = (ImageButton) itemView.findViewById(R.id.tli_delete);
        }
    }
}
