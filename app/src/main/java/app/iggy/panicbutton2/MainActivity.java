package app.iggy.panicbutton2;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends BaseActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
AddEditActivityFragment.OnSaveClicked, AppDialog.DialogEvents{
    private static final String TAG = "MainActivity";

    protected static final int CHOOSE_CONTACTS = 0;

//    whether or not the activity is in two-pane mode
//    i.e. running in landscape on a tablet
    private boolean mTwoPane = false;
    
    public static final int DIALOG_ID_DELETE = 1;
    public static final int DIALOG_ID_CANCEL_EDIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        activateToolbar(true);

        if(findViewById(R.id.task_details_container)!=null){
//           the detail container view will be present only in the large screen layouts (res/values-land and res/values-sw600dp).
//           if this view is present, then the activity should be in two-pane mode
            mTwoPane = true;
        }


    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);
        if (fragment!=null){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        AppProvider appProvider = new AppProvider();
        appProvider.onCreate();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.menumain_addTask:
                    taskEditRequest(null);
                break;
            case R.id.menumain_addcontact:

        if (appProvider.getProfilesCount()<10){
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                    startActivityForResult(intent, CHOOSE_CONTACTS);
        }else{
            Toast.makeText(this, R.string.maximum_allowed, Toast.LENGTH_SHORT).show();
        }
                break;
//            case R.id.menumain_showDurations:
//                break;
//            case R.id.menumain_settings:
//                break;
//            case R.id.menumain_showAbout:
//                break;
//            case R.id.menumain_generate:
//                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(Contact contact) {
        taskEditRequest(contact);
    }

    @Override
    public void onDeleteClick(Contact contact) {
        Log.d(TAG, "onDeleteClick: starts");

        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, contact.getId(), contact.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

        args.putLong("TaskId", contact.getId());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),null);
    }

    private void taskEditRequest(Contact contact){
        Log.d(TAG, "TaskEditRequest: starts");
        if (mTwoPane){
            Log.d(TAG, "TaskEditRequest: in two pane mode (tablet)");
            AddEditActivityFragment fragment = new AddEditActivityFragment();

            Bundle arguments = new Bundle();
            arguments.putSerializable(Contact.class.getSimpleName(), contact);
            fragment.setArguments(arguments);

//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.task_details_container, fragment);
//            fragmentTransaction.commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.task_details_container, fragment)
                    .commit();

        }else{
            Log.d(TAG, "TaskEditRequest: in single-pane mode (phone)");
//          in single-pane mode, start the detail activity for the selected item id.
            Intent detailIntent = new Intent(this, AddEditActivity.class);
            if (contact != null){ //editing a task
                detailIntent.putExtra(Contact.class.getSimpleName(), contact);
                startActivity(detailIntent);
            }else{ //adding a new task
                    startActivity(detailIntent);
            }
        }
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
        switch (dialogId){
            case DIALOG_ID_DELETE:
                Long taskId = args.getLong("TaskId");
                if (BuildConfig.DEBUG && taskId == 0) throw new AssertionError("Task ID is zero");
                getContentResolver().delete(ContactContract.buildContactUri(taskId), null, null);
                break;
            case DIALOG_ID_CANCEL_EDIT:
//              no action required
                break;
        }
        }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        switch (dialogId){
            case DIALOG_ID_DELETE:
//              no action required
                break;
            case DIALOG_ID_CANCEL_EDIT:
                finish();
                break;
        }
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp: here");
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: starts");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.task_details_container);
        if ((fragment==null) || fragment.canClose()){
            super.onBackPressed();
        }else{
//            show dialogue to get confirmation to quit editing
            AppDialog dialog = new AppDialog();
            Bundle args = new Bundle();
            args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
            args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), null);
        }
    }


    //gonna be called after we chooose a contact
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (CHOOSE_CONTACTS) :
                Log.d(TAG, "onActivityResult: starts");
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "onActivityResult: is ok");
                    Uri contactData = data.getData();
                    Cursor c =  getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        Log.d(TAG, "onActivityResult: moved to first");
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String  name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                        String phone = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//                        Toast.makeText(this,  name + " has number " + phone, Toast.LENGTH_LONG).show();
                        String phoneNumber="test";

                        if ( phone.equalsIgnoreCase("1")){
                            phone = "true";
                        }else{
                            phone = "false" ;
                        }

                        if (Boolean.parseBoolean(phone))
                        {
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
                            while (phones.moveToNext())
                            {
                                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }
//                            Toast.makeText(Config.this, "Phone number: " + phoneNumber, Toast.LENGTH_LONG).show();
                            phones.close();

                            ContentResolver contentResolver = getContentResolver();
                            ContentValues values = new ContentValues();

                            values.put(ContactContract.Columns.CONTACT_NAME, name);
                            values.put(ContactContract.Columns.CONTACT_NUMBER, phoneNumber);
//                                values.put(TaskContract.Columns.TASKS_SORTORDER, so);
                            contentResolver.insert(ContactContract.CONTENT_URI, values);

                            Toast.makeText(MainActivity.this, " Contact added: " + name, Toast.LENGTH_LONG).show();
                        }
                    }




                }
                break;
        }

    }

}
