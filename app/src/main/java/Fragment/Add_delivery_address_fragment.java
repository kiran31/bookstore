package Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Config.BaseURL;
import Module.Module;
import shoparounds.com.AppController;
import shoparounds.com.MainActivity;
import shoparounds.com.R;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.Session_management;



public class Add_delivery_address_fragment extends Fragment implements View.OnClickListener {

    private static String TAG = Add_delivery_address_fragment.class.getSimpleName();

     Module module;
    String chg="";
    String type="";
    private EditText et_phone, et_name,  et_address;
    private TextView et_pin,txt_note ;
    private RelativeLayout btn_update;
    private TextView tv_phone, tv_name, tv_pin, tv_house, tv_socity, select_city;
    private String getsocity = "";
    private ImageView image_normal,image_standard;
    LinearLayout lay_standard,lay_normal;
    private Session_management sessionManagement;

    private boolean isEdit = false;

    private String getlocation_id;
    ProgressDialog progressDialog;



    public Add_delivery_address_fragment() {
        // Required empty public constructor
    }
    //String pincodes [] ={"111111","2222222","3333333","333445"};

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
        View view = inflater.inflate(R.layout.fragment_add_delivery_address, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.add));

        sessionManagement = new Session_management(getActivity());

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");
        et_phone = (EditText) view.findViewById(R.id.et_add_adres_phone);
        et_name = (EditText) view.findViewById(R.id.et_add_adres_name);
        tv_phone = (TextView) view.findViewById(R.id.tv_add_adres_phone);
        tv_name = (TextView) view.findViewById(R.id.tv_add_adres_name);
        tv_pin = (TextView) view.findViewById(R.id.tv_add_adres_pin);
        et_pin = (TextView) view.findViewById(R.id.et_add_adres_pin);
        txt_note = (TextView) view.findViewById(R.id.txt_note);
        et_address = (EditText) view.findViewById(R.id.address);
        tv_house = (TextView) view.findViewById(R.id.tv_add_adres_home);
        tv_socity = (TextView) view.findViewById(R.id.tv_add_adres_socity);
        btn_update = (RelativeLayout) view.findViewById(R.id.btn_add_adres_edit);
        select_city = (TextView) view.findViewById(R.id.select_city);
//        image_normal=(ImageView)view.findViewById(R.id.image_normal);
//        image_standard=(ImageView)view.findViewById(R.id.image_standard);
//        lay_standard=(LinearLayout) view.findViewById(R.id.lay_standard);
//        lay_normal=(LinearLayout) view.findViewById(R.id.lay_normal);

        module=new Module();
        String getsocity_name = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_NAME);
        String getsocity_id = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_ID);

        Bundle args = getArguments();

        if (args != null) {
            getlocation_id = getArguments().getString("location_id");
            String get_name = getArguments().getString("name");
            String get_phone = getArguments().getString("mobile");
            String get_pine = getArguments().getString("pincode");
            String get_socity_id = getArguments().getString("socity_id");
            String get_socity_name = getArguments().getString("socity_name");
            String get_house = getArguments().getString("house");
            String del_type=getArguments().getString("type");
            if (TextUtils.isEmpty(get_name) && get_name == null) {
                isEdit = false;
            } else {
                isEdit = true;

              //  Toast.makeText(getActivity(), "edit", Toast.LENGTH_SHORT).show();

                et_name.setText(get_name);
                et_phone.setText(get_phone);
                et_pin.setText(get_socity_name);
                et_address.setText(get_house);
                select_city.setText("Gwalior");
                if(del_type.equals("normal"))
                {
                    type=del_type;
                    image_normal.setVisibility(View.VISIBLE);
                }
                else if(del_type.equals("standard"))
                {
                    type=del_type;
                    image_standard.setVisibility(View.VISIBLE);
                }
                sessionManagement.updateSocity(get_socity_name, get_socity_id);
            }
        }

        if (!TextUtils.isEmpty(getsocity_name)) {
            et_pin.setText(getsocity_name);
            select_city.setText("Gwalior");
            sessionManagement.updateSocity(getsocity_name, getsocity_id);
        }

        btn_update.setOnClickListener(this);
       // select_city.setOnClickListener(this);
        et_pin.setOnClickListener(this);
//        lay_normal.setOnClickListener(this);
//        lay_standard.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_add_adres_edit) {
            attemptEditProfile();
        } else if (id == R.id.et_add_adres_pin) {

            /*String getpincode = et_pin.getText().toString();

            if (!TextUtils.isEmpty(getpincode)) {*/

                Bundle args = new Bundle();
                Fragment fm = new Socity_fragment();
                //args.putString("pincode", getpincode);
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            /*} else {
                Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_pincode), Toast.LENGTH_SHORT).show();
            }*/

        }
