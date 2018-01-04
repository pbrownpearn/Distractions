package pbrownpearn.github.com.distractions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pbrownpearn.github.com.distractions.database.WebsiteCursorWrapper;
import pbrownpearn.github.com.distractions.database.WebsiteDbHelper;
import pbrownpearn.github.com.distractions.database.WebsiteDbSchema;

import static pbrownpearn.github.com.distractions.database.WebsiteDbSchema.WebsiteTable.Cols.DATE;
import static pbrownpearn.github.com.distractions.database.WebsiteDbSchema.WebsiteTable.Cols.PRIORITY;
import static pbrownpearn.github.com.distractions.database.WebsiteDbSchema.WebsiteTable.Cols.TITLE;
import static pbrownpearn.github.com.distractions.database.WebsiteDbSchema.WebsiteTable.Cols.URL;
import static pbrownpearn.github.com.distractions.database.WebsiteDbSchema.WebsiteTable.NAME;


public class WebsiteRetrieval {

    private static final String TAG = WebsiteRetrieval.class.getSimpleName();

    private static WebsiteRetrieval sWebsiteRetrieval;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private int masterListSize;

    public static WebsiteRetrieval get(Context context) {
        if (sWebsiteRetrieval == null) {
            sWebsiteRetrieval = new WebsiteRetrieval(context);
        }

        return sWebsiteRetrieval;
    }

    private WebsiteRetrieval(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new WebsiteDbHelper(mContext).getWritableDatabase();

    }

    public void addWebsite(Website website) {
        ContentValues values = getContentValues(website);
        mDatabase.insert(NAME, null, values);
    }

    public int deleteWebsite(String URL) {
        return mDatabase.delete(NAME, WebsiteDbSchema.WebsiteTable.Cols.URL + "=?", new String[]{URL});
    }

    public void saveForLater(String url) {

        Website website = new Website(url);
        String URL = website.getmURL();
        Cursor cursor = mDatabase.query(
                NAME,
                null,
                WebsiteDbSchema.WebsiteTable.Cols.URL + "=?",
                 new String[]{URL},
                null,
                null,
                null
        );
        WebsiteCursorWrapper cursorWrapper = new WebsiteCursorWrapper(cursor);
        cursorWrapper.moveToFirst();
        int priority = cursorWrapper.getWebsitePriority();
        website.setmWebsitePriority(--priority);

        website.setmName(WebsiteUtils.parsePageTitle(URL));

        deleteWebsite(url);

        if (website.getmWebsitePriority() >= Website.LOW_PRIORITY) {
            addWebsite(website);
        } else {
            Toast.makeText(mContext, "'" + website.getmName() + "'" + "\ndismissed too many times. Page is deleted", Toast.LENGTH_LONG).show();
        }

        cursorWrapper.close();
    }

    public List<Website> getmWebsites(int numberToRetrieve) {

        ArrayList<Website> masterList = new ArrayList<>();
        List<Website> returnWebsites = new ArrayList<>();

        WebsiteCursorWrapper cursor = queryWebsites(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                masterList.add(cursor.getWebsite());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }


        Collections.sort(masterList, Website.compByPriority());

        for (int i = 0; i < numberToRetrieve; i++) {
            if (masterList.size() > i) {
                returnWebsites.add(masterList.get(i));
            }
        }

        setBackCatalogueSize(masterList.size() - returnWebsites.size());

        return returnWebsites;
    }

    private static ContentValues getContentValues(Website website) {
        ContentValues values=  new ContentValues();
        values.put(URL, website.getmURL().toString());
        values.put(TITLE, website.getmName().toString());
        values.put(DATE, website.getmDateAdded().toString());
        values.put(PRIORITY, String.valueOf((website.getmWebsitePriority())));

        return values;
    }

    private WebsiteCursorWrapper queryWebsites(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new WebsiteCursorWrapper(cursor);
    }

    public int getNumberOfWebsites() {
        return masterListSize;
    }


    public void setBackCatalogueSize(int differenceInSize) {
        this.masterListSize = differenceInSize;
    }
}
