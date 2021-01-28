package app.iggy.panicbutton2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * basic database class for the application
 *
 * the only class that should use this is {@link AppProvider}.
 *
 * this class will create the db and also provide the connection to it
 */

class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";
    public static final String DATABASE_NAME = "TaskTimer.db";
    public static final int DATABASE_VERSION = 1;

//    implement AppDatabase as a singleton
    private static AppDatabase instance = null;

//we set the constructor to private so we avoid instantiates of the class to being created
//we want a single instance of the class to exist
    private AppDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor");
    }

    /**
     *when another class needs an instance of this class it'll call getInstance method,
     * the first time its gonna be null and it'd be created cuz we have declared it null
     * but the second time it's not gonna be null anymore so it's gonna return the instance
     *
     * get an instance of the app's singleton database helper object
     *
     * @param context the content providers context
     * @return a SQLite database helper object
     */
    static AppDatabase getInstance(Context context){
        if (instance == null){
            Log.d(TAG, "getInstance: creating new instance");
            instance = new AppDatabase(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSQL;
//        sSQL = "create table tasks (_id integer primary key not null, Name text not null, Description Text, SortOrder integer);";
        sSQL = "CREATE TABLE " + ContactContract.TABLE_NAME + " ("
            + ContactContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
            + ContactContract.Columns.CONTACT_NAME + " TEXT NOT NULL, "
            + ContactContract.Columns.CONTACT_NUMBER + " TEXT, "
            + ContactContract.Columns.CONTACT_SORTORDER + " INTEGER);";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);

        Log.d(TAG, "onCreate: ends");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
