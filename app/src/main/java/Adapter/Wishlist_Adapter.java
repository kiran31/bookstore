package Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Config.BaseURL;
import Fragment.Details_Fragment;
import Fragment.Wishlist_fragment;
import Model.Wish_model;

import Module.Module;
import shoparounds.com.R;
import util.DatabaseCartHandler;
import util.DatabaseHandlerWishList;
import util.Session_management;

import static android.content.Context.MODE_PRIVATE;

public class Wishlist_Adapter extends RecyclerView.Adapter<Wishlist_Adapter.MyViewHolder> {

    ArrayList<HashMap<String, String>> list;
    private List<Wish_model> wishList;
    RelativeLayout no_prod_image;
    Activity context;
    private DatabaseCartHandler dbcart;
    private DatabaseHandlerWishList dbWish;
    String language;
    String user_id="";
    Module module;
    Session_management session_management;
    SharedPreferences preferences;
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_title, tv_price, tv_reward, tv_total, tv_contetiy ,tv_subcat_mrp ,tv_discount,tv_reward_point;
        public ImageView iv_logo, iv_plus, iv_minus, iv_remove,wish_before,wish_after;
        public RelativeLayout rel_click;
        public Double reward;
        RelativeLayout rel_no,rel_stock;
        Button tv_add ;

        public MyViewHolder(View view) {
            super(view);
            module=new Module();
           session_management=new Session_management(context);
            rel_no =(RelativeLayout)view.findViewById( R.id.rel_no );
            rel_stock =(RelativeLayout)view.findViewById( R.id.rel_stock );
            tv_title = (TextView) view.findViewById(R.id.product_name);
            tv_price = (TextView) view.findViewById(R.id.product_prize);
            tv_reward = (TextView) view.findViewById(R.id.tv_reward_point);
            tv_total = (TextView) view.findViewById(R.id.tv_subcat_total);
            tv_discount=(TextView)view.findViewById( R.id.product_discount );
            tv_reward_point=(TextView)view.findViewById( R.id.tv_reward_point );
            tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_contetiy);
            tv_subcat_mrp = (TextView) view.findViewById(R.id.product_mrp);
            tv_add = (Button) view.findViewById(R.id.btn_add);
            iv_logo = (ImageView) view.findViewById(R.id.iv_icon);
            iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            iv_remove = (ImageView) view.findViewById(R.id.iv_subcat_remove);

            rel_click = (RelativeLayout) view.findViewById(R.id.rel_wish);
           // iv_remove.setOnClickListener( this );
           user_id=session_management.getUserDetails().get(BaseURL.KEY_ID);
            rel_click.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();

