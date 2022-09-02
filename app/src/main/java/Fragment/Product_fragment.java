package Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
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

import Adapter.NewAdapter;
import Adapter.Product_adapter;
import Adapter.SortAdapter;
import Config.BaseURL;
import Model.Category_model;
import Model.Deal_Of_Day_model;
import Model.Product_model;
import Model.Slider_subcat_model;
import Module.Module;
import shoparounds.com.AppController;
import shoparounds.com.CustomSlider;
import shoparounds.com.FilterActivity;
import shoparounds.com.MainActivity;
import shoparounds.com.R;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.DatabaseCartHandler;
import util.DatabaseHandlerWishList;
import util.Session_management;

import static Config.BaseURL.KEY_ID;
import static android.content.Context.MODE_PRIVATE;



public class Product_fragment extends Fragment implements View.OnClickListener{
    ProgressDialog loadingBar;
    Module module;
    private static String TAG = Product_fragment.class.getSimpleName();
    private RecyclerView rv_cat;
    private TabLayout tab_cat;
    private List<Category_model> category_modelList = new ArrayList<>();
    private List<Slider_subcat_model> slider_subcat_models = new ArrayList<>();
    private List<String> cat_menu_id = new ArrayList<>();
    private List<Product_model> product_modelList = new ArrayList<>();
    private Product_adapter adapter_product;
    private SliderLayout  banner_slider;
    String view_all ;
    String getcat_title="";
    String language;
    Session_management session_management;
    String getcat_id="";
    String user_id="";
    ImageView img_no_products,img_filter;
    TextView img_sort;
    SharedPreferences preferences;
    private DatabaseCartHandler dbcart;
    private DatabaseHandlerWishList dbwish;
    private SortAdapter sortAdapter ;
    private boolean isSubcat = false;
    NewAdapter new_adapter ;
    String get_top_sale_id ;
    String id="";

    private List<Deal_Of_Day_model> deal_of_day_models = new ArrayList<>();


