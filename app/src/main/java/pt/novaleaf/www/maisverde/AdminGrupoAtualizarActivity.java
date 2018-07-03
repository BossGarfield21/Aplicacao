package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminGrupoAtualizarActivity extends AppCompatActivity implements Serializable {

    private Button bEscolher;
    private TextInputEditText mNomeGrupo;
    private Button mSubmeter;
    private Switch mSwitch;
    private ImageView mImage;
    private Grupo grupo;
    static final int PICK_IMAGE = 2;
    byte[] imageBytes = null;
    private boolean isImage = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_grupo_atualizar);
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


        bEscolher = (Button) findViewById(R.id.bEscolherImagem);
        mSubmeter = (Button) findViewById(R.id.btn_mudar);
        mNomeGrupo = (TextInputEditText) findViewById(R.id.alterarNome);
        mSwitch = (Switch) findViewById(R.id.switchGoups);
        mImage = (ImageView) findViewById(R.id.imageMudar);

        mNomeGrupo.setText(grupo.getName());

        bEscolher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);


                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        mSubmeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNomeGrupo.setError(null);
                if (!TextUtils.isEmpty(mNomeGrupo.getText().toString())) {
                    if (imageBytes != null) {
                        String id = UUID.randomUUID().toString().concat(String.valueOf(System.currentTimeMillis()));
                        enviarImagemVolley(imageBytes, id);
                        volleyUpdateGrupo(id);
                    } else
                        volleyUpdateGrupo(null);
                } else
                    mNomeGrupo.setError("Nome do grupo não pode estar vazio");


            }
        });

        /*
        if (grupo.getPrivacy().equals("public"))
            mSwitch.setChecked(false);
        else
            mSwitch.setChecked(true);
        */

    }

    private void volleyUpdateGrupo(String id) {
        String tag_json_obj = "json_request";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/gadmin/update";

        Log.d("ché bate só", url);

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final JSONObject grupo = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");
        final String privaci;
        boolean privacy = mSwitch.isChecked();
        if (privacy)
            privaci= "private";
        else
            privaci = "public";

        try {


            grupo.put("groupId", this.grupo.getGroupId());
            grupo.put("privacy", privaci);
            grupo.put("name", mNomeGrupo.getText().toString());
            if (id!=null)
                grupo.put("image_uri", "https://novaleaf-197719.appspot.com/gcs/novaleaf-197719.appspot.com/"+id);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, grupo,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        int index = GruposListActivity.grupos.indexOf(grupo);
                        GruposListActivity.grupos.get(index).setName(mNomeGrupo.getText().toString());

                        GruposListActivity.grupos.get(index).setPrivacy(privaci);
                        GruposListActivity.adapter.notifyDataSetChanged();

                        Toast.makeText(AdminGrupoAtualizarActivity.this, "Alterações efetuadas", Toast.LENGTH_SHORT).show();
                    }

                }, new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminGrupoAtualizarActivity.this, "Verifique a ligação", Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    mImage.setImageBitmap(bitmap);

                    isImage = true;
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/7, bitmap.getHeight()/7, true);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);

                    imageBytes = bao.toByteArray();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
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

                int index = GruposListActivity.grupos.indexOf(grupo);
                GruposListActivity.grupos.get(index).setBitmap(imageBytes);
                GruposListActivity.adapter.notifyDataSetChanged();


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



}
