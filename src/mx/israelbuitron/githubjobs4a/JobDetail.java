package mx.israelbuitron.githubjobs4a;

import java.io.IOException;

import mx.israelbuitron.githubjobs4a.http.HttpHelper;
import mx.israelbuitron.githubjobs4a.pojos.Job;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.TextView;

public class JobDetail extends Activity {

    private TextView jobTitleTxt;
    private TextView jobLocationTxt;
    private TextView jobTypeTxt;
    private WebView jobDescriptionWeb;
    private WebView jobHowToApplyWeb;
    private TextView jobCompanyTxt;
    private TextView jobCompanyUrlTxt;
    private TextView jobCompanyLogoTxt;
    private TextView jobUrlTxt;

    private Job job;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        // Load view controls
        jobTitleTxt = (TextView) findViewById(R.id.job_title_txt);
        jobLocationTxt = (TextView) findViewById(R.id.job_location_txt);
        jobTypeTxt = (TextView) findViewById(R.id.job_type_txt);
        jobDescriptionWeb = (WebView) findViewById(R.id.job_description_web);
        jobHowToApplyWeb = (WebView) findViewById(R.id.job_how_to_apply_web);
        jobCompanyTxt = (TextView) findViewById(R.id.job_company_txt);
        jobCompanyUrlTxt = (TextView) findViewById(R.id.job_company_url_txt);
        jobCompanyLogoTxt = (TextView) findViewById(R.id.job_company_logo_txt);
        jobUrlTxt = (TextView) findViewById(R.id.job_url_txt);

        // Retrieve job data
        Bundle bundle = getIntent().getExtras();
        String job_id = bundle.getString("job_id");

        if (job_id == null) {
            // TODO Notify wrong job id
        } else {
            // Retrieve from parameterized from Internet
            LoadJob task = new LoadJob(this);
            task.execute(job_id);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_job_detail, menu);
        return true;
    }

    private void beanToView(Job job) {
        jobTitleTxt.setText(job.getTitle());
        jobLocationTxt.setText(job.getLocation());
        jobTypeTxt.setText(job.getType());
        jobDescriptionWeb.loadData(job.getDescription(), "text/html", null);
        jobHowToApplyWeb.loadData(job.getHow_to_apply(), "text/html", null);
        jobCompanyTxt.setText(job.getCompany());
        jobCompanyLogoTxt.setText(job.getCompany_logo());
        jobCompanyUrlTxt.setText(job.getCompany_url());
        jobUrlTxt.setText(job.getUrl());
    }

    public class LoadJob extends AsyncTask<String, Integer, Job> {
        private final Context context;
        private ProgressDialog dialog;

        public LoadJob(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage(getString(R.string.load_job_dialog_label));
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Job doInBackground(String... params) {
            HttpHelper helper = new HttpHelper(context);
            try {
                Job job = helper.getJob(params[0]);
                return job;
            } catch (ClientProtocolException e) {
                Log.e(GitHubJobsApp.TAG, e.getMessage(), e);
            } catch (IOException e) {
                Log.e(GitHubJobsApp.TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Job result) {
            super.onPostExecute(result);

            // Update job view
            job = result;
            beanToView(job);

            // Hide dialog
            dialog.dismiss();
        }
    }
}
