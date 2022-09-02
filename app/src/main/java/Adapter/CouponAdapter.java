package Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import Model.CouponModel;
import shoparounds.com.R;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 23,December,2019
 */
public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {
    Activity activity;
    List<CouponModel> modelList;

    public CouponAdapter(Activity activity, List<CouponModel> modelList) {
        this.activity = activity;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_coupon_rv,null);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        CouponModel couponModel=modelList.get(i);
        String dis_type=couponModel.getDiscount_type();
        if(dis_type.equals("per"))
        {
            holder.tv_per.setText(couponModel.getDiscount_value()+"%");

        }
        else
        {
            holder.tv_per.setText(activity.getResources().getString(R.string.currency)+couponModel.getDiscount_value());
        }
        holder.tv_desc.setText(couponModel.getCoupon_name());
        holder.tv_code.setText(couponModel.getCoupon_code());
        holder.tv_date.setText("Valid Date :-"+couponModel.getValid_to());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_per,tv_desc,tv_date,tv_code;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_per=(TextView)itemView.findViewById(R.id.tv_per);
            tv_desc=(TextView)itemView.findViewById(R.id.tv_desc);
            tv_date=(TextView)itemView.findViewById(R.id.tv_date);
            tv_code=(TextView)itemView.findViewById(R.id.tv_code);
        }
    }
}
