package app.iggy.panicbutton;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * provider for the TaskTimer app. This is the only class that knows about {@link AppDatabase}
 **/

public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;



    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //   i use my package name
    static final String CONTENT_AUTHORITY = "app.iggy.panicbutton";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;

    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;

    /*
    private static final int TASK_TIMINGS = 300;
    private static final int TASK_TIMINGS_ID = 301;
     */

    private static final int TASK_DURATIONS = 400;
    private static final int TASK_DURATIONS_ID = 401;

    private static UriMatcher buildUriMatcher() {
        Log.d(TAG, "buildUriMatcher: starts");
//        if there's no table names in the uri the match is gonna return NO_MATCH
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

//        e.g. content://app.iggy.tasktimer/Tasks
//        if the task table is specified without an id the match is gonna return 100 which is the value we defined previously
        matcher.addURI(CONTENT_AUTHORITY, ContactContract.TABLE_NAME, TASKS);
//        e.g. content://app.iggy.tasktimer/Tasks/8
//        if the task table is specified with an id the match is gonna return 101 which is the value we defined previously
        matcher.addURI(CONTENT_AUTHORITY, ContactContract.TABLE_NAME + "/#", TASKS_ID);

//        matcher.addURI(CONTENT_AUTHORITY, TimingsContrac.TABLE_NAME, TIMINGS);
//        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME + "/#", TIMINGS_ID);
//
//        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME, TASK_DURATIONS);
//        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME + "/#", TASK_DURATIONS_ID);
        Log.d(TAG, "buildUriMatcher: ends");
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: uri: " + uri);
//      gonna compare the uri in the method above "private static UriMatcher buildUriMatcher()"
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case TASKS:
                queryBuilder.setTables(ContactContract.TABLE_NAME);
                break;
            case TASKS_ID:
                queryBuilder.setTables(ContactContract.TABLE_NAME);
//            we use long because the id is a long
                long taskId = ContactContract.getContactId(uri);
                queryBuilder.appendWhere(ContactContract.Columns._ID + " = " + taskId);
                break;
//            case TIMINGS:
//                queryBuilder.setTables(TimingsContract.TABLE_NAME);
//                break;
//            case TIMINGS_ID:
//                queryBuilder.setTables(TimingsContract.TABLE_NAME);
//                long timingId = TimingsContract.getTimingId(uri);
//                queryBuilder.appendWhere(TimingsContract.Columns._ID + " = " + timingId);
//                break;
//            case TASK_DURATIONS:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                break;
//            case TASK_DURATIONS_ID:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                long durationId = DurationContract.getDuration(uri);
//                queryBuilder.appendWhere(DurationsContract.Columns._ID + " = " + durationId);
//                break;

            default:
                throw new IllegalArgumentException("Unknown uri " + uri);

        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
//        gonna return a cursor
        Log.d(TAG, "query: returns");
//        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Log.d(TAG, "query: rows in returned cursor = " + cursor.getCount()); // TODO remove this line

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return ContactContract.CONTENT_TYPE;

            case TASKS_ID:
                return ContactContract.CONTENT_ITEM_TYPE;

//            case TIMINGS:
//                return  TimingsContract.Timings.CONTENT_TYPE;

//            case TIMINGS_ID:
//                return TimingsContract.Timings.CONTENT_ITEM_TYPE;

//            case TASK_DURATIONS:
//                return DurationsContract.TaskDurations.CONTENT_TYPE;

//            case TASK_DURATIONS_ID:
//                return DurationsContract.TaskDurations.CONTENT_ITEM_TYPE;


            default:
                throw new IllegalArgumentException("unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "Entering insert, called with uri: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is: " + match);

        final SQLiteDatabase db;

        Uri returnUri;
        long recordId;

        switch (match) {
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                //returns the id of the inserted row, returns -1 if an error occurred
                recordId = db.insert(ContactContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = ContactContract.buildContactUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;
//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                recordId = db.insert(TimingsContract.TABLE_NAME, null, values);
//                if (recordId >= 0){
//                    returnUri = TimingsContract.Timings.buildTaskUri(recordId);
//                }else{
//                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
//                }
//                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if (recordId >= 0){
            //means something was inserted, if it couldn't have been inserted record id would be -1
            Log.d(TAG, "insert: Setting notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }else{
            Log.d(TAG, "insert: nothing inserted");
        }

        Log.d(TAG, "exiting insert, returning " + returnUri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update: called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "update: match is " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(ContactContract.TABLE_NAME, selection, selectionArgs);
                break;
            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = ContactContract.getContactId(uri);
                selectionCriteria = ContactContract.Columns._ID + " = " + taskId;
                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(ContactContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.delete(TimingsContract.TABLE_NAME, selection, selectionArgs);
//                break;
//            case TIMINGS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long timingsId = TimingsContract.getTaskId(uri);
//                selectionCriteria = TimingsContract.Columns._ID + " = " + timingsId;
//                if ((selection != null) && (selection.length()>0)){
//                    selectionCriteria += " AND (" + selection + ")";
//                }
//                count = db.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs);
//                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        if (count > 0){
            //something was deleted
            Log.d(TAG, "delete: Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }else{
            Log.d(TAG, "delete: nothing deleted");
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update: called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "update: match is " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(ContactContract.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = ContactContract.getContactId(uri);
                selectionCriteria = ContactContract.Columns._ID + " = " + taskId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.update(ContactContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.update(TimingsContract.TABLE_NAME, values, selection, selectionArgs);
//                break;
//            case TIMINGS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long timingsId = TimingsContract.getTaskId(uri);
//                selectionCriteria = TimingsContract.Columns._ID + " = " + timingsId;
//                if ((selection != null) && (selection.length()>0)){
//                    selectionCriteria += " AND (" + selection + ")";
//                }
//                count = db.update(TimingsContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
//                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        if (count > 0){
            //something was updated
            Log.d(TAG, "update: Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }else{
            Log.d(TAG, "update: nothing updated");
        }

        return count;
    }

    public ArrayList<String> Search(){
        Cursor c = mOpenHelper.getReadableDatabase().rawQuery("select * from " + ContactContract.TABLE_NAME, null);
        if (c.getCount()==0){
//            Toast.makeText(getContext(), "nothing was found", Toast.LENGTH_LONG).show();
            return null;
        }
        ArrayList<String> phoneNumbersList = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()){
            buffer.append("Phone number: " + c.getString(2) + "\n");
            phoneNumbersList.add(c.getString(2));
        }
        Log.d(TAG, "Search: Result" + buffer.toString());
        return phoneNumbersList;
    }
}
