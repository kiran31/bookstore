package Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.My_Cancel_Order_adapter;
import Config.BaseURL;
import Model.My_Cancel_order_model;
import Module.Module;
import shoparounds.com.AppController;
import shoparounds.com.MainActivity;
import shoparounds.com.MyOrderDetail;
import shoparounds.com.R;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonArrayRequest;
import util.RecyclerTouchListener;
import util.Session_management;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 02,December,2019
 */
public class My_cancel_order_fragment extends Fragment {

    private RecyclerView rv_mycancel;
    Module module;
    RelativeLayout rel_no;
    private List<My_Cancel_order_model> my_order_modelList = new ArrayList<>();
    TabHost tHost;
    ProgressDialog progressDialog;
    public My_cancel_order_fragment() {
    }

    private static String TAG = My_cancel_order_fragment.class.getSimpleName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_cancel_order, container, false);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");
        module=new Module();
        // handle the touch event if true
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // check user can press back button or not
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

//                    Fragment fm = new Home_fragment();
//                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                            .addToBackStack(null).commit();
                    return true;
                }
                return false;
            }
        });

        rv_mycancel = (RecyclerView) view.findViewById(R.id.rv_mycancel);
        rel_no = (RelativeLayout) view.findViewById(R.id.rel_no);
        rv_mycancel.setLayoutManager(new LinearLayoutManager(getActivity()));

        Session_management sessionManagement = new Session_management(getActivity());
        String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

        // check internet connection
        if (ConnectivityReceiver.isConnected())

        {
            makeGetOrderRequest(user_id);
        } else

        {
            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
        }

        rv_mycancel.addOnItemTouchListener(new
                RecyclerTouchListener(getActivity(), rv_mycancel, new RecyclerTouchListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position) {
                Bundle args = new Bundle();
                String sale_id = my_order_modelList.get(position).getSale_id();
                String date = my_order_modelList.get(position).getOn_date();
                String time = my_order_modelList.get(position).getDelivery_time_from();
                String total = my_order_modelList.get(position).getTotal_amount();
                String status = my_order_modelList.get(position).getStatus();
                String deli_charge = my_order_modelList.get(position).getDelivery_charge();
                Intent intent=new Intent(getContext(), MyOrderDetail.class);
                intent.putExtra("sale_id", sale_id);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("total", total);
                intent.putExtra("status", status);
                intent.putExtra("deli_charge", deli_charge);
                intent.putExtra("type","cancel");
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return view;
    }


    private void makeGetOrderRequest(String userid) {
        String tag_json_obj = "json_socity_req";
        progressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userid);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                BaseURL.GET_CANCEL_ORDERS, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                progressDialog.dismiss();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<My_Cancel_order_model>>() {
                }.getType();

                my_order_modelList = gson.fromJson(response.toString(), listType);
                My_Cancel_Order_adapter myPendingOrderAdapter = new My_Cancel_Order_adapter(my_order_modelList);
                rv_mycancel.setAdapter(myPendingOrderAdapter);
                myPendingOrderAdapter.notifyDataSetChanged();
                if(response.length()<=0)
                {
                    rel_no.setVisibility(View.VISIBLE);
                    rv_mycancel.setVisibility(View.GONE);
                }

                if (my_order_modelList.isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
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
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    }
