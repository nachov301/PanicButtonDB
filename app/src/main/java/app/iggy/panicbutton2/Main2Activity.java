package app.iggy.panicbutton2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;


public class Main2Activity extends AppCompatActivity implements AppDialog.DialogEvents, GetLocation.SendMessage {
    private Handler mHandler = new Handler();
    private static final String TAG = "Main2Activity";
    private Button config;
    private TextView textView3, textView6;
    private String phoneNumber;
    private String textMessage;
    private String phoneNumbers;
    private String cont;
    private int cont2;
    private String latitude;
    private String longitude;
    private String country;
    private String address;
    public static final int DELETE_DIALOG_ID = 1;

    private ListView contactNames;
    private Button contacts;
    private Button btnStop;
    private Button btnTweet;
    private ImageView btnPanic;

    private boolean savedInstanceStateDone;

    public static final int REQUEST_CODE_READ_CONTACTS = 1;

    private static final String SHARED_PREFES_CALL = "number";

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // The request code used in ActivityCompat.requestPermissions()
// and returned in the Activity's onRequestPermissionsResult()
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


//      to ask for permissions to send message
        if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Main2Activity.this, new String[]
                    {Manifest.permission.SEND_SMS,}, 1000);
        } else {

        }

//      initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // checks whether location is enabled or disabled
        if (isLocationEnabled(Main2Activity.this)) {
            Log.d(TAG, "onCreate: location is enabled");
            Toast.makeText(Main2Activity.this, getString(R.string.LocationEnabled), Toast.LENGTH_LONG).show();
            startRepeating();
        } else {
            Log.d(TAG, "onCreate: location is disabled");
            Toast.makeText(Main2Activity.this, getString(R.string.LocationDisabled), Toast.LENGTH_LONG).show();
            AppDialog dialog = new AppDialog();
            Bundle args = new Bundle();
            args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.diag_message));
            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.diag_settings);

            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), null);
        }

//        final Button config = (Button) findViewById(R.id.configBtn);
//        Button panicBtn = (Button) findViewById(R.id.panicBtn);
        btnStop = findViewById(R.id.btn_stop);
        btnTweet = findViewById(R.id.btnTweet);
        btnPanic = findViewById(R.id.btnpanic2);

        Config mconfig = new Config();
        Main3Activity main3Activity = new Main3Activity();

        textView3 = (TextView) findViewById(R.id.textView3);
//        textView4 = (TextView) findViewById(R.id.textView4);
//        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (TextView) findViewById(R.id.textView6);

//        phoneNumber = mconfig.getPhoneNumber();
        textMessage = mconfig.getMESSAGE();

        btnPanic.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //      checks whether location is off or on
                if (isLocationEnabled(Main2Activity.this)) {
                    Log.d(TAG, "onCreate: location is enabled");

                    if ((ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                            && (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(new Intent(Main2Activity.this, ExampleService.class));
                        } else {
                            startService(new Intent(Main2Activity.this, ExampleService.class));
                        }


                    } else {
//          when permission isn't granted request permission
                        ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                    }


                    Toast.makeText(Main2Activity.this, getString(R.string.LocationEnabled), Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "onCreate: location is disabled");
                    Toast.makeText(Main2Activity.this, getString(R.string.LocationDisabled), Toast.LENGTH_LONG).show();
                    AppDialog dialog = new AppDialog();
                    Bundle args = new Bundle();
                    args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
                    args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.diag_message));
                    args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.diag_settings);

                    dialog.setArguments(args);
                    dialog.show(getSupportFragmentManager(), null);
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(Main2Activity.this, ExampleService.class);
                stopService(serviceIntent);
            }
        });

        //for sending tweets
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                LocationManager locationManager = (LocationManager) getSystemService(
                        Context.LOCATION_SERVICE
                );
//          check condition
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                when location service is enabled
//                get last location
                    if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        return;
                    }
                    fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
//                        initialize location
                            final Location location = task.getResult();

                            LocationRequest locationRequest = new LocationRequest()
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(10000)
                                    .setFastestInterval(1000)
//                                one for one refresh only
                                    .setNumUpdates(1);
//                            initialize location callback
                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    Location location = locationResult.getLastLocation();
                                    try {
                                        Geocoder geocoder = new Geocoder(Main2Activity.this, Locale.getDefault());

                                        List<Address> addresses1 = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//
//                                        GetLocation getLocation = new GetLocation(Main2Activity.this, addresses1);
//                                        getLocation.execute(location);


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


                                        // Create intent using ACTION_VIEW and a normal Twitter url:
                                        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                                                urlEncode(text),
                                                urlEncode("https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude()));
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

                                        // Narrow down to official Twitter app, if available:
                                        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
                                        for (ResolveInfo info : matches) {
                                            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                                                intent.setPackage(info.activityInfo.packageName);
                                            }
                                        }

                                        startActivity(intent);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            };
