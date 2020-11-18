package app.iggy.panicbutton;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class Main3Activity extends BaseActivity {

    private static final String TAG = "Main3Activity";

    public static final String PHONE_NUMBER2 = "phoneNumber2.txt";
    public static final String COUNTER = "counter.txt";

    private ListView contactNames;
//    private TextView contactsAdded;

//    public static int cont = 0;

    public static final int REQUEST_CODE_READ_CONTACTS = 1;

//    private Button btnSave;

    final ArrayList<String> numbers = new ArrayList<String>();
    final ArrayList<String> listContacts = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activateToolbar(true);

        contactNames = (ListView) findViewById(R.id.contact_names);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Main3Activity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(view, "This app can't display your contacts records unless you...", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Grant access", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "Snackbar onClick: starts");
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(Main3Activity.this, Manifest.permission.READ_CONTACTS)) {
                                        Log.d(TAG, "Snackbar onClick: calling request permissions");
                                        ActivityCompat.requestPermissions(Main3Activity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
                                    } else {
//                                    the user permanently denied the permission, so we take 'em to the settings
                                        Log.d(TAG, "Snackbar onClick: launching settings");
                                        Intent intent = new Intent();
//                                      set an action to be performed, in this case launch the details settings of the app
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                      builds the uri we are gonna that we are gonna use for setting the data of our intent
                                        Uri uri = Uri.fromParts("package", Main3Activity.this.getPackageName(), null);
                                        Log.d(TAG, "Snackbar onClick: Intent uri is " + uri.toString());
//                                      set the data for the intent
                                        intent.setData(uri);
//                                      we start our activity passing the intent we created previously
                                        Main3Activity.this.startActivity(intent);
                                    }
                                    Log.d(TAG, "Snackbar onClick: ends");
                                }
                            }).show();
                } else {
                    Log.d(TAG, "onClick: starts else");
                    String[] projection = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
                    ContentResolver contentResolver = getContentResolver();

                    Contacts contacts = new Contacts();

                    Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            null,
                            null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    if (cursor != null) {
                        final ArrayList<Contacts> contactsList = new ArrayList<Contacts>();
                        int i = 0;
                        while (cursor.moveToNext() && (i < 1000)) {
                            i++;
                            contacts = new Contacts();
                            contacts.setContactName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                            contacts.setPhoneNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            contactsList.add(contacts);
                            Log.d(TAG, "onClick: ++++++++++++++" + contactsList);
//                            contactsList.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                        }
                        cursor.close();
                        Log.d(TAG, "onClick: contacts list is " + contactsList);
                        Adapter adapter = new Adapter(Main3Activity.this, R.layout.contact_detail, contactsList);
                        contactNames.setAdapter(adapter);

                        contactNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //gets the phone number and the name of the contact that was clicked
                                String num = contactsList.get(position).getPhoneNumber();
                                String name = contactsList.get(position).getContactName();

                                ContentResolver contentResolver = getContentResolver();
                                ContentValues values = new ContentValues();

                                values.put(ContactContract.Columns.CONTACT_NAME, name);
                                values.put(ContactContract.Columns.CONTACT_NUMBER, num);
//                                values.put(TaskContract.Columns.TASKS_SORTORDER, so);
                                contentResolver.insert(ContactContract.CONTENT_URI, values);

                                Toast.makeText(Main3Activity.this, " Contacto agregado: " + name, Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                }
            }

        });

    }

}
