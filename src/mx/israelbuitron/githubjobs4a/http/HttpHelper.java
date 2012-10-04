package mx.israelbuitron.githubjobs4a.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import mx.israelbuitron.githubjobs4a.R;
import mx.israelbuitron.githubjobs4a.pojos.Job;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

public class HttpHelper {
    private final Context context;
    private final SharedPreferences pref;
    private final HttpClient client;

    public HttpHelper(Context context) {
        this.context = context;
        this.pref = PreferenceManager.getDefaultSharedPreferences(context);
        this.client = new DefaultHttpClient();
    }

    private String executeRequest(String url) throws ClientProtocolException,
                                             IOException {
        HttpGet get = new HttpGet(url);

        // Query HttpRequest
        HttpResponse response = client.execute(get);

        // Extract raw response from HttpResponse
        BufferedReader br = new BufferedReader(new InputStreamReader(response
                .getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = br.readLine()) != null) {
            sb.append(s);
        }

        return sb.toString();
    }

    public Job getJob(String id) throws ClientProtocolException, IOException {
        // Look for URL in preferences
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = pref.getString(
                "url_base",
                context.getString(R.string.url_base_preference_default_value));

        // Query HttpRequest
        String response = executeRequest(url + "/" + id + ".json");

        // Parsing JSON response
        Job job = new Gson().fromJson(response, Job.class);
        return job;
    }

    public Job[] getJobsList() throws ClientProtocolException, IOException {
        // Look for URL in preferences
        String url = pref.getString(
                "url_base",
                context.getString(R.string.url_base_preference_default_value));

        // Query HttpRequest
        String response = executeRequest(url + ".json");

        // Parsing JSON response
        Job[] jobs = new Gson().fromJson(response, Job[].class);
        return jobs;
    }

}
