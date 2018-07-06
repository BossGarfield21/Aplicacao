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
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

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
import java.util.concurrent.ExecutionException;

import utils.ByteRequest;

public class GrupoFeedActivity extends AppCompatActivity implements Serializable {


    static MyPostRecyclerViewAdapter adapter;
    public static ArrayList<Post> posts = new ArrayList<>();
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
        new Thread(new Runnable() {
            @Override

            public void run() {
                for (int i = 0; i < 4 && !isFinishedPosts; i++)
                    volleyGetPosts();
            }


        }).start();

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
                intent.putExtra("post", (Serializable) item);
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
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/detail_info?group_id=" + group.getGroupId();

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
                            boolean hasRequested = false;
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
                            JSONObject imag = null;
                            if (grupo.has("image_uri")) {
                                imag = grupo.getJSONObject("image_uri");
                                if (imag.has("value"))
                                    image_uri = imag.getString("value");
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

                            if (grupo.has("admins")) {
                                JSONArray ads = grupo.getJSONArray("admins");
                                for (int i = 0; i < ads.length(); i++)
                                    admins.add(ads.getString(i));
                            }

                            if (grupo.has("base_users")) {
                                JSONArray base = grupo.getJSONArray("base_users");
                                for (int i = 0; i < base.length(); i++)
                                    base_users.add(base.getString(i));
                            }

                            if (grupo.has("hasRequested")) {
                                hasRequested = grupo.getBoolean("hasRequested");
                            }


                            Log.d("name", name);
                            Log.d("groupId", groupId);
                            Log.d("tao crl", admins.get(0));
                            //Log.d("tao crl" , privacy);


                            novoGrupo = new Grupo(name, base_users, admins, points,
                                    creationDate, image_uri, groupId, privacy, distrito, isAdmin, isMember,
                                    numbMembers, hasRequested);


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
            Intent intent = new Intent(GrupoFeedActivity.this, GruposActivity.class);
            intent.putExtra("grupo", novoGrupo);
            intent.putExtra("isMember", true);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void leaveGroupVolley() {

        String groupID = "id";
        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/leave_group?group_id=" + groupID;

        JSONObject grupo = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);

    }

    private void likePostVolley(final Post item) {

        String groupID = "id";
        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/like?group_id=" + group.getGroupId() +
                "&publication=" + item.getId();

        JSONObject grupo = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/remove_like?group_id=" + group.getGroupId() +
                "&publication=" + item.getId();

        JSONObject grupo = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/feed?group_id=" +
                    group.getGroupId() + "&cursor=startquery";
        else
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/feed?cursor=" + cursorPosts;

        Log.d("ché bate só", url);

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");

        final RequestFuture<JSONObject> future = RequestFuture.newFuture();
        final JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
                future, future) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };
        future.setRequest(jsonObjectRequest1);


        jsonObjectRequest1.setTag(tag_json_obj);
        AppController.getInstance().addToRequestQueue(jsonObjectRequest1);

        try {
            final JSONObject response = future.get();
            cursorPosts = response.getString("cursor");
            Log.d("SUA PUTA TAS AI???", response.toString());
            final JSONArray list = response.getJSONArray("list");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONArray list = response.getJSONArray("list");
                        if (!response.isNull("list"))
                            if (!isFinishedPosts) {
                                isFinishedPosts = response.getBoolean("isFinished");
                                Log.d("ACABOU???", String.valueOf(isFinishedPosts));

                                for (int i = 0; i < list.length(); i++) {

                                    String id = null;
                                    String author = null;
                                    String message = null;
                                    String image = null;
                                    String user_image = null;
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
                                    if (post.has("likes"))
                                        likes = post.getLong("likes");
                                    if (post.has("liked"))
                                        liked = post.getBoolean("liked");

                                    if (post.has("likers")) {
                                        JSONArray lik = post.getJSONArray("likers");
                                        for (int a = 0; a < lik.length(); a++)
                                            likers.add(lik.getString(a));
                                    }

                                    JSONObject imag = null;
                                    if (post.has("image")) {
                                        imag = post.getJSONObject("image");
                                        if (imag.has("value"))
                                            image = imag.getString("value");
                                    }

                                    JSONObject imageuser = null;
                                    if (post.has("user_image")) {
                                        imageuser = post.getJSONObject("user_image");
                                        if (imageuser.has("value"))
                                            user_image = imageuser.getString("value");
                                    }

                                    if (post.has("comments")) {
                                        JSONArray coms = post.getJSONArray("comments");


                                        for (int a = 0; a < coms.length(); a++) {
                                            int origem;
                                            JSONObject com = coms.getJSONObject(a);
                                            if (com.getString("author").equals(
                                                    getSharedPreferences("Prefs", MODE_PRIVATE).getString("username", "")))
                                                origem = 1;
                                            else origem = 2;

                                            String comentImage = null;
                                            JSONObject im = null;
                                            if (post.has("image")) {
                                                im = post.getJSONObject("image");
                                                if (im.has("value"))
                                                    comentImage = im.getString("value");
                                            }


                                            String comentID = com.getString("id");


                                            comments.put(comentID, new Comentario(comentID, com.getString("author"),
                                                    com.getString("message"), comentImage,
                                                    com.getLong("creation_date"), origem, null, id, group.getGroupId()));

                                        }
                                    }

//                                    Log.d("POST???", id);


                                    Post post1 = new Post(id, author, message, image, likers, comments, likes, liked, group.getGroupId(), user_image);

                                    if (post1.getImage() != null && !posts.contains(post1))
                                        receberImagemVolley(post1);
                                    if (post1.getUser_image() != null && !posts.contains(post1))
                                        receberImagemUserVolley(post1);



                                    if (!posts.contains(post1))
                                        posts.add(post1);





                                    adapter.notifyDataSetChanged();


                                }
                            }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void receberImagemVolley(final Post item) {
        String tag_json_obj = "octect_request";
        String url = item.getImage();


        final String token = getSharedPreferences("Prefs", MODE_PRIVATE).getString("tokenID", "erro");
        ByteRequest stringRequest = new ByteRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {

            @Override
            public void onResponse(byte[] response) {
                item.setBitmap(response);
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroIMAGEMGrupopost", "Error: " + error.getMessage());
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


    private void receberImagemUserVolley(final Post item) {
        String tag_json_obj = "octect_request";
        String url = item.getUser_image();


        final String token = getSharedPreferences("Prefs", MODE_PRIVATE).getString("tokenID", "erro");
        ByteRequest stringRequest = new ByteRequest(Request.Method.GET, url, new Response.Listener<byte[]>() {

            @Override
            public void onResponse(byte[] response) {
                item.setBitmapUser(response);
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erroIMAGEMocorrencia", "Error: " + error.getMessage());
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
