package pt.novaleaf.www.maisverde;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class CriarOcorrenciaActivity extends AppCompatActivity {

    private ReportTask mReportTask = null;
    private AutoCompleteTextView mTituloView;
    private AutoCompleteTextView mDescricaoView;
    private Button bCriar;
    private CheckBox estaLocal;
    private CheckBox outraLocal;
    private double latitude;
    private double longitude;
    private FusedLocationProviderClient mFusedLocationClient;


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

        mTituloView = (AutoCompleteTextView) findViewById(R.id.titulo);
        mDescricaoView = (AutoCompleteTextView) findViewById(R.id.descricao);
        bCriar = (Button) findViewById(R.id.bCriar);
        estaLocal = (CheckBox) findViewById(R.id.checkBox);
        outraLocal = (CheckBox) findViewById(R.id.checkBox2);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        estaLocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (estaLocal.isChecked()) {
                    outraLocal.setChecked(false);
                }
            }
        });

        outraLocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (outraLocal.isChecked()) {
                    estaLocal.setChecked(false);
                }
            }
        });




        bCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (estaLocal.isChecked()){
                    setLocal();
                    Log.i("latitude", String.valueOf(latitude));
                    Log.i("longitude", String.valueOf(longitude));
                } else if(outraLocal.isChecked()){
                    //ir ao mapa e seleccionar
                }

                attemptAddReport();
            }
        });

        setLocal();

    }

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

    public void setLocal(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            int permissionChecked1 = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionChecked2 = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionChecked1 != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, 1);
                } else {
                    requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, 1);
                }
            }

            if (permissionChecked1 != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_FINE_LOCATION, 1);
                } else {
                    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1);
                }
            }
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                });
    }

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

        if(!estaLocal.isChecked() && !outraLocal.isChecked()){
            Toast.makeText(CriarOcorrenciaActivity.this, "Escolha um modo de localiazação, por favor", Toast.LENGTH_LONG).show();
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

                JSONObject token = new JSONObject();
                SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
                token.put("username", sharedPreferences.getString("username", "erro"));
                token.put("tokenID", sharedPreferences.getString("tokenID", "erro"));
                token.put("creationData", sharedPreferences.getLong("creationData", 0));
                token.put("expirationData", sharedPreferences.getLong("expirationData", 0));

                JSONObject marker = new JSONObject();
                marker.put("name", mTitulo);
                marker.put("owner", sharedPreferences.getString("username", "erro"));
                marker.put("description", mDescricao);
                JSONObject coordinates = new JSONObject();
                coordinates.put("latitude", latitude);
                coordinates.put("longitude", longitude);

                marker.put("coordinates", coordinates);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("marker", marker);
                jsonObject.put("token", token);

                Log.i("objeto", jsonObject.toString());

                URL url = new URL("https://novaleaf-197719.appspot.com/rest/mapsupport/addmarker");
                return RequestsREST.doPOST(url, jsonObject);
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
