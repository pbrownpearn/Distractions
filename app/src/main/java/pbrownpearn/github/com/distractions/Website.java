package pbrownpearn.github.com.distractions;

import java.util.Comparator;
import java.util.Date;

public class Website {

    public static final int HIGH_PRIORITY = 3;
    public static final int MEDIUM_PRIORITY = 2;
    public static final int LOW_PRIORITY = 1;

    private String mName;
    private String mURL;
    private int mWebsitePriority;
    private Date mDateAdded;

    public Website() {

    }

    public Website(String url) {
        mURL = url;
        mName = WebsiteUtils.parsePageTitle(url);
        setmWebsitePriority(HIGH_PRIORITY);
        setmDateAdded(new Date());
    }

    public Date getmDateAdded() {
        return mDateAdded;
    }

    public void setmDateAdded(Date mDateAdded) {
        this.mDateAdded = mDateAdded;
    }

    public int getmWebsitePriority() {
        return mWebsitePriority;
    }

    public void setmWebsitePriority(int mWebsitePriority) {
        this.mWebsitePriority = mWebsitePriority;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmURL() {
        return mURL;
    }

    public void setmURL(String mURL) {
        this.mURL = mURL;
    }

    public int getPriorityColor() {
        switch(mWebsitePriority) {
            case LOW_PRIORITY: return R.color.priorityLow;
            case MEDIUM_PRIORITY: return R.color.priorityMedium;
            default: return R.color.priorityHigh;
        }
    }

    @Override
    public String toString() {
        return "Website{" +
                "mName='" + mName + '\'' +
                ", mURL='" + mURL + '\'' +
                ", mPriority= '" + mWebsitePriority + '\'' +
                '}';
    }

    public static Comparator<Website> compByPriority()
    {
        Comparator comparator = new Comparator<Website>() {
            @Override
            public int compare(Website w1, Website w2) {
                return w2.getmWebsitePriority() - w1.getmWebsitePriority();
            }
        };
        return comparator;
    }
}
