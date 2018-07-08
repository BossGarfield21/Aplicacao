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



}
