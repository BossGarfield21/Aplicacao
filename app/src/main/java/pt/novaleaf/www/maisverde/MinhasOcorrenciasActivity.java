package pt.novaleaf.www.maisverde;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MinhasOcorrenciasActivity extends AppCompatActivity {

    private ListView mList;
    private UserReportsTask mReportsTask = null;
    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_ocorrencias);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        arrayList = new ArrayList<>();


        attemptGetReports();

        mList = (ListView) findViewById(R.id.myList);

    }

    private void attemptGetReports() {
        if (mReportsTask != null) {
            return;
        }

            mReportsTask = new UserReportsTask();
            mReportsTask.execute((Void) null);
    }



    /**
     * Represents a task used to get user info
     * the user.
     */
    public class UserReportsTask extends AsyncTask<Void, Void, String> {


        UserReportsTask() {

        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                // If no connectivity, cancel task and update Callback with null data.
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                //TODO: create JSON object with credentials and call doPost

                JSONObject jsonObject = new JSONObject();
                SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
                jsonObject.put("username", sharedPreferences.getString("username", "erro"));
                jsonObject.put("tokenID", sharedPreferences.getString("tokenID", "erro"));
                jsonObject.put("creationData", sharedPreferences.getLong("creationData", 0));
                jsonObject.put("expirationData", sharedPreferences.getLong("expirationData", 0));

                URL url = new URL("https://novaleaf-197719.appspot.com/rest/mapsupport/listmymarkers");
                return RequestsREST.doPOST(url, jsonObject);
            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mReportsTask = null;

            if (result != null) {
                JSONArray token = null;
                try {
                    // We parse the result
                    Log.i("TOKENOCORRENCIAS", result);

                    token = new JSONArray(result);
                    Log.i("TOKENOCORRENCIAS", token.toString());
                    // TODO: store the token in the SharedPreferences


                    for(int i =0; i < token.length(); i++){
                        arrayList.add(token.getJSONObject(i).getString("name"));
                        Log.i("TOKENOCORRENCIAS", "CHE " + token.getJSONObject(i).getString("name"));

                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MinhasOcorrenciasActivity.this, android.R.layout.simple_list_item_1, arrayList);
                    mList.setAdapter(arrayAdapter);

                } catch (JSONException e) {
                    // WRONG DATA SENT BY THE SERVER

                    Log.e("Authentication", e.toString());
                }
            }
        }

        @Override
        protected void onCancelled() {
            mReportsTask = null;

        }
    }

}
