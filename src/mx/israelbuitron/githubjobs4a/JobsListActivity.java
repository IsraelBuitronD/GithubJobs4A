package mx.israelbuitron.githubjobs4a;

import java.io.IOException;

import mx.israelbuitron.githubjobs4a.http.HttpHelper;
import mx.israelbuitron.githubjobs4a.pojos.Job;

import org.apache.http.client.ClientProtocolException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class JobsListActivity extends SherlockFragmentActivity implements OnItemClickListener {

    private Job[] mJobsLoaded;
    private ArrayAdapter<Job> mAdapter;
    private ActionBar mActionBar;
    private ListView mJobsListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_list);

        // Load controls
        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mJobsListView = (ListView) findViewById(R.id.jobsList);
        mJobsListView.setOnItemClickListener(this);

        // Call task
        LoadJobsList task = new LoadJobsList(this);
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_jobs_list, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case R.id.settings_menu:
            Intent i = new Intent(this, GithubJobsPreferenceActivity.class);
            startActivity(i);
            break;
        case R.id.about_menu:
            SherlockDialogFragment aboutDialog = new AboutDialog();
            String tag = getString(R.string.about_dialog_fragment_tag);
            aboutDialog.show(getSupportFragmentManager(), tag);
            break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    public static class ViewHolder {
        public TextView jobTitle;
        public TextView jobCompany;
        public TextView jobLocation;
        public TextView jobType;
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
        // Get selected job
        Job job = mJobsLoaded[position];

        // Send parameters to activity
        Intent intent = new Intent(this, JobDetail.class);
        intent.putExtra("job_id", job.getId());

        startActivity(intent);
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
                holder.jobTitle = (TextView)convertView
                        .findViewById(R.id.job_title);
                holder.jobCompany = (TextView)convertView
                        .findViewById(R.id.job_company);
                holder.jobLocation = (TextView)convertView
                        .findViewById(R.id.job_location);
                holder.jobType = (TextView)convertView
                        .findViewById(R.id.job_type);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
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
                holder.jobCompany.setText(job.getCompany());
                holder.jobLocation.setText(job.getLocation());
                holder.jobType.setText(job.getType());
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
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Job[] doInBackground(String... params) {
            HttpHelper helper = new HttpHelper(context);
            try {
                Job[] jobs = helper.getJobsList();
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
            mJobsLoaded = result;
            mAdapter = new JobArrayAdapter(context, R.id.jobsList, mJobsLoaded);
            mJobsListView.setAdapter(mAdapter);

            // Hide dialog
            dialog.dismiss();
        }
    }
}
