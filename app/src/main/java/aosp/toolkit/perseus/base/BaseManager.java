package aosp.toolkit.perseus.base;
/*
 * OsToolkit - Kotlin
 *
 * Date : 31/12/2018
 *
 * By   : 1552980358
 *
 */

import android.app.Application;
import android.support.v4.app.Fragment;

import aosp.toolkit.perseus.BuildConfig;
import aosp.toolkit.perseus.fragments.MainFragment;
import aosp.toolkit.perseus.*;

import com.topjohnwu.superuser.Shell;

public class BaseManager extends Application  {

    public BaseManager() {

    }

    static {
        // Set configurations in a static block
        Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR);
        Shell.Config.verboseLogging(BuildConfig.DEBUG);
        Shell.Config.setTimeout(10);
    }

    public static BaseManager instance;

    private WelcomeActivity welcomeActivity;

    public synchronized static BaseManager getInstance() {
        if (instance == null) {
            instance = new BaseManager();
        }
        return instance;
    }

    public void setWelcomeActivity(WelcomeActivity welcomeActivity) {
        this.welcomeActivity = welcomeActivity;
    }

    public WelcomeActivity getWelcomeActivity() {
        return this.welcomeActivity;
    }

    public void finishActivities() {
        if (welcomeActivity != null) {
            try {
                welcomeActivity.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mainActivity != null) {
            try {
                mainActivity.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private MainActivity mainActivity;
    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    public MainActivity getMainActivity() {
        return this.mainActivity;
    }
}