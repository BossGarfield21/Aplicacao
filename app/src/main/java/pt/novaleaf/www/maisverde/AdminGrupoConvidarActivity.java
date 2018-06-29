package pt.novaleaf.www.maisverde;

import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminGrupoConvidarActivity extends AppCompatActivity implements Serializable {


    private Button mButtonAdicionar;
    private TextInputEditText mUsername;
    private Grupo grupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_grupos_convidar);
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
        mUsername = (TextInputEditText) findViewById(R.id.usernameConvidar);
        mButtonAdicionar = (Button) findViewById(R.id.bAdicionar);

        mButtonAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsername.setError(null);
                String username = mUsername.getText().toString();
                if (!TextUtils.isEmpty(username))
                    volleyConvidarGrupo(username);
                else
                    mUsername.setError("campo vazio");
            }
        });


    }

    private void volleyConvidarGrupo(final String user) {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/invite?group_id=" + grupo.getGroupId() +
                "&username=" + user;

        JSONObject grupo = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, grupo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(AdminGrupoConvidarActivity.this, user + " convidado", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 409)
                        Toast.makeText(AdminGrupoConvidarActivity.this, user + " já está no grupo", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(AdminGrupoConvidarActivity.this, "Username não encontrado", Toast.LENGTH_SHORT).show();
                    VolleyLog.d("erroJoingrupo", "Error: " + error.getMessage());
                }
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
