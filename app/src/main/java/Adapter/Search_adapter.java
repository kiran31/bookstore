package Adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import Config.BaseURL;
import Fragment.Details_Fragment;
import Model.Product_model;
import Module.Module;
import shoparounds.com.R;


public class Search_adapter extends RecyclerView.Adapter<Search_adapter.MyViewHolder>
        implements Filterable {

    private List<Product_model> modelList;
    private List<Product_model> mFilteredList;
    private Context context;
     Module module;
  //  private DatabaseCartHandler dbcart;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_title, tv_price, tv_reward, tv_total, tv_contetiy ,tv_discount ,tv_mrp;
        public ImageView iv_logo, iv_plus, iv_minus, iv_remove;
        Button tv_add ;
        RelativeLayout rel_no,rel_stock ;

        public MyViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_subcat_title);
            tv_price = (TextView) view.findViewById(R.id.tv_subcat_price);
            tv_reward = (TextView) view.findViewById(R.id.tv_reward_point);
            tv_total = (TextView) view.findViewById(R.id.tv_subcat_total);
            tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_contetiy);
            tv_add = (Button) view.findViewById(R.id.tv_subcat_add);
            iv_logo = (ImageView) view.findViewById(R.id.iv_subcat_img);
            iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            iv_remove = (ImageView) view.findViewById(R.id.iv_subcat_remove);
            tv_discount =(TextView)view.findViewById( R.id.product_discount );
            tv_mrp=(TextView)view.findViewById( R.id.tv_subcat_mrp );
            rel_stock=(RelativeLayout) view.findViewById( R.id.rel_stock );
            iv_remove.setVisibility(View.GONE);
            tv_add.setVisibility( View.GONE );
            tv_total.setVisibility( View.GONE );
            iv_minus.setOnClickListener(this);
            iv_plus.setOnClickListener(this);
            tv_add.setOnClickListener(this);
            iv_logo.setOnClickListener(this);
            module=new Module();
            CardView cardView = (CardView) view.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();

