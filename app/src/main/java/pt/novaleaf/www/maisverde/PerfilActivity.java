package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import utils.ByteRequest;

import static pt.novaleaf.www.maisverde.LoginActivity.sharedPreferences;
import static pt.novaleaf.www.maisverde.OcorrenciaFragment.myOcorrenciaRecyclerViewAdapter;


/**
 * Author: Hugo Mochao
 * Atividade que serve para modificar os dados de um utilizador
 */

public class PerfilActivity extends AppCompatActivity
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
    private ImageView mImage;
    private static final int PICK_IMAGE = 2;
    private byte[] imageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        constraintLayout = (ConstraintLayout) findViewById(R.id.mConstr);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


        mImage = (ImageView) findViewById(R.id.profile_pic);

        String image = getSharedPreferences("Prefs", MODE_PRIVATE).getString("image_user", null);
        if (image!=null)
            receberImagemVolley(image);

        mImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                popupImagem(view);
                return false;
            }
        });


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
        arrayList.add(new PerfilItem("Nome", getSharedPreferences("Prefs", MODE_PRIVATE).getString("name", "")));
        arrayList.add(new PerfilItem("Aprovação dos reports", getSharedPreferences("Prefs", MODE_PRIVATE).getString("approval_rate", "erro")));
        arrayList.add(new PerfilItem("Reports efetuados", getSharedPreferences("Prefs", MODE_PRIVATE).getString("numb_reports", "erro")));
        arrayList.add(new PerfilItem("Morada", getSharedPreferences("Prefs", MODE_PRIVATE).getString("firstaddress", "")));
        arrayList.add(new PerfilItem("Morada complementar", getSharedPreferences("Prefs", MODE_PRIVATE).getString("complementaryaddress", "")));
        arrayList.add(new PerfilItem("Localidade", getSharedPreferences("Prefs", MODE_PRIVATE).getString("locality", "")));
        arrayList.add(new PerfilItem("Código Postal", getSharedPreferences("Prefs", MODE_PRIVATE).getString("postalcode", "")));
        arrayList.add(new PerfilItem("Telemóvel", getSharedPreferences("Prefs", MODE_PRIVATE).getString("mobile_phone", "")));
        arrayList.add(new PerfilItem("Mudar a password", ""));

        adapter = new MyPerfilRecyclerViewAdapter(this, arrayList);
        mRecyclerViewPerfil.setLayoutManager(new LinearLayoutManager(this));

        float density = this.getResources().getDisplayMetrics().density;

        int actionBarHeight = 56;
        int paddingPixel = (int) ((actionBarHeight + 150) * density + 0.5f);
        Log.e("pixel", "" + paddingPixel + " tool " + actionBar.getHeight() + " " + constraintLayout.getHeight());
        mRecyclerViewPerfil.setPadding(0, paddingPixel, 0, 0);
        mRecyclerViewPerfil.setAdapter(adapter);

        if (getIntent().getBooleanExtra("mudou", false))
            chageData(getIntent());
    }

    private void popupImagem(View view) {
        PopupMenu popupMenu = new PopupMenu(PerfilActivity.this, view);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.mudar_pic) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }
                return false;
            }
        });

        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.mudar_imagem_menu, popupMenu.getMenu());
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    mImage.setImageBitmap(bitmap);


                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                    imageBytes = bao.toByteArray();


                    String id = UUID.randomUUID().toString().concat(String.valueOf(System.currentTimeMillis()));
                    enviarImagemVolley(imageBytes, id);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void chageData(Intent intent) {
        String email, nome, morada, morada_complementar, localidade, codigo_postal, telemovel;

        email = intent.getStringExtra("email");
        nome = intent.getStringExtra("nome");
        morada = intent.getStringExtra("morada");
        morada_complementar = intent.getStringExtra("morada_complementar");
        localidade = intent.getStringExtra("localidade");
        codigo_postal = intent.getStringExtra("codigo_postal");
        telemovel = intent.getStringExtra("telemovel");
        SharedPreferences.Editor editor = sharedPreferences.edit();


        if (!TextUtils.isEmpty(email)) {
            arrayList.remove(0);
            arrayList.add(0, new PerfilItem("Email", email));
            editor.putString("email", email);
        }

        if (!TextUtils.isEmpty(nome)) {
            arrayList.remove(1);
            arrayList.add(1, new PerfilItem("Nome", nome));
            editor.putString("nome", nome);
        }

        if (!TextUtils.isEmpty(morada)) {
            arrayList.remove(4);
            arrayList.add(4, new PerfilItem("Morada", morada));
            editor.putString("firstaddress", morada);
        }

        if (!TextUtils.isEmpty(morada_complementar)) {
            arrayList.remove(5);
            arrayList.add(5, new PerfilItem("Morada complementar", morada_complementar));
            editor.putString("complementaryaddress", morada_complementar);
        }

        if (!TextUtils.isEmpty(localidade)) {
            arrayList.remove(6);
            arrayList.add(6, new PerfilItem("Localidade", localidade));
            editor.putString("locality", localidade);
        }

        if (!TextUtils.isEmpty(codigo_postal)) {
            arrayList.remove(7);
            arrayList.add(7, new PerfilItem("Código postal", codigo_postal));
            editor.putString("postalcode", codigo_postal);
        }

        if (!TextUtils.isEmpty(telemovel)) {
            arrayList.remove(8);
            arrayList.add(8, new PerfilItem("Telemóvel", telemovel));
            editor.putString("mobile_phone", telemovel);
        }

        editor.commit();

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
            Intent intent = new Intent(PerfilActivity.this, AlterarDadosActivity.class);
            intent.putExtra("email", arrayList.get(0).getDescricao());
            intent.putExtra("nome", arrayList.get(1).getDescricao());
            intent.putExtra("morada", arrayList.get(4).getDescricao());
            intent.putExtra("morada_complementar", arrayList.get(5).getDescricao());
            intent.putExtra("localidade", arrayList.get(6).getDescricao());
            intent.putExtra("codigo_postal", arrayList.get(7).getDescricao());
            intent.putExtra("telemovel", arrayList.get(8).getDescricao());
            startActivity(intent);

        } else if (id == R.id.action_help) {
            return true;
        } else if (id == R.id.action_logout) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(PerfilActivity.this);
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
                            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
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
            Intent i = new Intent(PerfilActivity.this, FeedEventosActivity.class);
            startActivityForResult(i, 0);
        } else if (id == R.id.nav_feed) {
            Intent i = new Intent(PerfilActivity.this, FeedActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_mapa) {


            Intent i = new Intent(PerfilActivity.this, MapsActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_area_pessoal) {

        } else if (id == R.id.nav_grupos) {


            Intent i = new Intent(PerfilActivity.this, GruposListActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void alterarDadosVolley(String image) {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/users/complete_profile";

        JSONObject profileInfo = new JSONObject();
        final String token = getSharedPreferences("Prefs", MODE_PRIVATE).getString("tokenID", "erro");
        try {

            profileInfo.put("image", image);
            Log.d("ya BINA", profileInfo.toString());


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, profileInfo,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void enviarImagemVolley(final byte[] imageBytes, String id) {

        String tag_json_obj = "octect_request";
        final String url = "https://novaleaf-197719.appspot.com/gcs/novaleaf-197719.appspot.com/" + id;

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();

                editor.putString("image_user", url);
                editor.putString("image_bytes", Arrays.toString(imageBytes));
                editor.commit();
                alterarDadosVolley(url);
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

    private void receberImagemVolley(final String url) {
        String tag_json_obj = "octect_request";

        final String token = getSharedPreferences("Prefs", MODE_PRIVATE).getString("tokenID", "erro");
        ByteRequest stringRequest = new ByteRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {

            @Override
            public void onResponse(byte[] response) {
                SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();

                editor.putString("image_bytes", Arrays.toString(response));
                editor.commit();

                mImage.setImageBitmap(BitmapFactory.decodeByteArray(response, 0, response.length));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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




