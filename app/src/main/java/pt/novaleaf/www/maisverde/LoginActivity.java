package pt.novaleaf.www.maisverde;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private UserInfoTask mInfoTask = null;
    private UserReportsTask mReportsTask = null;


    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ImageView mLogoView;
    private Context mContext;
    private CheckBox mCheckBox;
    private String usernome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);



        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.username || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        boolean isChecked = getSharedPreferences("Prefs", MODE_PRIVATE).getBoolean("checked", false);
        if (isChecked){
            mCheckBox.setChecked(true);
            mUsernameView.setText(getSharedPreferences("Prefs", MODE_PRIVATE).getString("username", ""));
            mPasswordView.setText(getSharedPreferences("Prefs", MODE_PRIVATE).getString("password", ""));
        }

        SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

        usernome = getSharedPreferences("Prefs", MODE_PRIVATE).getString("username", null);

        Button mEmailSignInButton = (Button) findViewById(R.id.btn_login);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView mRegisterTextView = (TextView) findViewById(R.id.registar_textView);
        mRegisterTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                registar();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mLogoView = findViewById(R.id.imageLogo);
        mContext = this;
    }

    private void registar() {
        Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("Password inválida");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError("Campo não pode estar vazio");
            focusView = mUsernameView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mUsernameView.setError("Username inválido");
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
            //showProgress(true);

            loginVolley(email, password);
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);
        }
    }

    private void loginVolley(final String email, final String password) {

        String tag_json_obj = "json_obj_req";
        String url = "https://novaleaf-197719.appspot.com/rest/login";

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("password", password);

            jsonObject.put("username", email);

            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("A Carregar...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
            final SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (usernome!=null)
                            if (!usernome.equals(email)) {
                                editor.clear();
                                Log.d("ya", "deu clear!!!");
                            }
                            editor.putString("username", email);
                            editor.putString("password", password);
                            if (mCheckBox.isChecked())
                                editor.putBoolean("checked", true);
                            else
                                editor.putBoolean("checked", false);

                            editor.commit();
                            pDialog.hide();
                            voleyGetInfo();
                            // TODO: call the main activity (to be implemented) with data in the intent
                            Intent myIntent = new Intent(LoginActivity.this, FeedActivity.class);

                            LoginActivity.this.startActivity(myIntent);
                            finish();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("erroLOGIN", "Error: " + error.getMessage());
                    // hide the progress dialog
                    pDialog.hide();
                    Toast.makeText(mContext, "Por favor verifique a sua ligação", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    try {

//                        pDialog.hide();
                        String jsonString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                        JSONObject jsonResponse = new JSONObject(response.headers);
                        //jsonResponse.put("headers", new JSONObject(response.headers));
                        Log.d("YA BINA", jsonResponse.getString("Authorization"));
                        editor.putString("tokenID", jsonResponse.getString("Authorization"));
                        editor.commit();
                        return Response.success(jsonResponse,
                                HttpHeaderParser.parseCacheHeaders(response));
                    } catch (UnsupportedEncodingException e) {
                        return Response.error(new ParseError(e));
                    } catch (JSONException je) {
                        return Response.error(new ParseError(je));
                    }
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void voleyGetInfo() {


        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        String url = "https://novaleaf-197719.appspot.com/rest/withtoken/users/profileinfo?user=" +
                sharedPreferences.getString("username", "erro");
        final String token = sharedPreferences.getString("tokenID", "erro");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("TokenAreaPessoal", response.toString());
                //Log.i("TokenAreaPessoal", token.toString());
                // TODO: store the token in the SharedPreferences

                SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
                try {

                    if (response.has("email"))
                        editor.putString("email", response.getString("email"));
                    if (response.has("role"))
                        editor.putString("role", response.getString("role"));
                    if (response.has("numb_reports"))
                        editor.putString("numb_reports", response.getString("numb_reports"));
                    if (response.has("approval_rate"))
                        editor.putString("approval_rate", response.getString("approval_rate"));
                    if (response.has("name"))
                        editor.putString("name", response.getString("name"));
                    if (response.has("locality"))
                        editor.putString("locality", response.getString("locality"));
                    if (response.has("firstaddress"))
                        editor.putString("firstaddress", response.getString("firstaddress"));
                    if (response.has("complementaryaddress"))
                        editor.putString("complementaryaddress", response.getString("complementaryaddress"));
                    if (response.has("mobile_phone"))
                        editor.putString("mobile_phone", response.getString("mobile_phone"));
                    if (response.has("name"))
                        editor.putString("name", response.getString("name"));
                    if (response.has("image_uri"))
                        editor.putString("image_user", response.getJSONObject("image_uri").getString("value"));
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("erro", "Error: " + error.getMessage());
                Toast.makeText(mContext, "Por favor verifique a sua ligação", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "UserInfo");

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
        //       return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
        //       return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     *
     * @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) private void showProgress(final boolean show) {
     * // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
     * // for very easy animations. If available, use these APIs to fade-in
     * // the progress spinner.
     * //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
     * int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
     * <p>
     * mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
     * mLogoView.setVisibility(show ? View.GONE : View.VISIBLE);
     * mLoginFormView.animate().setDuration(shortAnimTime).alpha(
     * show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
     * @Override public void onAnimationEnd(Animator animation) {
     * mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
     * }
     * });
     * <p>
     * mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
     * mProgressView.animate().setDuration(shortAnimTime).alpha(
     * show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
     * @Override public void onAnimationEnd(Animator animation) {
     * mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
     * }
     * });
     * //}
     * /**else {
     * // The ViewPropertyAnimator APIs are not available, so simply show
     * // and hide the relevant UI components.
     * mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
     * mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
     * mLogoView.setVisibility(show ? View.GONE : View.VISIBLE);
     * }
     * }
     */

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
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
                jsonObject.put("password", mPassword);
                jsonObject.put("username", mEmail);


                return "ola";
                //return RequestsREST.doPOST(url, jsonObject, "");
            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            //showProgress(false);

            if (!result.contains("HTTP error code")) {
                // We parse the result
                Log.i("LoginActivity", result);
                // TODO: store the token in the SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
                editor.putString("tokenID", result);
                editor.putString("username", mEmail);
                editor.putString("password", mPassword);
                editor.commit();
                attemptGetInfo();
                attemptGetReports();
                // TODO: call the main activity (to be implemented) with data in the intent
                Intent myIntent = new Intent(LoginActivity.this, FeedActivity.class);
                LoginActivity.this.startActivity(myIntent);
                finish();
            } else {
                Log.i("LoginActivity", result);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }

    /**
     * Attempts to get user info
     */
    private void attemptGetInfo() {
        if (mInfoTask != null) {
            return;
        }

        mInfoTask = new UserInfoTask();
        mInfoTask.execute((Void) null);

    }

    /**
     * Attempts to get user info
     */
    private void attemptGetReports() {
        if (mReportsTask != null) {
            return;
        }

        mReportsTask = new UserReportsTask();
        mReportsTask.execute((Void) null);

    }

    /**
     * Represents a task used to get user info
     * Tenta ir buscar a informacao do user e adiciona-a as sharedpreferences
     */
    public class UserInfoTask extends AsyncTask<Void, Void, String> {


        UserInfoTask() {

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

                SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);

                String token = sharedPreferences.getString("tokenID", "erro");

                URL url = new URL("https://novaleaf-197719.appspot.com/rest/withtoken/users/profileinfo?user=" +
                        sharedPreferences.getString("username", "erro"));
                return RequestsREST.doGET(url, token);
            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mInfoTask = null;

            if (result != null) {
                JSONObject token = null;
                try {
                    // We parse the result
                    Log.i("TokenAreaPessoal", result);
                    token = new JSONObject(result);
                    Log.i("TokenAreaPessoal", token.toString());
                    // TODO: store the token in the SharedPreferences

                    SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();

                    editor.putString("email", token.getString("email"));
                    editor.putString("role", token.getString("role"));
                    editor.putString("numb_reports", token.getString("numb_reports"));
                    editor.putString("approval_rate", token.getString("approval_rate"));
                    editor.commit();

                } catch (JSONException e) {
                    // WRONG DATA SENT BY THE SERVER
                    Log.e("TOKENAREAPESSOAL", result);
                    Log.e("TOKENAREAPESSOAL", e.toString());
                }
            }
        }

        @Override
        protected void onCancelled() {
            mInfoTask = null;

        }
    }


    /**
     * Represents a task used to get user tasks
     * Tenta ir buscar todos os reports do utilizador, e adiciona-os as sharedpreferences
     */
    public class UserReportsTask extends AsyncTask<Void, Void, String> {


        UserReportsTask() {

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
                SharedPreferences sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);

                String token = sharedPreferences.getString("tokenID", "erro");

                URL url = new URL("https://novaleaf-197719.appspot.com/rest/withtoken/mapsupport/listmymarkers");
                return RequestsREST.doGET(url, token);
            } catch (Exception e) {
                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(final String result) {
            mReportsTask = null;

            if (result != null) {
                JSONArray token = null;
                try {
                    // We parse the result
                    Log.i("TOKENOCORRENCIAS", result);

                    token = new JSONArray(result);
                    Log.i("TOKENOCORRENCIAS", token.toString());
                    // TODO: store the token in the SharedPreferences

                    SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();


                    for (int i = 0; i < token.length(); i++) {
                        //editor.putString("userReport"+i,token.getJSONObject(i).getString("name"));
                        //editor.putInt("numReports", i);
                        //arrayList.add(token.getJSONObject(i).getString("name"));
                        Log.i("TOKENOCORRENCIAS", "CHE " + token.getJSONObject(i).getString("name"));
                        //editor.commit();
                    }


                } catch (JSONException e) {
                    // WRONG DATA SENT BY THE SERVER

                    Log.e("Authentication", e.toString());
                }
            }
        }

        @Override
        protected void onCancelled() {
            mReportsTask = null;

        }
    }


}

