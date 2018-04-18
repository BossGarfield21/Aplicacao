package pt.novaleaf.www.maisverde;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;



public class RegisterActivity extends AppCompatActivity {
    private UserRegisterTask mAuthTask = null;
    private EditText mEmailView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mMoradaView;
    private EditText mLocalidadeView;
    private EditText mCodPostalView;
    private EditText mPassConfirmView;
    private EditText mRoleView;
    private Button bRegistar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsernameView = (EditText) findViewById(R.id.username);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPassConfirmView = (EditText) findViewById(R.id.passwordConfirm);
        mMoradaView = (EditText) findViewById(R.id.morada);
        mLocalidadeView = (EditText) findViewById(R.id.localidade);
        mCodPostalView = (EditText) findViewById(R.id.cod_postal);
        mRoleView = (EditText) findViewById(R.id.role);
        bRegistar = (Button) findViewById(R.id.registar_button);

        bRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

    }

    private void attemptRegister(){

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mUsernameView.setError(null);
        mMoradaView.setError(null);
        mCodPostalView.setError(null);
        mLocalidadeView.setError(null);
        mPassConfirmView.setError(null);
        mRoleView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConf = mPassConfirmView.getText().toString();
        String username = mUsernameView.getText().toString();
        String morada = mMoradaView.getText().toString();
        String localidade = mLocalidadeView.getText().toString();
        String codPostal = mCodPostalView.getText().toString();
        String role = mRoleView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!password.equals(passwordConf)) {
            mPassConfirmView.setError("Password diferentes");
            focusView = mPassConfirmView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        // check for a valid morada
        if (TextUtils.isEmpty(morada)) {
            mMoradaView.setError(getString(R.string.error_field_required));
            focusView = mMoradaView;
            cancel = true;
        }

        //check for a valid localidade
        if (TextUtils.isEmpty(localidade)) {
            mLocalidadeView.setError(getString(R.string.error_field_required));
            focusView = mLocalidadeView;
            cancel = true;
        }

        if (TextUtils.isEmpty(role)) {
            mRoleView.setError(getString(R.string.error_field_required));
            focusView = mRoleView;
            cancel = true;
        }

        //check for a valid codigo postal
        if (TextUtils.isEmpty(codPostal)) {
            mCodPostalView.setError(getString(R.string.error_field_required));
            focusView = mCodPostalView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserRegisterTask(email, password, username, morada, localidade, codPostal, passwordConf, role);
            mAuthTask.execute((Void) null);
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isCodPostalValid(String codPostal) {
        //TODO: Replace this with your own logic
        return true;
        //       return password.length() > 4;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mPasswordConfirm;
        private final String mUsername;
        private final String mMorada;
        private final String mLocalidade;
        private final String mCodPostal;
        private final String mRole;



        UserRegisterTask(String email, String password, String username,
                         String morada, String localidade, String codPostal, String passwordConfirm, String role) {
            mEmail = email;
            mPassword = password;
            mPasswordConfirm = passwordConfirm;
            mUsername = username;
            mMorada = morada;
            mLocalidade = localidade;
            mCodPostal = codPostal;
            mRole = role;
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

                jsonObject.put("username", mUsername);
                jsonObject.put("password", mPassword);
                //jsonObject.put("confirmation_password", mPasswordConfirm);
                //jsonObject.put("name", mUsername);
                jsonObject.put("email", mEmail);
                //jsonObject.put("role", mRole);
                //jsonObject.put("telephone", "999999999");
                //jsonObject.put("mobile_phone", "999999999");
                jsonObject.put("morada", mMorada);
                //jsonObject.put("complementaryaddress", "rua das silvas");
                jsonObject.put("localidade", mLocalidade);
                //jsonObject.put("postalcode", mCodPostal);
                //jsonObject.put("nif", "999999999");
                //jsonObject.put("cc", "123456-SSS");




                URL url = new URL("https://empirical-axon-196102.appspot.com/rest/register/v3");
                return RequestsREST.doPOST(url, jsonObject);


            } catch (Exception e) {
                //Log.i("cx", "dbsaiobsdoi");
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            //Log.i("cxuvuy", result +" ffs+pk");

            if (result != null) {
                //JSONObject token = null
                // We parse the result

                //token = new JSONObject(result);
                //Log.i("RegisterActivity", token.toString());
                // TODO: store the token in the SharedPreferences

                // TODO: call the main activity (to be implemented) with data in the intent
                Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(myIntent);
                finish();


            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }



}
