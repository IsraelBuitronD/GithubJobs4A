package mx.israelbuitron.githubjobs4a;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class GitHubJobsApp extends Application {
    public static final String TAG = "GithubJobs4A";

    /**
     * Get application version code.
     *
     * @param context
     * @return Application version code.
     */
    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch(NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get application version name.
     * 
     * @param context
     * @return Application version name.
     */
    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch(final NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