            HashMap<String,String > map=list.get(position);
            if(id == R.id.rel_click)
            {


                }



        }
    }

    public Wishlist_Adapter(ArrayList<HashMap<String, String>> list, RelativeLayout no_prod_image, Activity context) {
        this.list = list;
        this.no_prod_image = no_prod_image;
        this.context = context;

        dbcart=new DatabaseCartHandler(context);
        dbWish=new DatabaseHandlerWishList(context);
    }


    @Override
    public Wishlist_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wishlist, parent, false);
        context = (Activity) parent.getContext();
        return new Wishlist_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final Wishlist_Adapter.MyViewHolder holder, final int position) {
        final HashMap<String, String> map = list.get(position);
        //imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.your_image));
    //    holder.wish_before.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_close));
        String first_image= module.getFirstImage(map.get("product_image"), context);

        Glide.with( context )
                .load( BaseURL.IMG_PRODUCT_URL + first_image)
                .fitCenter()
                .placeholder( R.drawable.icon )
                .crossFade()
                .diskCacheStrategy( DiskCacheStrategy.ALL )
                .dontAnimate()
                .into( holder.iv_logo );
        preferences = context.getSharedPreferences( "lan", MODE_PRIVATE );
        language = preferences.getString( "language", "" );
        Double price = Double.valueOf( map.get("price") );
        Double qty = Double.parseDouble( (String) holder.tv_contetiy.getText() );
        Double total = price *qty ;
        holder.tv_total.setText( context.getResources().getString( R.string.currency ) + price );



        String bk_lang="";
        String lang=map.get("language");
//        if(lang.equals("") || lang.isEmpty() || lang.equals(null))
//        {
//
//        }
//        else
//        {
//         //   bk_lang=" | "+getBookLanguage(lang);
//        }


        if (language.contains( "english" )) {
            holder.tv_title.setText( map.get("product_name") +bk_lang );
        } else {
            holder.tv_title.setText( map.get("product_name") +bk_lang);

        }

//        int in_stock=Integer.parseInt(mList.getIn_stock());
//        if(in_stock==0)
//        {
//            holder.rel_stock.setVisibility(View.VISIBLE);
//        }


        holder.tv_reward_point.setText(""+Double.parseDouble(map.get("rewards")));
        //  holder.tv_reward.setText( mList.getRewards() );
        holder.tv_price.setText( context.getResources().getString( R.string.currency ) + map.get("price") );
        holder.tv_total.setText( context.getResources().getString( R.string.currency ) + map.get("price") );

        boolean is_inCart=dbcart.isInCart(map.get("product_id"));
        if (is_inCart)
        {
            holder.tv_add.setVisibility(View.GONE);
            holder.rel_no.setVisibility(View.VISIBLE);
            String qt=dbcart.getCartItemQty(map.get("product_id"));
            holder.tv_contetiy.setText(qt);
        }
        else
        {

        }


        double mrp = Double.parseDouble( map.get("mrp") );
        int discount=getDiscount(map.get("price"),map.get("mrp"));
        if(mrp>price) {

            holder.tv_subcat_mrp.setPaintFlags( holder.tv_subcat_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
            holder.tv_subcat_mrp.setText( context.getResources().getString( R.string.currency ) + map.get("mrp") );
            holder.tv_discount.setText( discount + "%" );
        }
        else
        {
            holder.tv_subcat_mrp.setVisibility( View.GONE );
            holder.tv_discount.setVisibility( View.GONE );
        }

//        else {
//            holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
//        }
        Double items = Double.parseDouble( dbcart.getInCartItemQty(map.get("product_id") ) );
        // Double price = Double.parseDouble(mList.getPrice());
        Double reward = Double.parseDouble( map.get("rewards") );
        holder.tv_total.setText( context.getResources().getString( R.string.currency ) + price );
        // holder.tv_reward.setText( context.getResources().getString( R.string.currency ) + reward * items );

        holder.tv_add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Toast.makeText(context,"2nd",Toast.LENGTH_LONG).show();

                    HashMap<String, String> map = list.get(position);
                    int qty = Integer.valueOf(holder.tv_contetiy.getText().toString());
                    Double price = Double.parseDouble(map.get("price").toString());

                    holder.tv_total.setText(context.getResources().getString(R.string.currency) + price);

                    preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
                    language = preferences.getString("language", "");


                    String unt=map.get("unit_value")+map.get("unit");
                    Module module=new Module();
                    module.setIntoCart(context,map.get("product_id"),map.get("product_id"),
                            map.get("product_image"),map.get("category_id"),map.get("product_name"),map.get("price"),map.get("product_description"),map.get("rewards"),
                            map.get("price"),unt,map.get("increament"),
                            map.get("stock"),map.get("title"),map.get("mrp"),
                         map.get("seller_id"),map.get("book_class"),map.get("subject"),map.get("language"),qty);
                    updateintent();
                    holder.tv_add.setVisibility(View.GONE);
                    holder.rel_no.setVisibility( View.VISIBLE );




            }
        } );
        holder.iv_plus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.valueOf( holder.tv_contetiy.getText().toString() );
                qty = qty + 1;
                holder.tv_contetiy.setText( String.valueOf( qty ) );

                preferences = context.getSharedPreferences( "lan", MODE_PRIVATE );
                language = preferences.getString( "language", "" );


                String unt=map.get("unit_value")+map.get("unit");
                double unit_price=Double.parseDouble(dbcart.getUnitPrice(map.get("product_id")));
                // Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
                Module module=new Module();

                module.setIntoCart(context,map.get("product_id"),map.get("product_id"),
                        map.get("product_image"),map.get("category_id"),map.get("product_name"),String.valueOf(qty*unit_price),map.get("product_description"),map.get("rewards"),
                        map.get("price"),unt,map.get("increament"),
                        map.get("stock"),map.get("title"),map.get("mrp"),
                        map.get("seller_id"),map.get("book_class"),map.get("subject"),map.get("language"),qty);
