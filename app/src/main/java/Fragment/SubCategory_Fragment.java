package Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

import Adapter.Home_Icon_Adapter;
import Adapter.Shop_Now_adapter;
import Config.BaseURL;
import Model.Home_Icon_model;
import Module.Module;
import shoparounds.com.AppController;
import shoparounds.com.MainActivity;
import shoparounds.com.R;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.RecyclerTouchListener;
import util.Session_management;

public class SubCategory_Fragment extends Fragment {

    private static String TAG = SubCategory_Fragment.class.getSimpleName();
    private RecyclerView rv_items;
    private List<Home_Icon_model> category_modelList = new ArrayList<>();
    private Shop_Now_adapter adapter;
    private boolean isSubcat = false;
    String getid;
    Module module;
    String getcat_title;
    TextView no_item_found;
    ProgressDialog loadingBar;
    Home_Icon_Adapter home_icon_adapter;
    Session_management session_management;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragemt_sub_categories, container, false);
        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        setHasOptionsMenu(true);
        module=new Module();
        session_management=new Session_management(getActivity());
        String getcat_id = getArguments().getString("cat_id");
        String getcat_name = getArguments().getString("title");
        no_item_found = view.findViewById( R.id.img_no_items );

        ((MainActivity) getActivity()).setTitle(getcat_name);


        if (ConnectivityReceiver.isConnected()) {
       //     makeSubGetCategoryRequest();
            makeGetCategoryRequest(getcat_id);

        }

        rv_items = (RecyclerView) view.findViewById(R.id.rv_sub);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rv_items.setLayoutManager(gridLayoutManager);
        // rv_items.addItemDecoration(new GridSpacingItemDecoration(10, dpToPx(-25), true));
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setNestedScrollingEnabled(false);
        rv_items.setItemViewCacheSize(10);
        rv_items.setDrawingCacheEnabled(true);
        rv_items.setDrawingCacheQuality( View.DRAWING_CACHE_QUALITY_LOW);

        rv_items.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_items, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                getid=category_modelList.get(position).getId();
                String title=category_modelList.get(position).getTitle();
                Bundle args = new Bundle();
                Fragment fm = new Product_fragment();
                args.putString("cat_id", getid);
                args.putString( "title" , title);
                args.putString( "viewall","category" );
                session_management.setCategoryId(getid);
                session_management.setViewAll("category");

                // args.putString( "" );
                // Toast.makeText(getActivity(),""+getid,Toast.LENGTH_LONG).show();
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack("SubCategory_Fragment").commit();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return view;

    }

    private void makeGetCategoryRequest(final String parent_id) {
        loadingBar.show();
        String tag_json_obj = "json_category_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", parent_id);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest( Request.Method.POST,
                BaseURL.GET_CATEGORY_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response from category", response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        if(!response.has("data"))
                        {
                            no_item_found.setVisibility( View.VISIBLE );
                            rv_items.setVisibility( View.GONE );

                        }
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Home_Icon_model>>() {}.getType();
                        category_modelList = gson.fromJson(response.getString("data"), listType);
                        if (!category_modelList.isEmpty()) {
                            rv_items.setVisibility( View.VISIBLE);
                            no_item_found.setVisibility( View.GONE );
                            rv_items.setVisibility( View.VISIBLE );

                            home_icon_adapter=new Home_Icon_Adapter(category_modelList);
                            rv_items.setAdapter(home_icon_adapter);

                            home_icon_adapter.notifyDataSetChanged();

                        } else
                            {
                             no_item_found.setVisibility( View.VISIBLE );
                            rv_items.setVisibility( View.GONE );



                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadingBar.dismiss();
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

}
