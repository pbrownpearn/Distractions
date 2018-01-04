package pbrownpearn.github.com.distractions;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class WebsiteUtils {

    public static String parsePageTitle(String url) {
        String pageTitle = url;
        try {
            pageTitle = new FetchURLTask().execute(url).get();
        } catch (ExecutionException ex) {

        } catch (InterruptedException ex) {

        }

        return pageTitle;
    }

}

    class FetchURLTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Document doc = null;
            try {
                doc = Jsoup.connect(strings[0]).get();
                return doc.title();
            } catch (IOException ex) {

            }

            return null;

        }

    }


