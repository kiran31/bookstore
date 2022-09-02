package Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
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

import Adapter.Home_Icon_Adapter;
import Adapter.Home_adapter;
import Adapter.NewAdapter;
import Adapter.Top_Selling_Adapter;
import Config.BaseURL;
import Model.Category_model;
import Model.Deal_Of_Day_model;
import Model.Home_Icon_model;
import Model.Top_Selling_model;
import Module.Module;
import shoparounds.com.AppController;
import shoparounds.com.CustomSlider;
import shoparounds.com.MainActivity;
import shoparounds.com.R;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.RecyclerTouchListener;
import util.Session_management;


public class Home_fragment extends Fragment {
    private static String TAG = Home_fragment.class.getSimpleName();
    private SliderLayout imgSlider, banner_slider, featuredslider;
    private RecyclerView rv_items, rv_top_selling, rv_deal_of_day, rv_headre_icons;
    private List<Category_model> category_modelList = new ArrayList<>();
    private Home_adapter adapter;
    int version_code=0;
    String app_link="";
    ProgressDialog progressDialog;

    private boolean isSubcat = false;
    LinearLayout Search_layout;
    String getid;

    Module module;
    String getcat_title;
    ScrollView scrollView;
    SharedPreferences sharedpreferences;

    Session_management session_management;
    //Home Icons
    private Home_Icon_Adapter menu_adapter;
    private List<Home_Icon_model> menu_models = new ArrayList<>();


    //Deal O Day
    private NewAdapter new_adapter;
    private List<Deal_Of_Day_model> deal_of_day_models = new ArrayList<>();
    LinearLayout Deal_Linear_layout;
    FrameLayout Deal_Frame_layout, Deal_Frame_layout1;


    //Top Selling Products
    private Top_Selling_Adapter top_selling_adapter;
    private List<Top_Selling_model> top_selling_models = new ArrayList<>();


    //private ImageView iv_Call, iv_Whatspp, iv_reviews, iv_share_via;
    private TextView timer;

    TextView View_all_TopSell,View_all_deals;

    private ImageView Top_Selling_Poster, Deal_Of_Day_poster;

    View view;

    public Home_fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.app_name));
        ((MainActivity) getActivity()).updateHeader();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        module=new Module();
        session_management=new Session_management(getActivity());
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ((MainActivity) getActivity()).finish();
                    return true;
                }
                return false;
            }
        });
        //Check Internet Connection
        if (ConnectivityReceiver.isConnected()) {
            getAppSettingData();
            //Toast.makeText(getActivity(),""+version_code+"\n "+getUpdaterInfo(),Toast.LENGTH_SHORT).show();


        }

        View_all_deals = (TextView) view.findViewById(R.id.view_all_deals);
        View_all_TopSell = (TextView) view.findViewById(R.id.view_all_topselling);
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setSmoothScrollingEnabled(true);

        //Search
//        Search_layout = (LinearLayout) view.findViewById(R.id.search_layout);
//        Search_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fm = new Search_fragment();
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                        .addToBackStack(null).commit();
//
//            }
//        });
        //Slider
        imgSlider = (SliderLayout) view.findViewById(R.id.home_img_slider);
        banner_slider = (SliderLayout) view.findViewById(R.id.relative_banner);
        featuredslider = (SliderLayout) view.findViewById(R.id.featured_img_slider);


        //Catogary Icons
