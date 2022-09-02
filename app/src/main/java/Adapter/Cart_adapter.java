package Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;

import Config.BaseURL;
import Fragment.Cart_fragment;
import Module.Module;
import shoparounds.com.R;
import util.DatabaseCartHandler;

import static Fragment.Cart_fragment.lin_amt;
import static Fragment.Cart_fragment.no_prod_image;
import static Fragment.Cart_fragment.rv_cart;
import static Fragment.Cart_fragment.tv_clear;
import static android.content.Context.MODE_PRIVATE;



public class Cart_adapter extends RecyclerView.Adapter<Cart_adapter.ProductHolder> {
    ArrayList<HashMap<String, String>> list;
    Activity activity;
    String Reward;
    SharedPreferences preferences;
    String language;
    Module module;

    int lastpostion;
    DatabaseCartHandler dbHandler;

    public Cart_adapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        this.list = list;
        this.activity = activity;

        dbHandler = new DatabaseCartHandler(activity);
        /*common = new CommonClass(activity);
        File cacheDir = StorageUtils.getCacheDirectory(activity);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        imgconfig = new ImageLoaderConfiguration.Builder(activity)
                .build();
        ImageLoader.getInstance().init(imgconfig);*/
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart_product, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, final int position) {
        final HashMap<String, String> map = list.get(position);
        String first_image= module.getFirstImage(map.get("product_image"),activity);

        Glide.with(activity)
                .load(BaseURL.IMG_PRODUCT_URL + first_image)
                .centerCrop()
                .placeholder(R.drawable.icon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_logo);
        preferences = activity.getSharedPreferences("lan", MODE_PRIVATE);
        language=preferences.getString("language","");
            holder.tv_title.setText(map.get("product_name"));

        holder.tv_reward.setText(""+Double.parseDouble(map.get("rewards")));
        holder.tv_price.setText( activity.getResources().getString(R.string.currency)+map.get("unit_price"));

        holder.tv_contetiy.setText(map.get("qty"));
        int mrp = Integer.parseInt( map.get("mrp") );
        int price = Integer.parseInt(map.get("unit_price" ));
        if(mrp>price) {
            int discount=getDiscount(map.get("unit_price" ),map.get("mrp"));

            holder.tv_mrp.setText( activity.getResources().getString( R.string.currency ) + map.get("mrp") );
            holder.tv_mrp.setPaintFlags( holder.tv_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
            holder.tv_discount.setText( discount + "%" );
        }
        else
        {
            holder.tv_mrp.setVisibility( View.GONE );
            holder.tv_discount.setVisibility( View.GONE );
        }
        Double items = Double.parseDouble( String.valueOf( holder.tv_contetiy.getText() ) );
        final Double prices = Double.parseDouble(map.get("unit_price"));
      //  Double reward = Double.parseDouble(map.get("rewards"));
        holder.tv_total.setText("" + prices * items);
      //  holder.tv_reward.setText("" + reward * items);
        holder.iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = 1;
                if (!holder.tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(holder.tv_contetiy.getText().toString());

                if (qty > 1) {
                    qty = qty - 1;
                    holder.tv_contetiy.setText(String.valueOf(qty));

                    holder.tv_total.setText("" + prices * qty);
                    int id=Integer.parseInt(map.get("product_id"));

                    ArrayList<HashMap<String, String>> mapP=dbHandler.getCartProduct(id);

                    HashMap<String,String> mapProduct=mapP.get(0);

                    double t=Double.parseDouble(mapProduct.get("unit_price"));
                    //   double p=Double.parseDouble(mapProduct.get("unit_price"));
                    holder.tv_total.setText("" + t * qty);
                    //  String pr=String.valueOf(t+p);
                    float qt=Float.valueOf(qty);

                    double unit_price=Double.parseDouble(dbHandler.getUnitPrice(map.get( "product_id" ) ));
                    mapProduct.put("product_id", map.get( "product_id" ));
                    mapProduct.put("cart_id", map.get( "cart_id" ));
                    mapProduct.put("product_image",map.get("product_image"));
                    mapProduct.put("cat_id",map.get( "cat_id" ));
                    mapProduct.put("product_name",map.get( "product_name" ));
                    mapProduct.put("price", String.valueOf(qty*unit_price));
                    mapProduct.put("product_description",map.get("product_description"));
                    mapProduct.put("rewards", map.get("rewards"));
                    mapProduct.put("unit_price", map.get("price"));
                    mapProduct.put("unit", map.get("unit"));
                    mapProduct.put("increament", map.get("increament"));
                    mapProduct.put("stock",map.get( "stock" ));
                    mapProduct.put("title",map.get( "title" ));
                    mapProduct.put("mrp",map.get( "mrp" ));
                    mapProduct.put("sid",map.get( "sid" ));


                    boolean update_cart=dbHandler.setCart(mapProduct,qt);
                    if(update_cart==true)
                    {
                        Toast.makeText(activity,"Qty Not Updated",Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        Toast.makeText(activity,"Qty Updated",Toast.LENGTH_LONG).show();
                        Cart_fragment.tv_total.setText(activity.getResources().getString(R.string.currency)+" "+dbHandler.getTotalAmount());
                    }


                }

                if (holder.tv_contetiy.getText().toString().equalsIgnoreCase("0")) {
                    dbHandler.removeItemFromCart(map.get("product_id"));
                    list.remove(position);
                    notifyDataSetChanged();

                    updateintent();
                }
            }
        });



        holder.iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int qty = Integer.valueOf(holder.tv_contetiy.getText().toString());
                int id = Integer.parseInt(map.get("product_id"));

                ArrayList<HashMap<String, String>> mapP = dbHandler.getCartProduct(id);

                HashMap<String, String> mapProduct = mapP.get(0);
                int stock = Integer.parseInt(mapProduct.get("stock"));

                if (qty == stock) {
                    Toast.makeText(activity, "We have only " +stock +" in Stock", Toast.LENGTH_LONG).show();
                } else {

                    qty = qty + 1;
                    holder.tv_contetiy.setText(String.valueOf(qty));



                    double t = Double.parseDouble(mapProduct.get("unit_price"));
                    double unit_price = Double.parseDouble(dbHandler.getUnitPrice(map.get("product_id")));
                    //   double p=Double.parseDouble(mapProduct.get("unit_price"));
                    holder.tv_total.setText("" + t * qty);
                    //  String pr=String.valueOf(t+p);
                    float qt = Float.valueOf(qty);

                    // Toast.makeText(activity,"\npri "+map.get("unit_value")+"\n am "+pr,Toast.LENGTH_LONG ).show();
                    //  HashMap<String, String> mapProduct = new HashMap<String, String>();


                    mapProduct.put("product_id", map.get("product_id"));
                    mapProduct.put("cart_id", map.get("cart_id"));
                    mapProduct.put("product_image", map.get("product_image"));
                    mapProduct.put("cat_id", map.get("cat_id"));
                    mapProduct.put("product_name", map.get("product_name"));
                    mapProduct.put("price", String.valueOf(qty * unit_price));
                    mapProduct.put("product_description", map.get("product_description"));
                    mapProduct.put("rewards", map.get("rewards"));
                    mapProduct.put("unit_price", map.get("price"));
                    mapProduct.put("unit", map.get("unit"));
                    mapProduct.put("increament", map.get("increament"));
                    mapProduct.put("stock", map.get("stock"));
                    mapProduct.put("title", map.get("title"));
                    mapProduct.put("mrp", map.get("mrp"));
                    mapProduct.put("sid", map.get("sid"));

//                Toast.makeText(activity,"id- "+map.get("product_id")+"\n img- "+map.get("product_image")+"\n cat_id- "+map.get("category_id")+"\n" +
//                        "\n name- "+map.get("product_name")+"\n price- "+pr+"\n unit_price- "+map.get("unit_price")+
//                        "\n size- "+ map.get("size")+"\n col- "+ map.get("color")+"rew- "+ map.get("rewards")+"unit_value- "+ map.get("unit_value")+
//                        "unit- "+map.get("unit")+"\n inc- "+map.get("increament")+"stock- "+map.get("stock")+"title- "+map.get("title"),Toast.LENGTH_LONG).show();

                    boolean update_cart = dbHandler.setCart(mapProduct, qt);
                    if (update_cart == true) {
                        Toast.makeText(activity, "Qty Not Updated", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(activity, "Qty Updated", Toast.LENGTH_LONG).show();
                        Cart_fragment.tv_total.setText(activity.getResources().getString(R.string.currency) + " " + dbHandler.getTotalAmount());
                    }
                    //  holder.tv_total.setText(""+db_cart.getTotalAmount());
                }
            }
        });
