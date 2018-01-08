package pbrownpearn.github.com.distractions;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WebsiteListFragment extends Fragment {

    private static final String TAG = WebsiteListFragment.class.getSimpleName();

    public static final String DIALOG_NEW_WEBSITE = "DialogWebsite";

    public static final int ADD_WEBSITE = 0;


    public static int numberOfWebsites = 5;
    public static int numberOfDistractions = 3;

    public static int distractionsToday;
    public static long distractionTimer;

    public static final long DAY_IN_MILLISECONDS = (long) Math.pow(8.64, 7);
    public static final long MINUTE_IN_MILLISCONDS = 60000;

    private boolean disableReading = false;

    private RecyclerView mWebsiteRecyclerView;
    private WebsiteAdapter mAdapter;
    private WebsiteRetrieval websiteRetrieval;
    private List<Website> mWebsites;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_website_list, container, false);

        mWebsiteRecyclerView = (RecyclerView) view.findViewById(R.id.website_recycler_view);
        mWebsiteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        websiteRetrieval = WebsiteRetrieval.get(getContext());

        setRecyclerBackgroundColor();
        updateUI();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                String URL = null;
                int id = 0;

                try {
                    id = (int) viewHolder.getAdapterPosition();
                    Website currentUrl = mWebsites.get(id);
                    URL = currentUrl.getmURL();
                } catch (NullPointerException ex) {
                }

                if (swipeDir == ItemTouchHelper.LEFT) {
                    websiteRetrieval.deleteWebsite(URL);
                } else if (swipeDir == ItemTouchHelper.RIGHT) {
                    websiteRetrieval.saveForLater(URL);
                }

                updateUI();
            }
        }).attachToRecyclerView(mWebsiteRecyclerView);


        return view;
    }

    private void setRecyclerBackgroundColor() {

        mWebsiteRecyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), determineColorFromBackCatalogue()));

    }

    private int determineColorFromBackCatalogue() {

        int backCatalogueSize = websiteRetrieval.getNumberOfWebsites();

        switch(backCatalogueSize) {
            case 0: return R.color.backCatalogueEmpty;
            case 1: return R.color.backCatalogueOneItem;
            case 2: return R.color.backCatalogueTwoItems;
            case 3: return R.color.backCatalogueThreeItems;
            case 4: return R.color.backCatalogueFourItems;
            default: return R.color.backCatalogueManyItems;
        }

    }

    private class WebsiteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Website mWebsite;

        private TextView mTitleTextView;
        private TextView mURLTextView;

        public WebsiteHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_website, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.website_title);
            mURLTextView = (TextView) itemView.findViewById(R.id.website_url);
        }

        public void bind(Website website) {
            mWebsite = website;
            mTitleTextView.setText(mWebsite.getmName());
            mURLTextView.setText(mWebsite.getmURL());
            itemView.setBackgroundColor(ContextCompat.getColor(getContext(), website.getPriorityColor()));

        }

        @Override
        public void onClick(View view) {
            if (allowedToRead()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebsite.getmURL()));
                startActivity(browserIntent);
                websiteRetrieval.deleteWebsite(mWebsite.getmURL());
                distractionsToday++;
                Log.i(TAG, "DistractionsToday: " + distractionsToday);
            } else {
                Toast.makeText(getContext(), "You have reached your article limit for the day. Please come back tomorrow.", Toast.LENGTH_LONG).show();
                if (!disableReading) {
                    distractionTimer = System.currentTimeMillis() + MINUTE_IN_MILLISCONDS;
                }
                disableReading = true;
            }
        }
    }

    private class WebsiteAdapter extends RecyclerView.Adapter<WebsiteHolder> {

        private List<Website> mWebsites;

        private WebsiteAdapter(List<Website> websites) {
            mWebsites = websites;
        }

        @Override
        public WebsiteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new WebsiteHolder(layoutInflater, parent);

        }

        @Override
        public void onBindViewHolder(WebsiteHolder holder, int position) {
            Website website = mWebsites.get(position);
            holder.bind(website);
        }

        @Override
        public int getItemCount() {
            return mWebsites.size();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_website_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(getContext(), SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(getActivity(), "Invalid URL", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == ADD_WEBSITE) {
            Website website = new Website();
            String url = (String) data.getSerializableExtra(NewWebsiteFragment.EXTRA_URL);
            website.setmURL(url);

            website.setmName(WebsiteUtils.parsePageTitle(url));

            website.setmDateAdded(new Date());
            website.setmWebsitePriority(Website.HIGH_PRIORITY);
            websiteRetrieval.addWebsite(website);
            updateUI();
        }
    }

    @Override
    public void onResume() {
        updateUI();
        setRecyclerBackgroundColor();
        super.onResume();
    }

    @Override
    public void onStart() {
        if (WebsiteListFragment.distractionTimer < System.currentTimeMillis()) {
            WebsiteListFragment.distractionTimer = 0;
            disableReading = false;
        }
        super.onStart();
    }

    private void updateUI() {
        mWebsites = websiteRetrieval.getmWebsites(numberOfWebsites);
        mAdapter = new WebsiteAdapter(mWebsites);
        mWebsiteRecyclerView.setAdapter(mAdapter);
        setRecyclerBackgroundColor();
    }

    private boolean allowedToRead() {
        long currentTime = System.currentTimeMillis();

        Log.i(TAG, "Current time: " + currentTime);
        Log.i(TAG, "Distraction timer: " + distractionTimer);
        Log.i(TAG, "Difference in hours: " + ((distractionTimer * 2.77778e-7) - (currentTime * 2.77778e-7)));


        if (disableReading && currentTime > distractionTimer) {
                distractionsToday = 0;
                disableReading = false;

        }

        if (distractionsToday >= numberOfDistractions) {
            return false;
        }
        return true;
    }



}
