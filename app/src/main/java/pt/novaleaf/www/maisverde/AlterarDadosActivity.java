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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class AlterarDadosActivity extends AppCompatActivity {

    private String email;
    ArrayList<String> arrayList;
    ListView mList;
    SharedPreferences sharedPreferences;
    private UserAlteraTask mAlteraTask = null;
    private boolean changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_dados);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changed)
                    attemptSendData();
                finish();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);


        mList = (ListView) findViewById(R.id.myList);
        email = sharedPreferences.getString("email", "erro");

        arrayList = new ArrayList<>();
        arrayList.add("Username: " + sharedPreferences.getString("username", "erro"));
        arrayList.add("Email: " + email);
        arrayList.add("Rácio de aprovação dos reports:" + sharedPreferences.getString("approval_rate", "erro"));
        arrayList.add("Número de reports efetuados: " + sharedPreferences.getString("numb_reports", "erro"));
        //arrayList.add("Role: " + sharedPreferences.getString("role", "erro"));
        arrayList.add("Morada principal: " + sharedPreferences.getString("firstaddress", "ainda não definida"));
        arrayList.add("Morada complementar: " + sharedPreferences.getString("complementaryaddress", "ainda não definida"));
        arrayList.add("Localidade: " + sharedPreferences.getString("locality", "ainda não definida"));
        arrayList.add("Código Postal: " + sharedPreferences.getString("postalcode", "ainda não definido"));
        arrayList.add("Telefone: " + sharedPreferences.getString("telephone", "ainda não definido"));
        arrayList.add("Telemovel: " + sharedPreferences.getString("mobile_phone", "ainda não definido"));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        mList.setAdapter(arrayAdapter);

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 1:
                        setDialog("Alterar o Email", "Introduza o novo endereço de email", 1);
                        return true;
                    case 4:
                        setDialog("Morada", "Introduza a sua morada", 4);
                        return true;
                    case 5:
                        setDialog("Morada Complementar", "Introduza a sua morada complementar", 5);
                        return true;
                    case 6:
                        setDialog("Localidade", "Introduza a sua localidade", 6);
                        return true;
                    case 7:
                        setDialog("Código Postal", "Introduza o seu código postal", 7);
                        return true;
                    case 8:
                        setDialog("Telefone", "Introduza o seu número de telefone", 8);
                        return true;
                    case 9:
                        setDialog("Telemovel", "Introduza o seu número de telemovel", 9);
                        return true;
                    default:
                        return false;
                }

            }
        });

    }

    public void setDialog(String titulo, String mensagem, final int index) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AlterarDadosActivity.this);
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensagem);

        final EditText input = new EditText(AlterarDadosActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        String tipo="";
        String nomeCampo="";
        switch (index){
            case 1:
                tipo = "email";
                nomeCampo = "Email: ";
                break;
            case 4:
                tipo = "firstaddress";
                nomeCampo = "Morada principal: ";
                break;
            case 5:
                tipo = "complementaryaddress";
                nomeCampo = "Morada complementar: ";
                break;
            case 6:
                tipo = "locality";
                nomeCampo = "Localidade: ";
                break;
            case 7:
                tipo = "postalcode";
                nomeCampo = "Código Postal: ";
                break;
            case 8:
                tipo = "telephone";
                nomeCampo = "Telefone: ";
                break;
            case 9:
                tipo = "mobile_phone";
                nomeCampo = "Telemovel: ";
                break;
            default:
        }

        Log.i("tipo", tipo);
        final String finalTipo = tipo;
        final String finalNomeCampo = nomeCampo;
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        changed = true;
                        String campo = input.getText().toString();
                        Log.i("finaltipo", finalTipo);
                        arrayList.remove(index);
                        arrayList.add(index, finalNomeCampo + campo);
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AlterarDadosActivity.this, android.R.layout.simple_list_item_1, arrayList);
                        mList.setAdapter(arrayAdapter);
                        SharedPreferences.Editor ed =sharedPreferences.edit();
                        ed.putString(finalTipo, campo);
                        ed.commit();
                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSendData() {
        if (mAlteraTask != null) {
            return;
        }

        String email = sharedPreferences.getString("email", "");
        String firstaddress = sharedPreferences.getString("firstaddress", "");
        String complementaryaddress = sharedPreferences.getString("complementaryaddress", "");
        String locality = sharedPreferences.getString("locality", "");
        String telephone = sharedPreferences.getString("telephone", "");
        String mobile_phone = sharedPreferences.getString("mobile_phone", "");
        String postalcode = sharedPreferences.getString("postalcode", "");


        boolean cancel = false;
        View focusView = null;



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAlteraTask = new UserAlteraTask(email, firstaddress, complementaryaddress, locality, telephone, mobile_phone, postalcode);
            mAlteraTask.execute((Void) null);
        }
    }


    /**
     * Represents a task used to change things form the user
     */
    public class UserAlteraTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;// = sharedPreferences.getString("email", "");
        private final String mFirstaddress;// = sharedPreferences.getString("firstaddress", "");
        private final String mComplementaryaddress;// = sharedPreferences.getString("complementaryaddress", "");
        private final String mLocality;// = sharedPreferences.getString("locality", "");
        private final String mTelephone;// = sharedPreferences.getString("telephone", "");
        private final String mMobile_phone;// = sharedPreferences.getString("mobile_phone", "");
        private final String mPostalcode;// = sharedPreferences.getString("mobile_phone", "");

        UserAlteraTask(String email, String firstaddress, String complementaryaddress,
                       String locality, String telephone, String mobile_phone, String postalcode) {

            mEmail = email;
            mFirstaddress = firstaddress;
            mComplementaryaddress = complementaryaddress;
            mLocality = locality;
            mTelephone = telephone;
            mMobile_phone = mobile_phone;
            mPostalcode = postalcode;
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

                JSONObject token = new JSONObject();
                SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
                token.put("username", sharedPreferences.getString("username", "erro"));
                token.put("tokenID", sharedPreferences.getString("tokenID", "erro"));
                token.put("creationData", sharedPreferences.getLong("creationData", 0));
                token.put("expirationData", sharedPreferences.getLong("expirationData", 0));

                JSONObject profileInfo = new JSONObject();
                profileInfo.put("email", mEmail);
                profileInfo.put("firstaddress", mFirstaddress);
                profileInfo.put("complementaryaddress", mComplementaryaddress);
                profileInfo.put("locality", mLocality);
                profileInfo.put("telephone", mTelephone);
                profileInfo.put("mobile_phone", mMobile_phone);
                profileInfo.put("postalcode", mPostalcode);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("profileInfo", profileInfo);
                jsonObject.put("token", token);
                Log.i("profile info", jsonObject.toString());

                URL url = new URL("https://novaleaf-197719.appspot.com/rest/users/complete_profile");
                return RequestsREST.doPOST(url, jsonObject);
            } catch (Exception e) {
                Log.i("erro", e.toString());
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mAlteraTask = null;

            if (result != null) {
                    // We parse the result

                    Log.i("AlterarDados", result);
            }
        }

        @Override
        protected void onCancelled() {
            mAlteraTask = null;

        }
    }



}




