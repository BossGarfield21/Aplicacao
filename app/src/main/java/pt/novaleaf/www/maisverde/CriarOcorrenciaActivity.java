package pt.novaleaf.www.maisverde;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import utils.ByteRequest;

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
    private Button bTipos;
    private ImageButton imageButton;
    private ImageView imageView;
    private ImageView imageView4;
    private double latitude;
    private double longitude;
    private boolean isImage = false;
    byte[] imageBytes;
    String mCurrentPhotoPath;
    ConstraintLayout constraintLayout;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String tipo = "";


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
        if (estaLocal) {

            setLocal();

        } else {
            latitude = intent.getDoubleExtra("lat", -1);
            longitude = intent.getDoubleExtra("lon", -1);

            Log.i("Main Latitude", String.valueOf(latitude));
            Log.i("Main Longitude", String.valueOf(longitude));
        }


        constraintLayout = (ConstraintLayout) findViewById(R.id.constrOco);
        imageView = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        mTituloView = (AutoCompleteTextView) findViewById(R.id.titulo);
        mDescricaoView = (AutoCompleteTextView) findViewById(R.id.descricao);
        bCriar = (Button) findViewById(R.id.bCriar);
        bTipos = (Button) findViewById(R.id.bTipos);


        bTipos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CriarOcorrenciaActivity.this, TiposActivity.class);
                startActivityForResult(i, 2);
            }
        });


        bCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAddReport();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isImage) {
                    //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(intent, 0);

                    dispatchTakePictureIntent();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CriarOcorrenciaActivity.this);
                    builder.setTitle("Outra imagem")
                            .setCancelable(true)
                            .setMessage("Tem a certeza de que quer tirar outra foto?\n" +
                                    "A foto tirada anteriormente será perdida")
                            .setPositiveButton("Tirar outra", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    imageButton.setVisibility(View.GONE);
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, 0);
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.create().show();
                }
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("che", ex.getMessage());
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                imageButton.setVisibility(View.GONE);
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(mCurrentPhotoPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

                Bitmap rotatedBitmap = null;
                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }


                imageView.setImageBitmap(rotatedBitmap);
                isImage = true;
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bao);
                imageBytes = bao.toByteArray();

                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Fotografia captada", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Tirar outra fotografia", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dispatchTakePictureIntent();
                            }
                        });

                snackbar.show();

            } else {
                imageButton.setVisibility(View.VISIBLE);

            }
        } else if (requestCode == 2)
            if (resultCode == RESULT_OK) {

                /**String activity = null;
                if (data != null)
                    activity = data.getStringExtra("activity");
                if (activity != null)
                    tipo = data.getStringExtra("type");

                Log.e("TIPO", tipo);*/
                Intent intent = getIntent();
                String actividade = data.getStringExtra("activity");
                if (actividade != null)
                    tipo = data.getStringExtra("type");
                Log.i("CHEHCU TIPO", tipo);

            }
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
    public void setLocal() {
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

        if (TextUtils.isEmpty(titulo)) {
            mTituloView.setError("Título vazio");
            focusView = mTituloView;
            cancel = true;
        }

        if (TextUtils.isEmpty(descricao)) {
            mDescricaoView.setError("Descrição vazia");
            focusView = mDescricaoView;
            cancel = true;
        }

        if (TextUtils.isEmpty(tipo)) {
            bTipos.setError("Por favor escolha um tipo");
            //Toast.makeText(this, "Por favor escolha um tipo", Toast.LENGTH_SHORT).show();
            focusView = bTipos;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            enviarImagemVolley(imageBytes);
            enviarOcorrenciaVolley(titulo, descricao);
            //receberImagemVolley();
            //mReportTask = new ReportTask(titulo, descricao);
            //mReportTask.execute((Void) null);
        }
    }

    /**
     * private void receberImagemVolley() {
     * String tag_json_obj = "octect_request";
     * String url = "https://novaleaf-197719.appspot.com/gcs/novaleaf-197719.appspot.com/" + "pixa";
     * <p>
     * SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
     * final String token = sharedPreferences.getString("tokenID", "erro");
     * ByteRequest stringRequest = new ByteRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {
     *
     * @Override public void onResponse(byte[] response) {
     * Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
     * imageView4.setImageBitmap(bitmap);
     * }
     * }, new Response.ErrorListener() {
     * @Override public void onErrorResponse(VolleyError error) {
     * VolleyLog.d("erroIMAGEM", "Error: " + error.getMessage());
     * }
     * }){
     * @Override public Map<String, String> getHeaders() throws AuthFailureError {
     * HashMap<String, String> headers = new HashMap<String, String>();
     * headers.put("Authorization", token);
     * return headers;
     * }
     * <p>
     * <p>
     * };
     * AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
     * <p>
     * }
     */
    private void enviarImagemVolley(final byte[] imageBytes) {
        Random r = new Random();
        int number = r.nextInt();
        String id = Integer.toString(number);
        String tag_json_obj = "octect_request";
        String url = "https://novaleaf-197719.appspot.com/gcs/novaleaf-197719.appspot.com/" + id;

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroIMAGEM", "Error: " + error.getMessage());
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return imageBytes;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);

    }


    private void enviarOcorrenciaVolley(final String titulo, final String descricao) {
        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/mapsupport/addmarker";

        JSONObject marker = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");
        try {

            marker.put("name", titulo);
            marker.put("owner", sharedPreferences.getString("username", "erro"));
            marker.put("description", descricao);
            marker.put("type", tipo);
            JSONObject coordinates = new JSONObject();
            coordinates.put("latitude", latitude);
            coordinates.put("longitude", longitude);
            marker.put("coordinates", coordinates);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, marker,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Intent i = new Intent(CriarOcorrenciaActivity.this, FeedActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("erroNOVAOCORRENCIA", "Error: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", token);
                    return headers;
                }

            };
            AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Represents a task used to register a report
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
                int newNumReports = preferences.getInt("numReports", 0) + 1;
                editor.putString("userReport" + newNumReports, mTitulo);
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
