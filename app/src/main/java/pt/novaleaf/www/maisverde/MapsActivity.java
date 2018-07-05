package pt.novaleaf.www.maisverde;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.android.volley.toolbox.RequestFuture;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.concurrent.ExecutionException;

import utils.ByteRequest;

public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, Serializable {

    private ClusterManager<Ocorrencia> mClusterManager;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    String cursor = "";
    Marker newMarker;
    FloatingActionButton fab;
    Ocorrencia ocorrencia;
    private double topRightLatitude;
    private double topRightLongitude;
    private double bottomLeftLatitude;
    private double bottomLeftLongitude;
    boolean isFinished = false;


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

        ocorrencia = (Ocorrencia) getIntent().getSerializableExtra("ocorrencia");


        //getMarkers();
        fab = (FloatingActionButton) findViewById(R.id.fab);
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
        if (id == R.id.legenda) {
            Intent intent = new Intent(MapsActivity.this, LegendaMapaActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_help) {
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
        } else if (id == R.id.nav_feed) {

            Intent i = new Intent(MapsActivity.this, FeedActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_area_pessoal) {
            Intent i = new Intent(MapsActivity.this, PerfilActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_grupos) {

            Intent i = new Intent(MapsActivity.this, GruposListActivity.class);
            startActivity(i);

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


            if (ocorrencia != null)
                if (ocorrencia.equals(markerItem))
                    markerOptions.draggable(true);
            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_garbage_foreground));

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
        mMap.setOnMarkerClickListener(mClusterManager);


        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Ocorrencia>() {
            @Override
            public void onClusterItemInfoWindowClick(Ocorrencia ocorrencia) {
                Intent intent = new Intent(MapsActivity.this, OcorrenciaActivity.class);
                intent.putExtra("Ocorrencia", (Serializable) ocorrencia);
                startActivity(intent);

            }
        });

        //mMap.setOnCameraIdleListener(mClusterManager);

        mMap.setOnInfoWindowClickListener(mClusterManager);
        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {


        // Add ten cluster items in close proximity, for purposes of this example.
        for (Ocorrencia ocorrencia : FeedActivity.ocorrencias) {
            mClusterManager.addItem(ocorrencia);

        }
    }

    public void novaOcorrencia() {


        AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
        alert.setTitle("Criar ocorrência");
        alert
                .setMessage("Escolha a localização da ocorrência")
                .setCancelable(true)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (newMarker != null)
                            newMarker.remove();
                        LatLng latLng = mMap.getCameraPosition().target;
                        newMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                        newMarker.setDraggable(true);
                        newMarker.setSnippet("Clique aqui quando estiver escolhida");
                        newMarker.setTitle("Pressione e arraste para definir a localização");
                        newMarker.showInfoWindow();

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                if (!marker.equals(newMarker)) {
                                    newMarker.remove();
                                    mMap.setOnInfoWindowClickListener(mClusterManager);
                                }
                                return false;
                            }
                        });

                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                if (marker.equals(newMarker)) {
                                    Intent intent = new Intent(MapsActivity.this, CriarOcorrenciaActivity.class);
                                    intent.putExtra("lat", marker.getPosition().latitude);
                                    intent.putExtra("lon", marker.getPosition().longitude);
                                    startActivity(intent);
                                    newMarker.remove();
                                } else {
                                    newMarker.remove();
                                    mMap.setOnInfoWindowClickListener(mClusterManager);
                                }
                                //finish();
                            }
                        });
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

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    if (ocorrencia != null) {
                        ocorrencia.setLatitude(marker.getPosition().latitude);
                        ocorrencia.setLongitude(marker.getPosition().longitude);
                    }
                }
            });

            setUpClusterer();

            startMap();


        }
    }


    public void startMap() {
        //lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        final LatLng currrPos;

        if (ocorrencia != null) {

            fab.setImageResource(R.drawable.ic_check_white_24dp);
            //fab.setVisibility(View.GONE);
            currrPos = new LatLng(ocorrencia.getLatitude(), ocorrencia.getLongitude());
            Toast.makeText(this, "Arraste o marcador até à posição pretendida", Toast.LENGTH_SHORT).show();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currrPos, 15));

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    atualizarOcorrenciaVolley(ocorrencia);
                }
            });


        } else {
            currrPos = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());


            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currrPos, 10));

            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {

                    final LatLng bottomEsquerda;
                    final LatLng topDireita;

                    bottomEsquerda = mMap.getProjection().getVisibleRegion().nearLeft;
                    topDireita = mMap.getProjection().getVisibleRegion().farRight;

                    bottomLeftLatitude = mMap.getProjection().getVisibleRegion().nearLeft.latitude;
                    bottomLeftLongitude = mMap.getProjection().getVisibleRegion().nearLeft.longitude;
                    topRightLatitude = mMap.getProjection().getVisibleRegion().farRight.latitude;
                    topRightLongitude = mMap.getProjection().getVisibleRegion().farRight.longitude;


                    //Toast.makeText(MapsActivity.this, "Lat bot " + bottomEsquerda.latitude +
                      //      "\nLon bot" + bottomEsquerda.longitude + "\nLat top " + topDireita.latitude +
                        //    "\nLon top " + topDireita.longitude, Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (pedirMarkers(bottomEsquerda, topDireita))
                                for (int i = 0; i < 4 && !isFinished; i++)
                                    volleyGetMarkersBounds(bottomEsquerda, topDireita);

                        }
                    }).start();

                    mClusterManager.cluster();


                }
            });


        }

    }


    private boolean pedirMarkers(LatLng botL, LatLng topR) {

        if (botL.longitude >= bottomLeftLongitude && botL.latitude >= bottomLeftLatitude
                && topR.longitude <= topRightLongitude && topR.latitude <= topRightLatitude)
            return false;
        else {
            if (botL.longitude < bottomLeftLongitude)
                bottomLeftLongitude = botL.longitude;
            if (botL.latitude < bottomLeftLatitude)
                bottomLeftLatitude = botL.latitude;
            if (topR.latitude > topRightLatitude)
                topRightLatitude = topR.latitude;
            if (topR.longitude > topRightLongitude)
                topRightLongitude = topR.longitude;
            return true;
        }
    }

    private void atualizarOcorrenciaVolley(final Ocorrencia ocorrencia) {
        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/mapsupport/update";

        JSONObject marker = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");
        try {

            JSONObject coordinates = new JSONObject();
            marker.put("id", ocorrencia.id);
            marker.put("owner", sharedPreferences.getString("username", "erro"));
            coordinates.put("latitude", ocorrencia.getLatitude());
            coordinates.put("longitude", ocorrencia.getLongitude());
            marker.put("coordinates", coordinates);


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, marker,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mClusterManager.removeItem(ocorrencia);
                            int index = FeedActivity.ocorrencias.indexOf(ocorrencia);
                            FeedActivity.ocorrencias.get(index).setLatitude(ocorrencia.getLatitude());
                            FeedActivity.ocorrencias.get(index).setLongitude(ocorrencia.getLongitude());
                            FeedActivity.adapter.notifyDataSetChanged();
                            mClusterManager.addItem(FeedActivity.ocorrencias.get(index));
                            onBackPressed();


                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MapsActivity.this, "Não foi possível concluir a operação", Toast.LENGTH_SHORT).show();
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


    private void volleyGetMarkersBounds(LatLng bottomLeft, LatLng topRight) {

        final String tag_json_obj = "json_request";
        String url;


        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");
        try {

            JSONObject bounds = new JSONObject();
            JSONObject topR = new JSONObject();
            JSONObject botL = new JSONObject();
            topR.put("latitude", topRight.latitude);
            topR.put("longitude", topRight.longitude);
            botL.put("latitude", bottomLeft.latitude);
            botL.put("longitude", bottomLeft.longitude);
            bounds.put("topRight", topR);
            bounds.put("bottomLeft", botL);

            if (cursor.equals(""))
                url = "https://novaleaf-197719.appspot.com/rest/withtoken/mapsupport/boundedmarkers?cursor=startquery";
            else
                url = "https://novaleaf-197719.appspot.com/rest/withtoken/mapsupport/boundedmarkers?cursor=" + cursor;

            Log.d("ché bate só", url);

            final RequestFuture<JSONObject> future = RequestFuture.newFuture();
            final JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, url, bounds,
                    future, future) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", token);
                    return headers;
                }
            };
            future.setRequest(jsonObjectRequest1);


            jsonObjectRequest1.setTag(tag_json_obj);
            AppController.getInstance().addToRequestQueue(jsonObjectRequest1);
            try {
                final JSONObject response = future.get();
                cursor = response.getString("cursor");
                Log.d("SUA PUTA TAS AI???", response.toString());
                final JSONArray list = response.getJSONArray("list");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!response.isNull("list")) {
                                Log.d("nao é null", list.toString());
                                if (!isFinished)
                                    for (int i = 0; i < list.length(); i++) {
                                        Log.d("bina, empina?", list.toString());

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
                                        double latitude = 0;
                                        double longitude = 0;
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
                                            JSONArray coms = ocorrencia.getJSONArray("comments");


                                            for (int a = 0; a < coms.length(); a++) {
                                                int origem;
                                                JSONObject com = coms.getJSONObject(a);
                                                if (com.getString("author").equals(
                                                        getSharedPreferences("Prefs", MODE_PRIVATE).getString("username", "")))
                                                    origem = 1;
                                                else origem = 2;

                                                String imag = null;
                                                if (com.has("image"))
                                                    imag = com.getString("image");

                                                String comentID = com.getString("id");


                                                comentarios.put(comentID, new Comentario(comentID, com.getString("author"),
                                                        com.getString("message"), imag,
                                                        com.getLong("creation_date"), origem, id, null, null));

                                            }
                                        }
                                        if (ocorrencia.has("coordinates")) {
                                            JSONObject coordinates = ocorrencia.getJSONObject("coordinates");
                                            latitude = coordinates.getDouble("latitude");
                                            longitude = coordinates.getDouble("longitude");
                                        }

                                        if (ocorrencia.has("likers")) {
                                            JSONArray lik = ocorrencia.getJSONArray("likers");
                                            for (int a = 0; a < lik.length(); a++)
                                                likers.add(lik.getString(a));
                                        }


                                        Ocorrencia ocorrencia1 = new Ocorrencia(titulo, risk, "23:12", id,
                                                descricao, owner, likers, status, latitude, longitude, likes, type, image_uri,
                                                comentarios, creationDate, district, hasLiked);
                                        if (ocorrencia1.getImage_uri() != null)
                                            receberImagemVolley(ocorrencia1);
                                        else {
                                            String tipo = ocorrencia1.getType();
                                            if (tipo.equals("bonfire")) {
                                                ocorrencia1.setImageID(R.mipmap.ic_bonfire_foreground);
                                            } else if (tipo.equals("fire")) {
                                                ocorrencia1.setImageID(R.mipmap.ic_fire_foreground);
                                            } else if (tipo.equals("trash")) {
                                                ocorrencia1.setImageID(R.mipmap.ic_garbage_foreground);
                                            } else {
                                                ocorrencia1.setImageID(R.mipmap.ic_grass_foreground);
                                            }
                                        }


                                        if (!FeedActivity.ocorrencias.contains(ocorrencia1)) {
                                            FeedActivity.ocorrencias.add(ocorrencia1);
                                            mClusterManager.addItem(ocorrencia1);
                                        }
                                        Log.d("ID", id);
                                        Log.d("titulo", titulo);
                                        Log.d("desc", descricao);
                                        FeedActivity.adapter.notifyDataSetChanged();

                                    }
                                isFinished = response.getBoolean("isFinished");
                                Log.d("ACABOU???", String.valueOf(isFinished));
                            } else {
                                isFinished = true;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void receberImagemVolley(final Ocorrencia item) {
        String tag_json_obj = "octect_request";
        String url = item.getImage_uri();


        final String token = getSharedPreferences("Prefs", MODE_PRIVATE).getString("tokenID", "erro");
        ByteRequest stringRequest = new ByteRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {

            @Override
            public void onResponse(byte[] response) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
                item.setBitmap(response);
                FeedActivity.adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroIMAGEMocorrencia", "Error: " + error.getMessage());
                String tipo = item.getType();
                if (tipo.equals("bonfire")) {
                    item.setImageID(R.mipmap.ic_bonfire_foreground);
                } else if (tipo.equals("fire")) {
                    item.setImageID(R.mipmap.ic_fire_foreground);
                } else if (tipo.equals("trash")) {
                    item.setImageID(R.mipmap.ic_garbage_foreground);
                } else {
                    item.setImageID(R.mipmap.ic_grass_foreground);
                }
                FeedActivity.adapter.notifyDataSetChanged();
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
