package Adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import Config.BaseURL;
import Fragment.Details_Fragment;
import Model.Deal_Of_Day_model;
import Model.Product_model;
import Module.Module;
import shoparounds.com.R;

import static android.content.Context.MODE_PRIVATE;



public class NewAdapter extends RecyclerView.Adapter<NewAdapter.MyViewHolder> {

    private List<Deal_Of_Day_model> modelList;
    List<Product_model> product_models;
    private Context context;
    private Activity activity ;
    SharedPreferences preferences;
    Module module;

    public NewAdapter( Activity activity,List<Product_model> product_models) {
        this.product_models = product_models;
        this.activity = activity;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView product_nmae, product_prize, product_mrp , product_discount;
        public ImageView image, img_out_of_stock;
        public CardView card_view_top;
        public RelativeLayout rel_click ;

        public MyViewHolder(View view) {
            super(view);

            product_nmae = (TextView) view.findViewById( R.id.product_name );
            product_prize = (TextView) view.findViewById( R.id.product_prize );
            image = (ImageView) view.findViewById( R.id.iv_icon );
            product_discount=(TextView)view.findViewById( R.id.product_discount );
            product_mrp = (TextView) view.findViewById( R.id.product_mrp );
            card_view_top = (CardView) view.findViewById( R.id.card_view_top );
            img_out_of_stock = view.findViewById( R.id.img_out_of_stock );
            rel_click=view.findViewById( R.id.rel_click );
            module=new Module();
            rel_click.setOnClickListener( this );
           card_view_top.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();
            int stock = Integer.parseInt( modelList.get( position ).getStock() );
            if (id == R.id.rel_click) {

                    Details_Fragment details_fragment = new Details_Fragment();
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Bundle args = new Bundle();

                    //Intent intent=new Intent(context, Product_details.class);
                    args.putString( "product_id", modelList.get( position ).getProduct_id() );
                    args.putString( "product_name", modelList.get( position ).getProduct_name() );
                    args.putString( "category_id", modelList.get( position ).getCategory_id() );
                    args.putString( "product_description", modelList.get( position ).getProduct_description() );
                    args.putString( "price", modelList.get( position ).getPrice() );
                    args.putString( "mrp", modelList.get( position ).getMrp() );
                    args.putString( "product_image", modelList.get( position ).getProduct_image() );
                    args.putString( "status", modelList.get( position ).getStatus() );
                    args.putString( "in_stock", modelList.get( position ).getIn_stock() );
                    args.putString( "unit_value", modelList.get( position ).getUnit_value() );
                    args.putString( "unit", modelList.get( position ).getUnit() );
                    args.putString( "increment", modelList.get( position ).getIncreament() );
                    args.putString( "rewards", modelList.get( position ).getRewards() );
                    args.putString( "stock", modelList.get( position ).getStock() );
                    args.putString( "title", modelList.get( position ).getTitle() );
                    args.putString( "seller_id", modelList.get( position ).getSeller_id() );
                    args.putString( "book_class", modelList.get( position ).getBook_class() );
                    args.putString( "language", modelList.get( position ).getLanguage() );
                    args.putString( "subject", modelList.get( position ).getSubject() );

                    details_fragment.setArguments( args );
                    FragmentManager fragmentManager = activity.getFragmentManager();
                    fragmentManager.beginTransaction().replace( R.id.contentPanel, details_fragment )
                            .addToBackStack( null ).commit();
                }

            }
        }


    public NewAdapter(List<Deal_Of_Day_model> modelList, Activity activity) {
        this.modelList = modelList;
    }

    @Override
    public NewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_top_selling, parent, false);

        context = parent.getContext();

        return new NewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NewAdapter.MyViewHolder holder,final int position) {
        Deal_Of_Day_model mList = modelList.get(position);
        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
        String language=preferences.getString("language","");
        String first_image= module.getFirstImage(mList.getProduct_image(),activity);

        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + first_image)
                .placeholder(R.drawable.icon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.image);
        holder.product_prize.setText(context.getResources().getString(R.string.currency) + mList.getPrice());

        String p_id = mList.getProduct_id();
        int stock = Integer.parseInt( mList.getStock() );
        if (stock <= 0 )
        {

          holder.img_out_of_stock.setVisibility( View.VISIBLE );
        }
        else
        {
            holder.img_out_of_stock.setVisibility( View.GONE );
        }

        if (language.contains("english")) {
            holder.product_nmae.setText(mList.getProduct_name());
            holder.product_prize.setText( context.getResources().getString(R.string.currency) + mList.getPrice());
        }
        else {
            holder.product_nmae.setText(mList.getProduct_name());
            holder.product_prize.setText(context.getResources().getString(R.string.currency) + mList.getPrice());

        }

        Double mrp = Double.parseDouble( mList.getMrp() );
        Double price = Double.parseDouble( mList.getPrice() );

        double diff = mrp-price;
        if (diff>0) {
            double discount = (diff / mrp) * 100;
            holder.product_discount.setText( Math.round(discount) + "%" );
            holder.product_mrp.setText( context.getResources().getString( R.string.currency ) + mList.getMrp() );
            holder.product_mrp.setPaintFlags( holder.product_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
        }
        else
        {
            holder.product_mrp.setVisibility( View.GONE );
            holder.product_discount.setVisibility( View.GONE );
        }

        if (language.contains("english")) {
            holder.product_nmae.setText(mList.getProduct_name());
        }
        else {
            holder.product_nmae.setText(mList.getProduct_name());

        }



    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


}

