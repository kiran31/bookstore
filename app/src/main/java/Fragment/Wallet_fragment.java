package Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import Config.BaseURL;
import Config.SharedPref;
import Module.Module;
import shoparounds.com.MainActivity;
import shoparounds.com.R;
import shoparounds.com.RechargeWallet;
import util.ConnectivityReceiver;
import util.Session_management;


public class Wallet_fragment extends Fragment {

    private static String TAG = Wallet_fragment.class.getSimpleName();

    TextView Wallet_Ammount;
    ProgressDialog progressDialog;
   Module module;
    RelativeLayout Recharge_Wallet;
    private Session_management sessionManagement;


    public Wallet_fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_wallet_ammount, container, false);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.tv_app_name));
        sessionManagement = new Session_management(getActivity());
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        module=new Module();
        progressDialog.setMessage("Loading...");
        //   String getwallet = sessionManagement.getUserDetails().get(BaseURL.KEY_WALLET_Ammount);
        Wallet_Ammount = (TextView) view.findViewById(R.id.wallet_ammount);
        //     Wallet_Ammount.setText(getwallet);
        Recharge_Wallet = (RelativeLayout) view.findViewById(R.id.recharge_wallet);
        Recharge_Wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RechargeWallet.class);
                startActivity(intent);
            }
        });
        if (ConnectivityReceiver.isConnected()) {
            getRefresrh();
        }


        return view;

    }

    public void getRefresrh() {
        String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        StringRequest strReq = new StringRequest(Request.Method.GET, BaseURL.WALLET_REFRESH + user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            if (jObj.optString("success").equalsIgnoreCase("success")) {
                                String wallet_amount = jObj.getString("wallet");
                                Wallet_Ammount.setText(wallet_amount);
                                SharedPref.putString(getActivity(), BaseURL.KEY_WALLET_Ammount, wallet_amount);
                            } else {
                                // Toast.makeText(DashboardPage.this, "" + jObj.optString("msg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        }) {

        };
        rq.add(strReq);
    }

}