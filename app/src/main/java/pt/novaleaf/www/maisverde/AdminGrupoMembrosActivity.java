package pt.novaleaf.www.maisverde;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminGrupoMembrosActivity extends AppCompatActivity implements Serializable{

    private Grupo grupo;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_grupo_membros);
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

        listView = (ListView) findViewById(R.id.listMembros);

        grupo = (Grupo) getIntent().getSerializableExtra("grupo");

        final List<String> membros = new ArrayList<>();
        if (grupo.getBase_users() != null)
         membros.addAll(grupo.getBase_users());
        if (grupo.getAdmins() != null)
            membros.addAll(grupo.getAdmins());

        Log.d("TAS FIXE??", grupo.getName());

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, membros);

        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (!getSharedPreferences("Prefs", MODE_PRIVATE).getString("username", "heee").
                        equals(membros.get(i))) {
                    if (grupo.getAdmins() != null && grupo.getAdmins().contains(membros.get(i)))
                        popupAdmin(membros.get(i), view);
                    else if (grupo.getBase_users() != null && grupo.getBase_users().contains(membros.get(i))) {
                        popupBaseUsers(membros.get(i), view);
                    }
                }
                return false;
            }
        });


    }

    private void popupBaseUsers(final String s, View adapterView) {

        PopupMenu popupMenu = new PopupMenu(this, adapterView);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.expulsar_user){
                    volleyExpulsarUser(s);
                } else {
                    volleyAdicionarAdmin(s);
                }
                return false;
            }
        });

        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.gerir_membros_admin_menu, popupMenu.getMenu());
        popupMenu.show();

    }

    private void popupAdmin(final String s, View adapterView) {
        PopupMenu popupMenu = new PopupMenu(this, adapterView);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.expulsar_admin){
                    volleyRemoverAdmin(s);
                } else {
                    volleyExpulsarUser(s);
                }
                return false;
            }
        });

        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.gerir_membros_admin_menu, popupMenu.getMenu());
        popupMenu.show();

    }

    private void volleyExpulsarUser(final String user) {

        String tag_json_obj = "json_request";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/gadmin/kick_user/?group_id="
                + grupo.getGroupId() + "&username=" + user;


        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        JSONObject eventos = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String >() {

                    @Override
                    public void onResponse(String response) {
                        AdministrarGrupoActivity.grupo.getBase_users().remove(user);
                        AdministrarGrupoActivity.grupo.getAdmins().remove(user);

                        GrupoFeedActivity.novoGrupo.getBase_users().remove(user);
                        GrupoFeedActivity.novoGrupo.getAdmins().remove(user);

                        grupo.getBase_users().remove(user);
                        grupo.getAdmins().remove(user);

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

    private void volleyAdicionarAdmin(final String user) {

        String tag_json_obj = "json_request";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/gadmin/new_admin/?group_id="
                + grupo.getGroupId() + "&username=" + user;


        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        JSONObject eventos = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String >() {

                    @Override
                    public void onResponse(String  response) {
                        //AdministrarGrupoActivity.grupo.getBase_users().add(user);
                        AdministrarGrupoActivity.grupo.getAdmins().add(user);

                        //GrupoFeedActivity.novoGrupo.getBase_users().remove(user);
                        GrupoFeedActivity.novoGrupo.getAdmins().add(user);

                        grupo.getAdmins().add(user);
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


    private void volleyRemoverAdmin(final String user) {

        String tag_json_obj = "json_request";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/gadmin/remove_admin/?group_id="
                + grupo.getGroupId() + "&username=" + user;


        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        JSONObject eventos = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String  response) {

                        AdministrarGrupoActivity.grupo.getAdmins().remove(user);

                        //GrupoFeedActivity.novoGrupo.getBase_users().remove(user);
                        GrupoFeedActivity.novoGrupo.getAdmins().remove(user);

                        grupo.getAdmins().remove(user);

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

}