//        rv_items = (RecyclerView) view.findViewById(R.id.rv_home);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
//        rv_items.setLayoutManager(gridLayoutManager);
//        rv_items.setItemAnimator(new DefaultItemAnimator());
//        rv_items.setNestedScrollingEnabled(false);

        //DealOf the Day
        rv_deal_of_day = (RecyclerView) view.findViewById(R.id.rv_deal);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 2);
        rv_deal_of_day.setLayoutManager(gridLayoutManager1);
        rv_deal_of_day.setItemAnimator(new DefaultItemAnimator());
        rv_deal_of_day.setNestedScrollingEnabled(false);
        rv_deal_of_day.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));


        //Top Selling Products
        rv_top_selling = (RecyclerView) view.findViewById(R.id.top_selling_recycler);
       // GridLayoutManager gridLayoutManager2= new GridLayoutManager(getActivity(), 2);
       LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity() ,LinearLayoutManager.HORIZONTAL,false );
        rv_top_selling.setLayoutManager(layoutManager1);
        rv_top_selling.setItemAnimator(new DefaultItemAnimator());
       // rv_top_selling.setNestedScrollingEnabled(false);
        rv_top_selling.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));


        //make_menu_items Icons
        rv_headre_icons = (RecyclerView) view.findViewById(R.id.collapsing_recycler);
       GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),3) {

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {
                    private static final float SPEED = 2000f;// Change this value (default=25f)

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
       // layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_headre_icons.setLayoutManager(layoutManager);
        rv_headre_icons.setHasFixedSize(true);
        rv_headre_icons.setItemViewCacheSize(10);
        rv_headre_icons.setDrawingCacheEnabled(true);
        rv_headre_icons.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);


        //Call And Whatsapp
        /*iv_Call = (ImageView) view.findViewById(R.id.iv_call);
        iv_Whatspp = (ImageView) view.findViewById(R.id.iv_whatsapp);
        iv_reviews = (ImageView) view.findViewById(R.id.reviews);
        iv_share_via = (ImageView) view.findViewById(R.id.share_via);

        iv_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(isPermissionGranted()){
//                    call_action();
//                }
            }
        });
        iv_Whatspp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smsNumber = "9889887711";
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
                sendIntent.putExtra("jid",     PhoneNumberUtils.stripSeparators(smsNumber)+"@s.whatsapp.net");//phone number without "+" prefix
                startActivity(sendIntent);

            }
        });
        iv_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewOnApp();
            }
        });
        iv_share_via.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();

            }
        });
*/

        //Recycler View Shop By Catogary
//        rv_items.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_items, new RecyclerTouchListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                getid = category_modelList.get(position).getId();
//                getcat_title = category_modelList.get(position).getTitle();
//                Bundle args = new Bundle();
//                Fragment fm = new Product_fragment();
//                args.putString("cat_id", getid);
//                args.putString("cat_title", getcat_title);
//                fm.setArguments(args);
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                        .addToBackStack(null).commit();
//
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));
//

        //Recycler View Menu Products
        rv_headre_icons.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_headre_icons, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getid = menu_models.get(position).getId();

                String title=menu_models.get(position).getTitle();
                Bundle args = new Bundle();
                Fragment fm = new SubCategory_Fragment();
                args.putString("cat_id", getid);
                args.putString( "title" ,title );

                // args.putString( "" );
                // Toast.makeText(getActivity(),""+getid,Toast.LENGTH_LONG).show();
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        View_all_deals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Fragment fm = new Product_fragment();
                args.putString("viewall", "new");
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }
        });


//        rv_deal_of_day.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_deal_of_day, new RecyclerTouchListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                getid = deal_of_day_models.get(position).getProduct_id();
//
//                Fragment details_fragment=new Details_Fragment();
//                Bundle args = new Bundle();
//
//                //Intent intent=new Intent(context, Product_details.class);
//                args.putString("product_id",deal_of_day_models.get(position).getProduct_id());
//                args.putString("product_name", deal_of_day_models.get(position).getProduct_name());
//                args.putString("category_id",deal_of_day_models.get(position).getCategory_id());
//                args.putString("product_description",deal_of_day_models.get(position).getProduct_description());
//                args.putString("deal_price",deal_of_day_models.get(position).getDeal_price());
//                args.putString("start_date",deal_of_day_models.get(position).getStart_date());
//                args.putString("start_time",deal_of_day_models.get(position).getStart_time());
//                args.putString("end_date",deal_of_day_models.get(position).getEnd_date());
//                args.putString("end_time",deal_of_day_models.get(position).getEnd_time());
//                args.putString("price",deal_of_day_models.get(position).getPrice());
//                args.putString( "mrp",deal_of_day_models.get( position ).getMrp() );
//                args.putString("product_image",deal_of_day_models.get(position).getProduct_image());
//                args.putString("status", deal_of_day_models.get(position).getStatus());
//                args.putString("in_stock", deal_of_day_models.get(position).getIn_stock());
//                args.putString("unit_value", deal_of_day_models.get(position).getUnit_value());
//                args.putString("unit", deal_of_day_models.get(position).getUnit());
//                args.putString("increament",deal_of_day_models.get(position).getIncreament());
//                args.putString("rewards",deal_of_day_models.get(position).getRewards());
//                args.putString("stock",deal_of_day_models.get(position).getStock());
//                args.putString("title",deal_of_day_models.get(position).getTitle());
//                args.putString("seller_id",deal_of_day_models.get(position).getSeller_id());
//                details_fragment.setArguments(args);
//
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.contentPanel, details_fragment)
//                        .addToBackStack(null).commit();
//
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//
//            }
//        }));

        //REcyclerview Top Selling
