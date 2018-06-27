package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Author: Hugo Mochao
 * Atividade que serve para modificar os dados de um utilizador
 */

public class AlterarDadosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String email;
    public static ArrayList<PerfilItem> arrayList;
    public static RecyclerView mRecyclerViewPerfil;
    public static MyPerfilRecyclerViewAdapter adapter;
    public static SharedPreferences sharedPreferences;
    public static boolean changed = false;

    TextView textUsername;
    NavigationView navigationView;
    Toolbar toolbar;
    ConstraintLayout constraintLayout;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_dados);

        constraintLayout = (ConstraintLayout) findViewById(R.id.mConstr);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changed) {
                    changed = false;
                    attemptSendData();
                }
                finish();
            }
        });
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        textUsername = (TextView) findViewById(R.id.textUsername);
        sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);

        textUsername.setText(sharedPreferences.getString("username", "User"));

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(4).setChecked(true);


        mRecyclerViewPerfil = (RecyclerView) findViewById(R.id.myList);
        email = sharedPreferences.getString("email", "erro");

        //Ir buscar a informacao do utilizador
        arrayList = new ArrayList<>();
        //arrayList.add(new PerfilItem("Username", sharedPreferences.getString("username", "erro")));
        arrayList.add(new PerfilItem("Email", email));
        arrayList.add(new PerfilItem("Nome", sharedPreferences.getString("name", "")));
        arrayList.add(new PerfilItem("Aprovação dos reports", sharedPreferences.getString("approval_rate", "erro")));
        arrayList.add(new PerfilItem("Reports efetuados", sharedPreferences.getString("numb_reports", "erro")));
        arrayList.add(new PerfilItem("Morada", sharedPreferences.getString("firstaddress", "ainda não definida")));
        arrayList.add(new PerfilItem("Morada complementar", sharedPreferences.getString("complementaryaddress", "ainda não definida")));
        arrayList.add(new PerfilItem("Localidade", sharedPreferences.getString("locality", "ainda não definida")));
        arrayList.add(new PerfilItem("Código Postal", sharedPreferences.getString("postalcode", "ainda não definido")));
        arrayList.add(new PerfilItem("Telemóvel", sharedPreferences.getString("mobile_phone", "ainda não definido")));
        arrayList.add(new PerfilItem("Mudar a password", ""));

        adapter = new MyPerfilRecyclerViewAdapter(this, arrayList);
        mRecyclerViewPerfil.setLayoutManager(new LinearLayoutManager(this));

        float density = this.getResources().getDisplayMetrics().density;

        int actionBarHeight = 56;
        int paddingPixel = (int) ((actionBarHeight + 150) * density + 0.5f);
        Log.e("pixel", "" + paddingPixel + " tool " + actionBar.getHeight() + " " + constraintLayout.getHeight());
        mRecyclerViewPerfil.setPadding(0, paddingPixel, 0, 0);
        mRecyclerViewPerfil.setAdapter(adapter);
        mRecyclerViewPerfil.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });
    }

    private void hideViews() {
        constraintLayout.animate().translationY(-constraintLayout.getHeight() - toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

    }

    private void showViews() {
        constraintLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }
    //


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.area_pessoal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.editPerfil) {
            return true;
        } else if (id == R.id.action_help) {
            return true;
        } else if (id == R.id.action_logout) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(AlterarDadosActivity.this);
            alert.setTitle("Terminar sessão");
            alert
                    .setMessage("Deseja terminar sessão?")
                    .setCancelable(true)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
                            if (changed)
                                attemptSendData();
                            editor.clear();
                            editor.commit();
                            Intent intent = new Intent(AlterarDadosActivity.this, LoginActivity.class);
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

    @Override
    public void onBackPressed() {
        if (changed) {
            changed = false;
            attemptSendData();
        }
        //navigationView.getMenu().getItem(0).setChecked(true);
        finish();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSendData() {

        String email = sharedPreferences.getString("email", "");
        String firstaddress = sharedPreferences.getString("firstaddress", "");
        String complementaryaddress = sharedPreferences.getString("complementaryaddress", "");
        String locality = sharedPreferences.getString("locality", "");
        String mobile_phone = sharedPreferences.getString("mobile_phone", "");
        String postalcode = sharedPreferences.getString("postalcode", "");


        alterarDadosVolley(email, firstaddress, complementaryaddress, locality, mobile_phone, postalcode);

    }

    private void alterarDadosVolley(final String email, final String firstaddress, final String complementaryaddress,
                                    final String locality, final String mobile_phone,
                                    final String postalcode) {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/users/complete_profile";

        JSONObject profileInfo = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");
        try {

            profileInfo.put("email", email);
            profileInfo.put("firstaddress", firstaddress);
            profileInfo.put("complementaryaddress", complementaryaddress);
            profileInfo.put("locality", locality);
            profileInfo.put("mobile_phone", mobile_phone);
            profileInfo.put("postalcode", postalcode);


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, profileInfo,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    changed = true;
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_eventos) {
            Intent i = new Intent(AlterarDadosActivity.this, FeedEventosActivity.class);
            startActivityForResult(i, 0);
        } else if (id == R.id.nav_feed) {
            Intent i = new Intent(AlterarDadosActivity.this, FeedActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_mapa) {

            if (changed)
                attemptSendData();
            Intent i = new Intent(AlterarDadosActivity.this, MapsActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_area_pessoal) {

        } else if (id == R.id.nav_grupos) {

            if (changed)
                attemptSendData();
            Intent i = new Intent(AlterarDadosActivity.this, GruposListActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}