//                            request location updates
                            if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions

                                return;
                            }
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest
                                    , locationCallback, Looper.myLooper());
//                    }//else statement

                        }
                    });
                } else {
                    Log.d(TAG, "onClick: in the else in twitter");
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }



            }
        });

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    //    for tweeting
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.menumain2_pref:
//                in case my custom setting adapter doesnt work i come back to this one
//                openConfig();
                openSettings();
                break;
            case R.id.menumain2_howtouse:
                startActivity(new Intent(Main2Activity.this, Pop2.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startRepeating(){

        if (getLocationMode(this) < 3) {//if from getLocationMode
            Toast.makeText(Main2Activity.this, R.string.location_needs_on, Toast.LENGTH_LONG).show();
            //            starts the activity for the user to enable the location
//                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

//            Toast.makeText(Main2Activity.this, getString(R.string.LocationDisabled), Toast.LENGTH_LONG).show();

                try {
                    AppDialog dialog = new AppDialog();
                    Bundle args = new Bundle();
                    args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
                    args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.diag_message));
                    args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.diag_settings);

                    dialog.setArguments(args);
                    dialog.show(getSupportFragmentManager(), null);

                }catch (Exception e){
                    Log.d(TAG, "startRepeating: " + e);
                }




        }else{
            mHandler.postDelayed(mRunnable, 10000);

            mRunnable.run();
        }


    }


    public void stopRepeating(){
        mHandler.removeCallbacks(mRunnable);
        Log.d(TAG, "stopRepeating: " + mHandler);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: starts");
            getCurrentLocation();
            mHandler.postDelayed(this, 10000);
        }
    };

    public void openConfig() {
        Intent intent = new Intent(this, Config.class);
        startActivity(intent);
    }

    public void openSettings(){
        startActivity(new Intent(this, SettingsAdapter.class));
    }


    public void getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation: starts in Main2Activity");
//        if (getLocationMode(this) < 3) {//if from getLocationMode
//            Toast.makeText(Main2Activity.this, R.string.location_needs_on, Toast.LENGTH_LONG).show();
//            //            starts the activity for the user to enable the location
////                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//
////            Toast.makeText(Main2Activity.this, getString(R.string.LocationDisabled), Toast.LENGTH_LONG).show();
//            AppDialog dialog = new AppDialog();
//            Bundle args = new Bundle();
//            args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
//            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.diag_message));
//            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.diag_settings);
//
//            dialog.setArguments(args);
//            dialog.show(getSupportFragmentManager(), null);
//
//
//        }//if from getlocationmode

        LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE
        );
//          check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions

                Log.d(TAG, "getCurrentLocation: is returning");
                return;
            }//before was here
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Log.d(TAG, "onComplete: starts");
//                        initialize location
                        final Location location = task.getResult();

                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
//                                one for one refresh only
                                .setNumUpdates(1);
//                            initialize location callback
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Log.d(TAG, "onLocationResult: starts");
//                                    initialize location
                                Location location = locationResult.getLastLocation();
                                Log.d(TAG, "onLocationResult: here supposed to attach location to textviews");
//                                    set latitude
                                textView3.setText(getString(R.string.latitude2) + location.getLatitude() + "  " + getString(R.string.longitude2) + location.getLongitude());
//                                latitude = Double.toString(location.getLatitude());
//                                    set longitude
//                            textView4.setText(getString(R.string.longitude2) + location.getLongitude());

                                try {
                                    Geocoder geocoder = new Geocoder(Main2Activity.this, Locale.getDefault());

                                    List<Address> addresses1 = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                    GetLocation getLocation = new GetLocation(Main2Activity.this, addresses1);
                                    getLocation.execute(location);

//                                textView5.setText(getString(R.string.countryName2) + addresses1.get(0).getCountryName());

                                    textView6.setText(getString(R.string.address2) + addresses1.get(0).getAddressLine(0));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        };
                        if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions

                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest
                                , locationCallback, Looper.myLooper());
                    }
                });
//            }//now here
        } else {
//            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public int getLocationMode(Context context) {
        try {
            Log.d(TAG, "getLocationMode: starts in try");
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        check condition
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
//        when permissions are granted
//        call method
            getCurrentLocation();

        } else {

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
            return (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
//                when location service is not enabled
//                open location setting
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {

    }

    @Override
    public void onDialogCancelled(int dialogId) {

    }

    @Override
    protected void onDestroy() {
        stopRepeating();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
//        stopRepeating();
        super.onPause();
    }

    @Override
    public void sendMessage(String latitude, String longitude, String countryName, String address) {
        Log.d(TAG, "sendMessage: ends");
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: starts");
        startRepeating();
//        onCreate();
        super.onRestart();
    }
}
