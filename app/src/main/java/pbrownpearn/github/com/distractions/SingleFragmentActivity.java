package pbrownpearn.github.com.distractions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;



public abstract class SingleFragmentActivity extends AppCompatActivity  implements SharedPreferences.OnSharedPreferenceChangeListener  {

    private static final String TAG = SingleFragmentActivity.class.getSimpleName();

    protected abstract Fragment createFragment();

    private Fragment fragment;

    WebsiteRetrieval websiteRetrieval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        websiteRetrieval = WebsiteRetrieval.get(this);

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                targetFragment();
            }
        });

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            String receivedURL = intent.getStringExtra(Intent.EXTRA_TEXT);
            Website receivedWebsite = new Website(receivedURL);
            websiteRetrieval.addWebsite(receivedWebsite);
            fm.beginTransaction().detach(fragment).commit();
            fm.beginTransaction().attach(fragment).commit();
        }

    }

    private void targetFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        NewWebsiteFragment dialog = new NewWebsiteFragment();
        dialog.setTargetFragment(fragment, WebsiteListFragment.ADD_WEBSITE);
        dialog.show(fragmentManager, WebsiteListFragment.DIALOG_NEW_WEBSITE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        String keyForNumber = this.getString(R.string.settings_listed_websites_key);
        String defaultNumber = this.getString(R.string.settings_listed_websites_default);
        WebsiteListFragment.numberOfWebsites = (Integer.valueOf(prefs.getString(keyForNumber, defaultNumber)));

        String keyForDistractions = this.getString(R.string.settings_distractions_key);
        String defaultDistractions = this.getString(R.string.settings_distractions_default);
        WebsiteListFragment.numberOfDistractions = (Integer.valueOf(prefs.getString(keyForDistractions, defaultDistractions)));

    }

    @Override
    protected void onResume() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        String keyForNumber = this.getString(R.string.settings_listed_websites_key);
        String defaultNumber = this.getString(R.string.settings_listed_websites_default);
        WebsiteListFragment.numberOfWebsites = (Integer.valueOf(prefs.getString(keyForNumber, defaultNumber)));

        String keyForDistractions = this.getString(R.string.settings_distractions_key);
        String defaultDistractions = this.getString(R.string.settings_distractions_default);
        WebsiteListFragment.numberOfDistractions = (Integer.valueOf(prefs.getString(keyForDistractions, defaultDistractions)));

        WebsiteListFragment.distractionsToday = prefs.getInt("numberOfDistractions", 0);
        WebsiteListFragment.distractionTimer = prefs.getLong("distractionTimer", 0);

        super.onResume();
    }

    @Override
    protected void onStart() {

        Log.i(TAG, "Start");

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        WebsiteListFragment.distractionsToday = prefs.getInt("numberOfDistractions", 0);
        WebsiteListFragment.distractionTimer = prefs.getLong("distractionTimer", 0);
        super.onStart();
    }

    @Override
    protected void onPause() {

        Log.i(TAG, "Pause");

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("numberOfDistractions", WebsiteListFragment.distractionsToday);
        edit.putLong("distractionTimer", WebsiteListFragment.distractionTimer);
        edit.commit();
        super.onPause();
    }


    @Override
    protected void onStop() {

        Log.i(TAG, "Stop");

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("numberOfDistractions", WebsiteListFragment.distractionsToday);
        edit.putLong("distractionTimer", WebsiteListFragment.distractionTimer);
        edit.commit();


        super.onStop();
    }
}