    public Product_fragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.setMessage("Loading...");
        module=new Module();
        img_sort=(TextView) view.findViewById(R.id.img_sort);
        img_filter=(ImageView)view.findViewById(R.id.img_filter);
        tab_cat = (TabLayout) view.findViewById(R.id.tab_cat);
        banner_slider = (SliderLayout) view.findViewById(R.id.relative_banner);
        rv_cat = (RecyclerView) view.findViewById(R.id.rv_subcategory);
        rv_cat.setLayoutManager(new GridLayoutManager(getActivity(),2));
        session_management=new Session_management(getActivity());
        user_id=session_management.getUserDetails().get(KEY_ID);
        getcat_id = getArguments().getString("cat_id");
         id = getArguments().getString("id");
        String get_deal_id = getArguments().getString("cat_deal");
        get_top_sale_id = getArguments().getString("cat_top_selling");
         getcat_title = getArguments().getString("title");
        view_all = getArguments().getString( "viewall" );
        String filer_data=getArguments().getString("filter");
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.tv_product_name));
        img_no_products=(ImageView)view.findViewById(R.id.img_no_items);
        img_sort.setOnClickListener(this);
        img_filter.setOnClickListener(this);
        // check internet connection
    dbcart=new DatabaseCartHandler(getActivity());
        ((MainActivity) getActivity()).setTitle(getcat_title);


        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Fragment fm = new SubCategory_Fragment();
                    final FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm).addToBackStack(null)
                           .commit();

                    fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                        @Override
                        public void onBackStackChanged() {
                            Fragment fr = fragmentManager.findFragmentById(R.id.contentPanel);
                            if(fr!=null){
                                Log.e("fragment=", fr.getClass().getSimpleName());
                            }
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    dbwish=new DatabaseHandlerWishList( getActivity() );
        if (ConnectivityReceiver.isConnected()) {
            //Shop by Catogary
          //  Toast.makeText(getActivity(),""+id,Toast.LENGTH_LONG).show();
            makeGetSliderCategoryRequest(id);
            makeGetCategoryRequest(getcat_id);

            if(!TextUtils.isEmpty(filer_data))
            {
                getcat_title=getArguments().getString("title");
                ((MainActivity )getActivity()).setTitle(getcat_title);
                makeGetProductFilterRequest(filer_data);
            }
            //Deal Of The Day Products
//            if (view_all.equalsIgnoreCase( "new" )) {
//                make_deal_od_the_day();
//            }
            //Top Sale Products
//            else  {
                maketopsaleProductRequest( get_top_sale_id );
//            }

            //Slider
            //makeGetBannerSliderRequest();

        }



        tab_cat.setVisibility(View.GONE);
        tab_cat.setSelectedTabIndicatorColor(getActivity().getResources().getColor(R.color.white));

        tab_cat.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String getcat_id = cat_menu_id.get(tab.getPosition());
                if (ConnectivityReceiver.isConnected()) {
                    //Shop By Catogary Products
                  //  Toast.makeText(getActivity(),""+product_modelList.size(),Toast.LENGTH_LONG).show();
                    makeGetProductRequest(getcat_id);
                    ((MainActivity) getActivity()).setTitle(String.valueOf( tab.getText() ));
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();
        updateWishData();
    }

    public void replaceFragment(Fragment frag,String getcat_id) {
        FragmentManager manager = getFragmentManager();
        Bundle bundle=new Bundle();
        if (manager != null){
            FragmentTransaction t = manager.beginTransaction();
            Fragment currentFrag = manager.findFragmentById(R.id.contentPanel);
            bundle.putString("category_id", getcat_id);
            frag.setArguments(bundle);
            //Check if the new Fragment is the same
            //If it is, don't add to the back stack
            if (currentFrag != null && currentFrag.getClass().equals(frag.getClass())) {
                t.replace(R.id.contentPanel, frag).commit();
            } else {
                t.replace(R.id.contentPanel, frag).addToBackStack(null).commit();
            }
        }
    }


    //Get Shop By Catogary
    private void makeGetCategoryRequest(final String parent_id) {

        loadingBar.show();
        String tag_json_obj = "json_category_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", parent_id);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_CATEGORY_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    loadingBar.dismiss();
                    if (status) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Category_model>>() {}.getType();
                        category_modelList = gson.fromJson(response.getString("data"), listType);
                        if (!category_modelList.isEmpty()) {
                            tab_cat.setVisibility(View.VISIBLE);
                            cat_menu_id.clear();
                            for (int i = 0; i < category_modelList.size(); i++) {
                                cat_menu_id.add(category_modelList.get(i).getId());
                                preferences = getActivity().getSharedPreferences("lan", MODE_PRIVATE);

                                language=preferences.getString("language","");
                                if (language.contains("english")) {
                                    tab_cat.addTab(tab_cat.newTab().setText(category_modelList.get(i).getTitle()));
                                }
                                else {
                                    tab_cat.addTab(tab_cat.newTab().setText(category_modelList.get(i).getTitle()));

                                }
                            }
                        } else {
                            makeGetProductRequest(parent_id);
                        }

                    }
                } catch (JSONException e) {
                    loadingBar.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             loadingBar.dismiss();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void make_deal_od_the_day() {
        String tag_json_obj = "json_category_req";
        isSubcat = false;
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");
        isSubcat = true;
       /* if (parent_id != null && parent_id != "") {
        }*/

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.GET,
                BaseURL.GET_NEW_PRODUCT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    if (response != null && response.length() > 0) {
                        Boolean status = response.getBoolean("responce");
                        Gson gson = new Gson();
                        if (status) {
                            Type listType = new TypeToken<List<Deal_Of_Day_model>>() {
                            }.getType();
                            product_modelList = gson.fromJson(response.getString("new_product"), listType);
                            new_adapter = new NewAdapter( getActivity(),product_modelList);
                           rv_cat.setAdapter(new_adapter);
                            new_adapter.notifyDataSetChanged();
                            if (getActivity() != null) {
                                if (product_modelList.isEmpty()) {
                                    //  Toast.makeText(getActivity(), "No Deal For Day", Toast.LENGTH_SHORT).show();
                                    rv_cat.setVisibility(View.GONE);

                                    //  Deal_Frame_layout.setVisibility(View.GONE);
                                    //Deal_Frame_layout1.setVisibility(View.GONE);
                                    //Deal_Linear_layout.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "No Response", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    //Get Shop By Catogary Products
    private void makeGetProductRequest(String cat_id) {
loadingBar.show();
        String tag_json_obj = "json_product_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", cat_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("qwerty", response.toString());

                try {
                   loadingBar.dismiss();
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        product_modelList.clear();
                        product_modelList = gson.fromJson(response.getString("data"), listType);

                            adapter_product = new Product_adapter(product_modelList, getActivity());

                            img_no_products.setVisibility(View.GONE);
                            rv_cat.setVisibility(View.VISIBLE);
                            rv_cat.setAdapter(adapter_product);
                            adapter_product.notifyDataSetChanged();





                    }
                } catch (JSONException e) {
                    loadingBar.dismiss();
                    String msg=e.getMessage();
                    if(msg.equals("No value for data"))
                    {
                        rv_cat.setVisibility(View.GONE);
                        img_no_products.setVisibility(View.VISIBLE);
                    }
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               loadingBar.dismiss();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    //Get Shop By Catogary
    private void makeGetSliderCategoryRequest(final String sub_cat_id) {
        String tag_json_obj = "json_category_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("sub_cat", sub_cat_id);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_SLIDER_CATEGORY_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("slid", response.toString());

                try {
                    Boolean status = response.getBoolean("response");
                    if (status) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Slider_subcat_model>>() {}.getType();
                        slider_subcat_models = gson.fromJson(response.getString("subcat"), listType);
                        if (!slider_subcat_models.isEmpty()) {
                            tab_cat.setVisibility(View.VISIBLE);
                            cat_menu_id.clear();
                            for (int i = 0; i < slider_subcat_models.size(); i++) {
                                cat_menu_id.add(slider_subcat_models.get(i).getId());
                                preferences = getActivity().getSharedPreferences("lan", MODE_PRIVATE);

                                language=preferences.getString("language","");
                                if (language.contains("english")) {
                                    tab_cat.addTab(tab_cat.newTab().setText(slider_subcat_models.get(i).getTitle()));
                                }
                                else {
                                    tab_cat.addTab(tab_cat.newTab().setText(slider_subcat_models.get(i).getTitle()));

                                }
                            }
                        } else {

                        }

                    }
                } catch (Exception e) {
                   String s= e.getMessage();
                   if(s.equals("No value for response"))
                   {
                       makeGetProductRequest(sub_cat_id);

                   }
                  // Toast.makeText(getActivity(),""+s,Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }




    ////Get DEal Products
    private void makedealIconProductRequest(String cat_id) {
        String tag_json_obj = "json_product_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("dealproduct", cat_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_ALL_DEAL_OF_DAY_PRODUCTS, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        product_modelList = gson.fromJson(response.getString("Deal_of_the_day"), listType);
                        adapter_product = new Product_adapter(product_modelList, getActivity());
                        img_no_products.setVisibility(View.GONE);
                        rv_cat.setVisibility(View.VISIBLE);
                        rv_cat.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (JSONException e) {
                    String msg=e.getMessage();
                    if(msg.equals("No value for data"))
                    {
                        rv_cat.setVisibility(View.GONE);
                        img_no_products.setVisibility(View.VISIBLE);
                    }
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    ////Get Top Sale Products
    private void maketopsaleProductRequest(String cat_id) {
        String tag_json_obj = "json_product_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("top_selling_product", cat_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_ALL_TOP_SELLING_PRODUCTS, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        product_modelList = gson.fromJson(response.getString("top_selling_product"), listType);
                        adapter_product = new Product_adapter(product_modelList, getActivity());
                        img_no_products.setVisibility(View.GONE);
                        rv_cat.setVisibility(View.VISIBLE);
                        rv_cat.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (JSONException e) {
                    String msg=e.getMessage();
                    if(msg.equals("No value for data"))
                    {
                        rv_cat.setVisibility(View.GONE);
                        img_no_products.setVisibility(View.VISIBLE);
                    }
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    private void maketopFilterProductRequest(String cat_id,String book_class,String subject,String lang) {
        String tag_json_obj = "json_product_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("top_selling_product", cat_id);
        params.put("book_class", book_class);
        params.put("subject", subject);
        params.put("language", lang);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_ALL_TOP_SELLING_PRODUCTS, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        product_modelList = gson.fromJson(response.getString("top_selling_product"), listType);
                        adapter_product = new Product_adapter(product_modelList, getActivity());
                        img_no_products.setVisibility(View.GONE);
                        rv_cat.setVisibility(View.VISIBLE);
                        rv_cat.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (JSONException e) {
                    String msg=e.getMessage();
                    if(msg.equals("No value for data"))
                    {
                        rv_cat.setVisibility(View.GONE);
                        img_no_products.setVisibility(View.VISIBLE);
                    }
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void makeGetBannerSliderRequest() {
        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_BANNER_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = (JSONObject) response.get(i);
                                HashMap<String, String> url_maps = new HashMap<String, String>();
                                url_maps.put("slider_title", jsonObject.getString("slider_title"));
                                url_maps.put("sub_cat", jsonObject.getString("sub_cat"));
                                url_maps.put("slider_image", BaseURL.IMG_SLIDER_URL + jsonObject.getString("slider_image"));
                                listarray.add(url_maps);
                            }
                            for (HashMap<String, String> name : listarray) {
                                CustomSlider textSliderView = new CustomSlider(getActivity());
                                textSliderView.description(name.get("")).image(name.get("slider_image")).setScaleType(BaseSliderView.ScaleType.Fit);
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle().putString("extra", name.get("slider_title"));
                                textSliderView.getBundle().putString("extra", name.get("sub_cat"));
                                banner_slider.addSlider(textSliderView);
                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
//                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
//                                    @Override
//                                    public void onSliderClick(BaseSliderView slider) {
//                                        //   Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
//                                        Bundle args = new Bundle();
//                                        Fragment fm = new Product_fragment();
//                                        args.putString("id", sub_cat);
//                                        fm.setArguments(args);
//                                        FragmentManager fragmentManager = getFragmentManager();
//                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                                                .addToBackStack(null).commit();
//                                    }
//                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(req);

    }
    private void makeDescendingProductRequest(String cat_id) {
        String tag_json_obj = "json_product_desc_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", cat_id);
        loadingBar.show();
        product_modelList.clear();

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_DESC, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                loadingBar.dismiss();
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        product_modelList = gson.fromJson(response.getString("data"), listType);
                        adapter_product = new Product_adapter(product_modelList, getActivity());
                       // rv_cat.setLayoutManager( new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
                        rv_cat.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //  Toast.makeText(getActivity(),""+response, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void makeDescendingTop() {
        String tag_json_obj = "json_product_desc_top";
        Map<String, String> params = new HashMap<String, String>();
        params.put("top_selling_product", "2");
        product_modelList.clear();
        loadingBar.show();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_TOP_SELLING_HIGH, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                loadingBar.dismiss();
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        product_modelList = gson.fromJson(response.getString("top_selling_product"), listType);
                        adapter_product = new Product_adapter(product_modelList, getActivity());
                        //       rv_cat.setLayoutManager( new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
                        rv_cat.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //  Toast.makeText(getActivity(),""+response, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void makeAscendingProductRequest(String cat_id) {
        String tag_json_obj = "json_product_asc_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", cat_id);
        product_modelList.clear();
        loadingBar.show();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_ASC, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                loadingBar.dismiss();
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        product_modelList = gson.fromJson(response.getString("data"), listType);
                        adapter_product = new Product_adapter(product_modelList, getActivity());
                 //       rv_cat.setLayoutManager( new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
                        rv_cat.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //  Toast.makeText(getActivity(),""+response, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void makeAscendingTop() {
        String tag_json_obj = "json_product_asc_top";
        Map<String, String> params = new HashMap<String, String>();
        params.put("top_selling_product", "2");
        product_modelList.clear();
        loadingBar.show();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_TOP_SELLING_LOW, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                loadingBar.dismiss();
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        product_modelList = gson.fromJson(response.getString("top_selling_product"), listType);
                        adapter_product = new Product_adapter(product_modelList, getActivity());
                        //       rv_cat.setLayoutManager( new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
                        rv_cat.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //  Toast.makeText(getActivity(),""+response, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void makeNewestProductRequest(String cat_id) {
        String tag_json_obj = "json_product_newest_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", cat_id);
        product_modelList.clear();
        loadingBar.show();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_NEWEST, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                loadingBar.dismiss();
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        product_modelList = gson.fromJson(response.getString("data"), listType);
                        adapter_product = new Product_adapter(product_modelList, getActivity());
                    //    rv_cat.setLayoutManager( new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
                        rv_cat.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //  Toast.makeText(getActivity(),""+response, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void makeNewestTop() {
        String tag_json_obj = "json_product_new_top";
        Map<String, String> params = new HashMap<String, String>();
        params.put("top_selling_product", "2");
        product_modelList.clear();
        loadingBar.show();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_TOP_SELLING_NEW, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                loadingBar.dismiss();
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        product_modelList = gson.fromJson(response.getString("top_selling_product"), listType);
                        adapter_product = new Product_adapter(product_modelList, getActivity());
                        //       rv_cat.setLayoutManager( new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
                        rv_cat.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //  Toast.makeText(getActivity(),""+response, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
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
    public void onResume() {
        super.onResume();
        // register reciver
        getActivity().registerReceiver(mCart, new IntentFilter("Grocery_cart"));
        getActivity().registerReceiver(mWish, new IntentFilter("Grocery_wish"));

    }

    // broadcast reciver for receive data
    private BroadcastReceiver mCart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("cart")) {
                updateData();
            }
        }
    };

    private void updateData() {

        ((MainActivity) getActivity()).setCartCounter("" + dbcart.getCartCount());
    }

    private BroadcastReceiver mWish = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("wish")) {
                updateWishData();
            }

        }
    };
    private void updateWishData() {

        ((MainActivity) getActivity()).setWishCounter("" + dbwish.getWishlistCount(user_id));
    }



    @Override
    public void onPause() {
        super.onPause();
        // unregister reciver
        getActivity().unregisterReceiver(mCart);
        getActivity().unregisterReceiver(mWish);
    }


    @Override
    public void onClick(View view) {

        int v_id = view.getId();

        if(v_id==R.id.img_sort)
        {
            final ArrayList <String>  sort_List = new ArrayList<>(  );
            sort_List.add( "Price Low - High" );
            sort_List.add("Price High - Low");
            sort_List.add("Newest First");
            //  sort_List.add ("Trending");
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            LayoutInflater layoutInflater=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=layoutInflater.inflate(R.layout.dialog_sort_layout,null);
            ListView l1=(ListView)row.findViewById(R.id.list_sort);
            sortAdapter=new SortAdapter(getActivity(),sort_List);
            //productVariantAdapter.notifyDataSetChanged();
            l1.setAdapter(sortAdapter);
            builder.setView(row);
            final AlertDialog ddlg=builder.create();
            ddlg.show();
            l1.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ddlg.dismiss();
                    String item = sort_List.get( i ).toString();
                   // final String cat_id = getArguments().getString("cat_id");
                   final String cat_id = session_management.getCategoryId();
                   final String vw = session_management.getViewAll();
                  //  Toast.makeText(getActivity(),""+cat_id,Toast.LENGTH_SHORT).show();

                    if (item.equals( "Price Low - High" ))
                    {

                         if (vw.equals( "top" ))
                         {
                             //Toast.makeText(getActivity(),"top",Toast.LENGTH_LONG).show();
                             ddlg.dismiss();
                             makeAscendingTop(  );
                         }
                         else if (vw.equals("category")) {
                             //Toast.makeText(getActivity(),"category",Toast.LENGTH_LONG).show();
                             ddlg.dismiss();
                             makeAscendingProductRequest( cat_id );
                         }


                    }
                    else if(item.equals( "Price High - Low" ))
                    {
                       //  Toast.makeText( getActivity(), "category id :" +cat_id, Toast.LENGTH_SHORT ).show();

                        if (vw.equals( "top" ))
                        {
                            //Toast.makeText(getActivity(),"top",Toast.LENGTH_LONG).show();
                            ddlg.dismiss();
                            makeDescendingTop(  );
                        }
                        else if (vw.equals( "category" )) {
                            //Toast.makeText(getActivity(),"category",Toast.LENGTH_LONG).show();
                            ddlg.dismiss();
                            makeDescendingProductRequest( cat_id );
                        }


                    }
                    else if(item.equals( "Newest First" ))
                    {
                        if (vw.equals( "top" ))
                        {
                            //Toast.makeText(getActivity(),"top",Toast.LENGTH_LONG).show();
                            ddlg.dismiss();
                            makeNewestTop();
                        }
                        else if (vw.equals( "category" )) {
                            //Toast.makeText(getActivity(),"category",Toast.LENGTH_LONG).show();
                            ddlg.dismiss();
                            makeNewestProductRequest( cat_id );
                        }

                    }
                    else if (item.equals( "Trending" ))
                    {

                    }

                    // Toast.makeText( getActivity(),"Showing items:" +item,Toast.LENGTH_LONG ).show();
                }
            } );
        }

        else if(v_id==R.id.img_filter)
        {
            String idd=session_management.getCategoryId();
                FilterActivity filterActivity=new FilterActivity();
                Bundle args = new Bundle();
                args.putString("category_id", idd);
                args.putString("title", getcat_title);
//         /   fm.setArguments(args);
                filterActivity.setArguments(args);
                FragmentManager fragmentManager=getFragmentManager();
                filterActivity.show(fragmentManager,"Filter");

            //            Bundle args = new Bundle();
//            Fragment fm = new FilterActivity();
//            args.putString("category_id", getcat_id);
//         /   fm.setArguments(args);
            //FragmentManager fragmentManager = getFragmentManager();

          //   fragmentTransaction.replace(R.id.contentPanel, fm)
//                     .addToBackStack(null)
//                .commit();


//         Intent intent=new Intent(getActivity(), FilterActivity.class);
//          intent.putExtra("",getcat_id);
//          startActivity(intent);
        }
    }

    private void makeGetProductFilterRequest(String filter_data) {
        loadingBar.show();


        String book_class="";
        String subject="";
        String language="";
        try
        {
            JSONArray array=new JSONArray(filter_data);
            for(int i=0;i<array.length();i++)
            {
                JSONObject object=array.getJSONObject(i);
                getcat_id=object.getString("cat_id");
                book_class=object.getString("book_class");
                subject=object.getString("subject");
                language=object.getString("language");

            }
            if(getcat_id.equals("2"))
            {
                maketopFilterProductRequest(getcat_id,book_class,subject,language);
            }
            else
            {
                get_productFilter(getcat_id,book_class,subject,language);
            }
        }
        catch (Exception ex)
        {
            //Toast.makeText(getActivity(),""+ex.getMessage(),Toast.LENGTH_LONG).show();
        }


    }

    public void get_productFilter(String getcat_id,String book_class,String subject,String lang)
    {
        String tag_json_obj = "json_product_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", getcat_id);
        params.put("book_class", book_class);
        params.put("subject", subject);
        params.put("language", lang);

        //Toast.makeText(getActivity(),""+filter_data.toString(),Toast.LENGTH_LONG).show();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_FILTER, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("filter", response.toString());

                try {
                    loadingBar.dismiss();
                    Boolean status = response.getBoolean("responce");
                    if(!response.has("dara"))
                    {
                        img_no_products.setVisibility(View.VISIBLE);
                        rv_cat.setVisibility( View.GONE );
                    }
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        product_modelList.clear();
                        product_modelList = gson.fromJson(response.getString("data"), listType);
                        adapter_product = new Product_adapter( product_modelList, getActivity() );
                        img_no_products.setVisibility( View.GONE );
                        rv_cat.setVisibility( View.VISIBLE );
                        rv_cat.setAdapter( adapter_product );
                        adapter_product.notifyDataSetChanged();





                    }
                } catch (JSONException e) {
                    loadingBar.dismiss();
                    String msg=e.getMessage();
//                    if(msg.equals("No value for data"))
//                    {
//                        rv_cat.setVisibility(View.GONE);
//                        img_no_products.setVisibility(View.VISIBLE);
//                    }
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}



