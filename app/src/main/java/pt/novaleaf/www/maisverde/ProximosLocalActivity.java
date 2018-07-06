package pt.novaleaf.www.maisverde;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import utils.ByteRequest;

public class ProximosLocalActivity extends AppCompatActivity {

    private String cursorEventos = "";
    private boolean isFinishedEventos = false;
    public static List<Evento> proximosLocalEventosList = new ArrayList<>();
    EventoFragment.OnListFragmentInteractionListener mListener;
    public static MyEventoRecyclerViewAdapter adapterProximosLocal;
    RecyclerView recyclerView;
    Location lastKnownLocation;
    LocationManager locationManager;
    LocationListener locationListener;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 30, locationListener);

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximos_local);
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

        mListener = new EventoFragment.OnListFragmentInteractionListener() {
            @Override
            public void onLikeInteraction(Evento item) {

                Log.d("TAS LIKE???", item.isInteresse() + " PUTA");

                addInterestVolley(item);
            }

            @Override
            public void onLocationInteraction(Evento item) {
                Intent intent = new Intent(ProximosLocalActivity.this, MapsActivity.class);
                intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) item.getArea());
                item.setArea(null);
                intent.putExtra("evento", item);
                startActivity(intent);

            }

            @Override
            public void onFavoritoInteraction(Evento item) {

            }

            @Override
            public void onImagemInteraction(Evento item) {

                Intent intent = new Intent(ProximosLocalActivity.this, EventoActivity.class);
                intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) item.getArea());
                item.setArea(null);
                intent.putExtra("evento", item);
                startActivity(intent);


            }
        };

        adapterProximosLocal = new MyEventoRecyclerViewAdapter(proximosLocalEventosList, mListener);
        recyclerView = (RecyclerView) findViewById(R.id.container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterProximosLocal);
        adapterProximosLocal.notifyDataSetChanged();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {


                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 30, locationListener);

            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            for (Evento evento : FeedEventosActivity.eventosList) {
                LatLng latLng = new LatLng(evento.getMeetupPointLatitude(), evento.getMeetupPointLongitude());
                LatLng currrPos = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                float results[] = new float[1];
                Location.distanceBetween(
                        latLng.latitude,
                        latLng.longitude,
                        currrPos.latitude,
                        currrPos.longitude,
                        results);
                if (results[0] < 30000)
                    proximosLocalEventosList.add(evento);


            }

            if (proximosLocalEventosList.isEmpty())
                Toast.makeText(this, "Não há eventos perto de si...", Toast.LENGTH_SHORT).show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


    }

    private void addInterestVolley(final Evento item) {


        String tag_json_obj = "json_obj_req";
        String url;
        int method;
        if (!item.isInteresse()) {
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/events/newinterest?event=" + item.getId();
            method = Request.Method.PUT;
        } else {
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/events/removeinterest?event=" + item.getId();
            method = Request.Method.DELETE;
        }

        final SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");

        // StringRequest stringRequest = new StringRequest()

        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //eventosList.get(FeedEventosActivity.eventosList.indexOf(item)).getInterests().
                //      add(sharedPreferences.getString("username", "erro"));
                //adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProximosLocalActivity.this, "Erro", Toast.LENGTH_SHORT).show();

                item.setInteresse();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }
}