//            if (id == R.id.iv_subcat_plus) {
//
//                int qty = Integer.valueOf(tv_contetiy.getText().toString());
//                qty = qty + 1;
//
//                tv_contetiy.setText(String.valueOf(qty));
//                HashMap<String, String> map = new HashMap<>();
//
//                map.put("product_id", modelList.get(position).getProduct_id());
//                map.put("product_name", modelList.get(position).getProduct_name());
//                map.put("category_id",modelList.get(position).getCategory_id());
//                map.put("product_description", modelList.get(position).getProduct_description());
//                map.put("price", modelList.get(position).getPrice());
//                map.put( "mrp",modelList.get( position ).getMrp());
//                map.put("product_image", modelList.get(position).getProduct_image());
//                map.put("unit_value", modelList.get(position).getUnit_value());
//                map.put("unit", modelList.get(position).getUnit());
//                map.put("increament", modelList.get(position).getIncreament());
//                map.put("rewards",modelList.get(position).getRewards());
//
//
//
//                if (!tv_contetiy.getText().toString().equalsIgnoreCase("0")) {
//
//                    if (dbcart.isInCart(map.get("product_id"))) {
//                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
//                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
//                    } else {
//                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
//                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
//                    }
//                } else {
//                    dbcart.removeItemFromCart(map.get("product_id"));
//                    tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
//                }
//
//             //   Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
//                Double price = Double.parseDouble(map.get("price"));
//             //   Double reward = Double.parseDouble(map.get("rewards"));
//               // tv_reward.setText("" + reward * items);
//
//                tv_total.setText("" + price * qty);
//
//            } else if (id == R.id.iv_subcat_minus) {
//
//                int qty = 1;
//                if (!tv_contetiy.getText().toString().equalsIgnoreCase(""))
//                    qty = Integer.valueOf(tv_contetiy.getText().toString());
//
//                if (qty > 1) {
//                    qty = qty - 1;
//                    tv_contetiy.setText(String.valueOf(qty));
//                    HashMap<String, String> map = new HashMap<>();
//
//                    map.put("product_id", modelList.get(position).getProduct_id());
//                    map.put("product_name", modelList.get(position).getProduct_name());
//                    map.put("category_id",modelList.get(position).getCategory_id());
//                    map.put("product_description", modelList.get(position).getProduct_description());
//                    map.put("deal_price", modelList.get(position).getDeal_price());
//                    map.put("start_date", modelList.get(position).getStart_date());
//                    map.put("start_time", modelList.get(position).getStart_time());
//                    map.put("end_date", modelList.get(position).getEnd_date());
//                    map.put("end_time", modelList.get(position).getEnd_time());
//                    map.put("price", modelList.get(position).getPrice());
//                    map.put( "mrp",modelList.get( position ).getMrp() );
//                    map.put("product_image", modelList.get(position).getProduct_image());
//                    map.put("status", modelList.get(position).getStatus());
//                    map.put("in_stock", modelList.get(position).getIn_stock());
//                    map.put("unit_value", modelList.get(position).getUnit_value());
//                    map.put("unit", modelList.get(position).getUnit());
//                    map.put("increament", modelList.get(position).getIncreament());
//                    map.put("rewards",modelList.get(position).getRewards());
//                    map.put("stock", modelList.get(position).getStock());
//                    map.put("title", modelList.get(position).getTitle());
//
//
//                    if (!tv_contetiy.getText().toString().equalsIgnoreCase("1")) {
//
//                        if (dbcart.isInCart(map.get("product_id"))) {
//                            dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
//                           // tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
//                        } else {
//                            dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
//                           // tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
//                        }
//                    } else {
//                        dbcart.removeItemFromCart(map.get("product_id"));
//                        tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
//                    }
//
////                    Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
//                    Double price = Double.parseDouble(map.get("price"));
////                    Double reward = Double.parseDouble(map.get("rewards"));
////                    tv_reward.setText("" + reward * items);
//
//                    tv_total.setText("" + price * qty);
//                }
//
//            } else if (id == R.id.tv_subcat_add) {
//                HashMap<String, String> map = new HashMap<>();
//                int qty = Integer.valueOf(tv_contetiy.getText().toString());
//                Double price = Double.parseDouble(map.get("price"));
//                tv_total.setText("" + price * qty);
//
//
//
//                map.put("product_id", modelList.get(position).getProduct_id());
//                map.put("product_name", modelList.get(position).getProduct_name());
//                map.put("category_id",modelList.get(position).getCategory_id());
//                map.put("product_description", modelList.get(position).getProduct_description());
//                map.put("deal_price", modelList.get(position).getDeal_price());
//                map.put("start_date", modelList.get(position).getStart_date());
//                map.put("start_time", modelList.get(position).getStart_time());
//                map.put("end_date", modelList.get(position).getEnd_date());
//                map.put("end_time", modelList.get(position).getEnd_time());
//                map.put("price", modelList.get(position).getPrice());
//                map.put( "mrp",modelList.get( position ).getMrp() );
//                map.put("product_image", modelList.get(position).getProduct_image());
//                map.put("status", modelList.get(position).getStatus());
//                map.put("in_stock", modelList.get(position).getIn_stock());
//                map.put("unit_value", modelList.get(position).getUnit_value());
//                map.put("unit", modelList.get(position).getUnit());
//                map.put("increament", modelList.get(position).getIncreament());
//                map.put("rewards",modelList.get(position).getRewards());
//                map.put("stock", modelList.get(position).getStock());
//                map.put("title", modelList.get(position).getTitle());
//
//
//                if (!tv_contetiy.getText().toString().equalsIgnoreCase("1")) {
//
//                    if (dbcart.isInCart(map.get("product_id"))) {
//                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
//                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
//                    } else {
//                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
//                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
//                    }
//                } else {
//                    dbcart.removeItemFromCart(map.get("product_id"));
//                    tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
//                }
//
//                Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
//
//                Double reward = Double.parseDouble(map.get("rewards"));
//                tv_reward.setText("" + reward * items);
//
//                tv_total.setText("" + price * items);
//                ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());
//
//            }
////            else if (id == R.id.iv_subcat_img) {
////                showProductDetail(modelList.get(position).getProduct_image(),
////                        modelList.get(position).getTitle(),
////                        modelList.get(position).getProduct_description(),
////                        modelList.get(position).getProduct_name(),
////                        position, tv_contetiy.getText().toString());
////            }
          if (id == R.id.card_view) {
                Details_Fragment details_fragment=new Details_Fragment();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Bundle args = new Bundle();

                //Intent intent=new Intent(context, Product_details.class);
                args.putString("product_id",modelList.get(position).getProduct_id());
                args.putString("product_name", modelList.get(position).getProduct_name());
                args.putString("category_id",modelList.get(position).getCategory_id());
                args.putString("product_description",modelList.get(position).getProduct_description());
                args.putString("deal_price",modelList.get(position).getDeal_price());
                args.putString("start_date",modelList.get(position).getStart_date());
                args.putString("start_time",modelList.get(position).getStart_time());
                args.putString("end_date",modelList.get(position).getEnd_date());
                args.putString("end_time",modelList.get(position).getEnd_time());
                args.putString("price",modelList.get(position).getPrice());
                args.putString( "mrp",modelList.get( position ).getMrp() );
                args.putString("product_image",modelList.get(position).getProduct_image());
                args.putString("status", modelList.get(position).getStatus());
                args.putString("in_stock", modelList.get(position).getIn_stock());
                args.putString("unit_value", modelList.get(position).getUnit_value());
                args.putString("unit", modelList.get(position).getUnit());
                args.putString("increament",modelList.get(position).getIncreament());
                args.putString("rewards",modelList.get(position).getRewards());
                args.putString("stock",modelList.get(position).getStock());
                args.putString("title",modelList.get(position).getTitle());
                details_fragment.setArguments(args);
                FragmentManager fragmentManager=activity.getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel,details_fragment)
                        .addToBackStack(null).commit();

            }

        }
    }

    public Search_adapter(List<Product_model> modelList, Context context) {
        this.modelList = modelList;
        this.mFilteredList = modelList;

      //  dbcart = new DatabaseCartHandler(context);
    }

    @Override
    public Search_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_search_rv, parent, false);

        context = parent.getContext();

        return new Search_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Search_adapter.MyViewHolder holder, int position) {
        Product_model mList = modelList.get(position);

        int stock=Integer.parseInt(mList.getStock());
        if(stock<=0)
        {
            holder.rel_stock.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.rel_stock.setVisibility(View.GONE);
        }
        String first_image= module.getFirstImage(mList.getProduct_image(),context);
        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + first_image)
                .centerCrop()
                .crossFade()
                .placeholder(R.drawable.icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_logo);

        holder.tv_title.setText(mList.getProduct_name());
        holder.tv_reward.setText(mList.getRewards());
        holder.tv_price.setText(" " + context.getResources().getString(R.string.currency) + " " + mList.getPrice());
        holder.tv_total.setVisibility( View.GONE );
        holder.tv_total.setText(context.getResources().getString(R.string.currency) +mList.getPrice()  );
        Double mrp = Double.parseDouble( mList.getMrp() );
        Double price = Double.parseDouble( mList.getPrice() );
        Double diff = mrp-price;
        if (mrp>price) {
            Double discount = (diff / mrp) * 100;
            holder.tv_discount.setText( Math.round(discount )+ "%" );
            holder.tv_mrp.setText( context.getResources().getString( R.string.currency ) + mList.getMrp() );
            holder.tv_mrp.setPaintFlags( holder.tv_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
        }
        else
        {
            holder.tv_mrp.setVisibility( View.GONE );
            holder.tv_discount.setVisibility( View.GONE );
        }



//        if (dbcart.isInCart(mList.getProduct_id())) {
//
////            holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
//            holder.tv_contetiy.setText(dbcart.getCartItemQty(mList.getProduct_id()));
//        } else {
//            holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
//        }

   //     Double items = Double.parseDouble(dbcart.getInCartItemQty(mList.getProduct_id()));
        Double prices = Double.parseDouble(mList.getPrice());
        Double reward = Double.parseDouble(mList.getRewards());
        holder.tv_reward.setText("" + reward );
       // holder.tv_total.setText("" + price * items);

    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = modelList;
                } else {

                    ArrayList<Product_model> filteredList = new ArrayList<>();

                    for (Product_model androidVersion : modelList) {

                        if (androidVersion.getProduct_name().toLowerCase().contains(charString)) {

                            filteredList.add(androidVersion);
                        }
                    }
                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Product_model>) filterResults.values;
                notifyDataSetChanged();

            }
        };
    }



}