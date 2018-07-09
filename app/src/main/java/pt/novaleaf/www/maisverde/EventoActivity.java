package pt.novaleaf.www.maisverde;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EventoActivity extends AppCompatActivity {

    private TextView mDia;
    private TextView mMes;
    private TextView mHoras;
    private TextView mTitulo;
    private TextView mOwner;
    private TextView mPessoasVao;
    private TextView mPontoEncontro;
    private TextView mDescricao;
    private LinearLayout linearVou;
    private LinearLayout linearInteresse;
    private LinearLayout linearPessoasVao;
    private LinearLayout linearPontoEncontro;
    private ImageView imageInteresse;
    private ImageView imageVou;
    private ImageView imageEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

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

        final Evento evento = (Evento) getIntent().getSerializableExtra("evento");

        setTitle(evento.getName());

        mDia = (TextView) findViewById(R.id.textViewDia);
        mMes = (TextView) findViewById(R.id.textViewMes);
        mOwner = (TextView) findViewById(R.id.textEventoOwner);
        mPessoasVao = (TextView) findViewById(R.id.textPessoasVao);
        mHoras = (TextView) findViewById(R.id.hoursTextView);
        mPontoEncontro = (TextView) findViewById(R.id.textPontoEncontro);
        mTitulo = (TextView) findViewById(R.id.tituloEventTextView);
        mDescricao = (TextView) findViewById(R.id.textEventoDescricao);
        linearInteresse = (LinearLayout) findViewById(R.id.linearInteresse);
        linearVou = (LinearLayout) findViewById(R.id.linearVou);
        linearPessoasVao = (LinearLayout) findViewById(R.id.linearPessoasVao);
        linearPontoEncontro = (LinearLayout) findViewById(R.id.linearPontoEncontro);
        imageInteresse = (ImageView) findViewById(R.id.imageInteresse);
        imageVou = (ImageView) findViewById(R.id.imageVou);
        imageEvento = (ImageView) findViewById(R.id.imageEvento);

        mOwner.setText(MessageFormat.format("De {0}", evento.getCreator()));
        mTitulo.setText(evento.getName());
        int numPessoas = evento.getConfirmations().size();
        if (numPessoas != 1)
            mPessoasVao.setText(String.format("Vão %d pessoas", numPessoas));
        else
            mPessoasVao.setText(String.format("Vai %d pessoa", numPessoas));

        mDescricao.setText(evento.getDescription());

        final long time = evento.getMeetupDate();

        if (time != 0) {
            SimpleDateFormat simpleDateFormatMes = new SimpleDateFormat("MMM", Locale.UK);
            SimpleDateFormat simpleDateFormatDia = new SimpleDateFormat("dd", Locale.UK);
            SimpleDateFormat simpleDateFormatHora = new SimpleDateFormat("HH:mm", Locale.UK);
            String dataMes = simpleDateFormatMes.format(new Date(time));
            String dataDia = simpleDateFormatDia.format(new Date(time));
            String dataHora = simpleDateFormatHora.format(new Date(time));
            mMes.setText(dataMes.toUpperCase());
            mDia.setText(dataDia.toUpperCase());
            mHoras.setText(dataHora.toUpperCase());
        }

        if (evento.isInteresse())
            imageInteresse.setImageResource(R.drawable.ic_if_star_285661);
        if (evento.isIr())
            imageVou.setImageResource(R.drawable.ic_if_sign_check_299110);


        linearVou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evento.setIr();

                if (evento.isIr())
                    imageVou.setImageResource(R.drawable.ic_if_sign_check_299110);
                else
                    imageVou.setImageResource(R.drawable.ic_check_black_24dp);

                addConfirmationVolley(evento);
            }
        });

        linearInteresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evento.setInteresse();

                if (evento.isInteresse())
                    imageInteresse.setImageResource(R.drawable.ic_if_star_285661);

                else
                    imageInteresse.setImageResource(R.drawable.ic_star_border_black_24dp);


                addInterestVolley(evento);

            }
        });

        if (evento.getBitmap() != null)
            imageEvento.setImageBitmap(BitmapFactory.decodeByteArray(evento.getBitmap(), 0, evento.getBitmap().length));
        else
            imageEvento.setImageResource(R.drawable.ic_baseline_calendar_eventos_24px);

        LatLng latLng = new LatLng(evento.getMeetupPointLatitude(), evento.getMeetupPointLongitude());

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            String locality = addresses.get(0).getLocality();
            String adminarea = addresses.get(0).getAdminArea();
            mPontoEncontro.setText(String.format("%s, %s", locality, adminarea));
        }

        linearPessoasVao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(EventoActivity.this, view);
                for (String pessoa : evento.getConfirmations())
                    popupMenu.getMenu().add(pessoa);
                popupMenu.show();
            }
        });

    }


    private void addInterestVolley(final Evento item) {


        String tag_json_obj = "json_obj_req";
        String url;
        int method;
        if (item.isInteresse()) {
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/events/newinterest?event=" + item.getId();
            method = Request.Method.PUT;
        } else {
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/events/removeinterest?event=" + item.getId();
            method = Request.Method.DELETE;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");

        // StringRequest stringRequest = new StringRequest()

        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                int indexFeed = FeedEventosActivity.eventosList.indexOf(item);
                if (indexFeed != -1) {
                    FeedEventosActivity.eventosList.get(indexFeed).setInteresse();
                    FeedEventosActivity.adapter.notifyDataSetChanged();
                }
                int indexProximos = ProximosEventosActivity.proximosEventosList.indexOf(item);
                if (indexProximos != -1) {
                    ProximosEventosActivity.proximosEventosList.get(indexProximos).setInteresse();
                    ProximosEventosActivity.adapterProximos.notifyDataSetChanged();
                }

                if (ProximosLocalActivity.adapterProximosLocal != null)
                    ProximosLocalActivity.adapterProximosLocal.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EventoActivity.this, "Erro", Toast.LENGTH_SHORT).show();

                item.setInteresse();
                if (item.isInteresse()) {
                    imageInteresse.setImageResource(R.drawable.ic_if_star_285661);
                } else {
                    imageInteresse.setImageResource(R.drawable.ic_star_border_black_24dp);
                }
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

    private void addConfirmationVolley(final Evento item) {


        String tag_json_obj = "json_obj_req";
        String url;
        int method;
        if (item.isIr()) {
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/events/newconfirmation?event=" + item.getId();
            method = Request.Method.PUT;
        } else {
            url = "https://novaleaf-197719.appspot.com/rest/withtoken/events/removeconfirmation?event=" + item.getId();
            method = Request.Method.DELETE;
        }

        final SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        final String token = sharedPreferences.getString("tokenID", "erro");


        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                int indexFeed = FeedEventosActivity.eventosList.indexOf(item);
                int indexProximos = ProximosEventosActivity.proximosEventosList.indexOf(item);

                if (item.isIr()) {
                    item.getConfirmations().add(sharedPreferences.getString("username", "erro"));
                    if (indexFeed != -1)
                        FeedEventosActivity.eventosList.get(FeedEventosActivity.eventosList.indexOf(item))
                                .getConfirmations().add(sharedPreferences.getString("username", "erro"));
                    if (indexProximos != -1)
                        ProximosEventosActivity.proximosEventosList.get(indexProximos)
                                .getConfirmations().add(sharedPreferences.getString("username", "erro"));
                } else {
                    item.getConfirmations().remove(sharedPreferences.getString("username", "erro"));
                    if (indexFeed != -1)
                        FeedEventosActivity.eventosList.get(FeedEventosActivity.eventosList.indexOf(item))
                                .getConfirmations().remove(sharedPreferences.getString("username", "erro"));
                    if (indexProximos != -1)
                        ProximosEventosActivity.proximosEventosList.get(indexProximos)
                                .getConfirmations().remove(sharedPreferences.getString("username", "erro"));
                }

                int numPessoas = item.getConfirmations().size();
                if (numPessoas != 1)
                    mPessoasVao.setText(String.format("Vão %d pessoas", numPessoas));
                else
                    mPessoasVao.setText(String.format("Vai %d pessoa", numPessoas));

                if (indexFeed != -1) {
                    FeedEventosActivity.eventosList.get(indexFeed).setIr();
                    if (FeedEventosActivity.adapter != null)
                        FeedEventosActivity.adapter.notifyDataSetChanged();
                }


                if (indexProximos != -1) {
                    ProximosEventosActivity.proximosEventosList.get(indexProximos).setIr();
                    if (ProximosEventosActivity.adapterProximos != null)
                        ProximosEventosActivity.adapterProximos.notifyDataSetChanged();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EventoActivity.this, "Erro", Toast.LENGTH_SHORT).show();

                item.setIr();
                if (item.isIr())
                    imageVou.setImageResource(R.drawable.ic_if_sign_check_299110);
                else
                    imageVou.setImageResource(R.drawable.ic_check_black_24dp);
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
