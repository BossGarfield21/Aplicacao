package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import utils.ByteRequest;

public class GruposActivity extends AppCompatActivity implements Serializable {

    private Button mButtonPedido;
    private TextView mTextNumPessoas;
    private TextView mDistrito;
    private TextView mPontos;
    private ImageView mImage;
    Grupo grupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);
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


        if (grupo != null)
            setTitle(grupo.getName());
        else
            setTitle("Sem ligação");

        mButtonPedido = (Button) findViewById(R.id.buttonPedido);
        //mButtonCancelar = (Button) findViewById(R.id.buttonCancelar);
        mTextNumPessoas = (TextView) findViewById(R.id.textGrupoPessoas);
        mDistrito = (TextView) findViewById(R.id.textGrupoDistrito);
        mPontos = (TextView) findViewById(R.id.textGrupoPontos);
        mImage = (ImageView) findViewById(R.id.fragmentevent_img);


        long pontos = 0;
        if (grupo != null)
            pontos = grupo.getPoints();
        if (pontos != 1)
            mPontos.setText(String.format("%d pontos", pontos));
        else
            mPontos.setText(String.format("%d ponto", pontos));


        if (getIntent().getBooleanExtra("isMember", false)) {
            mButtonPedido.setVisibility(View.GONE);

        }


        if (grupo != null)
            if (grupo.getImage_uri() != null) {
                receberImagemVolley();
            } else {
                mImage.setImageResource(R.drawable.ic_people_black_24dp);
            }
        if (grupo != null)
            mDistrito.setText(String.format("%s", grupo.getDistrito().toUpperCase()));

        if (grupo != null)
            if (grupo.getNumPessoas() > 1)
                mTextNumPessoas.setText(String.format("%d pessoas", grupo.getNumPessoas()));
            else
                mTextNumPessoas.setText(String.format("%d pessoa", grupo.getNumPessoas()));

        if (grupo != null)
            if (grupo.isEnviouPedido())
                mButtonPedido.setText("Cancelar pedido");

        mButtonPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mButtonPedido.getText().equals("Enviar pedido"))
                    joinGroupVolley();
                else
                    cancelJoinGroupVolley();
            }
        });
/**
 if (grupo.getBitmap() != null) {
 mImage.setImageBitmap(BitmapFactory.decodeByteArray(grupo.getBitmap(), 0, grupo.getBitmap().length));
 } else {
 mImage.setImageResource(R.drawable.ic_people_black_24dp);
 }*/
    }

    private void cancelJoinGroupVolley() {

        String groupID = "id";
        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/cancel_request?group_id=" + groupID;

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GruposListActivity.grupos.get(GruposListActivity.grupos.indexOf(grupo)).setEnviouPedido(false);

                        mButtonPedido.setText("Enviar pedido");

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mButtonPedido.setText("Cancelar pedido");
                VolleyLog.d("erroJoingrupo", "Error: " + error.getMessage());
                Toast.makeText(GruposActivity.this, "Verifique a ligação", Toast.LENGTH_SHORT).show();
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

    private void joinGroupVolley() {

        String groupID = "id";
        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/request?group_id=" + grupo.getGroupId();


        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        GruposListActivity.grupos.get(GruposListActivity.grupos.indexOf(grupo)).setEnviouPedido(true);
                        mButtonPedido.setText("Cancelar pedido");

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mButtonPedido.setText("Enviar pedido");
                VolleyLog.d("erroJoingrupo", "Error: " + error.getMessage());
                Toast.makeText(GruposActivity.this, "Verifique a ligação", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detalhes_grupo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_help) {
            return true;
        } else if (id == R.id.action_logout) {
            //TODO: sair da app
            final AlertDialog.Builder alert = new AlertDialog.Builder(GruposActivity.this);
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
                            Intent intent = new Intent(GruposActivity.this, LoginActivity.class);
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

    private void receberImagemVolley() {
        String tag_json_obj = "octect_request";
        String url = grupo.getImage_uri();


        final String token = getSharedPreferences("Prefs", MODE_PRIVATE).getString("tokenID", "erro");
        ByteRequest stringRequest = new ByteRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {

            @Override
            public void onResponse(byte[] response) {
                mImage.setImageBitmap(BitmapFactory.decodeByteArray(response, 0, response.length));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroIMAGEMocorrencia", "Error: " + error.getMessage());

                mImage.setImageResource(R.drawable.ic_people_black_24dp);

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
