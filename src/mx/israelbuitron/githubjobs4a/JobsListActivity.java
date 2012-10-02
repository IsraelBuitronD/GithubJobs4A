package mx.israelbuitron.githubjobs4a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import mx.israelbuitron.githubjobs4a.pojos.Job;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

public class JobsListActivity extends Activity {

    private Job[] jobsLoaded;
    private ArrayAdapter<Job> adapter;
    private ListView jobsListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_list);

        // Load controls
        jobsListView = (ListView) findViewById(R.id.jobsList);

        // Call task
        LoadJobsList task = new LoadJobsList(this);
        task.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_jobs_list, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings_menu:
            Intent i = new Intent(this, GithubJobsPreferenceActivity.class);
            startActivity(i);
            break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public static class ViewHolder {
        public TextView jobTitle;
    }

    protected class JobArrayAdapter extends ArrayAdapter<Job> {
        private final Job[] jobs;
        private final LayoutInflater inflater;

        public JobArrayAdapter(Context context, int textViewResourceId,
                Job[] items) {
            super(context, textViewResourceId, items);
            this.jobs = items;
            this.inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            // Inflate layout
            if (convertView == null) {
                convertView = inflater.inflate(
                        R.layout.activity_jobs_list_item, parent, false);

                holder = new ViewHolder();
                holder.jobTitle = (TextView) convertView
                        .findViewById(R.id.jobTitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Fill layout
            Job job = jobs[position];
            if (job == null) {
                // Notify null job error
                Log.e(GitHubJobsApp.TAG,
                        "Intent to fill jobs list with null job, position="
                                + position);
            } else {
                holder.jobTitle.setText(job.getTitle());
            }

            return convertView;
        }
    }

    protected class LoadJobsList extends AsyncTask<String, Integer, Job[]> {

        private final Context context;
        private ProgressDialog dialog;

        public LoadJobsList(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage(getString(R.string.load_jobs_dialog_label));
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(4);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Job[] doInBackground(String... params) {
            // Look for URL in preferences
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(context);
            String url = pref.getString("url_base",
                    getString(R.string.url_base_preference_default_value));

            HttpGet get = new HttpGet(url + ".json");
            HttpClient client = new DefaultHttpClient();
            try {
                // Query HttpRequest
                HttpResponse response = client.execute(get);
                publishProgress(1);

                // Extract raw response from HttpResponse
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                String s = null;
                while ((s = br.readLine()) != null) {
                    sb.append(s);
                }
                publishProgress(2);

                // Parsing JSON response
                Job[] jobs = new Gson().fromJson(sb.toString(), Job[].class);
                publishProgress(3);
                return jobs;
            } catch (ClientProtocolException e) {
                Log.e(GitHubJobsApp.TAG, e.getMessage(), e);
            } catch (IOException e) {
                Log.e(GitHubJobsApp.TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Job[] result) {
            super.onPostExecute(result);

            // Update jobs list
            jobsLoaded = result;
            // adapter = new ArrayAdapter<Job>(context,
            // R.layout.activity_jobs_list_item, jobsLoaded);
            adapter = new JobArrayAdapter(context, R.id.jobsList, jobsLoaded);
            jobsListView.setAdapter(adapter);
            publishProgress(4);

            // Hide dialog
            dialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setProgress(values[0]);
        }
    }
}
