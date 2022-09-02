package shoparounds.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Config.BaseURL;
import Module.Module;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.Session_management;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = LoginActivity.class.getSimpleName();
    private Button btn_continue,btn_facebook,btn_google;
    private EditText et_password, et_email;
    Module module;
    private TextView tv_password, tv_email, btn_forgot , btn_register;
    private Session_management sessionManagement;
    ProgressDialog progressDialog ;
    @Override
    protected void attachBaseContext(Context newBase) {

        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        setContentView(R.layout.activity_login);
    module=new Module();
        et_password = (EditText) findViewById(R.id.et_login_pass);
        et_email = (EditText) findViewById(R.id.et_login_email);
        tv_password = (TextView) findViewById(R.id.tv_login_password);
        tv_email = (TextView) findViewById(R.id.tv_login_email);
        btn_continue = (Button) findViewById(R.id.btnContinue);
        btn_register = (TextView) findViewById(R.id.btnRegister);
        btn_forgot = (TextView) findViewById(R.id.btnForgot);
        btn_facebook=(Button)findViewById( R.id.btnfb );
        btn_google=(Button)findViewById( R.id.btngoogle );
        progressDialog=new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");


        btn_continue.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_forgot.setOnClickListener(this);
        btn_google.setOnClickListener( this );
        btn_facebook.setOnClickListener( this );
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnContinue) {
            attemptLogin();
        } else if (id == R.id.btnRegister) {
            Intent startRegister = new Intent(LoginActivity.this, MobileVerify.class);
            startRegister.putExtra( "type","r" );
            startActivity(startRegister);
        } else if (id == R.id.btnForgot) {
            Intent startRegister = new Intent(LoginActivity.this, MobileVerify.class);
            startRegister.putExtra( "type","f" );
            startActivity(startRegister);
        }
    }

    private void attemptLogin() {


        tv_password.setTextColor(getResources().getColor(R.color.black));
        tv_email.setTextColor(getResources().getColor(R.color.black));
        String getpassword = et_password.getText().toString();
        String getemail = et_email.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(getpassword)) {
            et_password.setError(getResources().getString(R.string.enter_password));
            focusView = et_password;
            cancel = true;
        } else if (!isPasswordValid(getpassword)) {
            et_password.setError(getResources().getString(R.string.password_too_short));
            tv_password.setTextColor(getResources().getColor(R.color.black));
          //  focusView = et_password;
            cancel = true;
        }

        if (TextUtils.isEmpty(getemail)) {
            et_email.setError(getResources().getString(R.string.enter_phone));
           // tv_email.setTextColor(getResources().getColor(R.color.black));
            focusView = et_email;
            cancel = true;
        } else if (!isPhoneValid(getemail)) {
            et_email.setError(getResources().getString(R.string.password_length));
           // tv_email.setTextColor(getResources().getColor(R.color.black));
            focusView = et_email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            if (ConnectivityReceiver.isConnected()) {
                makeLoginRequest(getemail, getpassword);
            }
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

    private boolean isPhoneValid(String phoneno) {
        //TODO: Replace this with your own logic
        return phoneno.length() > 9;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeLoginRequest(String email, final String password) {

        // Tag used to cancel the request
        String tag_json_obj = "json_login_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_phone", email);
        params.put("password", password);
        progressDialog.show();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.LOGIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        JSONObject obj = response.getJSONObject("data");
                        String user_id = obj.getString("user_id");
                        String user_fullname = obj.getString("user_fullname");
                        String user_email = obj.getString("user_email");
                        String user_phone = obj.getString("user_phone");
                        String user_image = obj.getString("user_image");
                        String wallet_ammount = obj.getString("wallet");
                        String reward_points = obj.getString("rewards");
                        Session_management sessionManagement = new Session_management(LoginActivity.this);
                        sessionManagement.createLoginSession(user_id, user_email, user_fullname, user_phone, user_image, wallet_ammount, reward_points, "", "", "", "", password);
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
//                        MyFirebaseRegister myFirebaseRegister=new MyFirebaseRegister(LoginActivity.this);
//                        myFirebaseRegister.RegisterUser(user_id);

                        btn_continue.setEnabled(false);

                    }
                    else {
                        String error = response.getString("error");
                        btn_continue.setEnabled(true);

                        Toast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                  progressDialog.dismiss();
                }
                catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(LoginActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }



}
