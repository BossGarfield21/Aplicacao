package pt.novaleaf.www.maisverde;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

public class AlterarDadosActivity extends AppCompatActivity {

    private TextInputEditText mEmail;
    private TextInputEditText mNome;
    private TextInputEditText mMorada;
    private TextInputEditText mMoradaComplementar;
    private TextInputEditText mLocalidade;
    private TextInputEditText mCodigoPostal;
    private TextInputEditText mTelemovel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_dados);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mEmail = (TextInputEditText) findViewById(R.id.alterarEmail);
        mNome = (TextInputEditText) findViewById(R.id.alterarNome);
        mMorada = (TextInputEditText) findViewById(R.id.alterarMorada);
        mMoradaComplementar = (TextInputEditText) findViewById(R.id.alterarMoradaComplementar);
        mLocalidade = (TextInputEditText) findViewById(R.id.alterarLocalidade);
        mCodigoPostal = (TextInputEditText) findViewById(R.id.alterarCodigoPostal);
        mTelemovel = (TextInputEditText) findViewById(R.id.alterarTelemovel);

        String email = getIntent().getStringExtra("email");
        String nome = getIntent().getStringExtra("nome");
        String morada = getIntent().getStringExtra("morada");
        String morada_complementar = getIntent().getStringExtra("morada_complementar");
        String localidade = getIntent().getStringExtra("localidade");
        String codigo_postal = getIntent().getStringExtra("codigo_postal");
        String telemovel = getIntent().getStringExtra("telemovel");

        mEmail.setText(email);
        mNome.setText(nome);
        mMorada.setText(morada);
        mMoradaComplementar.setText(morada_complementar);
        mLocalidade.setText(localidade);
        mCodigoPostal.setText(codigo_postal);
        mTelemovel.setText(telemovel);

    }

    @Override
    public void onBackPressed() {

        String email = mEmail.getText().toString();
        String nome = mNome.getText().toString();
        String morada = mMorada.getText().toString();
        String morada_complementar = mMoradaComplementar.getText().toString();
        String localidade = mLocalidade.getText().toString();
        String codigo_postal = mCodigoPostal.getText().toString();
        String telemovel = mTelemovel.getText().toString();

        if (TextUtils.isEmpty(email))
            email = null;

        if (TextUtils.isEmpty(nome))
            nome = null;

        if (TextUtils.isEmpty(morada))
            morada = null;

        if (TextUtils.isEmpty(morada_complementar))
            morada_complementar= null;

        if (TextUtils.isEmpty(localidade))
            localidade = null;

        if (TextUtils.isEmpty(telemovel))
            telemovel = null;

        if (TextUtils.isEmpty(codigo_postal))
            codigo_postal = null;

        alterarDadosVolley(email, morada, morada_complementar, localidade, telemovel, codigo_postal, nome);


        Intent intent = new Intent(AlterarDadosActivity.this, PerfilActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("nome", nome);
        intent.putExtra("morada", morada);
        intent.putExtra("morada_complementar", morada_complementar);
        intent.putExtra("localidade", localidade);
        intent.putExtra("codigo_postal", codigo_postal);
        intent.putExtra("telemovel", telemovel);
        intent.putExtra("mudou", true);
        startActivity(intent);
        finish();
        //super.onBackPressed();
    }

    private void alterarDadosVolley(final String email, final String firstaddress, final String complementaryaddress,
                                    final String locality, final String mobile_phone,
                                    final String postalcode, String name) {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/users/complete_profile";

        JSONObject profileInfo = new JSONObject();
        final String token = getSharedPreferences("Prefs", MODE_PRIVATE).getString("tokenID", "erro");
        try {

            profileInfo.put("email", email);
            profileInfo.put("firstaddress", firstaddress);
            profileInfo.put("complementaryaddress", complementaryaddress);
            profileInfo.put("locality", locality);
            profileInfo.put("mobile_phone", mobile_phone);
            profileInfo.put("postalcode", postalcode);
            profileInfo.put("name", name);
            profileInfo.put("name", name);
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

}