//        rv_top_selling.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_top_selling, new RecyclerTouchListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                getid = top_selling_models.get(position).getProduct_id();
//
//                Fragment details_fragment=new Details_Fragment();
//                Bundle args = new Bundle();
//
//                //Intent intent=new Intent(context, Product_details.class);
//                args.putString("product_id",top_selling_models.get(position).getProduct_id());
//                args.putString("product_name", top_selling_models.get(position).getProduct_name());
//                args.putString("category_id",top_selling_models.get(position).getCategory_id());
//                args.putString("product_description",top_selling_models.get(position).getProduct_description());
//                args.putString("deal_price",top_selling_models.get(position).getDeal_price());
//                args.putString("start_date",top_selling_models.get(position).getStart_date());
//                args.putString("start_time",top_selling_models.get(position).getStart_time());
//                args.putString("end_date",top_selling_models.get(position).getEnd_date());
//                args.putString("end_time",top_selling_models.get(position).getEnd_time());
//                args.putString("price",top_selling_models.get(position).getPrice());
//                args.putString( "mrp",top_selling_models.get( position ).getMrp() );
//                args.putString("product_image",top_selling_models.get(position).getProduct_image());
//                args.putString("status", top_selling_models.get(position).getStatus());
//                args.putString("in_stock", top_selling_models.get(position).getIn_stock());
//                args.putString("unit_value", top_selling_models.get(position).getUnit_value());
//                args.putString("unit", top_selling_models.get(position).getUnit());
//                args.putString("increament",top_selling_models.get(position).getIncreament());
//                args.putString("rewards",top_selling_models.get(position).getRewards());
//                args.putString("stock",top_selling_models.get(position).getStock());
//                args.putString("title",top_selling_models.get(position).getTitle());
//                args.putString("seller_id",top_selling_models.get(position).getSeller_id());
//                details_fragment.setArguments(args);
//
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.contentPanel, details_fragment)
//                        .addToBackStack(null).commit();
//
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));
        View_all_TopSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent=new Intent(getActivity(), SelectStore.class);
