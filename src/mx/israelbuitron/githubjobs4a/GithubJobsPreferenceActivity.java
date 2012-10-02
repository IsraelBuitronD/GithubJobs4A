package mx.israelbuitron.githubjobs4a;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class GithubJobsPreferenceActivity extends PreferenceActivity {
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
