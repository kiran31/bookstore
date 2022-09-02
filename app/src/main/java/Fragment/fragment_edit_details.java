package Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import shoparounds.com.AppController;
import shoparounds.com.MainActivity;
import shoparounds.com.R;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.Session_management;

import static shoparounds.com.AppController.TAG;

public class fragment_edit_details extends Fragment {
    EditText name,address,phone;
    RelativeLayout edit_address;
    TextView pincode;
    Fonts.LatoBLack font,select_city;
    Dialog ProgressDialog ;
    Session_management session_management;
    String getlocation_id;
    Module module;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_delivery_address,container,false);
        initUi(view);
        session_management = new Session_management(getActivity());
        String getsocity_name = session_management.getUserDetails().get(BaseURL.KEY_SOCITY_NAME);

        ProgressDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);
        module=new Module();
        Bundle b = getArguments();
        getlocation_id = b.getString("location_id");
        name.setText(b.getString("name"));
        pincode.setText(b.getString("socity_name"));
        phone.setText(b.getString("mobile"));
        address.setText(b.getString("house"));
        select_city.setText("Gwalior");
        pincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fm = new Socity_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }
        });
        edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             attemptEditProfile();
            }
        });
        return  view;
    }
    private boolean isPhoneValid(String phoneno) {
        //TODO: Replace this with your own logic
        return phoneno.length() > 9;
    }
    private void attemptEditProfile() {

       /* phone.setText(getResources().getString(R.string.receiver_mobile_number));
        pincode.setText(getResources().getString(R.string.tv_reg_pincode));
        name.setText(getResources().getString(R.string.receiver_name_req));
        address.setText(getResources().getString(R.string.tv_reg_house));
        select_city.setText(getResources().getString(R.string.tv_reg_socity));
        name.setTextColor(getResources().getColor(R.color.dark_gray));
        phone.setTextColor(getResources().getColor(R.color.dark_gray));
        pincode.setTextColor(getResources().getColor(R.color.dark_gray));
        address.setTextColor(getResources().getColor(R.color.dark_gray));
        select_city.setTextColor(getResources().getColor(R.color.dark_gray));
*/      ProgressDialog.show();
        String getphone = phone.getText().toString();
        String getname = name.getText().toString();
        String getpin = pincode.getText().toString();
        String gethouse = address.getText().toString();
        String getsocity = session_management.getUserDetails().get(BaseURL.KEY_SOCITY_ID);
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(getphone)) {
            phone.setError(getResources().getString(R.string.enter_phone));
          //  phone.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView =phone;
            cancel = true;
            ProgressDialog.dismiss();
        } else if (!isPhoneValid(getphone)) {
            Toast.makeText(getActivity(), "Phone not valid", Toast.LENGTH_SHORT).show();
            phone.setError(getResources().getString(R.string.phone_too_short));
            focusView = phone;
            cancel = true;
            ProgressDialog.dismiss();
        }

        if (TextUtils.isEmpty(getname)) {
            name.setError("Please Enter Name");
            focusView = name;
            cancel = true;
            ProgressDialog.dismiss();
        }

        if (TextUtils.isEmpty(getpin)) {
            pincode.setError("Please Choose any one society");
           // Toast.makeText(getContext(), "Pincode not valid", Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "Pincode not valid", Toast.LENGTH_SHORT).show();
            focusView = pincode;
            cancel = true;
            ProgressDialog.dismiss();
        }

        if (TextUtils.isEmpty(gethouse)) {
            address.setError("Please Enter Address");
            Toast.makeText(getActivity(), "address not valid", Toast.LENGTH_SHORT).show();
            focusView = address;
            cancel = true;
            ProgressDialog.dismiss();
        }

        if (TextUtils.isEmpty(getsocity) && getsocity == null) {

            Toast.makeText(getActivity(), "Society can't be empty", Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "city can't be empty", Toast.LENGTH_SHORT).show();
            focusView = select_city;
            cancel = true;
            ProgressDialog.dismiss();
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

                String user_id = session_management.getUserDetails().get(BaseURL.KEY_ID);

                // check internet connection
                if (ConnectivityReceiver.isConnected()) {

                        makeEditAddressRequest(getlocation_id, getpin, getsocity, gethouse, getname, getphone);

                        //makeAddAddressRequest(user_id, getpin, getsocity, gethouse, getname, getphone);
                }
            }
        }
    }
    private void makeEditAddressRequest(String location_id, String pincode, String socity_id,
                                        String house_no, String receiver_name, String receiver_mobile) {
    ProgressDialog.show();
        // Tag used to cancel the request
        String tag_json_obj = "json_edit_address_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("location_id", location_id);
        params.put("pincode", pincode);
        params.put("socity_id", socity_id);
        params.put("house_no", house_no);
        params.put("receiver_name", receiver_name);
        params.put("receiver_mobile", receiver_mobile);

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
                ProgressDialog.dismiss();
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

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void initUi(View view) {
        name = view.findViewById(R.id.et_add_adres_name);
        address = view.findViewById(R.id.address);
        pincode = view.findViewById(R.id.et_add_adres_pin);
        phone = view.findViewById(R.id.et_add_adres_phone);
        edit_address = view.findViewById(R.id.btn_add_adres_edit);
        font = view.findViewById(R.id.address_text);
        select_city= view.findViewById(R.id.select_city);
        font.setText("Edit Address");
    }
}
