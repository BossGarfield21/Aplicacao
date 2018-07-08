package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CriarGrupoActivity extends AppCompatActivity implements Serializable {

    private Button mButtonCriarGrupo;
    private Button mButtonDistrito;
    private AutoCompleteTextView mNomeGrupo;
    private Switch mPrivacidade;
    private PopupMenu popup = null;
    private String distrito = "";
    LinearLayout linearLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_grupo);
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

        mButtonCriarGrupo = (Button) findViewById(R.id.btn_criar_grupo);
        mButtonDistrito = (Button) findViewById(R.id.bDistritos);
        mNomeGrupo = (AutoCompleteTextView) findViewById(R.id.input_group);
        mPrivacidade = (Switch) findViewById(R.id.switchGoups);
        linearLayout = (LinearLayout) findViewById(R.id.linearCriarGrupo);

        mButtonCriarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateGroup();
            }
        });

        mButtonDistrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMenu(findViewById(R.id.bDistritos));
            }
        });


    }

    private void setUncheckedMenu(PopupMenu menu, MenuItem item) {

        for (int i = 0; i < menu.getMenu().size(); i++) {
            if (!menu.getMenu().getItem(i).equals(item)) {
                menu.getMenu().getItem(i).setCheckable(false);
                menu.getMenu().getItem(i).setChecked(false);
            }
        }

    }


    public void showMenu(View v) {
        if (popup == null) {
            popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    item.setCheckable(true);
                    item.setChecked(!item.isChecked());
                    setUncheckedMenu(popup, item);
                    mButtonDistrito.setText(item.getTitle());
                    mButtonDistrito.setError(null);
                    distrito = item.getTitle().toString().toLowerCase();

                    return false;
                }
            });// to implement on click event on items of menu
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.distritos_menu, popup.getMenu());
            popup.getMenu().getItem(0).setVisible(false);
        }
        popup.show();
    }


    private void attemptCreateGroup() {

        String nomeGrupo = mNomeGrupo.getText().toString();

        boolean isPrivado = mPrivacidade.isChecked();

        mNomeGrupo.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nomeGrupo)) {
            mNomeGrupo.setError("O nome tem de ter pelo menos 4 caracteres");
            focusView = mNomeGrupo;
            cancel = true;
        } else if (TextUtils.isEmpty(distrito)) {
            mButtonDistrito.setError("Escolha um distrito");
            mButtonDistrito.setText("Escolha um distrito");
            focusView = mButtonDistrito;
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
            String id = UUID.randomUUID().toString();

            //enviarImagemVolley(imageBytes, id);
            criarGrupoVolley(nomeGrupo, privacy);
        }
    }

    private void criarGrupoVolley(String nomeGrupo, String privacy) {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/create?group=" + nomeGrupo + "&privacy=" +
                privacy + "&district=" + distrito;

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            String name = null;
                            long numbMembers = 0;
                            long points = 0;
                            long creationDate = 0;
                            String groupId = null;
                            String privacy = null;
                            String image_uri = null;
                            String distrito = null;
                            boolean isMember = false;
                            boolean isAdmin = false;
                            boolean hasRequested = false;

                            JSONObject grupo = response;
                            if (grupo.has("groupId"))
                                groupId = grupo.getString("groupId");
                            if (grupo.has("name"))
                                name = grupo.getString("name");
                            if (grupo.has("creationDate"))
                                creationDate = grupo.getLong("creationDate");
                            if (grupo.has("points"))
                                points = grupo.getLong("points");
                            JSONObject image = null;
                            if (grupo.has("image_uri")) {
                                image = grupo.getJSONObject("image_uri");
                                if (image.has("value"))
                                    image_uri = image.getString("value");
                            }
                            if (grupo.has("groupId"))
                                groupId = grupo.getString("groupId");
                            if (grupo.has("privacy"))
                                privacy = grupo.getString("privacy");
                            if (grupo.has("district"))
                                distrito = grupo.getString("district");


                            if (grupo.has("isAdmin")) {
                                isAdmin = grupo.getBoolean("isAdmin");
                            }
                            if (grupo.has("isMember")) {
                                isMember = grupo.getBoolean("isMember");
                            }
                            if (grupo.has("numbMembers")) {
                                numbMembers = grupo.getLong("numbMembers");
                            }
                            if (grupo.has("hasRequested")) {
                                hasRequested = grupo.getBoolean("hasRequested");
                            }

                            Log.d("name", name);
                            Log.d("groupId", groupId);
//                                    Log.d("privacy", privacy);

                            Grupo grupo1 = new Grupo(name, null, null, points,
                                    creationDate, image_uri, groupId, privacy, distrito, isAdmin, isMember,
                                    numbMembers, hasRequested);
                            GruposListActivity.grupos.add(grupo1);
                            GruposListActivity.adapter.notifyDataSetChanged();

                            Intent i = new Intent(CriarGrupoActivity.this, GruposListActivity.class);
                            startActivity(i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CriarGrupoActivity.this, "Erro", Toast.LENGTH_SHORT).show();
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

    }


}
