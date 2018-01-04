package pbrownpearn.github.com.distractions.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;

import pbrownpearn.github.com.distractions.Website;

import static pbrownpearn.github.com.distractions.database.WebsiteDbSchema.WebsiteTable.Cols.TITLE;
import static pbrownpearn.github.com.distractions.database.WebsiteDbSchema.WebsiteTable.Cols.URL;


public class WebsiteCursorWrapper extends CursorWrapper {

    public WebsiteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Website getWebsite() {
        String urlString = getString(getColumnIndex(URL));
        String title = getString(getColumnIndex(TITLE));
        long date = getLong(getColumnIndex(WebsiteDbSchema.WebsiteTable.Cols.DATE));
        int priority = getInt(getColumnIndex(WebsiteDbSchema.WebsiteTable.Cols.PRIORITY));

        Website website = new Website();
        website.setmURL(urlString);
        website.setmName(title);
        website.setmDateAdded(new Date(date));
        website.setmWebsitePriority(priority);

        return website;
    }

    public int getWebsitePriority() {
        return getInt(getColumnIndex(WebsiteDbSchema.WebsiteTable.Cols.PRIORITY));
    }
}
