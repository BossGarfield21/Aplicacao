package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import utils.ByteRequest;
import utils.LruBitmapCache;

import static pt.novaleaf.www.maisverde.ComentariosActivity.comentarios;
import static pt.novaleaf.www.maisverde.EventoFragment.listEventos;
import static pt.novaleaf.www.maisverde.EventoFragment.myEventoRecyclerViewAdapter;
import static pt.novaleaf.www.maisverde.OcorrenciaFragment.listOcorrencias;
import static pt.novaleaf.www.maisverde.OcorrenciaFragment.myOcorrenciaRecyclerViewAdapter;

/**
 * Author: Hugo Mochao
 * Atividade do feed de ocorrencias
 * Implementa um cardview
 */

public class FeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Serializable {

    NavigationView navigationView;
    private String cursorOcorrencias = "";
    private boolean isFinishedOcorrencias = false;
    private PopupMenu popup = null;
    Fragment ocorrenciaFragment;
    private LinearLayout linearLayout;
    public static List<Ocorrencia> ocorrencias = new ArrayList<>();
    public static List<Ocorrencia> tempOcorrencias = new ArrayList<>();
    OcorrenciaFragment.OnListFragmentInteractionListener mListener;
    public static MyOcorrenciaRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    private boolean canScroll = false;
    private String distrito;
    LruBitmapCache lruBitmapCache = new LruBitmapCache();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        distrito = "TUDO";


        // Set up the ViewPager with the sections adapter.

        new Thread(new Runnable() {
            @Override

            public void run() {
                volleyGetOcorrencias();
            }


        }).start();


        //updateEventos();

//        linearLayout = (LinearLayout) findViewById(R.id.container);
/**
 FragmentManager fragmentManager = getSupportFragmentManager();
 Fragment fragment = fragmentManager.findFragmentById(R.id.container);
 if (fragment == null) {
 fragment = OcorrenciaFragment.newInstance(1, FeedActivity.this);

 fragmentManager.beginTransaction()
 .add(R.id.container, fragment)
 .commit();


 }*/

