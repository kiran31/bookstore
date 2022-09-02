package Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import Config.BaseURL;
import Config.SharedPref;
import Module.Module;
import shoparounds.com.AppController;
import shoparounds.com.MainActivity;
import shoparounds.com.R;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.DatabaseCartHandler;
import util.Session_management;

public class Delivery_payment_detail_fragment extends Fragment {

    private static String TAG = Delivery_payment_detail_fragment.class.getSimpleName();

   //String charges="";
    String chg="";
     Module module;
    private TextView tv_timeslot, tv_address, tv_total,tvstan;
    private LinearLayout btn_order;

    private TextView recivername,mobileno,pincode,Address,tvItems,tvMrp,tvDiscount,tvDelivary,tvSubTotal ,tvDeliveryType;

    private String getlocation_id = "";
    private String gettime = "";
    private String getdate = "";
    private String getuser_id = "";
    private String getstore_id = "";
    String getDeliveryType ;

    private int deli_charges;
    String checkout ,product_id ;
    Double total;
    SharedPreferences preferences;
    private DatabaseCartHandler db_cart;
    private Session_management sessionManagement;
    ProgressDialog progressDialog;
    String buy_now_tot ,type;
    RelativeLayout rel_stan;


    public Delivery_payment_detail_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_confirm_order, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.payment));
        module=new Module();
        db_cart = new DatabaseCartHandler(getActivity());
        sessionManagement = new Session_management(getActivity());
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");
        tv_timeslot = (TextView) view.findViewById(R.id.textTimeSlot);
        // tv_address = (TextView) view.findViewById(R.id.txtAddress);
        //tv_item = (TextView) view.findViewById(R.id.textItems);
        //tv_total = (TextView) view.findViewById(R.id.textPrice);
        // tv_total = (TextView) view.findViewById(R.id.txtTotal);
        tvItems =view.findViewById( R.id.tvItems );
        tvMrp = view.findViewById( R.id.tvMrp );
        tvDiscount = view.findViewById( R.id.tvDiscount );
        tvDelivary = view.findViewById( R.id.tvDelivary );
        tvSubTotal = view.findViewById( R.id.tvSubTotal );
        tvstan = view.findViewById( R.id.tvstan );
        recivername =view.findViewById( R.id.recivername );
        mobileno = view.findViewById( R.id.mobileno );
        pincode = view.findViewById( R.id.pincode );
        Address = view.findViewById( R.id.Address );
        rel_stan = view.findViewById( R.id.rel_stan );
        tvDeliveryType=view.findViewById( R.id.tvdelivery_type );

        // Houseno = view.findViewById( R.idi.Houseno );
        //  Society = view.findViewById( R.d.Society );



        btn_order = (LinearLayout) view.findViewById(R.id.btn_order_now);

        getdate = getArguments().getString("getdate");


            gettime = getArguments().getString("time");

        getlocation_id = getArguments().getString("location_id");
        getstore_id = getArguments().getString("store_id");
        getDeliveryType = getArguments().getString( "delivery_type" );
        deli_charges = Integer.parseInt(getArguments().getString("deli_charges"));

        String name = getArguments().getString("name");
        String phone = getArguments().getString( "phone" );
        String house = getArguments().getString( "house" );
        String pin = getArguments().getString( "pin" );
        String societys = getArguments().getString( "society" );
        checkout = getArguments().getString( "checkout" );
        product_id=getArguments().getString( "product_id" );
        buy_now_tot =getArguments().getString( "total" );
        type =getArguments().getString( "type" );
        tv_timeslot.setText(getdate + " " + gettime);
        tvDeliveryType.setText( getDeliveryType.toUpperCase() );
        //tv_address.setText(getaddress);

//        if (getDeliveryType .equals( "standard" ))
//        {
//          //  deli_charges = Integer.parseInt( getStandardCharges() );
//
//
//            charges=sessionManagement.getStandardCharges();
//            tvDelivary.setText(getActivity().getResources().getString(R.string.currency)+ charges );
//
//          //  tvstan.setText(getActivity().getResources().getString(R.string.currency)+String.valueOf(charges));
//        }
//        else if (getDeliveryType.equals( "normal" ))
//        {
//
//            tvDelivary.setText(getActivity().getResources().getString(R.string.currency)+ deli_charges );
//        }


//        tv_total.setText("" + db_cart.getTotalAmount());
        //  tv_item.setText("" + db_cart.getWishlistCount());
        recivername.setText( name );
        mobileno.setText( phone );
//        tvDelivary.setText( deli_charges );
        // houseno.setText( house );
        pincode.setText( pin );
        Address.setText( societys );






        if (checkout.equalsIgnoreCase( "null" )) {

            tvItems.setText( String.valueOf( db_cart.getCartCount() ) );


                total = Double.parseDouble( db_cart.getTotalAmount() ) + deli_charges;
            // String mrp= String.valueOf(db_cart.getTotalMRP());
            String price = String.valueOf( db_cart.getTotalAmount() );
            tvMrp.setText( getResources().getString( R.string.currency ) + price );

           tvDelivary.setText( getResources().getString( R.string.currency ) + deli_charges );
            tvSubTotal.setText( getResources().getString( R.string.currency ) + total );
        }
        else
        {
             ArrayList<HashMap<String, String>> list = db_cart.getCartProduct(Integer.parseInt( product_id ));
            HashMap<String,String> map = list.get( 0 );
          double p = Double.parseDouble( ( map.get( "unit_price" ) ) );
          double q = Double.parseDouble( ( map.get("qty") ) );
            double t_price = p*q;
             tvItems.setText("1");

            tvMrp.setText( getResources().getString( R.string.currency ) + t_price );

            total = t_price+deli_charges;
            tvDelivary.setText( getResources().getString( R.string.currency ) + deli_charges );
            tvSubTotal.setText( getResources().getString( R.string.currency ) + total );
         //    Toast.makeText(getActivity() ,"" +list.size(),Toast.LENGTH_LONG ).show();
        }

        //  tv_total.setText("" + db_cart.getTotalAmount());
        //tv_item.setText("" + db_cart.getWishlistCount());
