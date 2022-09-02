package Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Config.BaseURL;
import Config.SharedPref;
import Module.Module;
import shoparounds.com.AppController;
import shoparounds.com.MainActivity;
import shoparounds.com.PaymentActivity;
import shoparounds.com.Paytm;
import shoparounds.com.R;
import shoparounds.com.networkconnectivity.NetworkConnection;
import shoparounds.com.networkconnectivity.NetworkError;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.DatabaseCartHandler;
import util.Session_management;

import static com.android.volley.VolleyLog.TAG;


public class Payment_fragment extends Fragment {
    RelativeLayout confirm;
    RelativeLayout rel_coupon;
    private DatabaseCartHandler db_cart;
    private Session_management sessionManagement;
    TextView payble_ammount, my_wallet_ammount, used_wallet_ammount, used_coupon_ammount, order_ammount;
    private String getlocation_id = "";
    private String getstore_id = "";

    private String gettime = "";
    private String getdate = "";
    private String getuser_id = "";
    String getDeliveryType ="";
    int deli_charges ;
    private Double rewards;
    RadioButton rb_Store, rb_Cod, rb_card, rb_Netbanking, rb_paytm;
    CheckBox checkBox_Wallet, checkBox_coupon;
    EditText et_Coupon;
    String getvalue;
    String text;
     Module module;

    String cp;
    String Used_Wallet_amount;
    String total_amount;
    String order_total_amount;
    RadioGroup radioGroup;
    String Prefrence_TotalAmmount;
    String getwallet;
    String checkout , product_id;
    LinearLayout Promo_code_layout, Coupon_and_wallet;
    RelativeLayout Apply_Coupon_Code, Relative_used_wallet, Relative_used_coupon;

    ProgressDialog progressDialog;
    public Payment_fragment() {

    }

    public static Payment_fragment newInstance(String param1, String param2) {
        Payment_fragment fragment = new Payment_fragment();
        Bundle args = new Bundle();
        return fragment;
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
        final View view = inflater.inflate(R.layout.activity_payment_method, container, false);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.payment));
        module=new Module();
        Prefrence_TotalAmmount = SharedPref.getString(getActivity(), BaseURL.TOTAL_AMOUNT);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Font/Bold.ttf");

        checkBox_Wallet = (CheckBox) view.findViewById(R.id.use_wallet);
        checkBox_Wallet.setTypeface(font);
        rel_coupon=(RelativeLayout)view.findViewById(R.id.rel_coupon);

        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                checkBox_Wallet.setChecked(false);
                getvalue = radioButton.getText().toString();
            }
        });


        rb_Store = (RadioButton) view.findViewById(R.id.use_store_pickup);
        rb_Store.setTypeface(font);
        rb_Cod = (RadioButton) view.findViewById(R.id.use_COD);
        rb_Cod.setTypeface(font);
        rb_card = (RadioButton) view.findViewById(R.id.use_card);
        rb_card.setTypeface(font);
        rb_Netbanking = (RadioButton) view.findViewById(R.id.use_netbanking);
        rb_Netbanking.setTypeface(font);
        rb_paytm = (RadioButton) view.findViewById(R.id.use_wallet_ammount);
        rb_paytm.setTypeface(font);
        checkBox_coupon = (CheckBox) view.findViewById(R.id.use_coupon);
        checkBox_coupon.setTypeface(font);
        et_Coupon = (EditText) view.findViewById(R.id.et_coupon_code);
        Promo_code_layout = (LinearLayout) view.findViewById(R.id.prommocode_layout);
        Apply_Coupon_Code = (RelativeLayout) view.findViewById(R.id.apply_coupoun_code);
        Apply_Coupon_Code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Coupon_code();

            }
        });

        sessionManagement = new Session_management(getActivity());

        deli_charges = Integer.parseInt(getArguments().getString("deli_charges"));
        Coupon_and_wallet = (LinearLayout) view.findViewById(R.id.coupon_and_wallet);
        Relative_used_wallet = (RelativeLayout) view.findViewById(R.id.relative_used_wallet);
        Relative_used_coupon = (RelativeLayout) view.findViewById(R.id.relative_used_coupon);

        getRefresrh();
       // final String WAmmount = SharedPref.getString(getActivity(), BaseURL.KEY_WALLET_Ammount);
      //  final String WAmmount =  getRefresrh();
        //Show  Wallet
        //Toast.makeText(getActivity(),"ww"+WAmmount,Toast.LENGTH_LONG).show();
