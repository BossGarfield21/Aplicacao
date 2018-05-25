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
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.net.URL;



public class RegisterActivity extends AppCompatActivity {
    private UserRegisterTask mAuthTask = null;
    private EditText mEmailView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mPassConfirmView;
    private Button bRegistar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsernameView = (EditText) findViewById(R.id.input_username);
        mEmailView = (EditText) findViewById(R.id.input_email);
        mPasswordView = (EditText) findViewById(R.id.input_password);
        mPassConfirmView = (EditText) findViewById(R.id.input_password_confirmation);
        bRegistar = (Button) findViewById(R.id.btn_signup);

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
        mPassConfirmView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConf = mPassConfirmView.getText().toString();
        String username = mUsernameView.getText().toString();

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

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserRegisterTask(email, password, username, passwordConf);
            mAuthTask.execute((Void) null);
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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
        private final String mRole;



        UserRegisterTask(String email, String password, String username,
                          String passwordConfirm) {
            mEmail = email;
            mPassword = password;
            mPasswordConfirm = passwordConfirm;
            mUsername = username;
            mRole = "volunteer";
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
                jsonObject.put("email", mEmail);
                jsonObject.put("role", mRole);
                jsonObject.put("confirmation_password", mPasswordConfirm);


                URL url = new URL("https://novaleaf-197719.appspot.com/rest/register");
                return RequestsREST.doPOST(url, jsonObject, "");


            } catch (Exception e) {
                //Log.i("cx", "dbsaiobsdoi");
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            Log.i("result", result +" ffs+pk");

            if (result != null) {

                Log.i("RegisterActivity", result);
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
