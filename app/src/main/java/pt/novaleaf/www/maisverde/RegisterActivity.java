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

import butterknife.BindView;
import butterknife.ButterKnife;


public class RegisterActivity extends AppCompatActivity {
    private static final int PASSWORD_MIN_LENGTH = 6;
    private static final String REGISTER_URL = "https://novaleaf-197719.appspot.com/rest/register";
    private UserRegisterTask mAuthTask = null;


    @BindView(R.id.input_usrnamename) EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_password_confirmation) EditText _passwordConfirmation;
    @BindView(R.id.btn_signup) Button _signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

    }

    private void attemptRegister(){

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        _emailText.setError(null);
        _passwordText.setError(null);
        _nameText.setError(null);
        _passwordConfirmation.setError(null);

        // Store values at the time of the register attempt.
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String passwordConf = _passwordConfirmation.getText().toString();
        String username = _nameText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            _passwordText.setError(getString(R.string.error_invalid_password));
            focusView = _passwordText;
            cancel = true;
        }

        if (!password.equals(passwordConf)) {
            _passwordConfirmation.setError(getString(R.string.error_diffenrent_passwords));
            focusView = _passwordConfirmation;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            _emailText.setError(getString(R.string.error_field_required));
            focusView = _emailText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            _emailText.setError(getString(R.string.error_invalid_email));
            focusView = _emailText;
            cancel = true;
        }

        //check for a valid username
        if (TextUtils.isEmpty(username)) {
            _nameText.setError(getString(R.string.error_field_required));
            focusView = _nameText;
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

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        if(password.length() < PASSWORD_MIN_LENGTH)
            return false;

        return true;
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
            mRole = getString(R.string.VOLUNTEER);
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
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("username", mUsername);
                jsonObject.put("password", mPassword);
                jsonObject.put("confirmation_password", mPasswordConfirm);
                jsonObject.put("email", mEmail);
                jsonObject.put("role", mRole);

                URL url = new URL(REGISTER_URL);
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
                _passwordText.setError(getString(R.string.error_incorrect_password));
                _passwordText.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }



}
