package pt.novaleaf.www.maisverde;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import static pt.novaleaf.www.maisverde.MapsActivity.markers;


/**
 * Author: Hugo Mochao
 * Atividade responsavel por criar ocorrencias
 */
public class CriarOcorrenciaActivity extends AppCompatActivity {

    private ReportTask mReportTask = null;
    private AutoCompleteTextView mTituloView;
    private AutoCompleteTextView mDescricaoView;
    private Button bCriar;
    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_ocorrencia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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


        //Verificar que atividade antecedeu esta, ou maps ou feed
        //Consoante a atividade proceder corretamente:
        //Se veio do feed, quer dizer que nao e preciso ir ao mapa
        Intent intent = getIntent();
        final boolean estaLocal = intent.getBooleanExtra("estaLocal", false);
        if (estaLocal){

            setLocal();

        } else {
            latitude = intent.getDoubleExtra("lat", -1);
            longitude = intent.getDoubleExtra("lon", -1);

            Log.i("Main Latitude", String.valueOf(latitude));
            Log.i("Main Longitude", String.valueOf(longitude));
        }




        mTituloView = (AutoCompleteTextView) findViewById(R.id.titulo);
        mDescricaoView = (AutoCompleteTextView) findViewById(R.id.descricao);
        bCriar = (Button) findViewById(R.id.bCriar);


        bCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAddReport();
            }
        });


    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    //Pedir permissoes
    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    /**
     * Ir buscar o ultimo lugar conhecido do GPS
     * Verifica as permissoes
     */
    public void setLocal(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            int permissionChecked2 = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (permissionChecked2 != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showExplanation("É necessária esta permissão", "Rationale", Manifest.permission.ACCESS_FINE_LOCATION, 1);
                } else {
                    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1);
                }
            }
        }

        LocationManager locationManager;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location lastPlace = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitude = lastPlace.getLatitude();
        longitude = lastPlace.getLongitude();

    }


    /**
     * Tentativa de adicionar ocorrencia
     */
    private void attemptAddReport() {
        if (mReportTask != null) {
            return;
        }

        mDescricaoView.setError(null);
        mTituloView.setError(null);

        String titulo = mTituloView.getText().toString();
        String descricao = mDescricaoView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(titulo) ) {
            mTituloView.setError("Título fazio");
            focusView = mTituloView;
            cancel = true;
        }

        if (TextUtils.isEmpty(descricao) ) {
            mDescricaoView.setError("Descrição vazia");
            focusView = mDescricaoView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mReportTask = new ReportTask(titulo, descricao);
            mReportTask.execute((Void) null);
        }
    }


    /**
     * Represents a task used to register a report
     *
     */
    public class ReportTask extends AsyncTask<Void, Void, String> {


        private final String mTitulo;
        private final String mDescricao;

        ReportTask(String titulo, String descricao) {

            mTitulo = titulo;
            mDescricao = descricao;
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


                SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("tokenID", "erro");

                JSONObject marker = new JSONObject();
                marker.put("name", mTitulo);
                marker.put("owner", sharedPreferences.getString("username", "erro"));
                marker.put("description", mDescricao);
                JSONObject coordinates = new JSONObject();
                coordinates.put("latitude", latitude);
                coordinates.put("longitude", longitude);

                marker.put("coordinates", coordinates);


                Log.i("objeto", marker.toString());

                URL url = new URL("https://novaleaf-197719.appspot.com/rest/withtoken/mapsupport/addmarker");
                return RequestsREST.doPOST(url, marker, token);
            } catch (Exception e) {
                Log.i("ERRO", e.toString());
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mReportTask = null;

            if (result != null) {
                //JSONObject token = null;
                SharedPreferences preferences = getSharedPreferences("Prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
                int newNumReports = preferences.getInt("numReports",0)+1;
                editor.putString("userReport"+newNumReports, mTitulo);
                editor.putInt("numReports", newNumReports);
                editor.commit();
                LatLng position = new LatLng(latitude, longitude);
                markers.put(position, mTitulo);

                Intent i = new Intent(CriarOcorrenciaActivity.this, FeedActivity.class);
                startActivity(i);

            }
        }

        @Override
        protected void onCancelled() {
            mReportTask = null;

        }
    }



}