        mListener = new OcorrenciaFragment.OnListFragmentInteractionListener() {
            @Override
            public void onLikeInteraction(Ocorrencia item) {

                Log.d("TAS LIKE???", item.isLiked() + " PUTA");

                likeReportVolley(item);
                //item.like();


            }

            @Override
            public void onCommentInteraction(Ocorrencia item) {
                //Toast.makeText(FeedActivity.this, "IR PARA A PAGINA DOS COMENTARIOS", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(FeedActivity.this, ComentariosActivity.class);
                List<Comentario> comentarios = new ArrayList<>(item.getComments().values());
                i.putExtra("comentarios", (Serializable) item);
                startActivity(i);
            }

            @Override
            public void onEditInteraction(final Ocorrencia itemm, View view) {

                PopupMenu popup = new PopupMenu(FeedActivity.this, view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.editar_informacoes) {
                            Intent intent = new Intent(FeedActivity.this, CriarOcorrenciaActivity.class);
                            intent.putExtra("ocorrencia", (Serializable) itemm);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(FeedActivity.this, MapsActivity.class);
                            intent.putExtra("ocorrencia", (Serializable) itemm);
                            startActivity(intent);
                        }

                        return false;
                    }
                });// to implement on click event on items of menu
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_editar_ocorrencia, popup.getMenu());
                popup.show();

            }

            @Override
            public void onImagemInteraction(Ocorrencia item) {
                //Toast.makeText(FeedActivity.this, "IR PARA A PAGINA DA OCORRENCIA", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(FeedActivity.this, OcorrenciaActivity.class);
                i.putExtra("Ocorrencia", (Serializable) item);
                startActivity(i);
            }
        };

        adapter = new MyOcorrenciaRecyclerViewAdapter(ocorrencias, mListener, FeedActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && canScroll) {
                    //volleyGetOcorrencias();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            volleyGetOcorrencias();
                        }
                    }).start();

                }
            }
        });

        // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setVisibility(View.GONE);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);


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
        if (id == R.id.filter) {

            showMenu(findViewById(R.id.filter));

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_eventos) {
            Intent i = new Intent(FeedActivity.this, FeedEventosActivity.class);
            startActivityForResult(i, 0);
        } else if (id == R.id.nav_mapa) {

            Intent i = new Intent(FeedActivity.this, MapsActivity.class);
            startActivityForResult(i, 0);
            //startActivity(i);
            //finish();

        } else if (id == R.id.nav_area_pessoal) {
            Intent i = new Intent(FeedActivity.this, PerfilActivity.class);
            startActivityForResult(i, 0);
            //startActivity(i);
            //finish();

        } else if (id == R.id.nav_grupos) {

            Intent i = new Intent(FeedActivity.this, GruposListActivity.class);
            startActivityForResult(i, 0);
            //startActivity(i);
            //finish();

        } else if (id == R.id.nav_acerca) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://anovaleaf.ddns.net"));
            startActivity(i);
        } else if (id == R.id.nav_help) {
            return true;
        } else if (id == R.id.nav_end) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(FeedActivity.this);
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
                            Intent intent = new Intent(FeedActivity.this, LoginActivity.class);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_CANCELED) {
                // user pressed back from 2nd activity to go to 1st activity. code here
                navigationView.getMenu().getItem(0).setChecked(true);
            }
        }
    }

    private void showDistrict(String distrito) {

        //tempGrupos.clear();
        Log.d("DITRITO", distrito);

        if (!distrito.equals("TUDO")) {
            tempOcorrencias.clear();

            for (Ocorrencia ocorrencia : ocorrencias) {
                if (ocorrencia.getDistrict() != null)
                    if (ocorrencia.getDistrict().toUpperCase().equals(distrito.toUpperCase()))
                        tempOcorrencias.add(ocorrencia);
            }
            MyOcorrenciaRecyclerViewAdapter.mValues = tempOcorrencias;
            adapter.notifyDataSetChanged();
        } else {
            tempOcorrencias = new ArrayList<>(ocorrencias);
            MyOcorrenciaRecyclerViewAdapter.mValues = tempOcorrencias;
            adapter.notifyDataSetChanged();
        }

    }

    public void showMenu(View v) {
        if (popup == null) {
            popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (!item.isChecked()) {
                        item.setCheckable(true);
                        item.setChecked(!item.isChecked());
                        setUncheckedMenu(popup, item);
                        distrito = item.getTitle().toString();
                        showDistrict(distrito);
                    }

                    return false;
                }
            });// to implement on click event on items of menu
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_distritos_feed, popup.getMenu());

            popup.getMenu().findItem(R.id.d0).setCheckable(true);
            popup.getMenu().findItem(R.id.d0).setChecked(true);
        }
        popup.show();
    }

    private void setUncheckedMenu(PopupMenu menu, MenuItem item) {


        for (int i = 0; i < menu.getMenu().size(); i++) {
            if (!menu.getMenu().getItem(i).equals(item)) {
                menu.getMenu().getItem(i).setCheckable(false);
                menu.getMenu().getItem(i).setChecked(false);
            }
        }

    }

    public void volleyGetOcorrencias() {

        final String tag_json_obj = "json_request";
        String url;
        if (cursorOcorrencias.equals(""))
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/social/feed?cursor=startquery";
        else
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/social/feed?cursor=" + cursorOcorrencias;

        Log.d("ché bate só", url);

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        JSONObject reports = new JSONObject();
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
            cursorOcorrencias = response.getString("cursor");
            Log.d("SUA PUTA TAS AI???", response.toString());
            final JSONArray list = response.getJSONArray("list");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        if (!response.isNull("list")) {
                            Log.d("nao é null", list.toString());
                            if (!isFinishedOcorrencias)
                                for (int i = 0; i < list.length(); i++) {
                                    //Log.d("bina, empina?", list.toString());

                                    String id = null;
                                    String titulo = null;
                                    String descricao = null;
                                    String owner = null;
                                    String type = null;
                                    boolean hasLiked = false;
                                    String image_uri = null;
                                    String user_image = null;
                                    List<String> likers = new ArrayList<>();
                                    long creationDate = 0;
                                    String district = null;
                                    double risk = 0;
                                    double radius = 0;
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
                                    if (ocorrencia.has("district"))
                                        district = ocorrencia.getString("district");
                                    JSONObject image = null;
                                    if (ocorrencia.has("image_uri")) {
                                        image = ocorrencia.getJSONObject("image_uri");
                                        if (image.has("value"))
                                            image_uri = image.getString("value");
                                    }
                                    JSONObject imageuser = null;
                                    if (ocorrencia.has("user_image")) {
                                        imageuser = ocorrencia.getJSONObject("user_image");
                                        if (imageuser.has("value"))
                                            user_image = imageuser.getString("value");
                                        Log.d("user image bina:", user_image);
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
                                            JSONObject im = null;
                                            if (com.has("image")) {
                                                im = com.getJSONObject("image");
                                                if (im.has("value"))
                                                    imag = im.getString("value");
                                            }


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

                                    if (ocorrencia.has("radius"))
                                        radius = ocorrencia.getDouble("radius");

                                    if (district == null) {
                                        Geocoder geocoder;
                                        List<Address> addresses;
                                        geocoder = new Geocoder(FeedActivity.this, Locale.getDefault());

                                        try {
                                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                        } catch (IOException e) {
                                            continue;
                                        }
                                        if (addresses != null && addresses.size() > 0) {
                                            if (addresses.get(0).getAdminArea() != null)
                                                district = addresses.get(0).getAdminArea();
                                            else if (addresses.get(0).getLocality() != null)
                                                district = addresses.get(0).getLocality();
                                        }
                                    }
                                    Ocorrencia ocorrencia1 = new Ocorrencia(titulo, risk, "23:12", id,
                                            descricao, owner, likers, status, latitude, longitude, likes, type, image_uri,
                                            comentarios, creationDate, district, hasLiked, user_image, radius);
                                    if (ocorrencia1.getImage_uri() != null) {
                                        if (lruBitmapCache.getBitmap(ocorrencia1.getImage_uri()) == null)
                                            receberImagemVolley(ocorrencia1);
                                        else {
                                            Log.d("Usou a cache", "BOA!!!");
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            lruBitmapCache.getBitmap(ocorrencia1.getImage_uri()).compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            ocorrencia1.setBitmap(stream.toByteArray());
                                        }
                                    } else {
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

                                    if (ocorrencia1.getUser_image() != null && !ocorrencias.contains(ocorrencia1)) {
                                        if (lruBitmapCache.getBitmap(ocorrencia1.getUser_image()) == null)
                                            receberImagemUserVolley(ocorrencia1);
                                        else {
                                            Log.d("Usou a cache", "BOA!!!");
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            lruBitmapCache.getBitmap(ocorrencia1.getUser_image()).compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            ocorrencia1.setBitmapUser(stream.toByteArray());
                                        }
                                    } else {
                                        ocorrencia1.setImageIDUser(R.drawable.ic_person_black_24dp);
                                    }


                                    if (!ocorrencias.contains(ocorrencia1))
                                        ocorrencias.add(ocorrencia1);
                                    Log.d("ID", id);
                                    Log.d("titulo", titulo);
                                    if (titulo.contains("Árvore")) {
                                        Log.d("imagem oco", image_uri);
                                        Log.d("imagem minha", user_image);
                                    }
                                    Log.d("desc", descricao);
                                    adapter.notifyDataSetChanged();

                                }

                            Log.d("cache", lruBitmapCache.hitCount() + " hits");
                            showDistrict(distrito);
                            isFinishedOcorrencias = response.getBoolean("isFinished");
                            if (list.length() < 15 || isFinishedOcorrencias)
                                canScroll = false;
                            else
                                canScroll = true;
                            Log.d("ACABOU???", String.valueOf(isFinishedOcorrencias));
                        } else {
                            isFinishedOcorrencias = true;
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


    private void likeReportVolley(final Ocorrencia item) {


        String tag_json_obj = "json_obj_req";
        String url;
        int method;
        if (!item.isLiked()) {
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/social/addlike?markerid=" + item.getId();
            method = Request.Method.PUT;
        } else {
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/social/removelike?markerid=" + item.getId();
            method = Request.Method.DELETE;
        }

        JSONObject grupo = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");

        // StringRequest stringRequest = new StringRequest()

        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("ERROLIKE", "Error: " + error.getMessage());
                item.like();
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

    private void receberImagemVolley(final Ocorrencia item) {
        String tag_json_obj = "octect_request";
        final String url = item.getImage_uri();


        final String token = getSharedPreferences("Prefs", MODE_PRIVATE).getString("tokenID", "erro");
        ByteRequest stringRequest = new ByteRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {

            @Override
            public void onResponse(byte[] response) {
                item.setBitmap(response);
                adapter.notifyDataSetChanged();
                lruBitmapCache.putBitmap(url, BitmapFactory.decodeByteArray(response, 0, response.length));


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

    private void receberImagemUserVolley(final Ocorrencia item) {
        String tag_json_obj = "octect_request";
        final String url = item.getUser_image();


        final String token = getSharedPreferences("Prefs", MODE_PRIVATE).getString("tokenID", "erro");
        ByteRequest stringRequest = new ByteRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {

            @Override
            public void onResponse(byte[] response) {
                item.setBitmapUser(response);
                adapter.notifyDataSetChanged();
                lruBitmapCache.putBitmap(url, BitmapFactory.decodeByteArray(response, 0, response.length));
                Log.d("cache", lruBitmapCache.hitCount() + " hits");


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroIMAGEMocorrencia", "Error: " + error.getMessage());

                item.setImageID(R.drawable.ic_person_black_24dp);

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


}
