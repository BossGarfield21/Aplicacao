package pt.novaleaf.www.maisverde;

import android.content.Context;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    public static MyComentariosRecyclerViewAdapter mMessageAdapter;
    public static List<Comentario> comentarios;
    private Button bEnviar;
    private EditText mComentario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);


        final SharedPreferences preferences = getSharedPreferences("Prefs", MODE_PRIVATE);

        bEnviar = (Button) findViewById(R.id.button_chatbox_send);
        mComentario = (EditText) findViewById(R.id.edittext_chatbox);
        bEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comentario = mComentario.getText().toString();
                if (!TextUtils.isEmpty(comentario)){
                    Calendar calendar= Calendar.getInstance();
                    SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
                    Comentario com = new Comentario(comentario,
                            preferences.getString("username", "desconhecido"),mdformat.format(calendar.getTime()),1);
                    comentarios.add(com);
                    mMessageAdapter.notifyDataSetChanged();
                    View v = getCurrentFocus();
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        mComentario.setText("");
                        mComentario.clearFocus();
                    }
                } else{
                    Toast.makeText(ComentariosActivity.this, "Comentário vazio", Toast.LENGTH_SHORT).show();
                }

            }
        });

        comentarios = new ArrayList<>();
        comentarios.add(new Comentario("tao isso ja ta limpo?", "bombeirotuga", "03:49", 1));
        comentarios.add(new Comentario("nepia puto", "macambuzio", "05:29", 2));
        comentarios.add(new Comentario("chama os bombeiros", "bombeirotuga", "17:22", 1));

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MyComentariosRecyclerViewAdapter(this, comentarios);
        Log.e("numero de cenas " , " " +mMessageAdapter.getItemCount());
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
