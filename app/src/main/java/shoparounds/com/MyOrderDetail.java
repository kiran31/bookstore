package shoparounds.com;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.My_order_detail_adapter;
import Config.BaseURL;
import Fragment.My_order_detail_fragment;
import Model.My_order_detail_model;
import Module.Module;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonArrayRequest;
import util.CustomVolleyJsonRequest;
import util.Session_management;

public class MyOrderDetail extends AppCompatActivity {
    private static String TAG = My_order_detail_fragment.class.getSimpleName();
     Dialog dialog;
    EditText et_remark;
    Button btn_yes,btn_no;
    Module module;
    private TextView tv_date, tv_time, tv_total, tv_delivery_charge;
    private RelativeLayout btn_cancle;
    private RecyclerView rv_detail_order;

    private String sale_id;
    ImageView back_button;
     SharedPreferences preferences;
    private List<My_order_detail_model> my_order_detail_modelList = new ArrayList<>();
    ProgressDialog progressDialog ;

    public MyOrderDetail() {
        // Required empty public constructor
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_order_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        module=new Module();
        dialog=new Dialog(MyOrderDetail.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_cancel_order_layout);
        dialog.setCanceledOnTouchOutside(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.order_detail));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyOrderDetail.this, MainActivity.class);
                startActivity(intent);
            }
        });

        progressDialog=new ProgressDialog(MyOrderDetail.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");

        tv_date = (TextView) findViewById(R.id.tv_order_Detail_date);
        tv_time = (TextView) findViewById(R.id.tv_order_Detail_time);
        tv_delivery_charge = (TextView) findViewById(R.id.tv_order_Detail_deli_charge);
        tv_total = (TextView) findViewById(R.id.tv_order_Detail_total);
        btn_cancle = (RelativeLayout) findViewById(R.id.btn_order_detail_cancle);
        rv_detail_order = (RecyclerView) findViewById(R.id.rv_order_detail);

        rv_detail_order.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        sale_id = getIntent().getStringExtra("sale_id");
        String total_rs = getIntent().getStringExtra("total");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String status = getIntent().getStringExtra("status");
        String deli_charge = getIntent().getStringExtra("deli_charge");
        String type = getIntent().getStringExtra("type");

        if (status.equals("0")) {
            btn_cancle.setVisibility(View.VISIBLE);
        } else {
            btn_cancle.setVisibility(View.GONE);
        }

        tv_total.setText(total_rs);
        tv_date.setText(getResources().getString(R.string.date) + date);
        preferences = getSharedPreferences("lan", MODE_PRIVATE);
        String language=preferences.getString("language","");
        if (language.contains("spanish")) {
           time=time.replace("pm","م");
           time=time.replace("am","ص");
            tv_time.setText(getResources().getString(R.string.time) + time);

        }else {
            tv_time.setText(getResources().getString(R.string.time) + time);


        }

        tv_delivery_charge.setText(getResources().getString(R.string.delivery_charge) + deli_charge);

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            if(type.equals("cancel"))
            {
                makeGetCancelOrderDetailRequest(sale_id);
            }
            else
            {
                makeGetOrderDetailRequest(sale_id);
            }

        } else {
            Toast.makeText(MyOrderDetail.this, "Error Network Issues", Toast.LENGTH_SHORT).show();
            // ((MainActivity) getApplication()).onNetworkConnectionChanged(false);
        }

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_no=(Button)dialog.findViewById(R.id.btn_no);
                btn_yes=(Button)dialog.findViewById(R.id.btn_yes);
                et_remark=(EditText) dialog.findViewById(R.id.et_remark);
                dialog.show();

                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Session_management sessionManagement = new Session_management(MyOrderDetail.this);
                        String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

                        String remark=et_remark.getText().toString();
                        if(remark.isEmpty())
                        {
                            et_remark.setError("Please provide a reason");
                            et_remark.requestFocus();
                        }
                        else if(remark.length()<20)
                        {
                            et_remark.setError("Too short");
                            et_remark.requestFocus();

                        }
                        else
                        {
                            if (ConnectivityReceiver.isConnected()) {
                                makeDeleteOrderRequest(sale_id, user_id,remark);

                            }

                        }
                        // check internet connection
                        }
                });

                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                });
              //  showDeleteDialog();
            }
        });

    }

    private void makeGetCancelOrderDetailRequest(String sale_id) {

        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("sale_id", sale_id);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                BaseURL.ORDER_CANCEL_DETAIL_URL, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                Gson gson = new Gson();
                Type listType = new TypeToken<List<My_order_detail_model>>() {
                }.getType();

                my_order_detail_modelList = gson.fromJson(response.toString(), listType);

                My_order_detail_adapter adapter = new My_order_detail_adapter(my_order_detail_modelList);
                rv_detail_order.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (my_order_detail_modelList.isEmpty()) {
                    Toast.makeText(MyOrderDetail.this, getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(MyOrderDetail.this, "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    // alertdialog for cancle order
    private void showDeleteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyOrderDetail.this);
        alertDialog.setMessage(getResources().getString(R.string.cancle_order_note));
        alertDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {



                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    /**
     * Method to make json array request where json response starts wtih
     */
    private void makeGetOrderDetailRequest(String sale_id) {

        // Tag used to cancel the request
        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("sale_id", sale_id);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                BaseURL.ORDER_DETAIL_URL, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                Gson gson = new Gson();
                Type listType = new TypeToken<List<My_order_detail_model>>() {
                }.getType();

                my_order_detail_modelList = gson.fromJson(response.toString(), listType);

                My_order_detail_adapter adapter = new My_order_detail_adapter(my_order_detail_modelList);
                rv_detail_order.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (my_order_detail_modelList.isEmpty()) {
                    Toast.makeText(MyOrderDetail.this, getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(MyOrderDetail.this, "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeDeleteOrderRequest(String sale_id, String user_id,String remark) {

        progressDialog.show();

        // Tag used to cancel the request
        String tag_json_obj = "json_delete_order_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("sale_id", sale_id);
        params.put("user_id", user_id);
        params.put("remark", remark);


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.DELETE_ORDER_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressDialog.dismiss();
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        String msg = response.getString("message");
                        dialog.dismiss();
                        Toast.makeText(MyOrderDetail.this, "" + msg, Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(MyOrderDetail.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        // ((MainActivity) getActivity()).onBackPressed();

                    } else {
                        String error = response.getString("error");
                        Toast.makeText(MyOrderDetail.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
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
                    Toast.makeText(MyOrderDetail.this, "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }
}
