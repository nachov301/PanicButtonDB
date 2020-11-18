package app.iggy.panicbutton;

import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

class GetLocation extends AsyncTask<Location, Void, String> {
    private static final String TAG = "GetLocation";

    SendMessage mCallback;
    private String latitude;
    private String longitude;
    private String countryName;
    private String address;
    private List<Address> mAddresses;
    interface SendMessage{
        void sendMessage(String latitude, String longitude, String countryName, String address);
    }

    public GetLocation(SendMessage callback, List<Address> addresses) {
        mCallback = callback;
        mAddresses = addresses;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: starts");
        if (mCallback!=null){
            mCallback.sendMessage(s, longitude, countryName, address);
        }
    }

    @Override
    protected String doInBackground(Location... locations) {
        Log.d(TAG, "doInBackground: starts");

        latitude = Double.toString(locations[0].getLatitude());
        longitude = Double.toString(locations[0].getLongitude());

        countryName = mAddresses.get(0).getCountryName();
        address = mAddresses.get(0).getAddressLine(0);

        Log.d(TAG, "doInBackground: ends");
        return latitude;
    }

//    @Override
//    protected String doInBackground(Location... locations) {
//        Log.d(TAG, "doInBackground: starts");
//        LocationService locationService = new LocationService();
//
//        latitude = Double.toString(locationService.getLatitude());
//        longitude = Double.toString(locationService.getLongitude());
//
//        countryName = mAddresses.get(0).getCountryName();
//        address = mAddresses.get(0).getAddressLine(0);
//
//        Log.d(TAG, "doInBackground: ends");
//        return latitude;
//    }
}
