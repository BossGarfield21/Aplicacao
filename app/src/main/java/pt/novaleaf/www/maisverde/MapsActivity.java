package pt.novaleaf.www.maisverde;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static pt.novaleaf.www.maisverde.OcorrenciaFragment.listOcorrencias;
import static pt.novaleaf.www.maisverde.OcorrenciaFragment.myOcorrenciaRecyclerViewAdapter;

public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, Serializable {

    private ClusterManager<Ocorrencia> mClusterManager;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    getMarkersTask mMarkersTask = null;
    public static Map<LatLng, String> markers = new HashMap<>();
    String cursorMarkers = "";
    boolean isFinishedMarkers = false;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 30, locationListener);

                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                //centrar o mapa na ultima localizacao
                centerMapOnLocation(lastKnownLocation);

                //lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


                // Add a marker in Sydney and move the camera
                LatLng currrPos = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(currrPos).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currrPos, 15));
                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(final LatLng latLng) {

                        //Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());


                        AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
                        alert.setTitle("Criar report");
                        alert
                                .setMessage("Quer fazer um report nesta localização?")
                                .setCancelable(false)
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(MapsActivity.this, CriarOcorrenciaActivity.class);
                                        intent.putExtra("lat", latLng.latitude);
                                        intent.putExtra("lon", latLng.longitude);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();

                /*
                TODO:
                perguntar se quer criar a ocorrencia nesse sitio, se sim:
                ir para o criar ocorrencia ativity, levando com ele as coordenadas
                quando se cria a ocorrencia vai-se para o maps ativity
                levando o titulo e a descricao, com que se vai criar o marker
                (possivelmente atualizando logo na criar ocorrencia)
                mMap.addMarker(new MarkerOptions().position(latLng).title(morada));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                */

                    }
                });

                mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        LatLng botLeft = mMap.getProjection().getVisibleRegion().nearLeft;
                        LatLng topRight = mMap.getProjection().getVisibleRegion().farRight;
                        updateMarkers(botLeft, topRight);
                    }
                });
            }
        }
    }


    public void centerMapOnLocation(Location location) {

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            // First incarnation of this activity.
            mapFragment.setRetainInstance(true);
            mapFragment.getMapAsync(this);
        } else {
            // Reincarnated activity. The obtained map is the same map instance in the previous
            // activity life cycle. There is no need to reinitialize it.
            mapFragment.getMapAsync(this);
        }


        //getMarkers();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                novaOcorrencia();
            }
        });
        Intent i = getIntent();
        boolean toast = i.getBooleanExtra("toast", false);

        if (toast)
            Toast.makeText(this, "Pressionar no local pretendido", Toast.LENGTH_LONG).show();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //      .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            setResult(RESULT_CANCELED);
        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            return true;
        } else if (id == R.id.action_logout) {
            //TODO: revogar token
            final AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
            alert.setTitle("Terminar sessão");
            alert
                    .setMessage("Deseja terminar sessão?")
                    .setCancelable(true)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
                            editor.clear();
                            editor.commit();
                            Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        } else if (id == R.id.action_acerca) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://anovaleaf.ddns.net"));
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_eventos) {
            Intent i = new Intent(MapsActivity.this, FeedEventosActivity.class);
            startActivityForResult(i, 0);
            finish();
        } else if (id == R.id.nav_feed) {

            Intent i = new Intent(MapsActivity.this, FeedActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_area_pessoal) {
            Intent i = new Intent(MapsActivity.this, AlterarDadosActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_grupos) {

            Intent i = new Intent(MapsActivity.this, GruposListActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class CustomMapClusterRenderer<T extends ClusterItem> extends DefaultClusterRenderer<T> {
        CustomMapClusterRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(T item,
                                                   MarkerOptions markerOptions) {
            Ocorrencia markerItem = (Ocorrencia) item;
            if (markerItem.getStatus() == 1)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            else if (markerItem.getStatus() == 2)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            else if (markerItem.getStatus() == 3)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        }
    }

    private void setUpClusterer() {
        // Position the map.
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<Ocorrencia>(this, mMap);
        mClusterManager.setRenderer(new CustomMapClusterRenderer<Ocorrencia>(this, mMap, mClusterManager));

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);


        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Ocorrencia>() {
            @Override
            public void onClusterItemInfoWindowClick(Ocorrencia ocorrencia) {
                Intent intent = new Intent(MapsActivity.this, OcorrenciaActivity.class);
                intent.putExtra("Ocorrencia", (Serializable) ocorrencia);
                startActivity(intent);
            }
        });

        mMap.setOnInfoWindowClickListener(mClusterManager);
        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {


        // Add ten cluster items in close proximity, for purposes of this example.
        for (Ocorrencia ocorrencia : OcorrenciaFragment.listOcorrencias) {
            mClusterManager.addItem(ocorrencia);

        }
    }

    public void novaOcorrencia() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
        alert.setTitle("Criar report");
        alert
                .setMessage("O local do report é a sua localização atual?")
                .setCancelable(true)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MapsActivity.this, CriarOcorrenciaActivity.class);
                        intent.putExtra("estaLocal", true);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Escolher no mapa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Toast.makeText(MapsActivity.this, "Pressionar no local pretendido", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (mMap == null) {
            mMap = googleMap;

            mMap.getUiSettings().setMapToolbarEnabled(false);

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


            if (Build.VERSION.SDK_INT < 23) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 30, locationListener);

                else {
                    Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(myIntent);
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                }
            } else {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 30, locationListener);

                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    centerMapOnLocation(lastKnownLocation);

                } else {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                }


            }

            startMap();

            setUpClusterer();

        }
    }

    //TODO: ver que marcadores estao nesta area
    public void getMarkers() {


        mMarkersTask = new getMarkersTask();
        mMarkersTask.execute((Void) null);

    }

    public void updateMarkers(LatLng botLeft, LatLng topRight) {

        List<LatLng> list = new ArrayList<>();
        for (LatLng latLng : markers.keySet()) {
            if (latLng.latitude >= botLeft.latitude && latLng.longitude >= botLeft.longitude
                    && latLng.latitude <= topRight.latitude && latLng.longitude <= topRight.longitude) {
                mMap.addMarker(new MarkerOptions().position(latLng).title(markers.get(latLng)));
                list.add(latLng);
            }
        }
        markers.keySet().removeAll(list);

    }

    private void volleyGetMarkers() {

        String tag_json_obj = "json_request";
        String url;
        if (cursorMarkers.equals(""))
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/social/feed/?cursor=startquery";
        else
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/social/feed/?cursor=" + cursorMarkers;

        Log.d("ché bate só", url);

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        JSONObject reports = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, reports,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("nabo", cursorMarkers + "pixa");
                            cursorMarkers = response.getString("cursor");
                            Log.d("nabo", cursorMarkers);


                            JSONArray list = response.getJSONArray("list");
                            if (!isFinishedMarkers) {
                                isFinishedMarkers = response.getBoolean("isFinished");
                                Log.d("ACABOU???", String.valueOf(isFinishedMarkers));
                                for (int i = 0; i < list.length(); i++) {

                                    String id = null;
                                    String titulo = null;
                                    String descricao = null;
                                    String owner = null;
                                    String type = null;
                                    boolean hasLiked = false;
                                    String image_uri = null;
                                    List<String> likers = new ArrayList<>();
                                    long creationDate = 0;
                                    String district = null;
                                    double risk = 0;
                                    long likes = 0;
                                    long status = 0;
                                    long latitude = 0;
                                    long longitude = 0;
                                    Map<String, Comentario> comentarios = new HashMap<>();

                                    JSONObject ocorrencia = list.getJSONObject(i);
                                    if (ocorrencia.has("id"))
                                        id = ocorrencia.getString("id");
                                    if (ocorrencia.has("name"))
                                        titulo = ocorrencia.getString("name");
                                    if (ocorrencia.has("description"))
                                        descricao = ocorrencia.getString("description");
                                    if (ocorrencia.has("owner"))
                                        owner = ocorrencia.getString("owner");
                                    if (ocorrencia.has("risk"))
                                        risk = ocorrencia.getInt("risk");
                                    if (ocorrencia.has("likes"))
                                        likes = ocorrencia.getInt("likes");
                                    if (ocorrencia.has("status"))
                                        status = ocorrencia.getLong("status");
                                    if (ocorrencia.has("type"))
                                        type = ocorrencia.getString("type");
                                    JSONObject image = null;
                                    if (ocorrencia.has("image_uri")) {
                                        image = ocorrencia.getJSONObject("image_uri");
                                        if (image.has("value"))
                                            image_uri = image.getString("value");
                                    }

                                    if (ocorrencia.has("hasLike"))
                                        hasLiked = ocorrencia.getBoolean("hasLike");
                                    if (ocorrencia.has("creationDate"))
                                        creationDate = ocorrencia.getLong("creationDate");

                                    Log.d("HASLIKE???", hasLiked + "FDPDPDPD");
                                    if (ocorrencia.has("comments")) {
                                        JSONObject coms = ocorrencia.getJSONObject("comments");

                                        Iterator<String> comentario = coms.keys();
                                        while (comentario.hasNext()) {
                                            String comentID = comentario.next();
                                            int origem = 1;
                                            JSONObject com = coms.getJSONObject(comentID);
                                            if (com.getString("author").equals(
                                                    getSharedPreferences("Prefs", MODE_PRIVATE).getString("username", "")))
                                                origem = 2;
                                            else origem = 1;
                                            comentarios.put(comentID, new Comentario(comentID, com.getString("author"),
                                                    com.getString("message"), com.getString("image"),
                                                    com.getLong("creationDate"), origem));

                                        }
                                    }
                                    if (ocorrencia.has("coordinates")) {
                                        JSONObject coordinates = ocorrencia.getJSONObject("coordinates");
                                        latitude = coordinates.getLong("latitude");
                                        longitude = coordinates.getLong("longitude");
                                    }

                                    if (ocorrencia.has("likers")) {
                                        JSONArray lik = ocorrencia.getJSONArray("likers");
                                        for (int a = 0; a < lik.length(); a++)
                                            likers.add(lik.getString(a));
                                    }


                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroLOGIN", "Error: " + error.getMessage());
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        AppController.getInstance().

                addToRequestQueue(jsonObjectRequest);


    }


    public class getMarkersTask extends AsyncTask<Void, Void, String> {


        /**
         * private final Double mLatBotLeft;
         * private final Double mLonBotLeft;
         * private final Double mLatTopRight;
         * private final Double mLonTopRight;
         */

        getMarkersTask() {
            /**
             mLonBotLeft = botLeft.longitude;
             mLatBotLeft = botLeft.latitude;
             mLonTopRight = topRight.longitude;
             mLatTopRight = topRight.latitude;*/
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

                //JSONObject botLeft = new JSONObject();
                //JSONObject topRight = new JSONObject();
                //JSONObject jsonObject = new JSONObject();
                SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);

                String token = sharedPreferences.getString("tokenID", "erro");
                /*
                botLeft.put("latitude", mLatBotLeft);
                botLeft.put("longitude", mLonBotLeft);
                topRight.put("latitude", mLatTopRight);
                topRight.put("longitude", mLonTopRight);
                jsonObject.put("bottomLeft", botLeft);
                jsonObject.put("topRight", topRight);
                */
                URL url = new URL("https://novaleaf-197719.appspot.com/rest/withtoken/mapsupport/mymarkers");
                return RequestsREST.doGET(url, token);
            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mMarkersTask = null;

            if (result != null) {
                JSONArray token = null;
                try {
                    // We parse the result
                    Log.i("TOKENMARKERS", result);

                    token = new JSONArray(result);
                    Log.i("TOKENMARKERS", token.toString());
                    // TODO: store the token in the SharedPreferences

                    SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();

                    for (int i = 0; i < token.length(); i++) {
                        JSONObject marker = token.getJSONObject(i);
                        Double lat = marker.getJSONObject("coordinates").getDouble("latitude");
                        Double lon = marker.getJSONObject("coordinates").getDouble("longitude");

                        String titulo = marker.getString("name");
                        String descricao = marker.getString("description");

                        //MyItem offsetItem = new MyItem(lat, lon, titulo, descricao);
                        //mClusterManager.addItem(offsetItem);

                        //LatLng position = new LatLng(lat, lon);
                        //if (!markers.keySet().contains(position)) {
                        //  markers.put(position, titulo);
                        // }

                    }


                } catch (JSONException e) {
                    // WRONG DATA SENT BY THE SERVER

                    Log.e("Authentication", e.toString());
                }
            }
        }

        @Override
        protected void onCancelled() {
            mMarkersTask = null;

        }
    }


    public void startMap() {
        //lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        // Add a marker in Sydney and move the camera
        LatLng currrPos = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(currrPos).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currrPos, 15));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {

                AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
                alert.setTitle("Criar report");
                alert
                        .setMessage("Quer fazer um report nesta localização?")
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MapsActivity.this, CriarOcorrenciaActivity.class);
                                intent.putExtra("lat", latLng.latitude);
                                intent.putExtra("lon", latLng.longitude);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();

            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng botLeft = mMap.getProjection().getVisibleRegion().nearLeft;
                LatLng topRight = mMap.getProjection().getVisibleRegion().farRight;
                updateMarkers(botLeft, topRight);
                //setUpClusterer();
            }
        });
    }


}
