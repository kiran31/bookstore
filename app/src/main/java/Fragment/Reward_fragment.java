package Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Config.BaseURL;
import Config.SharedPref;
import Module.Module;
import shoparounds.com.GifImageView;
import shoparounds.com.MainActivity;
import shoparounds.com.R;
import shoparounds.com.networkconnectivity.NetworkConnection;
import shoparounds.com.networkconnectivity.NetworkError;
import util.ConnectivityReceiver;
import util.Session_management;



public class Reward_fragment extends Fragment {
    private GifImageView gifImageView;
    private static String TAG = Reward_fragment.class.getSimpleName();
    RelativeLayout Reedeem_Points;
    TextView Rewards_Points;
    Module module;
    String rewards_amt,wallet_amt="";
    private Session_management sessionManagement;
    ProgressDialog progressDialog ;

    public Reward_fragment() {
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
        final View view = inflater.inflate(R.layout.activity_reward_points, container, false);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.tv_toolbar_name));
        sessionManagement = new Session_management(getActivity());
        module=new Module();
        String getrewards = sessionManagement.getUserDetails().get(BaseURL.KEY_REWARDS_POINTS);
        Rewards_Points = (TextView) view.findViewById(R.id.reward_points);
        //  Rewards_Points.setText(getrewards);
        gifImageView = (GifImageView) view.findViewById(R.id.gif_image);
        gifImageView.setGifImageResource(R.drawable.pay);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");

        Reedeem_Points = (RelativeLayout) view.findViewById(R.id.reedme_point);
        Reedeem_Points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double rew=Double.parseDouble(rewards_amt);
                if(rew<100)
                {
                    Toast.makeText(getActivity(),"Minimum Reedem Points limit is 100",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Shift_Reward_to_WAllet();
                    gifImageView.setVisibility(View.VISIBLE);
                    final View myview = gifImageView;
                    view.postDelayed(new Runnable() {
                        public void run() {
                            myview.setVisibility(View.GONE);
                        }
                    }, 5000);
                }

            }
        });

        if (ConnectivityReceiver.isConnected()) {
            getRewards();
        }
        return view;

    }

    public void getRewards() {
        progressDialog.show();
        String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        StringRequest strReq = new StringRequest(Request.Method.GET, BaseURL.REWARDS_REFRESH + user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status=jObj.getString("success");
                            if(status.equals("success"))
                            {
                             rewards_amt=jObj.getString("rewards");
                             wallet_amt=jObj.getString("wallet");
                                Rewards_Points.setText(""+Double.parseDouble(rewards_amt.toString()));
                                    SharedPref.putString(getActivity(), BaseURL.KEY_REWARDS_POINTS, rewards_amt);
                           // if (jObj.optString("success").equalsIgnoreCase("success")) {
//                                String rewards_points = jObj.getString("total_rewards");
//                                if (rewards_points.equals("null")) {
//                                    Rewards_Points.setText("0");
//                                } else {
//                                    Rewards_Points.setText(rewards_points);
//                                    SharedPref.putString(getActivity(), BaseURL.KEY_REWARDS_POINTS, rewards_points);
//                                }

                            } else {
                                Rewards_Points.setText("0.0");
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

    private void Shift_Reward_to_WAllet() {
        final String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        final String getreward = Rewards_Points.getText().toString();
       // final String getwallet = SharedPref.getString(getActivity(), BaseURL.KEY_WALLET_Ammount);
        final String getwallet = wallet_amt;
        if (NetworkConnection.connectionChecking(getActivity())) {
            RequestQueue rq = Volley.newRequestQueue(getActivity());
            StringRequest postReq = new StringRequest(Request.Method.POST, BaseURL.BASE_URL+"index.php/api/shift",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("eclipse", "Response=" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                JSONArray Jarray = object.getJSONArray("wallet_amount");
                                for (int i = 0; i < Jarray.length(); i++) {
                                    JSONObject json_data = Jarray.getJSONObject(i);
                                    String final_amount = json_data.getString("final_amount");
                                    String final_rewards = json_data.getString("final_rewards");
                                    Rewards_Points.setText(final_rewards);
                                    Toast.makeText(getActivity(),"Rewards Redeem successfully.",Toast.LENGTH_SHORT).show();
                                    Rewards_Points.setEnabled(false);
                                    SharedPref.putString(getActivity(), BaseURL.KEY_WALLET_Ammount, final_amount);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", user_id);
                    params.put("rewards", getreward);
                    params.put("amount", getwallet);
                    return params;
                }
            };
            rq.add(postReq);
        } else {
            Intent intent = new Intent(getActivity(), NetworkError.class);
            startActivity(intent);
        }

    }


}