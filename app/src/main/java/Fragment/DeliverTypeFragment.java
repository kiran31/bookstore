package Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.CouponAdapter;
import Config.BaseURL;
import Model.CouponModel;
import Module.Module;
import shoparounds.com.AppController;
import shoparounds.com.R;
import util.CustomVolleyJsonRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeliverTypeFragment extends android.app.Fragment {
    Module module;
    RecyclerView rv_coupon;
    CouponAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<CouponModel> couponList;
    public DeliverTypeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate( R.layout.fragment_deliver_type, container, false );
        rv_coupon=(RecyclerView)view.findViewById(R.id.rv_coupon);
        couponList=new ArrayList<>();
        module=new Module();
        layoutManager=new LinearLayoutManager(getActivity());

        rv_coupon.setLayoutManager(layoutManager);
        getCouponData();
        return view;
    }

    private void getCouponData() {
        String json_tag="json_coupon_tag";
        HashMap<String,String> map=new HashMap<>();

        CustomVolleyJsonRequest customVolleyJsonRequest=new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.GET_COUPON, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try
                {
                    boolean status=response.getBoolean("responce");
                    if(status)
                    {
                        JSONArray array=response.getJSONArray("data");
                        for(int i=0; i<array.length();i++)
                        {
                            JSONObject object=array.getJSONObject(i);
                            CouponModel couponModel=new CouponModel();
                            couponModel.setId(object.getString("id"));
                            couponModel.setCoupon_name(object.getString("coupon_name"));
                            couponModel.setCoupon_code(object.getString("coupon_code"));
                            couponModel.setValid_from(object.getString("valid_from"));
                            couponModel.setValid_to(object.getString("valid_to"));
                            couponModel.setValidity_type(object.getString("validity_type"));
                            couponModel.setProduct_name(object.getString("product_name"));
                            couponModel.setDiscount_type(object.getString("discount_type"));
                            couponModel.setDiscount_value(object.getString("discount_value"));
                            couponModel.setCart_value(object.getString("cart_value"));
                            couponModel.setUses_restriction(object.getString("uses_restriction"));
                            couponList.add(couponModel);
                        }

                        adapter=new CouponAdapter(getActivity(),couponList);
                        rv_coupon.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                    else
                    {
                       Toast.makeText(getActivity(),""+response.getString("data"),Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String msg=module.VolleyErrorMessage(error);
                if(!(msg.isEmpty() || msg.equals("")))
                {
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(customVolleyJsonRequest,json_tag);

    }

}
