package pt.novaleaf.www.maisverde;

import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OcorrenciaActivity extends AppCompatActivity implements Serializable {

    private TextView mTitulo;
    private TextView mLocal;
    private TextView mEventos;
    private TextView mData;
    private TextView mTexto;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocorrencia);

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


        Ocorrencia ocorrencia = (Ocorrencia) getIntent().getSerializableExtra("Ocorrencia");


        mTitulo = (TextView) findViewById(R.id.ocorrenciaTitulo);
        mData = (TextView) findViewById(R.id.ocorrenciaData);
        mTexto = (TextView) findViewById(R.id.ocorrenciaTexto);
        mLocal = (TextView) findViewById(R.id.ocorrenciaLocal);
        mEventos = (TextView) findViewById(R.id.ocorrenciaEventos);
        mImage = (ImageView) findViewById(R.id.ocorrenciaImagem);

        setTitle(ocorrencia.getName());

        mTitulo.setText(ocorrencia.getName());
        long time = ocorrencia.getCreationDate();

        if (time != 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm");
            String data = simpleDateFormat.format(new Date(time));
            mData.setText(data);
        }

        mTexto.setText(ocorrencia.getDescription());


        if (ocorrencia.getImage_uri() == null && ocorrencia.getBitmap() == null)
            mImage.setImageResource(ocorrencia.getImageID());
        else
            mImage.setImageBitmap(BitmapFactory.decodeByteArray(ocorrencia.getBitmap(), 0, ocorrencia.getBitmap().length));

    }
}
