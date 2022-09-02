package shoparounds.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = ForgotActivity.class.getSimpleName();
   Module module;
    private RelativeLayout btn_continue;
    private EditText et_email;
    private TextView tv_email;
    String lan;
    SharedPreferences preferences;
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

        setContentView(R.layout.activity_forgot);
        // Call the function callInstamojo to start payment here
      module=new Module();
        et_email = (EditText) findViewById(R.id.et_login_email);
        tv_email = (TextView) findViewById(R.id.tv_login_email);
        btn_continue = (RelativeLayout) findViewById(R.id.btnContinue);

        btn_continue.setOnClickListener(this);
        preferences = getSharedPreferences("lan", MODE_PRIVATE);
        lan=preferences.getString("language","");
        progressDialog=new ProgressDialog(ForgotActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnContinue) {
            attemptForgot();
        }
    }

    private void attemptForgot() {

        tv_email.setText(getResources().getString(R.string.tv_login_email));

        tv_email.setTextColor(getResources().getColor(R.color.dark_gray));

        String getemail = et_email.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(getemail)) {
            tv_email.setTextColor(getResources().getColor(R.color.black));
            focusView = et_email;
            cancel = true;
        } else if (!isEmailValid(getemail)) {
            tv_email.setText(getResources().getString(R.string.invalide_email_address));
            tv_email.setTextColor(getResources().getColor(R.color.black));
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
                makeForgotRequest(getemail);
            }
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeForgotRequest(String email) {

        // Tag used to cancel the request
        String tag_json_obj = "json_forgot_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.FORGOT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    String error = response.getString("error");
                    String error_arb=response.getString("error_arb");
                    if (status) {
                     //   Toast.makeText(ForgotActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ForgotActivity.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();

                    } else {
                        Toast.makeText(ForgotActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                        /*if (lan.contains("english")) {
                            Toast.makeText(ForgotActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ForgotActivity.this, "" + error_arb, Toast.LENGTH_SHORT).show();

                        }*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(ForgotActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
