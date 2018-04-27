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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MinhasOcorrenciasActivity.this, android.R.layout.simple_list_item_1, arrayList);
        mList.setAdapter(arrayAdapter);

    }

    private void attemptGetReports() {

        SharedPreferences preferences = getSharedPreferences("Prefs", MODE_PRIVATE);

        for (int i = 0; i<= preferences.getInt("numReports",0);i++){
            arrayList.add(preferences.getString("userReport" + i,""));
        }

    }



}
