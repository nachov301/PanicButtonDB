package app.iggy.panicbutton;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class WidgetActivity extends AppCompatActivity implements AppDialog.DialogEvents, GetLocation.SendMessage {
    private static final String TAG = "WidgetActivity";
    private TextView latitude, longitude, country, address, message;
    private String phoneNumber;
    private String textMessage;
    private String phoneNumbers;
    private String cont;
    private int cont2;
    public static final int DELETE_DIALOG_ID = 1;
    Button btnStop;

    public static final int REQUEST_CODE_READ_CONTACTS = 1;

    Handler mHandler = new Handler();


    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);

//      initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Config mconfig = new Config();
        Main2Activity main2Activity = new Main2Activity();

        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        country = (TextView) findViewById(R.id.country);
        address = (TextView) findViewById(R.id.address);
        message = (TextView) findViewById(R.id.message);

        //      checks whether location is off or on
        if (isLocationEnabled(this)) {
            Log.d(TAG, "onCreate: location is enabled");
            Toast.makeText(this, getString(R.string.LocationEnabled), Toast.LENGTH_LONG).show();

            //      to ask for permissions to send message
            if (ActivityCompat.checkSelfPermission(WidgetActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(WidgetActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WidgetActivity.this, new String[]
                        {Manifest.permission.SEND_SMS,}, 1000);
            } else {

            }


            int hasReadContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
            Log.d(TAG, "onCreate: hasReadContactsPermission: " + hasReadContactsPermission);

            if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onCreate: requesting permissions");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
            }

//            sendMessage();


            textMessage = mconfig.getMESSAGE();



//            getCurrentLocation();

            if ((ActivityCompat.checkSelfPermission(WidgetActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    && (ActivityCompat.checkSelfPermission(WidgetActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

//                getCurrentLocation();
                    startRepeating();

            } else {
//          when permission isnt granted
//          request permission
                ActivityCompat.requestPermissions(WidgetActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }



        } else {
            Log.d(TAG, "onCreate: location is disabled");
            Toast.makeText(this, getString(R.string.LocationDisabled), Toast.LENGTH_LONG).show();


            AppDialog dialog = new AppDialog();
            Bundle args = new Bundle();
            args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.diag_message));
            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.diag_settings);

            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), null);

            message.setText(R.string.enableLocation);

        }
        btnStop = findViewById(R.id.btn_widget_stop);

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRepeating();
            }
        });
    }

    public void startRepeating(){
//        mHandler.postDelayed(mRunnable, 5000);
        mRunnable.run();
    }

    public void stopRepeating(){
        mHandler.removeCallbacks(mRunnable);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            getCurrentLocation();
            mHandler.postDelayed(this, 10000);
        }
    };


    private void getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation: starts");
        LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE
        );
//          check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                when location service is enabled
//                get last location
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
//                        initialize location
                    final Location location = task.getResult();
//                        check condition
//                    if (location != null) {
////                            when location result is not null
////                            set latitude
//                        latitude.setText("Latitude: " + location.getLatitude());
////                            set longitude
//                        longitude.setText("Longitude: " + location.getLongitude());
//
//                        try {
//
//                            Geocoder geocoder = new Geocoder(WidgetActivity.this, Locale.getDefault());
//
//                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//
//                            GetLocation getLocation = new GetLocation(WidgetActivity.this, addresses);
//                            getLocation.execute(location);
//
//                            country.setText("Country name: " + addresses.get(0).getCountryName());
//                            address.setText("Address: " + addresses.get(0).getAddressLine(0));
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    } else {
//                            when location result is null
//                            initialize location request
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
//                            initialize location callback
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
//                                    initialize location
                                Location location = locationResult.getLastLocation();

                                latitude.setText(getString(R.string.latitude2) + location.getLatitude());
                                longitude.setText(getString(R.string.longitude2) + location.getLongitude());

                                try {
                                    Geocoder geocoder = new Geocoder(WidgetActivity.this, Locale.getDefault());

                                    List<Address> addresses1 = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                    GetLocation getLocation = new GetLocation(WidgetActivity.this, addresses1);
                                    getLocation.execute(location);

                                    country.setText(getString(R.string.countryName2) + addresses1.get(0).getCountryName());
                                    address.setText(getString(R.string.address2) + addresses1.get(0).getAddressLine(0));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        };
//                            request location updates
                        if (ActivityCompat.checkSelfPermission(WidgetActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(WidgetActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest
                                , locationCallback, Looper.myLooper());
//                    }//else statement

                }
            });
        }else{
//                when location service is not enabled
//                open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        check condition
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
//        when permissions are granted
//        call method
            getCurrentLocation();
        }else{
//            when permission are denied
//            display toast
//            Toast.makeText(WidgetActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
        }
    }


    //    to check if gps is enabled
    public static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
// This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
// This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
        //                when location service is not enabled
        //                open location setting
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");
    }

    @Override
    public void sendMessage(String latitude, String longitude, String countryName, String address) {

//        Toast.makeText(WidgetActivity.this, "Latitude: " + latitude + " Longitude: " + longitude + " Country name: " + countryName + " Address: " + address, Toast.LENGTH_LONG).show();
        AppProvider appProvider = new AppProvider();
        appProvider.onCreate();
        List<String> contacts = appProvider.Search();
        FileInputStream fis2 = null;
        String text = "";
        try {
            fis2 = openFileInput(textMessage);
            InputStreamReader isr2 = new InputStreamReader(fis2);
            BufferedReader br2 = new BufferedReader(isr2);
            StringBuilder sb2 = new StringBuilder();
            String text2;

            while ((text2 = br2.readLine()) != null) {
                sb2.append(text2).append("\n");
            }

            text = sb2.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        message.setText(text);

        if ((contacts!=null) && (contacts.size()>0)){
            for (int i = 0; i < contacts.size(); i++) {
//            Toast.makeText(this, "Number: " + contacts.get(i), Toast.LENGTH_LONG).show();
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(contacts.get(i), null, text + "\nLatitud: " + latitude + "\nLongitud: " + longitude + "\nDireccion: " + address + "Pais: " + countryName, null, null);
                Toast.makeText(this, getString(R.string.toast_number) + contacts.get(i) + "\nSMS: " + text + getString(R.string.toast_latitude) + latitude + getString(R.string.toast_longitude) + longitude + getString(R.string.toast_address) + address + getString(R.string.toast_country) + countryName, Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, R.string.no_contacts, Toast.LENGTH_LONG).show();
        }


        Log.d(TAG, "sendMessage: ends");
    }
}