//
//


        holder.tv_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cnt=dbHandler.getCartCount();
//                Toast.makeText(activity,"id- "+map.get("product_id")+"\n img- "+map.get("product_image")+"\n cat_id- "+map.get("category_id")+"\n" +
//                        "\n name- "+map.get("product_name")+"\n price- "+map.get("price")+"\n unit_price- "+map.get("unit_price")+
//                        "\n size- "+ map.get("size")+"\n col- "+ map.get("color")+"rew- "+ map.get("rewards")+"unit_value- "+ map.get("unit_value")+
//                        "unit- "+map.get("unit")+"\n inc- "+map.get("increament")+"stock- "+map.get("stock")+"title- "+map.get("title")+"cnt- "+cnt,Toast.LENGTH_LONG).show();


            }
        });

        holder.tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbHandler.setCart(map, Float.valueOf(holder.tv_contetiy.getText().toString()));

                Double items = Double.parseDouble(dbHandler.getInCartItemQty(map.get("product_id")));
                Double price = Double.parseDouble(map.get("price"));
                Double reward = Double.parseDouble(map.get("rewards"));
                holder.tv_total.setText("" + price * items);
               // holder.tv_reward.setText("" + reward * items);
             //   holder.tv_total.setText(activity.getResources().getString(R.string.tv_cart_total) + price * items + " " + activity.getResources().getString(R.string.currency));
                updateintent();
            }
        });

        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHandler.removeItemFromCart(map.get("product_id"));
                list.remove(position);
                notifyDataSetChanged();

                if(list.size()<=0)
                {
                    rv_cart.setVisibility(View.GONE);
                    no_prod_image.setVisibility(View.VISIBLE);
                    lin_amt.setVisibility(View.GONE);
                    tv_clear.setVisibility(View.GONE);
                }
                updateintent();
            }
        });

        holder.rel_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Toast.makeText(activity,""+map.get("sid").toString(),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        public TextView tv_title, tv_price, tv_reward, tv_total, tv_contetiy, tv_mrp,tv_discount,
                tv_unit, tv_unit_value;
        Button tv_add;
        RelativeLayout rel_no ;
        public ImageView iv_logo, iv_plus, iv_minus, iv_remove;
   RelativeLayout rel_click;
        public ProductHolder(View view) {
            super(view);

            tv_title = (TextView) view.findViewById(R.id.tv_subcat_title);
            tv_price = (TextView) view.findViewById(R.id.tv_subcat_price);
            tv_total = (TextView) view.findViewById(R.id.tv_subcat_total);
            tv_reward = (TextView) view.findViewById(R.id.tv_reward_point);
            tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_contetiy);
            tv_discount=(TextView)view.findViewById( R.id.product_discount );
            tv_add = (Button) view.findViewById(R.id.tv_subcat_add);
            iv_logo = (ImageView) view.findViewById(R.id.iv_subcat_img);
            iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            iv_remove = (ImageView) view.findViewById(R.id.iv_subcat_remove);
            rel_no =(RelativeLayout)view.findViewById( R.id.rel_no );
            rel_click =(RelativeLayout)view.findViewById( R.id.rel_click );
            tv_mrp =(TextView)view.findViewById( R.id.tv_subcat_mrp );
           // tv_add.setText(R.string.tv_pro_update);
            rel_no.setVisibility( View.VISIBLE );
                tv_add.setVisibility( View.GONE );
                module=new Module();
        }
    }

    private void updateintent() {
        Intent updates = new Intent("Grocery_cart");
        updates.putExtra("type", "cart");
        activity.sendBroadcast(updates);
    }

    public int getDiscount(String price, String mrp)
    {
        double mrp_d=Double.parseDouble(mrp);
        double price_d=Double.parseDouble(price);
        double per=((mrp_d-price_d)/mrp_d)*100;
        double df=Math.round(per);
        int d=(int)df;
        return d;
    }
}