//                startActivity(intent);
                Bundle args = new Bundle();
                Fragment fm = new Product_fragment();
                args.putString("cat_top_selling", "2");
                args.putString( "viewall","top" );
                args.putString("title","Top Selling Products");
                session_management.setCategoryId("2");
                session_management.setViewAll("top");
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();

            }
        });


        return view;
    }


    private void makeGetSliderRequest() {
        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_SLIDER_URL,
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
                            for (final HashMap<String, String> name : listarray) {
                                CustomSlider textSliderView = new CustomSlider(getActivity());
                                textSliderView.description(name.get("")).image(name.get("slider_image")).setScaleType(BaseSliderView.ScaleType.Fit);
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle().putString("extra", name.get("slider_title"));
                                textSliderView.getBundle().putString("extra", name.get("sub_cat"));
                                imgSlider.addSlider(textSliderView);
                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        //   Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
                                        Bundle args = new Bundle();
                                        Fragment fm = new Product_fragment();
                                        args.putString("id", sub_cat);
                                        args.putString("title", name.get("slider_title"));
                                        args.putString( "viewall","category" );
                                        session_management.setCategoryId(sub_cat);
                                        session_management.setViewAll("category");

                                        fm.setArguments(args);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                                .addToBackStack(null).commit();
                                    }
                                });


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
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(req);

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
                                url_maps.put("product_id",jsonObject.getString("product_id"));
                                url_maps.put("product_name",jsonObject.getString("product_name"));
                                url_maps.put("product_description",jsonObject.getString("product_description"));
                                url_maps.put("product_image",jsonObject.getString("product_image"));
                                url_maps.put("category_id",jsonObject.getString("category_id"));
                                url_maps.put("in_stock",jsonObject.getString("in_stock"));
                                url_maps.put("price",jsonObject.getString("price"));
                                url_maps.put("mrp",jsonObject.getString("mrp"));
                                url_maps.put("unit_value",jsonObject.getString("unit_value"));
                                url_maps.put("unit",jsonObject.getString("unit"));
                                url_maps.put("increament",jsonObject.getString("increament"));
                                url_maps.put("rewards",jsonObject.getString("rewards"));
                                url_maps.put("seller_id",jsonObject.getString("seller_id"));
                                url_maps.put("book_class",jsonObject.getString("book_class"));
                                url_maps.put("language",jsonObject.getString("language"));
                                url_maps.put("subject",jsonObject.getString("subject"));
                                url_maps.put("stock",jsonObject.getString("stock"));
                                url_maps.put("title",jsonObject.getString("title"));
                                url_maps.put("status","0");
                                listarray.add(url_maps);
                            }
                            for (final HashMap<String, String> name : listarray) {
                                CustomSlider textSliderView = new CustomSlider(getActivity());
                                textSliderView.description(name.get("")).image(name.get("slider_image")).setScaleType(BaseSliderView.ScaleType.Fit);
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle().putString("extra", name.get("slider_title"));
                                textSliderView.getBundle().putString("extra", name.get("sub_cat"));
                                banner_slider.addSlider(textSliderView);
                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        //   Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
                                        Bundle args = new Bundle();
                                        Fragment fm = new Details_Fragment();

                                        //Intent intent=new Intent(context, Product_details.class);
                                        args.putString("product_id", name.get("product_id").toString());
                                        args.putString("product_name", name.get("product_name").toString());
                                        args.putString("category_id", name.get("category_id").toString());
                                        args.putString("product_description", name.get("product_description").toString());
                                        //         args.putString("deal_price",modelList.get(position).getDeal_price());
                                        //       args.putString("start_date",modelList.get(position).getStart_date());
                                        //     args.putString("start_time",modelList.get(position).getStart_time());
                                        //   args.putString("end_date",modelList.get(position).getEnd_date());
                                        // args.putString("end_time",modelList.get(position).getEnd_time());
                                        args.putString("price", name.get("price").toString());
                                        args.putString("mrp",name.get("mrp").toString());
                                        args.putString("product_image",name.get("product_image").toString());
                                        args.putString("status", name.get("status").toString());
                                        args.putString("in_stock", name.get("in_stock").toString());
                                        args.putString("unit_value", name.get("unit_value").toString());
                                        args.putString("unit", name.get("unit").toString());
                                        args.putString("increament", name.get("increament").toString());
                                        args.putString("rewards", name.get("rewards").toString());
                                        args.putString("stock",name.get("stock").toString());
                                        args.putString("title", name.get("title").toString());
                                        args.putString("seller_id", name.get("seller_id").toString());
                                        args.putString("book_class", name.get("book_class").toString());
                                        args.putString("language", name.get("language").toString());
                                        args.putString("subject", name.get("subject").toString());

                                        fm.setArguments(args);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                                .addToBackStack(null).commit();
                                    }
                                });

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
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(req);

    }


    private void makeGetFeaturedSlider() {
        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_FEAATURED_SLIDER_URL,
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
                                //  textSliderView.getBundle().putString("extra", name.get("slider_title"));
                                textSliderView.getBundle().putString("extra", name.get("sub_cat"));
                                featuredslider.addSlider(textSliderView);
                                final String sub_cat = (String) textSliderView.getBundle().get("extra");
                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        //   Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
                                        Bundle args = new Bundle();
                                        Fragment fm = new Product_fragment();
                                        args.putString("id", sub_cat);
                                        fm.setArguments(args);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                                .addToBackStack(null).commit();
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
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
        AppController.getInstance().addToRequestQueue(req);

    }


    private void makeGetCategoryRequest() {
        progressDialog.show();
        String tag_json_obj = "json_category_req";
        isSubcat = false;
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");
        isSubcat = true;
       /* if (parent_id != null && parent_id != "") {
        }*/

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_CATEGORY_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    progressDialog.dismiss();
                   // module.showToast("asdasdasds",getActivity());
                    if (response != null && response.length() > 0) {
                        Boolean status = response.getBoolean("responce");
                        if (status) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Category_model>>() {
                            }.getType();
                            category_modelList = gson.fromJson(response.getString("data"), listType);
                            adapter = new Home_adapter(category_modelList);
                           // rv_items.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
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
                            deal_of_day_models = gson.fromJson(response.getString("new_product"), listType);
                            new_adapter = new NewAdapter(deal_of_day_models, getActivity());
                            rv_deal_of_day.setAdapter(new_adapter);
                            new_adapter.notifyDataSetChanged();
                            if (getActivity() != null) {
                                if (deal_of_day_models.isEmpty()) {
                                    //  Toast.makeText(getActivity(), "No Deal For Day", Toast.LENGTH_SHORT).show();
                                    rv_deal_of_day.setVisibility(View.GONE);
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
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    private void make_top_selling() {
        String tag_json_obj = "json_category_req";
        isSubcat = false;
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");
        isSubcat = true;
       /* if (parent_id != null && parent_id != "") {
        }*/

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_TOP_SELLING_PRODUCTS, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("top_sell", response.toString());

                try {
                    if (response != null && response.length() > 0) {
                        Boolean status = response.getBoolean("responce");
                        if (status) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Top_Selling_model>>() {
                            }.getType();
                            top_selling_models = gson.fromJson(response.getString("top_selling_product"), listType);
                            top_selling_adapter = new Top_Selling_Adapter(top_selling_models);
                            rv_top_selling.setAdapter(top_selling_adapter);
                            top_selling_adapter.notifyDataSetChanged();
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
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }


    private void make_menu_items() {
        String tag_json_obj = "json_category_req";
        isSubcat = false;
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");
        isSubcat = true;
       /* if (parent_id != null && parent_id != "") {
        }*/

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_CATEGORY_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    if (response != null && response.length() > 0) {
                        Boolean status = response.getBoolean("responce");
                        if (status) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Home_Icon_model>>() {
                            }.getType();
                            menu_models = gson.fromJson(response.getString("data"), listType);
                            menu_adapter = new Home_Icon_Adapter(menu_models);
                            rv_headre_icons.setAdapter(menu_adapter);
                            menu_adapter.notifyDataSetChanged();
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
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.equals("") || msg.isEmpty())) {
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        //Defining retrofit api service

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void reviewOnApp() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi friends i am using ." + " http://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + " APP");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
//    public  boolean isPermissionGranted() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (getContext().checkSelfPermission(android.Manifest.permission.CALL_PHONE)
//                    == PackageManager.PERMISSION_GRANTED) {
//                Log.v("TAG","Permission is granted");
//                return true;
//            } else {
//
//                Log.v("TAG","Permission is revoked");
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
//                return false;
//            }
//        }
//        else { //permission is automatically granted on sdk<23 upon installation
//            Log.v("TAG","Permission is granted");
//            return true;
//        }
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//
//            case 1: {
//
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
//                    call_action();
//                } else {
//                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }
//    public void call_action(){
//
//        Intent callIntent = new Intent(Intent.ACTION_CALL);
//        callIntent.setData(Uri.parse("tel:" + "919889887711"));
//        startActivity(callIntent);
//    }


    public boolean getUpdaterInfo()
    {
        boolean st=false;
        try
        {
            PackageInfo packageInfo=getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0);
            int ver_code=packageInfo.versionCode;
            if(ver_code == version_code)
            {
                st=true;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
return st;
    }

    public void getAppSettingData()
    {
        progressDialog.show();
        String json_tag="json_app_tag";
        HashMap<String,String> map=new HashMap<>();

        CustomVolleyJsonRequest request=new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.GET_VERSTION_DATA, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            progressDialog.dismiss();
                try
                {
                    boolean sts=response.getBoolean("responce");

                    if(sts)
                    {
                       JSONObject object=response.getJSONObject("data");
                       version_code=Integer.parseInt(object.getString("app_version"));
                       app_link=object.getString("data");

                        if(getUpdaterInfo())
                        {
                            makeGetSliderRequest();
                            makeGetBannerSliderRequest();
                            makeGetCategoryRequest();
                            makeGetFeaturedSlider();
                            make_menu_items();
                            make_deal_od_the_day();
                            make_top_selling();

                        }
                        else
                        {

                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                            builder.setCancelable(false);
                            builder.setMessage("The new version of app is available please update to get access.");
                            builder.setPositiveButton("Update now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    String url = app_link;
                                    Intent in = new Intent(Intent.ACTION_VIEW);
                                    in.setData(Uri.parse(url));
                                    startActivity(in);
                                    getActivity().finish();
                                    //Toast.makeText(getActivity(),"updating",Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                    getActivity().finishAffinity();
                                }
                            });
                            AlertDialog dialog=builder.create();
                            dialog.show();
                        }


                    }
                    else
                    {
                        Toast.makeText(getActivity(),""+response.getString("error"),Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.isEmpty() || msg.equals("")))
                {
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(request,json_tag);
    }
}