//        else if(id == R.id.lay_normal)
//        {
//            if(image_normal.getVisibility()==View.GONE)
//            {
//                txt_note.setVisibility(View.GONE);
//                image_normal.setVisibility(View.VISIBLE);
//                image_standard.setVisibility(View.GONE);
//                type="normal";
//            }
//            else
//            {
//
//            }
//        }else if(id == R.id.lay_standard)
//        {
//            if(image_standard.getVisibility()==View.GONE)
//            {
//                getStandardCharges();
//                txt_note.setVisibility(View.VISIBLE);
//                image_standard.setVisibility(View.VISIBLE);
//                image_normal.setVisibility(View.GONE);
//                type="standard";
//
//
//            }
//
//        }
    }

    private void attemptEditProfile() {

//        tv_phone.setText(getResources().getString(R.string.receiver_mobile_number));
//        tv_pin.setText(getResources().getString(R.string.tv_reg_pincode));
//        tv_name.setText(getResources().getString(R.string.receiver_name_req));
//        tv_house.setText(getResources().getString(R.string.tv_reg_house));
//        tv_socity.setText(getResources().getString(R.string.tv_reg_socity));

//        tv_name.setTextColor(getResources().getColor(R.color.dark_gray));
//        tv_phone.setTextColor(getResources().getColor(R.color.dark_gray));
//        tv_pin.setTextColor(getResources().getColor(R.color.dark_gray));
//        tv_house.setTextColor(getResources().getColor(R.color.dark_gray));
//        tv_socity.setTextColor(getResources().getColor(R.color.dark_gray));

        String getphone = et_phone.getText().toString();
        String getname = et_name.getText().toString();
        String getpin = et_pin.getText().toString();
        String gethouse = et_address.getText().toString();
        String getsocity = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_ID);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(getphone)) {
            et_phone.setError(getResources().getString(R.string.enter_phone));
            //tv_phone.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_phone;
            cancel = true;
        } else if (!isPhoneValid(getphone)) {
            et_phone.setError(getResources().getString(R.string.phone_too_short));
//            tv_phone.setText(getResources().getString(R.string.phone_too_short));
//            tv_phone.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_phone;
            cancel = true;
        }

        if (TextUtils.isEmpty(getname)) {
            et_name.setError("Please Enter Name");
         //   tv_name.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_name;
            cancel = true;
        }

        if (TextUtils.isEmpty(getpin)) {
            et_pin.setError("Please Choose any one society");
            //tv_pin.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_pin;
            cancel = true;
        }

        if (TextUtils.isEmpty(gethouse)) {
            et_address.setError("Please Enter Address");
           // tv_house.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_address;
            cancel = true;
        }

//        if (TextUtils.isEmpty(getsocity) && getsocity == null) {
//            //tv_socity.setTextColor(getResources().getColor(R.color.colorPrimary));
//            focusView = select_city;
//            cancel = true;
//        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            if (ConnectivityReceiver.isConnected()) {

                String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

                // check internet connection
                if (ConnectivityReceiver.isConnected()) {
//                    if(type.equals("") || TextUtils.isEmpty(type))
//                    {
//                     Toast.makeText(getActivity(),"Please Select Any One Delivery Method",Toast.LENGTH_LONG).show();
//                    }
//                    else {
                        if (isEdit) {
                            makeEditAddressRequest(getlocation_id, getpin, getsocity, gethouse, getname, getphone,type);
                        } else {
                            makeAddAddressRequest(user_id, getpin, getsocity, gethouse, getname, getphone,type);
                        }
                    }
                }
            }
        }


    private boolean isPhoneValid(String phoneno) {
        //TODO: Replace this with your own logic
        return phoneno.length() > 9;
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeAddAddressRequest(String user_id, String pincode, String socity_id,
                                       String house_no, String receiver_name, String receiver_mobile,String type) {
        progressDialog.show();

        // Tag used to cancel the request
        String tag_json_obj = "json_add_address_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("pincode", pincode);
        params.put("socity_id", socity_id);
        params.put("house_no", house_no);
        params.put("receiver_name", receiver_name);
        params.put("receiver_mobile", receiver_mobile);
        params.put("delivery_type", type);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.ADD_ADDRESS_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        ((MainActivity) getActivity()).onBackPressed();

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

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeEditAddressRequest(String location_id, String pincode, String socity_id,
                                        String house_no, String receiver_name, String receiver_mobile,String type) {

        // Tag used to cancel the request
        String tag_json_obj = "json_edit_address_req";
        progressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("location_id", location_id);
        params.put("pincode", pincode);
        params.put("socity_id", socity_id);
        params.put("house_no", house_no);
        params.put("receiver_name", receiver_name);
        params.put("receiver_mobile", receiver_mobile);
        params.put("delivery_type", type);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.EDIT_ADDRESS_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        String msg = response.getString("data");
                        Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();

                        ((MainActivity) getActivity()).onBackPressed();

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
