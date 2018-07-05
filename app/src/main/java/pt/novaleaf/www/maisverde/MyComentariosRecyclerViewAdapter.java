package pt.novaleaf.www.maisverde;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static pt.novaleaf.www.maisverde.ComentariosActivity.comentarios;
import static pt.novaleaf.www.maisverde.ComentariosActivity.mMessageAdapter;

/**
 * Created by Hugo Mochão on 16/05/2018.
 */

public class MyComentariosRecyclerViewAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Comentario> mMessageList;

    public MyComentariosRecyclerViewAdapter(Context context, List<Comentario> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Comentario comentario = mMessageList.get(position);
        Log.i("ORIGEM", comentario.getOrigem() + " posiçao");

        if (comentario.getOrigem() == 1) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comentarioenviado, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comentarionormal, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Comentario comentario = mMessageList.get(position);


        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(comentario, position);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(comentario, position);
        }
    }

    class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ConstraintLayout mConstr;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            mConstr = (ConstraintLayout) itemView.findViewById(R.id.constrComentario);

        }

        void bind(final Comentario comentario, final int position) {
            messageText.setText(comentario.getMessage());
            messageText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    popupEliminarComentario(view, position);
                    return true;
                }
            });

            // Format the stored timestamp into a readable String using method.
            long time = comentarios.get(position).getCreation_date();

            if (time != 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm");
                String data = simpleDateFormat.format(new Date(time));
                timeText.setText(data);
            }


        }

    }

    private void popupEliminarComentario(View v, final int position) {
        PopupMenu popup = new PopupMenu(mContext, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                if (comentarios.get(position).getMarkerid() != null)
                    volleyEliminarComentarioOcorrencia(comentarios.get(position));
                else
                    volleyEliminarComentarioPost(comentarios.get(position));
                return false;
            }
        });// to implement on click event on items of menu
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.eliminar_comentario_menu, popup.getMenu());
        popup.show();
    }


    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Comentario comentario, int position) {
            messageText.setText(comentario.getMessage());

            // Format the stored timestamp into a readable String using method.
            long time = comentarios.get(position).getCreation_date();

            if (time != 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm");
                String data = simpleDateFormat.format(new Date(time));
                timeText.setText(data);
            }
            nameText.setText(comentario.getAuthor());

        }
    }

    private void volleyEliminarComentarioOcorrencia(final Comentario com) {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/social/removecomment?markerid="
                + com.getMarkerid() + "&commentid=" + com.getId();


        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        comentarios.remove(com);
                        notifyDataSetChanged();
                        OcorrenciaFragment.myOcorrenciaRecyclerViewAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Erro de ligação", Toast.LENGTH_SHORT).show();
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

    private void volleyEliminarComentarioPost(final Comentario com) {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/groups/member/comment?group_id="
                + com.getGroupId() + "&publication_id=" + com.getPostId() + "&comment_id=" + com.getId();

        JSONObject grupo = new JSONObject();

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String >() {
                    @Override
                    public void onResponse(String  response) {
                        comentarios.remove(com);
                        notifyDataSetChanged();
                        OcorrenciaFragment.myOcorrenciaRecyclerViewAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Erro de ligação", Toast.LENGTH_SHORT).show();
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

