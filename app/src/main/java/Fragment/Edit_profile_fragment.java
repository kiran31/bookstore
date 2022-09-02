package Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import Config.BaseURL;
import Module.Module;
import de.hdodenhof.circleimageview.CircleImageView;
import shoparounds.com.AppController;
import shoparounds.com.MainActivity;
import shoparounds.com.R;
import util.CustomVolleyJsonRequest;
import util.Session_management;

import static Config.BaseURL.KEY_CNT;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Rajesh Dabhi on 28/6/2017.
 */

public class Edit_profile_fragment extends Fragment implements View.OnClickListener {

     Module module;
    ProgressDialog progressDialog;
    int flag=1;
    private static String TAG = Edit_profile_fragment.class.getSimpleName();
    Session_management session_management;
    private final int IMG_REQUEST=1;

    private EditText et_phone, et_name, et_email, et_house;
    private RelativeLayout btn_update;
    private TextView tv_phone, tv_name, tv_email, tv_house, tv_socity, btn_socity;
    private CircleImageView iv_profile;
    SharedPreferences myPrefrence;
String userId="";
    private String getsocity = "";
    private String filePath = "";
    private static final int GALLERY_REQUEST_CODE1 = 201;
    private Bitmap bitmap;
    private Uri imageuri;
    String image;
    private Session_management sessionManagement;

    public Edit_profile_fragment() {
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

        sessionManagement=new Session_management(getActivity());
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        module=new Module();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.edit_profile));

        session_management = new Session_management(getActivity());

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");

        et_phone = (EditText) view.findViewById(R.id.et_pro_phone);
        et_name = (EditText) view.findViewById(R.id.et_pro_name);
        tv_phone = (TextView) view.findViewById(R.id.tv_pro_phone);
        tv_name = (TextView) view.findViewById(R.id.tv_pro_name);
        tv_email = (TextView) view.findViewById(R.id.tv_pro_email);
        et_email = (EditText) view.findViewById(R.id.et_pro_email);
        iv_profile = (CircleImageView) view.findViewById(R.id.iv_pro_img);
        /*et_house = (EditText) view.findViewById(R.id.et_pro_home);
        tv_house = (TextView) view.findViewById(R.id.tv_pro_home);
        tv_socity = (TextView) view.findViewById(R.id.tv_pro_socity);*/
        btn_update = (RelativeLayout) view.findViewById(R.id.btn_pro_edit);
        //btn_socity = (TextView) view.findViewById(R.id.btn_pro_socity);

        userId=sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        String getemail = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
        String getimage = sessionManagement.getUserDetails().get(BaseURL.KEY_IMAGE);
        String getname = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
        String getphone = sessionManagement.getUserDetails().get(BaseURL.KEY_MOBILE);
        String getpin = sessionManagement.getUserDetails().get(BaseURL.KEY_PINCODE);
        String gethouse = sessionManagement.getUserDetails().get(BaseURL.KEY_HOUSE);
        getsocity = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_ID);
        String getsocity_name = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_NAME);

        et_name.setText(getname);
        et_phone.setText(getphone);
        et_phone.setEnabled( false );

        if (!TextUtils.isEmpty(getimage)) {
            Glide.with( getActivity() )
                    .load( BaseURL.IMG_PROFILE_URL + getimage)
                    .fitCenter()
                    .placeholder( R.drawable.user )
                    .crossFade()
                    .diskCacheStrategy( DiskCacheStrategy.ALL )
                    .dontAnimate()
                    .into( iv_profile );
        }


        btn_update.setOnClickListener(this);
        iv_profile.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_pro_edit) {

            getUpdateValidation();

           } else if (id == R.id.iv_pro_img) {
           selectedImage();
        }
    }

    private void getUpdateValidation() {

        String user_name=et_name.getText().toString();

        if(TextUtils.isEmpty(user_name))
        {
          et_name.setError("Enter user name");
          et_name.requestFocus();
        }
        else
        {
            if(flag==1)
            {
                uplaodUserName(user_name);
            }
            else
            {
                String c="";
                String cnt=sessionManagement.getUpdateProfile().get(KEY_CNT);
                if( TextUtils.isEmpty(cnt))
                {
                    c="0";
                }
                else
                {
                    int f=Integer.parseInt(cnt);
                    f++;
                    c=String.valueOf(f);
                }
                String name=user_name+userId;
                String n=name+c+".jpg";


                uplaodImage(n,user_name,c);
            }


        }



    }

    private void selectedImage() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }

    private boolean isPhoneValid(String phoneno) {
        return phoneno.length() > 9;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMG_REQUEST && resultCode==RESULT_OK && data!=null)
        {
            Uri path=data.getData();
            try
            {
               // Toast.makeText(getActivity(),""+data,Toast.LENGTH_LONG).show();
                flag=2;
                bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),path);
                iv_profile.setImageBitmap(bitmap);

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Toast.makeText(getActivity(),""+ex.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(getActivity()," "+data,Toast.LENGTH_LONG).show();
        }
    }



    public String imageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }

    public void uplaodImage(final String name, final String user_name, final String c)
    {
        progressDialog.show();
        String user_id=session_management.getUserDetails().get(BaseURL.KEY_ID).toString();
        String json_tag="json_image_tag";
        HashMap<String,String> map=new HashMap<>();
        map.put("user_id",user_id);
        map.put("user_name",user_name);
        map.put("image",imageToString(bitmap));
        map.put("name",name);

        CustomVolleyJsonRequest customVolleyJsonRequest=new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.GET_UPLOAD, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try
                {


                    boolean b=response.getBoolean("responce");
                    if(b)
                    {

                        sessionManagement.updateProfile(name,user_name,c);
                        Glide.with( getActivity() )
                                .load( BaseURL.IMG_PROFILE_URL + name)
                                .fitCenter()
                                .placeholder( R.drawable.user )
                                .crossFade()
                                .diskCacheStrategy( DiskCacheStrategy.ALL )
                                .dontAnimate()
                                .into( MainActivity.iv_profile );
                        Toast.makeText(getActivity(),""+response.getString("message"),Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),""+response.getString("error"),Toast.LENGTH_LONG).show();
                    }

                }
                catch (Exception ed)
                {
                    Toast.makeText(getActivity(),""+ed.getMessage(),Toast.LENGTH_LONG).show();
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


    public void uplaodUserName(final String name)
    {
        progressDialog.show();
        String user_id=session_management.getUserDetails().get(BaseURL.KEY_ID).toString();
        String json_tag="json_image_tag";
        HashMap<String,String> map=new HashMap<>();
        map.put("user_id",user_id);
        map.put("user_name",name);

        CustomVolleyJsonRequest customVolleyJsonRequest=new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.GET_UPLOAD, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try
                {
                    boolean b=response.getBoolean("responce");
                    if(b)
                    {
                        sessionManagement.updateUserName(name);
                        Toast.makeText(getActivity(),""+response.getString("message"),Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),""+response.getString("error"),Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ed)
                {
                    Toast.makeText(getActivity(),""+ed.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }            }
        });
        AppController.getInstance().addToRequestQueue(customVolleyJsonRequest,json_tag);

    }
}
