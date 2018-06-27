package pt.novaleaf.www.maisverde;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GrupoFeedActivity extends AppCompatActivity {



    MyPostRecyclerViewAdapter adapter;
    static ArrayList<Post> posts = new ArrayList<>();
    private PostFragment.OnListFragmentInteractionListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo_feed);
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
        setTitle(getIntent().getStringExtra("toolbar"));


        posts.add(new Post("oj", "BombeiroTuga", "Hoje vamos limpar o terreno da Joana",
                "dm"));

        posts.add(new Post("oj", "admin", "Hoje vamos limpar a casa da bina",
                "dm"));

        mListener = new PostFragment.OnListFragmentInteractionListener() {
            @Override
            public void onLikeInteraction(Post item) {

                if (!item.isLiked()) {
                    item.like();
                    likePostVolley(item);
                } else {
                    item.like();
                    takeLikePostVolley(item);
                }
            }

            @Override
            public void onCommentInteraction(Post item) {

                Intent intent = new Intent(GrupoFeedActivity.this, ComentariosActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFavoritoInteraction(Post item) {

            }

            @Override
            public void onImagemInteraction(Post item) {

            }
        };
        adapter = new MyPostRecyclerViewAdapter(posts, mListener);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.gruposLinear);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.grupo_feed_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.addPost) {
            Intent intent = new Intent(GrupoFeedActivity.this, AddPostActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_help) {
            return true;
        } else if (id == R.id.action_logout) {
            //TODO: sair da app
            final AlertDialog.Builder alert = new AlertDialog.Builder(GrupoFeedActivity.this);
            alert.setTitle("Abandonar grupo");
            alert
                    .setMessage("Deseja sair deste grupo? Não poderá ver mais atualizações do grupo")
                    .setCancelable(true)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            leaveGroupVolley();

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
        } else if (id == R.id.detalhes_grupo) {
            Intent intent = new Intent(GrupoFeedActivity.this, DetalhesGrupoActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void leaveGroupVolley() {

        String groupID = "id";
        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/leave_group?group_id=" + groupID;

        JSONObject grupo = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, grupo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(GrupoFeedActivity.this, GruposListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroJoingrupo", "Error: " + error.getMessage());
                Toast.makeText(GrupoFeedActivity.this, "Verifique a ligação", Toast.LENGTH_SHORT).show();
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

    private void likePostVolley(final Post item) {

        String groupID = "id";
        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/like?group_id=" + groupID+
                "&publication=" + item.getId();

        JSONObject grupo = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, grupo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                item.like();
                VolleyLog.d("erroJoingrupo", "Error: " + error.getMessage());
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


    private void takeLikePostVolley(final Post item) {

        String groupID = "id";
        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/remove_like?group_id=" + groupID+
                "&publication=" + item.getId();

        JSONObject grupo = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, grupo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                item.like();
                VolleyLog.d("erroJoingrupo", "Error: " + error.getMessage());
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
