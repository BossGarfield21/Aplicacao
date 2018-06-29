package pt.novaleaf.www.maisverde;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import org.json.JSONObject;

import java.net.URL;

public class AlterarPassActivity extends AppCompatActivity {

    private AutoCompleteTextView mPassVelha;
    private AutoCompleteTextView mNovaPass;
    private AutoCompleteTextView mNovaPassConfirm;
    private Button mButtonChange;
    ChangePassTask mChangePass = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_pass);

        mPassVelha = (AutoCompleteTextView) findViewById(R.id.passVelha);
        mNovaPass = (AutoCompleteTextView) findViewById(R.id.passNova);
        mNovaPassConfirm = (AutoCompleteTextView) findViewById(R.id.passNovaConfirm);
        mButtonChange = (Button) findViewById(R.id.bChange);

        mButtonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(AlterarPassActivity.this);
                alert.setTitle("Mudar a password");
                alert
                        .setMessage("Tem a certeza? A operação é irreversível")
                        .setCancelable(true)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                attemptChangePass();
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

            }
        });

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
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
    }


    public void attemptChangePass(){

        if (mChangePass!=null)
            return;

        mPassVelha.setError(null);
        mNovaPass.setError(null);
        mNovaPassConfirm.setError(null);

        String passVelha = mPassVelha.getText().toString();
        String passNova = mNovaPass.getText().toString();
        String passNovaConfirm = mNovaPassConfirm.getText().toString();

        boolean cancel = false;
        View focusView = null;

        SharedPreferences preferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        String password = preferences.getString("password", "erro");


        if (passNova.length()<6){
            cancel = true;
            mNovaPass.setError("Password demasiado curta\nSao precisos 7 caracteres");
            focusView = mNovaPass;
        }

        if (!passNova.equals(passNovaConfirm)){
            cancel = true;
            mNovaPassConfirm.setError("Passwords diferentes");
            focusView = mNovaPassConfirm;
        }

        if (!passVelha.equals(password)){
            cancel = true;
            mPassVelha.setError("A sua password atual nao é esta");
            focusView = mPassVelha;

        }

        if (cancel){
            focusView.requestFocus();
        } else{

            mChangePass = new ChangePassTask(passVelha, passNova, passNovaConfirm);

        }
    }


    public class ChangePassTask extends AsyncTask<Void, Void, String> {

        private final String myPassVelha;
        private final String myPassNova;
        private final String myPassNovaConfirm;

        ChangePassTask(String passVelha, String passNova, String passConfirm) {
            myPassVelha = passVelha;
            myPassNova = passNova;
            myPassNovaConfirm = passConfirm;
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                // If no connectivity, cancel task and update Callback with null data.
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                //TODO: create JSON object with credentials and call doPost

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("oldPassword", myPassVelha);
                jsonObject.put("newPassword", myPassNova);
                jsonObject.put("newPassword_confirmation", myPassNovaConfirm);
                SharedPreferences preferences = getSharedPreferences("Prefs", MODE_PRIVATE);
                String token = preferences.getString("tokenID", "erro");

                URL url = new URL("https://novaleaf-197719.appspot.com/rest/withtoken/users/changepass");
                return RequestsREST.doPOST(url, jsonObject, token);
            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mChangePass = null;


            if (!result.contains("HTTP error code")) {
                // We parse the result
                Log.i("ALTERARPASS", result);
                // TODO: store the token in the SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();

                editor.putString("password", myPassNova);
                editor.commit();
                // TODO: call the main activity (to be implemented) with data in the intent
                Intent myIntent = new Intent(AlterarPassActivity.this, PerfilActivity.class);
                AlterarPassActivity.this.startActivity(myIntent);
                finish();
            }else {
                Log.i("ALTERARPASS", result);
            }
        }

        @Override
        protected void onCancelled() {
            mChangePass = null;
        }
    }


}
