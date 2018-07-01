package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import utils.ByteRequest;

import static pt.novaleaf.www.maisverde.ComentariosActivity.comentarios;
import static pt.novaleaf.www.maisverde.EventoFragment.listEventos;
import static pt.novaleaf.www.maisverde.EventoFragment.myEventoRecyclerViewAdapter;
import static pt.novaleaf.www.maisverde.LoginActivity.sharedPreferences;
import static pt.novaleaf.www.maisverde.OcorrenciaFragment.listOcorrencias;
import static pt.novaleaf.www.maisverde.OcorrenciaFragment.myOcorrenciaRecyclerViewAdapter;

/**
 * Author: Hugo Mochao
 * Atividade do feed de ocorrencias
 * Implementa um cardview
 */

public class FeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OcorrenciaFragment.OnListFragmentInteractionListener, EventoFragment.OnListFragmentInteractionListener,
        Serializable {

    NavigationView navigationView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String cursorOcorrencias = "";
    private boolean isFinishedOcorrencias = false;
    Fragment ocorrenciaFragment;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Set up the ViewPager with the sections adapter.

        updateOcorrencias();
        //updateEventos();


        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = OcorrenciaFragment.newInstance(1);

            fragmentManager.beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

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
        if (id == R.id.action_help) {
            return true;
        } else if (id == R.id.action_logout) {

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

        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter implements Serializable {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment;
            switch (position) {
                case 0:
                    ocorrenciaFragment = OcorrenciaFragment.newInstance(1);
                    return ocorrenciaFragment;
                case 1:
                    fragment = EventoFragment.newInstance(1);
                    return fragment;
                default:
                    return null;
            }

            //return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
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

        PopupMenu popup = new PopupMenu(this, view);
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

        Intent i = new Intent(FeedActivity.this, EventoActivity.class);
        startActivity(i);

    }

    public void updateOcorrencias() {

        volleyGetOcorrencias();


    }

    public void volleyGetOcorrencias() {

        String tag_json_obj = "json_request";
        String url;
        if (cursorOcorrencias.equals(""))
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/social/feed/?cursor=startquery";
        else
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/social/feed/?cursor=" + cursorOcorrencias;

        Log.d("ché bate só", url);

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        JSONObject reports = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, reports,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("nabo", cursorOcorrencias + "pixa");
                            cursorOcorrencias = response.getString("cursor");
                            Log.d("nabo", cursorOcorrencias);
                            isFinishedOcorrencias = response.getBoolean("isFinished");
                            Log.d("ACABOU???", String.valueOf(isFinishedOcorrencias));


                            JSONArray list = response.getJSONArray("list");
                            if (!response.isNull("list")) {
                                if (!isFinishedOcorrencias)
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
                                                        com.getLong("creation_date"), origem, id));

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


                                        if (!listOcorrencias.contains(ocorrencia1))
                                            listOcorrencias.add(ocorrencia1);
                                        Log.d("ID", id);
                                        Log.d("titulo", titulo);
                                        Log.d("desc", descricao);
                                        myOcorrenciaRecyclerViewAdapter.notifyDataSetChanged();

                                    }
                            } else {
                                isFinishedOcorrencias = true;
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

    @Override
    public void onLikeInteraction(Ocorrencia item) {

        Log.d("TAS LIKE???", item.isLiked() + " PUTA");

        likeReportVolley(item, !item.isLiked());
        //item.like();


    }

    private void likeReportVolley(final Ocorrencia item, boolean like) {


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


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, grupo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    VolleyLog.d("errolike", "Error: " + error.getMessage());
                } else
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
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void receberImagemVolley(final Ocorrencia item) {
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
