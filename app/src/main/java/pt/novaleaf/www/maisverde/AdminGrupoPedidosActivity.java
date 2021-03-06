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
import android.widget.Toast;

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

public class AdminGrupoPedidosActivity extends AppCompatActivity implements Serializable {

    private Grupo grupo;
    private ListView listView;
    ArrayAdapter adapter;
    List<String> pedidos;
    Map<String, String > ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_grupo_pedidos);
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

        listView = (ListView) findViewById(R.id.listPedidos);

        grupo = (Grupo) getIntent().getSerializableExtra("grupo");


        pedidos = new ArrayList<>();
        ids = new HashMap<>();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!pedidos.get(i).equals("Não há pedidos..."))
                    popupPedidos(view, pedidos.get(i));
            }
        });

        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, pedidos);

        listView.setAdapter(adapter);
        volleyGetPedidos();


    }

    private void popupPedidos(View view, final String user) {
        PopupMenu popupMenu = new PopupMenu(AdminGrupoPedidosActivity.this, view);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.aceitar_user) {
                    volleyAceitarUser(user);
                } else {
                    volleyRejeitarUser(user);
                }
                return false;
            }
        });

        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.pedidos_menu, popupMenu.getMenu());
        popupMenu.show();

    }

    private void volleyAceitarUser(final String user) {
        String tag_json_obj = "json_request";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/gadmin/accept?group_id=" +
                grupo.getGroupId() + "&group_request=" + ids.get(user);

        Log.d("ché bate só", url);

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        JSONObject eventos = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        ids.remove(user);
                        pedidos.remove(user);
                        adapter.notifyDataSetChanged();
                    }

                }, new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminGrupoPedidosActivity.this, "Erro", Toast.LENGTH_SHORT).show();
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

    private void volleyRejeitarUser(final String user) {
        String tag_json_obj = "json_request";
        final String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/gadmin/refuse?group_id=" +
                grupo.getGroupId() + "&request=" + ids.get(user);

        Log.d("ché bate só", url);

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        JSONObject eventos = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        ids.remove(user);
                        pedidos.remove(user);
                        adapter.notifyDataSetChanged();
                    }

                }, new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminGrupoPedidosActivity.this, "Erro", Toast.LENGTH_SHORT).show();
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


    private void volleyGetPedidos() {
        String tag_json_obj = "json_request";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/gadmin/group_requests?group_id="
                + grupo.getGroupId() + "&cursor=startquery";

        Log.d("ché bate só", url);

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        JSONObject eventos = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, eventos,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (!response.isNull("list") && response.getJSONArray("list").length()>0) {
                                JSONArray list = response.getJSONArray("list");
                                for (int i = 0; i < list.length(); i++) {


                                    String id = list.getJSONObject(i).getString("key");
                                    String pedido = list.getJSONObject(i).getString("value");
                                    pedidos.add(pedido);
                                    ids.put(pedido, id);
                                    adapter.notifyDataSetChanged();

                                }
                            } else {
                                Log.d("olá", "heheehehe");
                                pedidos.add("Não há pedidos...");
                                adapter.notifyDataSetChanged();

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


        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }
}