//        getwallet = SharedPref.getString(getActivity(), BaseURL.KEY_WALLET_Ammount);
        //Toast.makeText(getActivity(),"ww"+getwallet,Toast.LENGTH_LONG).show();
        my_wallet_ammount = (TextView) view.findViewById(R.id.user_wallet);
      //  my_wallet_ammount.setText(getActivity().getString(R.string.currency)+WAmmount);
        db_cart = new DatabaseCartHandler(getActivity());
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener()

        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Fragment fm = new Home_fragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                    return true;
                }
                return false;
            }
        });


        total_amount = getArguments().getString("total");
        order_total_amount = getArguments().getString("total");
        getdate = getArguments().getString("getdate");
        gettime = getArguments().getString("gettime");
        getlocation_id = getArguments().getString("getlocationid");
        getstore_id = getArguments().getString("getstoreid");

        checkout = getArguments().getString( "checkout" );
        product_id= getArguments().getString( "product_id" );
        getDeliveryType = getArguments().getString( "delivery_type" );

        payble_ammount = (TextView) view.findViewById(R.id.payable_ammount);
        order_ammount = (TextView) view.findViewById(R.id.order_ammount);
        used_wallet_ammount = (TextView) view.findViewById(R.id.used_wallet_ammount);
        used_coupon_ammount = (TextView) view.findViewById(R.id.used_coupon_ammount);
        payble_ammount.setText(getActivity().getString(R.string.currency)+total_amount);
        order_ammount.setText(getActivity().getString(R.string.currency)+order_total_amount);


        checkBox_Wallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    rb_Cod.setChecked(false);
                    double wall_amt=Double.parseDouble(getwallet);
                    double p_amt=Double.parseDouble(order_total_amount);
                    use_wallet_for_order();
                    //Toast.makeText(getActivity(),"ww"+getwallet,Toast.LENGTH_LONG).show();
                    Coupon_and_wallet.setVisibility(View.VISIBLE);
                    Relative_used_wallet.setVisibility(View.VISIBLE);
                    if (rb_card.isChecked() || rb_Netbanking.isChecked() || rb_paytm.isChecked()) {
                        rb_card.setChecked(false);
                        rb_Netbanking.setChecked(false);
                        rb_paytm.setChecked(false);
                    }
                } else {
                    if (payble_ammount != null) {
                        rb_Cod.setText(getResources().getString(R.string.cash));
                        rb_card.setClickable(true);
                        rb_card.setTextColor(getResources().getColor(R.color.dark_black));
                        rb_Netbanking.setClickable(true);
                        rb_Netbanking.setTextColor(getResources().getColor(R.color.dark_black));
                        rb_paytm.setClickable(true);
                        rb_paytm.setTextColor(getResources().getColor(R.color.dark_black));
                        checkBox_coupon.setClickable(true);
                        checkBox_coupon.setTextColor(getResources().getColor(R.color.dark_black));
                    }
                    final String Ammount = SharedPref.getString(getActivity(), BaseURL.TOTAL_AMOUNT);
                    final String WAmmount = SharedPref.getString(getActivity(), BaseURL.KEY_WALLET_Ammount);
                    //Toast.makeText(getActivity(),"sd\n "+WAmmount.toString(),Toast.LENGTH_LONG).show();
                    my_wallet_ammount.setText(getActivity().getResources().getString(R.string.currency)+WAmmount);
                    payble_ammount.setText(getResources().getString(R.string.currency)+Ammount);
                    used_wallet_ammount.setText("");
                    Relative_used_wallet.setVisibility(View.GONE);
                    if (checkBox_coupon.isChecked()) {
                        final String ammount = SharedPref.getString(getActivity(), BaseURL.COUPON_TOTAL_AMOUNT);
                        payble_ammount.setText(getResources().getString(R.string.currency)+ammount);
                    }
                }
            }
        });
        checkBox_coupon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()

        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Promo_code_layout.setVisibility(View.VISIBLE);
                    Coupon_and_wallet.setVisibility(View.VISIBLE);
                    Relative_used_coupon.setVisibility(View.VISIBLE);
                    if (rb_Store.isChecked() || rb_Cod.isChecked() || rb_card.isChecked() || rb_Netbanking.isChecked() || rb_paytm.isChecked()) {
                        rb_Store.setChecked(false);
                        rb_Cod.setChecked(false);
                        rb_card.setChecked(false);
                        rb_Netbanking.setChecked(false);
                        rb_paytm.setChecked(false);
                    }
                } else {
                    et_Coupon.setText("");
                    Relative_used_coupon.setVisibility(View.GONE);
                    Promo_code_layout.setVisibility(View.GONE);
                }
            }
        });


        confirm = (RelativeLayout) view.findViewById(R.id.confirm_order);
        confirm.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
                    if (checkBox_Wallet.isChecked()){
                        getuser_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

                        Usewalletfororder(getuser_id,Used_Wallet_amount);
                        checked();

                    }
                    else {
                        checked();

                    }



                } else {
                    confirm.setEnabled(true);

                    ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
                }
            }
        });

        rel_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment=new DeliverTypeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fragment)
                        .addToBackStack(null).commit();

            }
        });
        return view;
    }

    private void use_wallet_for_order() {

        double pay_amt=Double.parseDouble(order_total_amount);
        double w_amt=Double.parseDouble(getwallet);

        if(w_amt<=0)
        {
            Toast.makeText(getActivity(),"You don't have enough wallet amount",Toast.LENGTH_LONG).show();
        }
        else
        {
           // checkBox_Wallet.setChecked(false);
            payble_ammount.setText(getResources().getString(R.string.currency)+String.valueOf(pay_amt));
            //SharedPref.putString(getActivity(), BaseURL.WALLET_TOTAL_AMOUNT, total_amount);
            double diff=w_amt-pay_amt;
            if(diff<=0)
            {
                my_wallet_ammount.setText(getResources().getString(R.string.currency)+"0");
            }
            else
            {
                my_wallet_ammount.setText(getResources().getString(R.string.currency)+String.valueOf(diff));
            }
            used_wallet_ammount.setText("(" + getResources().getString(R.string.currency) + String.valueOf(w_amt)+ ")");


        }

       // attemptOrderWithWallet(String.valueOf(w_amt-pay_amt));
    }

    private void attemptOrder() {
       // Toast.makeText( getActivity(),"attempt order",Toast.LENGTH_LONG ).show();
        ArrayList<HashMap<String, String>> items = db_cart.getCartAll();
        rewards = Double.parseDouble(db_cart.getColumnRewards());
        if (items.size() > 0) {
            JSONArray passArray = new JSONArray();
            for (int i = 0; i < items.size(); i++) {
                HashMap<String, String> map = items.get(i);
                JSONObject jObjP = new JSONObject();
                try {
                    jObjP.put("product_id", map.get("product_id"));
                    jObjP.put("qty", map.get("qty"));
                    jObjP.put("unit_value","");
                    jObjP.put("unit", map.get("unit"));
                    jObjP.put("price", map.get("price"));
                    jObjP.put("rewards", map.get("rewards"));
                    jObjP.put("store_id", map.get("sid"));
                    jObjP.put("book_class", map.get("book_class"));
                    jObjP.put("subject", map.get("subject"));
                    jObjP.put("language", map.get("language"));

                    passArray.put(jObjP);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            getuser_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

            if (ConnectivityReceiver.isConnected()) {

                Log.e(TAG, "from:" + gettime + "\ndate:" + getdate +
                        "\n" + "\nuser_id:" + getuser_id + "\n" + getlocation_id +"\n"+ getstore_id + "\ndata:" + passArray.toString());

//                Toast.makeText(getActivity(),"from:" + gettime + "\ndate:" + getdate +
//                        "\n" + "\nuser_id:" + getuser_id + "\n" + getlocation_id +"\n"+ getstore_id + "\ndata:" + passArray.toString(),Toast.LENGTH_LONG).show();
                try {

                    makeAddOrderRequest(getdate, gettime, getuser_id, getlocation_id, getstore_id, passArray);
                }
                catch (Exception ex)
                {
                    Toast.makeText(getActivity(),""+ex.getMessage(),Toast.LENGTH_LONG).show();
                }




            }
        }
    }

    private void makeAddOrderRequest(String date, String gettime, String userid, String
            location, String store_id, JSONArray passArray) {
        progressDialog.show();

        String tag_json_obj = "json_add_order_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("date", date);
        params.put("time", gettime);
        params.put("user_id", userid);
        params.put("location", location);
        params.put("store_id", store_id);
        params.put("delivery_charges", String.valueOf(deli_charges));
        params.put("total_ammount",total_amount);
        params.put("payment_method", getvalue);
        params.put("data", passArray.toString());
        params.put( "delivery_type",getDeliveryType );
//        Toast.makeText(getActivity(),"date "+date+"\ntime  "+gettime+"\n user_id "+userid+"\n location "+location+"\n srote_id "+store_id+"\n t_amt:- "+total_amount+"\n p-method"+getvalue+"\n"+passArray,Toast.LENGTH_LONG).show();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.ADD_ORDER_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    progressDialog.dismiss();
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        String msg = response.getString("data");
                        String msg_arb=response.getString("data_arb");
                        if(checkout.equalsIgnoreCase( "null" )) {
                            db_cart.clearCart();
                        }
                        else
                        {
                            db_cart.removeItemFromCart( product_id );
                        }
                        Bundle args = new Bundle();
                        Fragment fm = new Thanks_fragment();
                        args.putString("msg", msg);
                        args.putString("msgarb",msg_arb);
                        fm.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();


                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    private void attemptOrderWithWallet(String wamt) {

        ArrayList<HashMap<String, String>> items = db_cart.getCartAll();
        rewards = Double.parseDouble(db_cart.getColumnRewards());
        if (items.size() > 0) {
            JSONArray passArray = new JSONArray();
            for (int i = 0; i < items.size(); i++) {
                HashMap<String, String> map = items.get(i);
                JSONObject jObjP = new JSONObject();
                try {
                    jObjP.put("product_id", map.get("product_id"));
                    jObjP.put("qty", map.get("qty"));
                    jObjP.put("unit_value","");
                    jObjP.put("unit", map.get("unit"));
                    jObjP.put("price", map.get("price"));
                    jObjP.put("rewards", map.get("rewards"));
                    jObjP.put("store_id", map.get("sid"));
                    jObjP.put("book_class", map.get("book_class"));
                    jObjP.put("subject", map.get("subject"));
                    jObjP.put("language", map.get("language"));

                    passArray.put(jObjP);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            getuser_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

            if (ConnectivityReceiver.isConnected()) {

                Log.e(TAG, "from:" + gettime + "\ndate:" + getdate +
                        "\n" + "\nuser_id:" + getuser_id + "\n" + getlocation_id +"\n"+ getstore_id + "\ndata:" + passArray.toString());

//                Toast.makeText(getActivity(),"from:" + gettime + "\ndate:" + getdate +
//                        "\n" + "\nuser_id:" + getuser_id + "\n" + getlocation_id +"\n"+ getstore_id + "\ndata:" + passArray.toString(),Toast.LENGTH_LONG).show();
                try {

                    makeAddOrderWithWalletRequest(getdate, gettime, getuser_id, getlocation_id, getstore_id,wamt, passArray);
                }
                catch (Exception ex)
                {
                    Toast.makeText(getActivity(),""+ex.getMessage(),Toast.LENGTH_LONG).show();
                }




            }
        }
    }

    private void makeAddOrderWithWalletRequest(String date, String gettime, String userid, String
            location, String store_id,String wamt,JSONArray passArray) {
        progressDialog.show();
        String tag_json_obj = "json_add_order_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("date", date);
        params.put("time", gettime);
        params.put("user_id", userid);
        params.put("location", location);
        params.put("store_id", store_id);
        params.put("delivery_charges", String.valueOf(deli_charges));
        params.put("total_ammount",total_amount);
        params.put("payment_method", "Wallet");
        params.put("wallet_amount",wamt);
        params.put("data", passArray.toString());
        params.put( "delivery_type",getDeliveryType );
        // Toast.makeText(getActivity(),"\n t_amt:- "+total_amount+"\n p-method"+getvalue+"\n"+passArray,Toast.LENGTH_LONG).show();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_ORDER_WALLET, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    progressDialog.dismiss();
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        String msg = response.getString("data");
                        String msg_arb=response.getString("data_arb");
                        if(checkout.equalsIgnoreCase( "null" )) {
                            db_cart.clearCart();
                        }
                        else
                        {
                            db_cart.removeItemFromCart( product_id );
                        }
                        Bundle args = new Bundle();
                        Fragment fm = new Thanks_fragment();
                        args.putString("msg", msg);
                        args.putString("msgarb",msg_arb);
                        fm.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();


                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    private void Usewalletfororder(String userid, String Wallet) {
        String tag_json_obj = "json_add_order_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userid);
        params.put("wallet_amount", Wallet);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.Wallet_CHECKOUT, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    String status = response.getString("responce");

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
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void Use_Wallet_Ammont() {
        final String Wallet_Ammount = SharedPref.getString(getActivity(), BaseURL.KEY_WALLET_Ammount);
        final String Coupon_Ammount = SharedPref.getString(getActivity(), BaseURL.COUPON_TOTAL_AMOUNT);
        final String Ammount = SharedPref.getString(getActivity(), BaseURL.TOTAL_AMOUNT);
        if (NetworkConnection.connectionChecking(getActivity())) {
            RequestQueue rq = Volley.newRequestQueue(getActivity());
            StringRequest postReq = new StringRequest(Request.Method.POST, BaseURL.BASE_URL+"index.php/api/wallet_on_checkout",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("eclipse", "Response=" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                JSONArray Jarray = object.getJSONArray("final_amount");
                                for (int i = 0; i < Jarray.length(); i++) {
                                    JSONObject json_data = Jarray.getJSONObject(i);
                                    String Wallet_amount = json_data.getString("wallet");
                                     Used_Wallet_amount = json_data.getString("used_wallet");
                                    total_amount = json_data.getString("total");
                                    if (total_amount.equals("0")) {
                                        rb_Cod.setText("Home Delivery");
                                        getvalue = rb_Cod.getText().toString();
                                        rb_card.setClickable(false);
                                        rb_card.setTextColor(getResources().getColor(R.color.gray));
                                        rb_Netbanking.setClickable(false);
                                        rb_Netbanking.setTextColor(getResources().getColor(R.color.gray));
                                        rb_paytm.setClickable(false);
                                        rb_paytm.setTextColor(getResources().getColor(R.color.gray));
                                        checkBox_coupon.setClickable(false);
                                        checkBox_coupon.setTextColor(getResources().getColor(R.color.gray));
                                    } else {
                                        if (total_amount != null) {
                                            rb_Cod.setText("Cash On Delivery");
                                            rb_card.setClickable(true);
                                            rb_card.setTextColor(getResources().getColor(R.color.dark_black));
                                            rb_Netbanking.setClickable(true);
                                            rb_Netbanking.setTextColor(getResources().getColor(R.color.dark_black));
                                            rb_paytm.setClickable(true);
                                            rb_paytm.setTextColor(getResources().getColor(R.color.dark_black));
                                            checkBox_coupon.setClickable(true);
                                            checkBox_coupon.setTextColor(getResources().getColor(R.color.dark_black));
                                        }
                                    }
                                    payble_ammount.setText(getResources().getString(R.string.currency)+total_amount);
                                    used_wallet_ammount.setText("(" + getResources().getString(R.string.currency) + Used_Wallet_amount + ")");
                                    SharedPref.putString(getActivity(), BaseURL.WALLET_TOTAL_AMOUNT, total_amount);
                                    my_wallet_ammount.setText(getResources().getString(R.string.currency)+Wallet_amount);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error [" + error + "]");
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    if (checkBox_Wallet.isChecked()){
                        params.put("wallet_amount", Wallet_Ammount);
                    }else {
                        params.put("total_amount", Ammount);

                    }

                    if (checkBox_coupon.isChecked()) {
                        params.put("total_amount", Coupon_Ammount);
                    } else {
                        params.put("total_amount", Ammount);

                    }
                    return params;
                }
            };
            rq.add(postReq);
        } else {
            Intent intent = new Intent(getActivity(), NetworkError.class);
            startActivity(intent);
        }
    }

    private void Coupon_code() {
        final String Ammount = SharedPref.getString(getActivity(), BaseURL.TOTAL_AMOUNT);
        final String Wallet_Ammount = SharedPref.getString(getActivity(), BaseURL.WALLET_TOTAL_AMOUNT);
        final String Coupon_code = et_Coupon.getText().toString();
        if (NetworkConnection.connectionChecking(getActivity())) {
            RequestQueue rq = Volley.newRequestQueue(getActivity());
            StringRequest postReq = new StringRequest(Request.Method.POST, BaseURL.COUPON_CODE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("eclipse", "Response=" + response);
                            try {
                                JSONObject obj = new JSONObject(response);
                                total_amount = obj.getString("Total_amount");
                                String Used_coupon_amount = obj.getString("coupon_value");
                                if (obj.optString("responce").equals("true")) {
                                    payble_ammount.setText(getResources().getString(R.string.currency)+total_amount);
                                    SharedPref.putString(getActivity(), BaseURL.COUPON_TOTAL_AMOUNT, total_amount);
                                    Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                    used_coupon_ammount.setText("(" + getActivity().getResources().getString(R.string.currency) + Used_coupon_amount + ")");
                                    Promo_code_layout.setVisibility(View.GONE);

                                } else {
                                    Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                    et_Coupon.setText("");
                                    used_coupon_ammount.setText("");
                                    payble_ammount.setText(getResources().getString(R.string.currency)+total_amount);
                                    Promo_code_layout.setVisibility(View.GONE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error [" + error + "]");
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("coupon_code", Coupon_code);
                    if (checkBox_Wallet.isChecked()) {
                        params.put("payable_amount", Wallet_Ammount);
                    } else {
                        params.put("payable_amount", Ammount);
                    }
                    return params;
                }
            };
            rq.add(postReq);
        } else {
            Toast.makeText(getActivity(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }

    }


    private void checked() {
        if (checkBox_Wallet.isChecked()) {


                double wall_amt=Double.parseDouble(getwallet);
                double p_amt=Double.parseDouble(order_total_amount);

                if(wall_amt<p_amt)
                {
                    if(!rb_Cod.isChecked())
                    {
                        checkBox_Wallet.setChecked(false);
                        Toast.makeText(getActivity(), "You don't have enough wallet amount ", Toast.LENGTH_SHORT).show();
                    }


                }
                else
                {

                    double check=wall_amt-p_amt;
                    String ch="";
                    if(check<=0)
                    {
                        ch="0";
                    }
                    else
                    {
                        ch=String.valueOf(check);
                    }
                    attemptOrderWithWallet(ch);
                }



        }

            else if (rb_Cod.isChecked()) {
                attemptOrder();
            }


        else if (rb_Store.isChecked()) {
            attemptOrder();
        }

       else if (rb_card.isChecked()) {
            Intent myIntent = new Intent(getActivity(), PaymentActivity.class);
            if (checkBox_Wallet.isChecked()) {
                myIntent.putExtra("total", total_amount);
            } else {
                myIntent.putExtra("total", Prefrence_TotalAmmount);
                myIntent.putExtra("getdate", getdate);
                myIntent.putExtra("gettime", gettime);
                myIntent.putExtra("getlocationid", getlocation_id);
                myIntent.putExtra("getstoreid", getstore_id);
                myIntent.putExtra("getpaymentmethod", getvalue);
            }
            getActivity().startActivity(myIntent);
        }
        else if (rb_Netbanking.isChecked()) {
            Intent myIntent1 = new Intent(getActivity(), PaymentActivity.class);
            if (checkBox_Wallet.isChecked()) {
                myIntent1.putExtra("total", total_amount);

            } else {
                myIntent1.putExtra("total", Prefrence_TotalAmmount);
                myIntent1.putExtra("getdate", getdate);
                myIntent1.putExtra("gettime", gettime);
                myIntent1.putExtra("getlocationid", getlocation_id);
                myIntent1.putExtra("getstoreid", getstore_id);
                myIntent1.putExtra("getpaymentmethod", getvalue);
            }
            getActivity().startActivity(myIntent1);
        }
              else if (rb_paytm.isChecked()) {
            Intent myIntent1 = new Intent(getActivity(), Paytm.class);
            if (checkBox_Wallet.isChecked()) {
                myIntent1.putExtra("total", total_amount);

            } else {
                myIntent1.putExtra("total", Prefrence_TotalAmmount);
                myIntent1.putExtra("getdate", getdate);
                myIntent1.putExtra("gettime", gettime);
                myIntent1.putExtra("getlocationid", getlocation_id);
                myIntent1.putExtra("getstoreid", getstore_id);
                myIntent1.putExtra("getpaymentmethod", getvalue);
            }
            getActivity().startActivity(myIntent1);

        }
        else if (checkBox_coupon.isChecked()) {
            if (rb_Store.isChecked() || rb_Cod.isChecked()) {
                attemptOrder();
            } else {
                Toast.makeText(getActivity(), "Select Store Or Cod", Toast.LENGTH_SHORT).show();
            }


        }

        else
        {
            Toast.makeText(getActivity(),"Please Select Payment method for order",Toast.LENGTH_LONG
                    ).show();
        }


    }


    public void getRefresrh() {
        progressDialog.show();
        String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        StringRequest strReq = new StringRequest(Request.Method.GET, BaseURL.WALLET_REFRESH + user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObj = new JSONObject(response);
                            if (jObj.optString("success").equalsIgnoreCase("success")) {
                                String wallet_amount = jObj.getString("wallet");
                                //wa[0] =wallet_amount;
                                my_wallet_ammount.setText(getActivity().getString(R.string.currency)+wallet_amount);
                                getwallet=wallet_amount;
                                //Toast.makeText(getActivity(), "" + wallet_amount + "\n wa:-  " + wa[0].toString(), Toast.LENGTH_LONG).show();
                                // Wallet_Ammount.setText( my_wallet_ammount.setText(getActivity().getString(R.string.currency)+WAmmount);
                                //       );
                                SharedPref.putString(getActivity(), BaseURL.KEY_WALLET_Ammount, wallet_amount);
                            } else {
                                // Toast.makeText(DashboardPage.this, "" + jObj.optString("msg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
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
