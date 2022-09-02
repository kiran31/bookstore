package Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.Search_adapter;
import Adapter.SuggestionAdapter;
import Config.BaseURL;
import Model.Product_model;
import Module.Module;
import shoparounds.com.AppController;
import shoparounds.com.MainActivity;
import shoparounds.com.R;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.RecyclerTouchListener;



public class Search_fragment extends Fragment {

    private static String TAG = Search_fragment.class.getSimpleName();
    //    String[] fruits = {"MIlk butter & cream", "Bread Buns & Pals", "Dals Mix Pack", "buns-pavs", "cakes", "Channa Dal", "Toor Dal", "Wheat Atta"
//            , "Beson", "Almonds", "Packaged Drinking", "Cola drinks", "Other soft drinks", "Instant Noodles", "Cup Noodles", "Salty Biscuits", "cookie", "Sanitary pads", "sanitary Aids"
//            , "Toothpaste", "Mouthwash", "Hair oil", "Shampoo", "Pure & pomace olive", "ICE cream", "Theme Egg", "Amul Milk", "AMul Milk Pack power", "kaju pista dd"};
    private AutoCompleteTextView acTextView;
    private RelativeLayout btn_search;
    private RecyclerView rv_search;
    Module module;
    private List<Product_model> modelList = new ArrayList<>();
    private Search_adapter adapter_product;
    ProgressDialog progressDialog ;
    ImageView img_no_item ;
    public Search_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");
         module=new Module();
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.search));
        img_no_item = view.findViewById( R.id.img_no_items );

        acTextView = view.findViewById(R.id.et_search);
        acTextView.setThreshold(1);
        acTextView.setAdapter(new SuggestionAdapter(getActivity(), acTextView.getText().toString()));
        acTextView.setTextColor(getResources().getColor(R.color.green));
        btn_search = (RelativeLayout) view.findViewById(R.id.btn_search);
        rv_search = (RecyclerView) view.findViewById(R.id.rv_search);
        rv_search.setLayoutManager(new LinearLayoutManager(getActivity()));

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String get_search_txt ="%"+ acTextView.getText().toString() +"%";
                if (TextUtils.isEmpty(get_search_txt)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.enter_keyword), Toast.LENGTH_SHORT).show();
                } else {
                    if (ConnectivityReceiver.isConnected()) {
                        makeGetProductRequest(get_search_txt);
                    } else {
                        ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
                    }
                }

            }
        });
        rv_search.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_search, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                int stock=Integer.parseInt(modelList.get(position).getStock());
                if(stock<=0)
                {

                }
                else {
                    Fragment details_fragment = new Details_Fragment();
                    // bundle.putString("data",as);
                    Bundle args = new Bundle();

//                //Intent intent=new Intent(context, Product_details.class);
//                args.putString("product_id", modelList.get(position).getProduct_id());
//                args.putString("product_name", modelList.get(position).getProduct_name());
//                args.putString("category_id", modelList.get(position).getCategory_id());
//                args.putString("product_description", modelList.get(position).getProduct_description());
//                args.putString("deal_price", modelList.get(position).getDeal_price());
//                args.putString("start_date", modelList.get(position).getStart_date());
//                args.putString("start_time", modelList.get(position).getStart_time());
//                args.putString("end_date", modelList.get(position).getEnd_date());
//                args.putString("end_time", modelList.get(position).getEnd_time());
//                args.putString("price", modelList.get(position).getPrice());
//                args.putString( "mrp",modelList.get( position ).getMrp() );
//                args.putString("product_image", modelList.get(position).getProduct_image());
//                args.putString("status", modelList.get(position).getStatus());
//                args.putString("in_stock", modelList.get(position).getIn_stock());
//                args.putString("unit_value", modelList.get(position).getUnit_value());
//                args.putString("unit", modelList.get(position).getUnit());
//                args.putString("increament", modelList.get(position).getIncreament());
//                args.putString("rewards", modelList.get(position).getRewards());
//                args.putString("stock", modelList.get(position).getStock());
//                args.putString("title", modelList.get(position).getTitle());

                    args.putString("product_id", modelList.get(position).getProduct_id());
                    args.putString("product_name", modelList.get(position).getProduct_name());
                    args.putString("category_id", modelList.get(position).getCategory_id());
                    args.putString("product_description", modelList.get(position).getProduct_description());
                    //         args.putString("deal_price",modelList.get(position).getDeal_price());
                    //       args.putString("start_date",modelList.get(position).getStart_date());
                    //     args.putString("start_time",modelList.get(position).getStart_time());
                    //   args.putString("end_date",modelList.get(position).getEnd_date());
                    // args.putString("end_time",modelList.get(position).getEnd_time());
                    args.putString("price", modelList.get(position).getPrice());
                    args.putString("mrp", modelList.get(position).getMrp());
                    args.putString("product_image", modelList.get(position).getProduct_image());
                    args.putString("status", modelList.get(position).getStatus());
                    args.putString("in_stock", modelList.get(position).getIn_stock());
                    args.putString("unit_value", modelList.get(position).getUnit_value());
                    args.putString("unit", modelList.get(position).getUnit());
                    args.putString("increament", modelList.get(position).getIncreament());
                    args.putString("rewards", modelList.get(position).getRewards());
                    args.putString("stock", modelList.get(position).getStock());
                    args.putString("title", modelList.get(position).getTitle());
                    args.putString("seller_id", modelList.get(position).getSeller_id());

                    details_fragment.setArguments(args);


                    // Toast.makeText(getActivity(),"col"+product_modelList.get(position).getColor(),Toast.LENGTH_LONG).show();


                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, details_fragment)

                            .addToBackStack(null).commit();

                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        return view;
    }





    private void makeGetProductRequest(String search_text) {
            progressDialog.show();
        // Tag used to cancel the request
        String tag_json_obj = "json_product_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("search", search_text);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        if(!response.has("data"))
                        {
                            img_no_item.setVisibility( View.VISIBLE );
                            rv_search.setVisibility( View.GONE );

                        }
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();

                     modelList = gson.fromJson(response.getString("data"), listType);


                        img_no_item.setVisibility( View.GONE );
                        rv_search.setVisibility( View.VISIBLE );


                        if (getActivity() != null) {
                            if (modelList.isEmpty()) {
                                img_no_item.setVisibility( View.VISIBLE );
                                rv_search.setVisibility( View.GONE );
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                img_no_item.setVisibility( View.GONE );
                                rv_search.setVisibility( View.VISIBLE );
                                adapter_product = new Search_adapter(modelList, getActivity());
                                rv_search.setAdapter(adapter_product);
                                adapter_product.notifyDataSetChanged();
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

}
