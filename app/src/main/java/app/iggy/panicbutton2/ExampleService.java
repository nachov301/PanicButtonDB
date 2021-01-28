package app.iggy.panicbutton2;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
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

import static app.iggy.panicbutton2.App.CHANNEL_ID;
import static app.iggy.panicbutton2.SetCall.NUMBER;
import static app.iggy.panicbutton2.SetCall.SWITCH;

public class ExampleService extends Service implements GetLocation.SendMessage{
    private static final String TAG = "ExampleService";
    private TextView latitude, longitude, country, address, message;
    private String phoneNumber;
    private String textMessage;
    private String phoneNumbers;
    private String cont;
    private int cont2;
    public static final int DELETE_DIALOG_ID = 1;
    Button btnStop;


    public static final String SHARED_PREFS = "sharedPrefs";
    private static final String SHARED_PREFES_CALL = "number";

    public static final String TEXT = "text";

    public static final int REQUEST_CODE_READ_CONTACTS = 1;

    Handler mHandler = new Handler();
    private int seconds;


    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
            Intent notificationIntent = new Intent(this, Main2Activity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(getString(R.string.not_alert))
                    .setContentText(getString(R.string.not_messages))
                    .setSmallIcon(R.drawable.roundedbutton)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);

//      do heavy work on background thread


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Config mconfig = new Config();
        Main2Activity main2Activity = new Main2Activity();


        textMessage = mconfig.getMESSAGE();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = sharedPreferences.getString(TEXT, "");
        if (text.length()>0){
            int time = Integer.parseInt(text);
            seconds = (time * 1000);
        }else{
            seconds = (20000);
        }



        sharedPreferences = getSharedPreferences(SHARED_PREFES_CALL, MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString(NUMBER, "");
        Boolean switchOnOff = sharedPreferences.getBoolean(SWITCH, false);

        if (switchOnOff){

            if (phoneNumber.length()>0){


                if (ContextCompat.checkSelfPermission(ExampleService.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permissions for calling weren't granted.", Toast.LENGTH_LONG).show();

                } else {

                    try {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        callIntent.setData(Uri.parse("tel:"+phoneNumber));
                        startActivity(callIntent);
                    } catch (ActivityNotFoundException activityException) {
                        Log.e("Calling a Phone Number", "Call failed", activityException);
                    }

                }


            }//checks phonenumber>0

        }//switch



        startRepeating();

//      do heavy work on background thread
        return START_NOT_STICKY;
    }


    public void startRepeating() {
//        mHandler.postDelayed(mRunnable, 5000);

        mRunnable.run();
    }

    public void stopRepeating() {
        mHandler.removeCallbacks(mRunnable);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            getCurrentLocation();
            mHandler.postDelayed(this, seconds);
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: starts");
        stopRepeating();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void getCurrentLocation() {
        if (getLocationMode(this) < 3) {//if from getLocationMode
            Toast.makeText(ExampleService.this, R.string.location_needs_on, Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }//if from getlocationmode
            Log.d(TAG, "getCurrentLocation: starts");

            LocationManager locationManager = (LocationManager) getSystemService(
                    Context.LOCATION_SERVICE
            );

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

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

                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(1000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
//                            initialize location callback
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
//                                    initialize location
                                Location location = locationResult.getLastLocation();


                                try {
                                    Geocoder geocoder = new Geocoder(ExampleService.this, Locale.getDefault());

                                    List<Address> addresses1 = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                    GetLocation getLocation = new GetLocation(ExampleService.this, addresses1);
                                    getLocation.execute(location);


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        };
//                            request location updates

                        if (ActivityCompat.checkSelfPermission(ExampleService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ExampleService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            } else {
//                when location service is not enabled
//                open location setting
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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



//        message.setText(text);

        if ((contacts!=null) && (contacts.size()>0)){
            for (int i = 0; i < contacts.size(); i++) {
//            Toast.makeText(this, "Number: " + contacts.get(i), Toast.LENGTH_LONG).show();

                try {
                    Log.d(TAG, "sendMessage: supposed to send a message in here");
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(contacts.get(i), null, text + "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude, null, null);
                }catch (Exception e){
                    Toast.makeText(ExampleService.this,"Error al enviar SMS",Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(this, getString(R.string.toast_number) + contacts.get(i) + "\nSMS: " + text, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, R.string.no_contacts, Toast.LENGTH_LONG).show();
        }


        Log.d(TAG, "sendMessage: ends");
    }


}
