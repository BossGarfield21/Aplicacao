package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.ByteRequest;

import static pt.novaleaf.www.maisverde.EventoFragment.listEventos;
import static pt.novaleaf.www.maisverde.EventoFragment.myEventoRecyclerViewAdapter;
import static pt.novaleaf.www.maisverde.LoginActivity.sharedPreferences;
import static pt.novaleaf.www.maisverde.OcorrenciaFragment.myOcorrenciaRecyclerViewAdapter;

public class FeedEventosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventoFragment.OnListFragmentInteractionListener {

    NavigationView navigationView;
    private String cursorEventos = "";
    private boolean isFinishedEventos = false;


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

        volleyGetEventos();
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
                navigationView.getMenu().getItem(3).setChecked(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed_menu, menu);
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

        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLikeInteraction(Evento item) {

    }

    @Override
    public void onCommentInteraction(Evento item) {

    }

    @Override
    public void onFavoritoInteraction(Evento item) {

    }

    @Override
    public void onImagemInteraction(Evento item) {

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
        JSONObject eventos = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, eventos,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("nabo", cursorEventos + "pixa");
                            cursorEventos = response.getString("cursor");
                            Log.d("nabo", cursorEventos);


                            JSONArray list = response.getJSONArray("list");
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
                                        double radious = 0;

                                        JSONObject evento = list.getJSONObject(i);
                                        if (evento.has("id"))
                                            id = evento.getString("alert");
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
                                        if (evento.has("radious"))
                                            radious = evento.getLong("radious");
                                        if (evento.has("endDate"))
                                            endDate = evento.getLong("endDate");
                                        if (evento.has("image_uri"))
                                            image_uri = evento.getString("image_uri");
                                        if (evento.has("weather"))
                                            weather = evento.getString("weather");

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
                                            JSONObject coordinates = evento.getJSONObject("coordinates");
                                            latitudeMeetUp = coordinates.getDouble("latitude");
                                            longitudeMeetUp = coordinates.getDouble("longitude");
                                        }

                                        if (evento.has("center")) {
                                            JSONObject coordinates = evento.getJSONObject("coordinates");
                                            latitudeCenter = coordinates.getDouble("latitude");
                                            longitudeCenter = coordinates.getDouble("longitude");
                                        }


                                        Evento evento1 = new Evento(name, creator, creationDate, meetupDate, endDate,
                                                interests, confirmations, admin, id, location, alert, description, weather, image_uri,
                                                latitudeMeetUp, longitudeMeetUp, latitudeCenter, longitudeCenter, radious);

                                        if (image_uri != null)
                                            receberImagemVolley(evento1);

                                        listEventos.add(evento1);
                                        myEventoRecyclerViewAdapter.notifyDataSetChanged();

                                    }
                                }

                            } else {
                                isFinishedEventos = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroLOGIN", "Error: " + error.getMessage());
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


    }

    private void receberImagemVolley(final Evento item) {
        String tag_json_obj = "octect_request";
        String url = item.getImage_uri();


        final String token = sharedPreferences.getString("tokenID", "erro");
        ByteRequest stringRequest = new ByteRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {

            @Override
            public void onResponse(byte[] response) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
                item.setBitmap(response);
                myOcorrenciaRecyclerViewAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroIMAGEMocorrencia", "Error: " + error.getMessage());

                item.setImageID(R.mipmap.ic_grass_foreground);

                myOcorrenciaRecyclerViewAdapter.notifyDataSetChanged();
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
