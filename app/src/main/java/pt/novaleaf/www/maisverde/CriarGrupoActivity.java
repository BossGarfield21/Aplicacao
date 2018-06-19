package pt.novaleaf.www.maisverde;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Switch;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CriarGrupoActivity extends AppCompatActivity {

    private Button mButtonCriarGrupo;
    private AutoCompleteTextView mNomeGrupo;
    private Switch mPrivacidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_grupo);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
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

        mButtonCriarGrupo = (Button) findViewById(R.id.btn_criar_grupo);
        mNomeGrupo = (AutoCompleteTextView) findViewById(R.id.input_group);
        mPrivacidade = (Switch) findViewById(R.id.switchGoups);

        mButtonCriarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateGroup();
            }
        });

    }

    private void attemptCreateGroup() {

        String nomeGrupo = mNomeGrupo.getText().toString();
        boolean isPrivado = mPrivacidade.isChecked();

        mNomeGrupo.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nomeGrupo)){
            mNomeGrupo.setError("O nome tem de ter pelo menos 4 caracteres");
            focusView = mNomeGrupo;
            cancel = true;
        }

        String privacy;
        if (isPrivado)
            privacy = "private";
        else
            privacy = "public";

        if (cancel) {
            // There was an error; focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            criarGrupoVolley(nomeGrupo, privacy);
        }
    }

    private void criarGrupoVolley(String nomeGrupo, String privacy) {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/create?group=" + nomeGrupo + "&privacy="+
                privacy;

        JSONObject grupo = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");
        try {

            grupo.put("name", nomeGrupo);
            grupo.put("privacy", privacy);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, grupo,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Intent i = new Intent(CriarGrupoActivity.this, GruposListActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("erroNOVAOCORRENCIA", "Error: " + error.getMessage());
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
}
