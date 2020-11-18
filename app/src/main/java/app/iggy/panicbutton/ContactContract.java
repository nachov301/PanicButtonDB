package app.iggy.panicbutton;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
//imports the constants or statics that we defined in our AppProvider class
import static app.iggy.panicbutton.AppProvider.CONTENT_AUTHORITY;
import static app.iggy.panicbutton.AppProvider.CONTENT_AUTHORITY_URI;



public class ContactContract {

    static final String TABLE_NAME = "Tasks";

//    Tasks fields
    public static class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String CONTACT_NAME = "Name";
        public static final String CONTACT_NUMBER = "Number";
        public static final String CONTACT_SORTORDER = "SortOrder";

        private Columns(){
//            private constructor to prevent instantiation
    }
}

/**
 * the URI to access the Task Table
 */
public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

static Uri buildContactUri(long taskId){
    return ContentUris.withAppendedId(CONTENT_URI, taskId);
}

static long getContactId(Uri uri){
    return ContentUris.parseId(uri);
}
}