//                module.setIntoCart(context,modelList.get(position).getProduct_id(),modelList.get(position).getProduct_id(),
//                        modelList.get(position).getProduct_image(),modelList.get(position).getCategory_id(),modelList.get(position).getProduct_name(),
//                        String.valueOf(qty*unit_price),modelList.get(position).getProduct_description(),modelList.get(position).getRewards()
//                        ,modelList.get(position).getPrice(),unt,modelList.get(position).getIncreament(),modelList.get(position).getStock()
//                        ,modelList.get(position).getTitle(),modelList.get(position).getMrp(),modelList.get(position).getSeller_id(),modelList.get(position).getBook_class(),modelList.get(position).getSubject(),modelList.get(position).getLanguage(),qty);
                updateintent();
                //      Toast.makeText(context,""+dbcart.getTotalAmount(),Toast.LENGTH_LONG).show();

                //   Double price = Double.parseDouble( modelList.get( position ).getPrice() );
                //     holder.tv_total.setText( context.getResources().getString( R.string.currency ) + price * qty );

            }
        } );
        holder.iv_minus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = 1;
                if (!holder.tv_contetiy.getText().toString().equalsIgnoreCase( "" ))
                    qty = Integer.valueOf( holder.tv_contetiy.getText().toString() );


                if (qty > 1) {
                    qty = qty - 1;
                    holder.tv_contetiy.setText( String.valueOf( qty ) );
                    preferences = context.getSharedPreferences( "lan", MODE_PRIVATE );
                    language = preferences.getString( "language", "" );

                    String unt=map.get("unit_value")+map.get("unit");
                    double unit_price=Double.parseDouble(dbcart.getUnitPrice(map.get("product_id")));
                    // Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
                    Module module=new Module();

                    module.setIntoCart(context,map.get("product_id"),map.get("product_id"),
                            map.get("product_image"),map.get("category_id"),map.get("product_name"),String.valueOf(qty*unit_price),map.get("product_description"),map.get("rewards"),
                            map.get("price"),unt,map.get("increament"),
                            map.get("stock"),map.get("title"),map.get("mrp"),
                            map.get("seller_id"),map.get("book_class"),map.get("subject"),map.get("language"),qty);
                    updateintent();
                    //Toast.makeText(context,""+dbcart.getTotalAmount(),Toast.LENGTH_LONG).show();
                    holder.tv_add.setVisibility( View.GONE );
                    holder.rel_no.setVisibility( View.VISIBLE );

                } else {
                    dbcart.removeItemFromCart( map.get("product_id") );
                    updateintent();
                    holder.rel_no.setVisibility( View.GONE );
                    holder.tv_add.setVisibility( View.VISIBLE );
                }
                // Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
                //  Double price = Double.parseDouble(map.get("price").trim());
                Double price = Double.parseDouble( map.get("price") );
                holder.tv_total.setText( context.getResources().getString( R.string.currency ) + price * qty );

            }

        } );

        holder.iv_remove.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbWish.removeItemFromWishlist( map.get( "product_id" ),user_id );
                list.remove(position);
                notifyDataSetChanged();

                if(list.size()<=0)
                {
                    no_prod_image.setVisibility(View.VISIBLE);
                    Wishlist_fragment.rv_wishlist.setVisibility(View.GONE);
                }
                updatewish();

            }
        } );

        holder.rel_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Details_Fragment details_fragment = new Details_Fragment();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Bundle args = new Bundle();


                args.putString("product_id", map.get("product_id"));
                args.putString("product_name", map.get("product_name"));
                args.putString("category_id", map.get("category_id"));
                args.putString("product_description",map.get("product_description"));
                args.putString("price", map.get("price"));
                args.putString("mrp",map.get("mrp"));
                args.putString("product_image",map.get("product_image"));
                args.putString("status", map.get("status"));
                args.putString("in_stock", map.get("in_stock"));
                args.putString("unit_value", map.get("unit_value"));
                args.putString("unit", map.get("unit"));
                args.putString("increament", map.get("increament"));
                args.putString("rewards",map.get("rewards"));
                args.putString("stock",map.get("stock"));
                args.putString("title", map.get("title"));
                args.putString("seller_id", map.get("seller_id"));
                args.putString("book_class", map.get("book_class"));
                args.putString("language", map.get("language"));
                args.putString("subject", map.get("subject"));
                details_fragment.setArguments(args);
                FragmentManager fragmentManager = activity.getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, details_fragment)
                        .addToBackStack(null).commit();

            }
        });


    }
    @Override
    public int getItemCount() {
        return list.size();
    }


    private void showImage(String image) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.product_image_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        ImageView iv_image_cancle = (ImageView) dialog.findViewById(R.id.iv_dialog_cancle);
        ImageView iv_image = (ImageView) dialog.findViewById(R.id.iv_dialog_img);

        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + image)
                .centerCrop()
                .placeholder(R.drawable.icon)
                .crossFade()
                .into(iv_image);

        iv_image_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

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

    private void updateintent() {
        Intent updates = new Intent("Grocery_cart");
        updates.putExtra("type", "cart");
        context.sendBroadcast(updates);
    }
    private  void updatewish()
    {
        Intent updates = new Intent( "Grocery_wish" );
        updates.putExtra( "type","wish" );
        context.sendBroadcast( updates );
    }

//    public String getBookLanguage(String lan)
//    {
//        String lang="";
//        List<String> list=new ArrayList<>();
//        try
//        {
//            JSONArray array=new JSONArray(lan);
//            for(int i=0; i<array.length();i++)
//            {
//                String l=array.getString(i).toString();
//                list.add(l);
//            }
//        }
//        catch (Exception ex)
//        {
//            Toast.makeText(context,""+ex.getMessage(),Toast.LENGTH_LONG).show();
//        }
//        StringBuilder sb = new StringBuilder();
//
//        // Appends characters one by one
//        for (String  ch : list) {
//            sb.append(ch);
//            sb.append(",");
//        }
//        lang=sb.toString();
//        return lang.substring(0,lang.length()-1);
//    }
}
