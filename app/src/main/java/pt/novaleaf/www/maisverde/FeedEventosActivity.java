package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import utils.ByteRequest;


public class FeedEventosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {

    NavigationView navigationView;
    private String cursorEventos = "";
    private boolean isFinishedEventos = false;
    public static List<Evento> eventosList = new ArrayList<>();
    EventoFragment.OnListFragmentInteractionListener mListener;
    public static MyEventoRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    private boolean canScroll = false;
    private LinearLayout mLinearCrono;
    private LinearLayout mLinearLocal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_eventos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);


        mListener = new EventoFragment.OnListFragmentInteractionListener() {
            @Override
            public void onLikeInteraction(Evento item) {

                Log.d("TAS LIKE???", item.isInteresse() + " PUTA");

                addInterestVolley(item);
            }

            @Override
            public void onLocationInteraction(Evento item) {
                Intent intent = new Intent(FeedEventosActivity.this, MapsActivity.class);
                intent.putExtra("longitude", item.getMeetupPointLongitude());
                intent.putExtra("latitude", item.getMeetupPointLatitude());
                startActivity(intent);

            }

            @Override
            public void onFavoritoInteraction(Evento item) {

            }

            @Override
            public void onImagemInteraction(Evento item) {

                Intent intent = new Intent(FeedEventosActivity.this, EventoActivity.class);
                intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) item.getArea());
                item.setArea(null);
                intent.putExtra("evento", item);
                startActivity(intent);

            }
        };

        adapter = new MyEventoRecyclerViewAdapter(eventosList, mListener);
        recyclerView = (RecyclerView) findViewById(R.id.container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mLinearCrono = (LinearLayout) findViewById(R.id.cronoproximos);
        mLinearLocal = (LinearLayout) findViewById(R.id.proximosLocal);

        mLinearCrono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedEventosActivity.this, ProximosEventosActivity.class);
                startActivity(intent);
            }
        });

        mLinearLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedEventosActivity.this, ProximosLocalActivity.class);
                startActivity(intent);
            }
        });


        new Thread(new Runnable() {
            @Override

            public void run() {
                for (int i = 0; i < 4 && !isFinishedEventos; i++)
                    volleyGetEventos();
            }


        }).start();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_CANCELED) {
                // user pressed back from 2nd activity to go to 1st activity. code here
                navigationView.getMenu().getItem(2).setChecked(true);
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_feed) {
            Intent i = new Intent(FeedEventosActivity.this, FeedActivity.class);
            startActivityForResult(i, 0);

        } else if (id == R.id.nav_mapa) {

            Intent i = new Intent(FeedEventosActivity.this, MapsActivity.class);
            startActivityForResult(i, 0);
            //startActivity(i);
            //finish();

        } else if (id == R.id.nav_area_pessoal) {
            Intent i = new Intent(FeedEventosActivity.this, PerfilActivity.class);
            startActivityForResult(i, 0);
            //startActivity(i);
            //finish();

        } else if (id == R.id.nav_grupos) {

            Intent i = new Intent(FeedEventosActivity.this, GruposListActivity.class);
            startActivityForResult(i, 0);
            //startActivity(i);
            //finish();

        } else if (id == R.id.nav_acerca) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://anovaleaf.ddns.net"));
            startActivity(i);
        } else if (id == R.id.nav_help) {
            return true;
        } else if (id == R.id.nav_end) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(FeedEventosActivity.this);
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
                            Intent intent = new Intent(FeedEventosActivity.this, LoginActivity.class);
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void volleyGetEventos() {

        String tag_json_obj = "json_request";
        String url;
        if (cursorEventos.equals(""))
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/events/?cursor=startquery";
        else
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/events/?cursor=" + cursorEventos;

        Log.d("ché bate só", url);

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final JSONObject eventos = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");

        final RequestFuture<JSONObject> future = RequestFuture.newFuture();
        final JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
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
            cursorEventos = response.getString("cursor");
            Log.d("SUA PUTA TAS AI???", response.toString());
            final JSONArray list = response.getJSONArray("list");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        if (!response.isNull("list")) {
                            if (!isFinishedEventos) {
                                isFinishedEventos = response.getBoolean("isFinished");
                                Log.d("ACABOU???", String.valueOf(isFinishedEventos));

                                for (int i = 0; i < list.length(); i++) {

                                    String name = null;
                                    String creator = null;
                                    long creationDate = 0;
                                    long meetupDate = 0;
                                    long endDate = 0;
                                    List<String> interests = new ArrayList<>();
                                    List<String> confirmations = new ArrayList<>();
                                    List<String> admin = new ArrayList<>();
                                    List<LatLng> area = new ArrayList<>();
                                    String image_uri = null;
                                    String id = null;
                                    String location = null;
                                    String alert = null;
                                    String description = null;
                                    String weather = null;
                                    double longitudeMeetUp = 0;
                                    double latitudeMeetUp = 0;
                                    double latitudeCenter = 0;
                                    double longitudeCenter = 0;
                                    double radius = 0;
                                    boolean hasInterest = false;
                                    boolean hasConfirmation = false;

                                    JSONObject evento = list.getJSONObject(i);
                                    if (evento.has("id"))
                                        id = evento.getString("id");
                                    if (evento.has("name"))
                                        name = evento.getString("name");
                                    if (evento.has("creator"))
                                        creator = evento.getString("creator");
                                    if (evento.has("description"))
                                        description = evento.getString("description");
                                    if (evento.has("creator"))
                                        creator = evento.getString("creator");
                                    if (evento.has("location"))
                                        location = evento.getString("location");
                                    if (evento.has("alert"))
                                        alert = evento.getString("alert");
                                    if (evento.has("creationDate"))
                                        creationDate = evento.getLong("creationDate");
                                    if (evento.has("meetupDate"))
                                        meetupDate = evento.getLong("meetupDate");
                                    if (evento.has("radius"))
                                        radius = evento.getLong("radius");
                                    if (evento.has("endDate"))
                                        endDate = evento.getLong("endDate");
                                    if (evento.has("image_uri"))
                                        image_uri = evento.getString("image_uri");
                                    if (evento.has("weather"))
                                        weather = evento.getString("weather");
                                    if (evento.has("hasInterest"))
                                        hasInterest = evento.getBoolean("hasInterest");

                                    if (evento.has("hasConfirmation"))
                                        hasConfirmation = evento.getBoolean("hasConfirmation");


                                    if (evento.has("interests")) {
                                        JSONArray interest = evento.getJSONArray("interests");
                                        for (int a = 0; a < interest.length(); a++)
                                            interests.add(interest.getString(a));
                                    }

                                    if (evento.has("confirmations")) {
                                        JSONArray confirmation = evento.getJSONArray("confirmations");
                                        for (int a = 0; a < confirmation.length(); a++)
                                            confirmations.add(confirmation.getString(a));
                                    }

                                    if (evento.has("admin")) {
                                        JSONArray admins = evento.getJSONArray("admin");
                                        for (int a = 0; a < admins.length(); a++)
                                            admin.add(admins.getString(a));
                                    }

                                    if (evento.has("meetupPoint")) {
                                        JSONObject coordinates = evento.getJSONObject("meetupPoint");
                                        latitudeMeetUp = coordinates.getDouble("latitude");
                                        longitudeMeetUp = coordinates.getDouble("longitude");
                                    }

                                    if (evento.has("center")) {
                                        JSONObject coordinates = evento.getJSONObject("center");
                                        latitudeCenter = coordinates.getDouble("latitude");
                                        longitudeCenter = coordinates.getDouble("longitude");
                                    }

                                    if (evento.has("area")) {
                                        JSONArray are = evento.getJSONArray("area");
                                        for (int c = 0; c < are.length(); c++) {
                                            JSONObject coords = are.getJSONObject(c);
                                            double lat = coords.getDouble("latitude");
                                            double lon = coords.getDouble("longitude");
                                            area.add(new LatLng(lat, lon));
                                        }
                                    }


                                    Evento evento1 = new Evento(name, creator, creationDate, meetupDate, endDate,
                                            interests, confirmations, admin, id, location, alert, description, weather, image_uri,
                                            latitudeMeetUp, longitudeMeetUp, latitudeCenter, longitudeCenter, radius, hasConfirmation,
                                            hasInterest, area);

                                    if (image_uri != null)
                                        receberImagemVolley(evento1);
                                    else
                                        evento1.setImageID(R.drawable.ic_baseline_calendar_eventos_24px);

                                    if (!eventosList.contains(evento1))
                                        eventosList.add(evento1);
                                    adapter.notifyDataSetChanged();

                                }
                            }

                        } else {
                            isFinishedEventos = true;
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void receberImagemVolley(final Evento item) {
        String tag_json_obj = "octect_request";
        String url = item.getImage_uri();


        final String token = getSharedPreferences("Prefs", MODE_PRIVATE).getString("tokenID", "erro");
        ByteRequest stringRequest = new ByteRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {

            @Override
            public void onResponse(byte[] response) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
                item.setBitmap(response);
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroIMAGEMocorrencia", "Error: " + error.getMessage());

                item.setImageID(R.drawable.ic_baseline_calendar_eventos_24px);

                adapter.notifyDataSetChanged();
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
                Toast.makeText(FeedEventosActivity.this, "Erro", Toast.LENGTH_SHORT).show();

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
