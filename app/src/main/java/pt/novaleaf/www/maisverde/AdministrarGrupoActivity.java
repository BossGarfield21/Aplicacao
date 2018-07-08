package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.List;
import java.util.Map;

public class AdministrarGrupoActivity extends AppCompatActivity implements Serializable {


    ListView listView;
    static Grupo grupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_grupo);

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

        grupo = (Grupo) getIntent().getSerializableExtra("grupo");

        Log.d("TAS FIXE??", grupo.getAdmins().get(0));

        List<String> lista = new ArrayList<>();
        lista.add("Convidar pessoas");
        lista.add("Atualizar informação");
        lista.add("Gerir membros");
        lista.add("Convites enviados");
//        if (grupo.getPrivacy().equals("public"))
        lista.add("Pedidos pendentes");
        listView = (ListView) findViewById(R.id.listAdmin);

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, lista);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = null;
                if (i == 0)
                    intent = new Intent(AdministrarGrupoActivity.this, AdminGrupoConvidarActivity.class);
                else if (i == 1)
                    intent = new Intent(AdministrarGrupoActivity.this, AdminGrupoAtualizarActivity.class);
                else if (i == 2)
                    intent = new Intent(AdministrarGrupoActivity.this, AdminGrupoMembrosActivity.class);
                else if (i == 3)
                    intent = new Intent(AdministrarGrupoActivity.this, AdminGrupoConvitesActivity.class);
                else if (i == 4)
                    intent = new Intent(AdministrarGrupoActivity.this, AdminGrupoPedidosActivity.class);


                intent.putExtra("grupo", grupo);
                startActivity(intent);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.adminstrador_grupo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.acabar_grupo) {
            return true;
        } else if (id == R.id.action_logout) {
            //TODO: sair da app
            final AlertDialog.Builder alert = new AlertDialog.Builder(AdministrarGrupoActivity.this);
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
                            Intent intent = new Intent(AdministrarGrupoActivity.this, LoginActivity.class);
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

        return super.onOptionsItemSelected(item);
    }


    private void volleyDissolverGrupo() {

        String tag_json_obj = "json_request";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/gadmin/dissolve?group_id="
                + grupo.getGroupId();

        Log.d("ché bate só", url);

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        JSONObject eventos = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, eventos,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        GrupoFeedActivity.posts.remove(grupo);
                        GrupoFeedActivity.adapter.notifyDataSetChanged();

                        finish();
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

                addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }


}
