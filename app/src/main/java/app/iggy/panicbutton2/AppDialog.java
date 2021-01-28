package app.iggy.panicbutton2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AppDialog extends DialogFragment {

    private static final String TAG = "AppDialog";

    public static final String DIALOG_ID = "id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";

    /**
     * the dialog's callback interface to notify of user selected results (deletion, etc)
     */
    interface DialogEvents{
        void onPositiveDialogResult(int dialogId, Bundle args);
        void onNegativeDialogResult(int dialogId, Bundle args);
        void onDialogCancelled(int dialogId);
    }

    private DialogEvents mDialogEvents;

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: entering onAttach activity is " + context.toString());
        super.onAttach(context);

        //activities containing the fragment must implements it's callbacks.
        if (!(context instanceof DialogEvents)){
            throw new ClassCastException(context.toString() + " must implement AppDialog.DialogEvents interface");
        }

        mDialogEvents = (DialogEvents) context;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: entering...");
        super.onDetach();

//        Reset the active callbacks interface, because we dont have an activity any longer.
        mDialogEvents = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: starts");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final Bundle arguments = getArguments();
        final int dialogId;
        String messageString;
        int positiveStringId;
        int negativeStringId;

        if (arguments!=null){
            dialogId = arguments.getInt(DIALOG_ID);
            messageString = arguments.getString(DIALOG_MESSAGE);

            if(dialogId == 0 || messageString == null) {
                throw new IllegalArgumentException("DIALOG_ID and/or DIALOG_MESSAGE not present in the bundle");
            }
//            Returns the value associated with the given key, or 0 if no mapping of the desired type exists for the given key.
            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID);
            if (positiveStringId == 0){
                positiveStringId = R.string.ok;
            }
            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID);
            if (negativeStringId == 0){
                negativeStringId = R.string.cancel;
            }
        }else{
            throw new IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in the bundle");
        }

        builder.setMessage(messageString)
                .setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        callback positive result method
                        if (mDialogEvents != null){
                            mDialogEvents.onPositiveDialogResult(dialogId, arguments);
                        }
                    }
                })
                .setNegativeButton(negativeStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        callback negative result method
                        if (mDialogEvents != null){
                            mDialogEvents.onNegativeDialogResult(dialogId, arguments);
                        }
                    }
                });

        return builder.create();
    }
    //it's called when the user dismisses the dialog without using the buttons inside it
    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "onCancel: called");
        if (mDialogEvents != null){
            int dialogId = getArguments().getInt(DIALOG_ID);
            mDialogEvents.onDialogCancelled(dialogId);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(TAG, "onDismiss: called");
        super.onDismiss(dialog);
    }

}
