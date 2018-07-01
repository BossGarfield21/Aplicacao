package pt.novaleaf.www.maisverde;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ComentariosActivity extends AppCompatActivity implements Serializable {
    private RecyclerView mMessageRecycler;
    public static MyComentariosRecyclerViewAdapter mMessageAdapter;
    public static List<Comentario> comentarios;
    private Button bEnviar;
    private EditText mComentario;
    private TextView mTextTitulo;
    private TextView mTextLikes;
    private Ocorrencia ocorrencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);


        final SharedPreferences preferences = getSharedPreferences("Prefs", MODE_PRIVATE);

        mTextLikes = (TextView) findViewById(R.id.textLikes);
        mTextTitulo = (TextView) findViewById(R.id.textTitulo);

        bEnviar = (Button) findViewById(R.id.button_chatbox_send);
        mComentario = (EditText) findViewById(R.id.edittext_chatbox);
        bEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comentario = mComentario.getText().toString();
                if (!isStringNullOrWhiteSpace(comentario)) {
                    long time = System.currentTimeMillis();
                    String image = getSharedPreferences("Prefs", MODE_PRIVATE).getString("image_user", null);

                    Comentario com = new Comentario(UUID.randomUUID().toString().concat(String.valueOf(time)),
                            preferences.getString("username", "desconhecido"), comentario, image, time, 1, ocorrencia.getId());
                    comentarios.add(com);
                    int a = OcorrenciaFragment.listOcorrencias.indexOf(ocorrencia);
                    OcorrenciaFragment.listOcorrencias.get(a).addComentario(com);
                    OcorrenciaFragment.myOcorrenciaRecyclerViewAdapter.notifyDataSetChanged();
                    mMessageAdapter.notifyDataSetChanged();
                    ocorrencia.addComentario(com);

                    volleyAdicionarComentario(com);
                    View v = getCurrentFocus();
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        mComentario.setText("");
                        mComentario.clearFocus();
                    }
                }

            }
        });


        ocorrencia = (Ocorrencia) getIntent().getSerializableExtra("comentarios");
        comentarios = new ArrayList<>(ocorrencia.getComments().values());

        Collections.sort(comentarios, new Comparator<Comentario>() {
            @Override
            public int compare(Comentario comentario, Comentario t1) {
                if (comentario.getCreation_date() < t1.getCreation_date())
                    return -1;
                else
                    return 1;
            }

        });

        mTextTitulo.setText(ocorrencia.getName());
        long likes = ocorrencia.getLikes();
        if (likes != 1)
            mTextLikes.setText(String.format("%d likes", likes));
        else
            mTextLikes.setText(String.format("%d like", likes));

        //comentarios = new ArrayList<>();
        //comentarios.add(new Comentario("tao isso ja ta limpo?", "bombeirotuga", "tao isso ja ta limpo?", "", 2, 1));
        //comentarios.add(new Comentario("nepia puto", "macambuzio", "05:29", "", 1, 2));
        //comentarios.add(new Comentario("chama os bombeiros", "bombeirotuga", "chama os bombeiros", "", 2, 1));

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MyComentariosRecyclerViewAdapter(this, comentarios);
        Log.e("numero de cenas ", " " + mMessageAdapter.getItemCount());
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        mMessageRecycler.getAdapter().getItemId(R.id.text_message_body);


    }

    private void volleyAdicionarComentario(Comentario com) {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/social/addcomment?markerid="
                + ocorrencia.getId() + "commentid=" + com.getId();

        JSONObject grupo = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");
        try {

            grupo.put("id", com.getAuthor());
            grupo.put("author", com.getAuthor());
            grupo.put("message", com.getMessage());
            grupo.put("image", com.getImage());
            grupo.put("creation_date", com.getCreation_date());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, grupo,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ComentariosActivity.this, "Falhou o envio", Toast.LENGTH_SHORT).show();
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

    public static boolean isStringNullOrWhiteSpace(String value) {
        if (value == null) {
            return true;
        }

        for (int i = 0; i < value.length(); i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }

        return true;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
