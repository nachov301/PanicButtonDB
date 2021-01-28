package app.iggy.panicbutton2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import static app.iggy.panicbutton2.MainActivity.DIALOG_ID_CANCEL_EDIT;

public class SetCall extends BaseActivity implements View.OnClickListener, AppDialog.DialogEvents {
    private static final String TAG = "SetCall";
    private static final String SHARED_PREFES_CALL = "number";
    public static final String NUMBER = "number";
    public static final String NAME = "name";
    public static final String SWITCH = "switch";
    private EditText number;
    private EditText name;
    private Switch mSwitch;
    protected static final int CHOOSE_CONTACTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_call);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activateToolbar(true);

        number = (EditText) findViewById(R.id.cal_number);
        name = (EditText) findViewById(R.id.cal_name);
        mSwitch = (Switch) findViewById(R.id.cal_switch);

        Button save = (Button) findViewById(R.id.cal_save);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFES_CALL, MODE_PRIVATE);
        Boolean switchOnOff = sharedPreferences.getBoolean(SWITCH, false);
//        Toast.makeText(this, "Value: " + switchOnOff, Toast.LENGTH_SHORT).show();
        mSwitch.setChecked(switchOnOff);

        refreshScreen();

        save.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menu_call:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                startActivityForResult(intent, CHOOSE_CONTACTS);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

        @Override
        public void onClick (View v){
            switch (v.getId()) {
                case R.id.cal_save:
                    Log.d(TAG, "onClick: starts");
                    String phoneNumber = number.getText().toString();
                    String contactName = name.getText().toString();
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFES_CALL, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(NUMBER, phoneNumber);
                    editor.putString(NAME, contactName);
                    editor.putBoolean(SWITCH, mSwitch.isChecked());
                    editor.commit();
                    Toast.makeText(SetCall.this, R.string.successfully_saved, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }

        public void refreshScreen(){
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFES_CALL, MODE_PRIVATE);
            String text = sharedPreferences.getString(NUMBER, "");
            String text2 = sharedPreferences.getString(NAME, "");
            number.setText(text);
            name.setText(text2);
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

                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFES_CALL, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(NUMBER, phoneNumber);
                            editor.putString(NAME, name);
                            editor.commit();

                            Toast.makeText(SetCall.this, getString(R.string.contact_added) + name, Toast.LENGTH_LONG).show();
                        }
                    }




                }
                break;
        }
        refreshScreen();
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {

    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        finish();
    }

    @Override
    public void onDialogCancelled(int dialogId) {

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
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(),null);
//        super.onBackPressed();
    }
}