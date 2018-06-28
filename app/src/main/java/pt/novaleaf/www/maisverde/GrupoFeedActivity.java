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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GrupoFeedActivity extends AppCompatActivity implements Serializable {


    MyPostRecyclerViewAdapter adapter;
    private static ArrayList<Post> posts = new ArrayList<>();
    private PostFragment.OnListFragmentInteractionListener mListener;
    private boolean isFinishedPosts = false;
    private String cursorPosts = "";
    Grupo group;
    Grupo novoGrupo;


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

        group = (Grupo) getIntent().getSerializableExtra("grupo");

        setTitle(group.getName());

        volleyGetGrupo();

        //group = (Grupo) getIntent().getSerializableExtra("grupo");



        //volleyGetPosts();

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

    private void volleyGetGrupo() {

        String tag_json_obj = "json_request";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/detail_info?group_id=" + group.getGroupId();

        Log.d("ché bate só", url);

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        JSONObject eventos = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, eventos,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("tao DRED", response.toString());

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
                            List<String> admins = new ArrayList<>();
                            List<String> base_users = new ArrayList<>();

                            JSONObject grupo = response;
                            if (grupo.has("groupId"))
                                groupId = grupo.getString("groupId");
                            if (grupo.has("name"))
                                name = grupo.getString("name");
                            if (grupo.has("creationDate"))
                                creationDate = grupo.getLong("creationDate");
                            if (grupo.has("points"))
                                points = grupo.getLong("points");
                            if (grupo.has("image_uri"))
                                image_uri = grupo.getString("image_uri");
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

                            if (grupo.has("admins")){
                                JSONArray ads = grupo.getJSONArray("admins");
                                for (int i = 0; i< ads.length(); i++)
                                    admins.add(ads.getString(i));
                            }

                            if (grupo.has("base_users")){
                                JSONArray base = grupo.getJSONArray("base_users");
                                for (int i = 0; i< base.length(); i++)
                                    base_users.add(base.getString(i));
                            }


                            Log.d("name", name);
                            Log.d("groupId", groupId);
                            Log.d("tao crl" , admins.get(0));


                            novoGrupo = new Grupo(name, base_users, admins, points,
                                    creationDate, image_uri, groupId, privacy, distrito, isAdmin, isMember,
                                    numbMembers);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroDETAILED", "Error: " + error.getMessage());
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
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (group.isAdmin())
            getMenuInflater().inflate(R.menu.grupos_admin, menu);
        else
            getMenuInflater().inflate(R.menu.grupo_feed_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.admin_grupo) {
            Intent intent = new Intent(GrupoFeedActivity.this, AdministrarGrupoActivity.class);
            intent.putExtra("grupo", novoGrupo);
            startActivity(intent);

        } else if (id == R.id.addPost) {
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
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/like?group_id=" + group.getGroupId() +
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

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/remove_like?group_id=" + group.getGroupId() +
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


    private void volleyGetPosts() {
        String tag_json_obj = "json_request";
        String url;
        if (cursorPosts.equals(""))
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/feed?group_id=" +
                    group.getGroupId() + "&cursor=startquery";
        else
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/feed?cursor=" + cursorPosts;

        Log.d("ché bate só", url);

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        JSONObject eventos = new JSONObject();
        final String token = sharedPreferences.getString("tokenID", "erro");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, eventos,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("nabo", cursorPosts + "pixa");
                            cursorPosts = response.getString("cursor");
                            Log.d("nabo", cursorPosts);


                            JSONArray list = response.getJSONArray("list");
                            if (!isFinishedPosts) {
                                isFinishedPosts = response.getBoolean("isFinished");
                                Log.d("ACABOU???", String.valueOf(isFinishedPosts));

                                for (int i = 0; i < list.length(); i++) {

                                    String id = null;
                                    String author = null;
                                    String message = null;
                                    String image = null;
                                    List<String> likers = new ArrayList<>();
                                    Map<String, Comentario> comments = new HashMap<>();
                                    long likes = 0;
                                    boolean liked = false;

                                    JSONObject post = list.getJSONObject(i);
                                    if (post.has("id"))
                                        id = post.getString("id");
                                    if (post.has("author"))
                                        author = post.getString("author");
                                    if (post.has("message"))
                                        message = post.getString("message");
                                    if (post.has("image"))
                                        message = post.getString("image");
                                    if (post.has("likes"))
                                        likes = post.getLong("likes");
                                    if (post.has("liked"))
                                        liked = post.getBoolean("liked");

                                    if (post.has("likers")) {
                                        JSONArray lik = post.getJSONArray("likers");
                                        for (int a = 0; a < lik.length(); a++)
                                            likers.add(lik.getString(a));
                                    }

                                    if (post.has("comments")) {
                                        JSONObject coms = post.getJSONObject("comments");

                                        Iterator<String> comentario = coms.keys();
                                        while (comentario.hasNext()) {
                                            String comentID = comentario.next();
                                            int origem = 1;
                                            JSONObject com = coms.getJSONObject(comentID);
                                            if (com.getString("author").equals(
                                                    getSharedPreferences("Prefs", MODE_PRIVATE).getString("username", "")))
                                                origem = 2;
                                            else origem = 1;
                                            comments.put(comentID, new Comentario(comentID, com.getString("author"),
                                                    com.getString("message"), com.getString("image"),
                                                    com.getLong("creationDate"), origem));

                                        }
                                    }

//                                    Log.d("POST???", id);


                                    if (id != null) {
                                        Post post1 = new Post(id, author, message, image, likers, comments, likes, liked);
                                        if (!posts.contains(post1))
                                            posts.add(post1);

                                        adapter.notifyDataSetChanged();
                                    }

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    }

}