//        tv_total.setText(getResources().getString(R.string.tv_cart_item) + db_cart.getCartCount() + "\n" +
//                getResources().getString(R.string.amount) + db_cart.getTotalAmount() + "\n" +
//                getResources().getString(R.string.delivery_charge) + deli_charges + "\n" +
//                getResources().getString(R.string.total_amount) +
//                db_cart.getTotalAmount() + " + " + deli_charges + " = " + total+ getResources().getString(R.string.currency));
//
        recivername.setText(name);
        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if (ConnectivityReceiver.isConnected()) {
                    Fragment fm = new Payment_fragment();
                    Bundle args = new Bundle();
                    args.putString("total", String.valueOf(total));
                    args.putString("getdate", getdate);
                    args.putString("gettime", gettime);
                    args.putString("getlocationid", getlocation_id);
                    args.putString("getstoreid", getstore_id);
                    args.putString( "checkout",checkout );
                    args.putString( "product_id",product_id );
                    args.putString( "deli_charges", String.valueOf(deli_charges));
                    args.putString( "delivery_type",getDeliveryType );
                    fm.setArguments(args);
                    progressDialog.dismiss();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                    SharedPref.putString(getActivity(), BaseURL.TOTAL_AMOUNT, String.valueOf(total));
                } else {
                    ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
                    progressDialog.dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    //    private void attemptOrder() {
//        // retrive data from cart database
//        ArrayList<HashMap<String, String>> items = db_cart.getCartAll();
//        if (items.size() > 0) {
//            JSONArray passArray = new JSONArray();
//            for (int i = 0; i < items.size(); i++) {
//                HashMap<String, String> map = items.get(i);
//                JSONObject jObjP = new JSONObject();
//                try {
//                    jObjP.put("product_id", map.get("product_id"));
//                    jObjP.put("qty", map.get("qty"));
//                    jObjP.put("unit_value", map.get("unit_value"));
//                    jObjP.put("unit", map.get("unit"));
//                    jObjP.put("price", map.get("price"));
//
//                    passArray.put(jObjP);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            getuser_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
//
//            if (ConnectivityReceiver.isConnected()) {
//
//                Log.e(TAG, "from:" + gettime + "\ndate:" + getdate +
//                        "\n" + "\nuser_id:" + getuser_id + "\n" + getlocation_id + "\ndata:" + passArray.toString());
//
//                makeAddOrderRequest(getdate, gettime, getuser_id, getlocation_id, passArray);
//            }
//        }
//    }

    /**
     * Method to make json object request where json response starts wtih
     */
//    private void makeAddOrderRequest(String date, String gettime, String userid, String location, JSONArray passArray) {
//        String tag_json_obj = "json_add_order_req";
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("date", date);
//        params.put("time", gettime);
//        params.put("user_id", userid);
//        params.put("location", location);
//        params.put("data", passArray.toString());
//        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
//                BaseURL.ADD_ORDER_URL, params, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, response.toString());
//
//                try {
//                    Boolean status = response.getBoolean("responce");
//                    if (status) {
//
//                        String msg = response.getString("data");
//
////                        db_cart.clearCart();
////                        ((MainActivity) getActivity()).setCartCounter("" + db_cart.getWishlistCount());
//                      //  Double total = Double.parseDouble(db_cart.getTotalAmount()) + deli_charges;
//                        Bundle args = new Bundle();
//                        Fragment fm = new Payment_fragment();
//                        args.putString("msg", msg);
//
//                        args.putString("total", String.valueOf(total));
//                        fm.setArguments(args);
//                        FragmentManager fragmentManager = getFragmentManager();
//                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                                .addToBackStack(null).commit();
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
//    }

    public void getStandardCharges()
    {
        final String[] ch = {};
        progressDialog.show();
        String json_tag="json_charges";
        HashMap<String,String> map=new HashMap<>();
        CustomVolleyJsonRequest customVolleyJsonRequest=new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.GET_STANDARD_CHARGES, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    progressDialog.dismiss();
                    String status=response.getString("status");
                    if(status.equals("success"))
                    {
                        chg =response.getString("data");
                       // txt_note.setText("Note : standard delivery charges "+getActivity().getResources().getString(R.string.currency)+String.valueOf(chg));

                        sessionManagement.setStandardCharges(chg);

                        //String h=sessionManagement.getStandardCharges();
                        //Toast.makeText(getActivity(),""+chg+"\n "+h, Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex)
                {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),""+ex.getMessage(),Toast.LENGTH_LONG).show();
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
        AppController.getInstance().addToRequestQueue(customVolleyJsonRequest,json_tag);


    }

}
