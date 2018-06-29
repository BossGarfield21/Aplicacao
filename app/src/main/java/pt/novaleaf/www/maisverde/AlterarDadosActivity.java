package pt.novaleaf.www.maisverde;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class AlterarDadosActivity extends AppCompatActivity {

    private TextInputEditText mEmail;
    private TextInputEditText mNome;
    private TextInputEditText mMorada;
    private TextInputEditText mMoradaComplementar;
    private TextInputEditText mLocalidade;
    private TextInputEditText mCodigoPostal;
    private TextInputEditText mTelemovel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_dados);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }




        mEmail = (TextInputEditText) findViewById(R.id.alterarEmail);
        mNome = (TextInputEditText) findViewById(R.id.alterarNome);
        mMorada = (TextInputEditText) findViewById(R.id.alterarMorada);
        mMoradaComplementar = (TextInputEditText) findViewById(R.id.alterarMoradaComplementar);
        mLocalidade = (TextInputEditText) findViewById(R.id.alterarLocalidade);
        mCodigoPostal = (TextInputEditText) findViewById(R.id.alterarCodigoPostal);
        mTelemovel = (TextInputEditText) findViewById(R.id.alterarTelemovel);

        String email = getIntent().getStringExtra("email");
        String nome = getIntent().getStringExtra("nome");
        String morada = getIntent().getStringExtra("morada");
        String morada_complementar = getIntent().getStringExtra("morada_complementar");
        String localidade = getIntent().getStringExtra("localidade");
        String codigo_postal = getIntent().getStringExtra("codigo_postal");
        String telemovel = getIntent().getStringExtra("telemovel");

        mEmail.setText(email);
        mNome.setText(nome);
        mMorada.setText(morada);
        mMoradaComplementar.setText(morada_complementar);
        mLocalidade.setText(localidade);
        mCodigoPostal.setText(codigo_postal);
        mTelemovel.setText(telemovel);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AlterarDadosActivity.this, PerfilActivity.class);
        intent.putExtra("email" , mEmail.getText().toString());
        startActivityForResult(intent, 5);
        finish();
        //super.onBackPressed();
    }

}
