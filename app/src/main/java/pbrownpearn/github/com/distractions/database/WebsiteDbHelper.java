package pbrownpearn.github.com.distractions.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static pbrownpearn.github.com.distractions.database.WebsiteDbSchema.WebsiteTable.Cols.DATE;
import static pbrownpearn.github.com.distractions.database.WebsiteDbSchema.WebsiteTable.Cols.PRIORITY;
import static pbrownpearn.github.com.distractions.database.WebsiteDbSchema.WebsiteTable.Cols.TITLE;
import static pbrownpearn.github.com.distractions.database.WebsiteDbSchema.WebsiteTable.Cols.URL;
import static pbrownpearn.github.com.distractions.database.WebsiteDbSchema.WebsiteTable.NAME;


public class WebsiteDbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "websiteDb.db";

    public WebsiteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + NAME + "(" +
                URL + " primary key, " +
                TITLE + ", " +
                DATE + ", " +
                PRIORITY + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
