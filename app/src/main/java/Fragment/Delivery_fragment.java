package Fragment;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.Delivery_get_address_adapter;
import Config.BaseURL;
import Config.SharedPref;
import Model.Delivery_address_model;
import Module.Module;
import shoparounds.com.AppController;
import shoparounds.com.MainActivity;
import shoparounds.com.R;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.DatabaseCartHandler;
import util.Session_management;

import static android.content.Context.MODE_PRIVATE;



public class Delivery_fragment extends Fragment implements View.OnClickListener {

    private static String TAG = Delivery_fragment.class.getSimpleName();

     Module module;
    private TextView tv_afternoon, tv_morning, tv_total, tv_item, tv_socity ,txt_note;

    private TextView tv_date, tv_time;
    private EditText et_address;
    private RelativeLayout btn_checkout, tv_add_adress;
    private RecyclerView rv_address;

    private Delivery_get_address_adapter adapter;
    private List<Delivery_address_model> delivery_address_modelList = new ArrayList<>();

    private DatabaseCartHandler db_cart;
SharedPreferences preferences;
    private Session_management sessionManagement;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private String gettime = "";
    private String getdate = "";
    String typedelivery = "" ;
    String chg="";

    private ImageView image_normal,image_standard;
    LinearLayout lay_standard,lay_normal;

    private String deli_charges ,checkout;
    String store_id ,product_id;
    String buy_now_tot ,type;

    ProgressDialog progressDialog;
String language;
    public Delivery_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delivery_time, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.delivery));
        Bundle bundle = getArguments();
        module=new Module();
        checkout = bundle.getString( "checkout" );
        product_id = bundle.getString( "product_id" );
        buy_now_tot =bundle.getString( "total" );
      //  type =bundle.getString( "type" );

        store_id = SharedPref.getString(getActivity(), BaseURL.STORE_ID);
        preferences = getActivity().getSharedPreferences("lan", MODE_PRIVATE);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");

        tv_date = (TextView) view.findViewById(R.id.tv_deli_date);
        tv_time = (TextView) view.findViewById(R.id.tv_deli_fromtime);
        tv_add_adress = (RelativeLayout) view.findViewById(R.id.tv_deli_add_address);
        tv_total = (TextView) view.findViewById(R.id.tv_deli_total);
        tv_item = (TextView) view.findViewById(R.id.tv_deli_item);
        btn_checkout = (RelativeLayout) view.findViewById(R.id.btn_deli_checkout);

        rv_address = (RecyclerView) view.findViewById(R.id.rv_deli_address);
        rv_address.setLayoutManager(new LinearLayoutManager(getActivity()));
        //tv_socity = (TextView) view.findViewById(R.id.tv_deli_socity);
        //et_address = (EditText) view.findViewById(R.id.et_deli_address);

        image_normal=(ImageView)view.findViewById(R.id.image_normal);
        image_standard=(ImageView)view.findViewById(R.id.image_standard);
        lay_standard=(LinearLayout) view.findViewById(R.id.lay_standard);
        lay_normal=(LinearLayout) view.findViewById(R.id.lay_normal);
        txt_note = (TextView) view.findViewById(R.id.txt_note);
        lay_normal.setOnClickListener( this );
        lay_standard.setOnClickListener( this );

        db_cart = new DatabaseCartHandler( getActivity());
        tv_total.setText(db_cart.getTotalAmount());
        tv_item.setText("" + db_cart.getCartCount());

        // get session user data
        sessionManagement = new Session_management(getActivity());
        String getsocity = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_NAME);
        String getaddress = sessionManagement.getUserDetails().get(BaseURL.KEY_HOUSE);

        //tv_socity.setText("Socity Name: " + getsocity);
        //et_address.setText(getaddress);

        tv_date.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        tv_add_adress.setOnClickListener(this);
        btn_checkout.setOnClickListener(this);

        String date = sessionManagement.getdatetime().get(BaseURL.KEY_DATE);

      //  String time = sessionManagement.getdatetime().get(BaseURL.KEY_TIME);

        String time = sessionManagement.getdatetime().get(BaseURL.KEY_TIME);



        if (date != null && time != null) {

            getdate = date;
            gettime = time;

            try {
                String inputPattern = "yyyy-MM-dd";
                String outputPattern = "dd-MM-yyyy";
                SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                Date date1 = inputFormat.parse(getdate);
                String str = outputFormat.format(date1);

                tv_date.setText(getResources().getString(R.string.delivery_date) + str);

            } catch (ParseException e) {
                e.printStackTrace();

                tv_date.setText(getResources().getString(R.string.delivery_date) + getdate);
            }
            language=preferences.getString("language","");
            if (language.contains("spanish")) {
                String timeset=time;
                 timeset=timeset.replace("PM","م");
                 timeset=timeset.replace("AM","ص");
                tv_time.setText(timeset);

            }
            else {

                tv_time.setText(time);
            }
        }




        if (ConnectivityReceiver.isConnected()) {
            String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
            makeGetAddressRequest(user_id);
        } else {
            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_deli_checkout) {
            String c_time=getCurrentTime(getdate);
            //Toast.makeText(getActivity(),"dt :-- "+getCurrentTime(getdate),Toast.LENGTH_LONG).show();
            attemptOrder();
        } else if (id == R.id.tv_deli_date) {
            getdate();
        } else if (id == R.id.tv_deli_fromtime) {

            if (TextUtils.isEmpty(getdate)) {
                Toast.makeText(getActivity(), getResources().getString(R.string.please_select_date), Toast.LENGTH_SHORT).show();
            } else {
                Bundle args = new Bundle();
                Fragment fm = new View_time_fragment();
                args.putString("date", getdate);
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }
        } else if (id == R.id.tv_deli_add_address) {

            sessionManagement.updateSocity("", "");

            Fragment fm = new Add_delivery_address_fragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();

        }
        if(id == R.id.lay_normal)
        {
            if(image_normal.getVisibility()==View.GONE)
            {
                txt_note.setVisibility(View.GONE);
                image_normal.setVisibility(View.VISIBLE);
                image_standard.setVisibility(View.GONE);
                typedelivery="normal";

            }
            else
            {

            }
        }else if(id == R.id.lay_standard)
        {
            if(image_standard.getVisibility()==View.GONE)
            {
                getStandardCharges();
                txt_note.setVisibility(View.VISIBLE);
                image_standard.setVisibility(View.VISIBLE);
                image_normal.setVisibility(View.GONE);
                typedelivery="standard";

            }

        }

    }

    private void getdate() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        progressDialog.show();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        getdate = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                        tv_date.setText(getResources().getString(R.string.delivery_date) + getdate);
                            progressDialog.dismiss();
                        try {
                            String inputPattern = "yyyy-MM-dd";
                            String outputPattern = "dd-MM-yyyy";
                            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                            Date date = inputFormat.parse(getdate);
                            String str = outputFormat.format(date);

                            tv_date.setText(getResources().getString(R.string.delivery_date) + str);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            tv_date.setText(getResources().getString(R.string.delivery_date) + getdate);
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                progressDialog.dismiss();
                getdate= "";

            }
        } );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();

    }

    private void attemptOrder() {


        //String getaddress = et_address.getText().toString();
        progressDialog.show();
        String location_id = "";
        String address = "";

        boolean cancel = false;

        if (TextUtils.isEmpty(getdate)) {
            Toast.makeText(getActivity(), "Please select date", Toast.LENGTH_SHORT).show();
            cancel = true;
            progressDialog.dismiss();
        }
       else if(typedelivery.equals("") || TextUtils.isEmpty(typedelivery))
                    {
                     Toast.makeText(getActivity(),"Please Select Any One Delivery Method",Toast.LENGTH_LONG).show();
                        cancel = true;
            progressDialog.dismiss();
                    }
//        else if (TextUtils.isEmpty(gettime)) {
//            Toast.makeText(getActivity(), getResources().getString(R.string.please_select_date_time), Toast.LENGTH_SHORT).show();
//            cancel = true;
//            progressDialog.dismiss();
//        }

        if (!delivery_address_modelList.isEmpty()) {
            if (adapter.ischeckd()) {
                location_id = adapter.getlocation_id();
                address = adapter.getaddress();
                progressDialog.dismiss();
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.please_select_address), Toast.LENGTH_SHORT).show();
                cancel = true;
                progressDialog.dismiss();
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.please_add_address), Toast.LENGTH_SHORT).show();
            cancel = true;
            progressDialog.dismiss();
        }

        /*if (TextUtils.isEmpty(getaddress)) {
            Toast.makeText(getActivity(), "Please add your address", Toast.LENGTH_SHORT).show();
            cancel = true;
        }*/

        if (!cancel) {
          //  Toast.makeText(getActivity(), "date:"+deli_charges, Toast.LENGTH_SHORT).show();

            sessionManagement.cleardatetime();
            gettime=getCurrentTime(getdate);
            Bundle args = new Bundle();
            Fragment fm = new Delivery_payment_detail_fragment();
            HashMap<String,String> addmap = adapter.getAlladdress();
            String name = addmap.get( "name" );
            String phone = addmap.get("phone");
            String society = addmap.get("society");
            String pin = addmap.get("pin");
            String house = addmap.get("house");
            String type = addmap.get("type");
            args.putString("getdate", getdate);
            args.putString("time", gettime);
            args.putString("location_id", location_id);
            args.putString("address", address);

            args.putString("store_id", store_id);
            args.putString("name",name);
            args.putString( "pin",pin );
            args.putString( "house",house );
            args.putString( "society",society );
            args.putString( "phone",phone );
            args.putString( "checkout" ,checkout );
            args.putString( "product_id",product_id );
            args.putString( "total",buy_now_tot );
            args.putString( "type",type );
            args.putString( "delivery_type",typedelivery );

            if(typedelivery.equalsIgnoreCase("standard"))
            {
                String std_chrg=sessionManagement.getStandardCharges();
                args.putString("deli_charges", std_chrg);
            }
            else
            {
                args.putString("deli_charges", deli_charges);
            }


        //    Toast.makeText(getActivity(),""+typedelivery,Toast.LENGTH_LONG).show();
            fm.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();

        }
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeGetAddressRequest(String user_id) {

        // Tag used to cancel the request
        String tag_json_obj = "json_get_address_req";
        progressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_ADDRESS_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        delivery_address_modelList.clear();

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Delivery_address_model>>() {
                        }.getType();

                        delivery_address_modelList = gson.fromJson(response.getString("data"), listType);

                        //RecyclerView.Adapter adapter1 = new Delivery_get_address_adapter(delivery_address_modelList);
                        adapter = new Delivery_get_address_adapter(delivery_address_modelList,getActivity());
                        //((Delivery_get_address_adapter) adapter).setMode(Attributes.Mode.Single);
                        rv_address.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        if (delivery_address_modelList.isEmpty()) {
                            if (getActivity() != null) {
                                //Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
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

    @Override
    public void onPause() {
        super.onPause();
        // unregister reciver
        getActivity().unregisterReceiver(mCart);
    }

    @Override
    public void onResume() {
        super.onResume();
        // register reciver
        getActivity().registerReceiver(mCart, new IntentFilter("Grocery_delivery_charge"));
    }

    // broadcast reciver for receive data
    private BroadcastReceiver mCart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("update")) {
                //updateData();
                deli_charges = intent.getStringExtra("charge");
              //  Toast.makeText(getActivity(), "dfghjkl"+typedelivery, Toast.LENGTH_SHORT).show();

                Double total = Double.parseDouble(db_cart.getTotalAmount()) + Integer.parseInt(deli_charges);

                tv_total.setText("" + db_cart.getTotalAmount() + " + " + deli_charges + " = "  + total+ getActivity().getResources().getString(R.string.currency));
            }
        }
    };


    public String getCurrentTime(String getdate)
    {
        sessionManagement.cleardatetime();


        String curr="";
        try
        {
            Date date=new Date();
            SimpleDateFormat smdf=new SimpleDateFormat("HH:mm aa");
            String ct=set12TimeFormat(smdf.format(date));
            curr=ct+"-"+ct;
            sessionManagement.creatdatetime(getdate,curr);

        }
        catch (Exception ex)
        {
            Toast.makeText(getActivity(),""+ex.getMessage(),Toast.LENGTH_LONG).show();
        }
        return curr;
    }

    public String set12TimeFormat(String time)
    {
        String tm="";
         String[] time_arr=time.split(":");
         int t=Integer.parseInt(time_arr[0].toString());
         if(t<=12)
         {
             if(t<10)
             {
                 tm="0"+String.valueOf(t);
             }
             else
             {
                 tm=String.valueOf(t);
             }

         }
         else
         {
             int tmm=t-12;
             if(tmm<10)
             {
                 tm="0"+String.valueOf(tmm);
             }
             else
             {
                 tm=String.valueOf(tmm);
             }

         }

         String c_tm=tm+":"+time_arr[1].toString();
         return c_tm;
    }

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
                        txt_note.setText("Note : standard delivery charges "+getActivity().getResources().getString(R.string.currency)+String.valueOf(chg));

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
