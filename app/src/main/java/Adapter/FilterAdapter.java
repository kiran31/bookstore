package Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import shoparounds.com.FilterActivity;
import shoparounds.com.R;

/**
 * Developed by Binplus Technologies pvt. ltd. on 13,November,2019
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    List<String> list;
    Activity activity;
    int pos=-1;
    int type=0;

    public FilterAdapter(List<String> list, Activity activity, int type) {
        this.list = list;
        this.activity = activity;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_class_layout,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,final int position) {

        holder.txt_title.setText(list.get(position).toString());
        if(pos==position)
            holder.img_icon.setVisibility(View.VISIBLE);
        else
        {
            holder.img_icon.setVisibility(View.GONE);
        }
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               pos=position;

               if(type==1)
               {
                   FilterActivity.book_class=list.get(position).toString();
                   FilterActivity.txt_class.setVisibility(View.VISIBLE);
                   FilterActivity.txt_class.setText("Class/Board : "+list.get(position).toString());
               }
               else if(type==2)
               {
                   FilterActivity.subject=list.get(position).toString();
                   FilterActivity.txt_subject.setVisibility(View.VISIBLE);
                   FilterActivity.txt_subject.setText("Subject : "+list.get(position).toString());
               }
               else if(type==3)
               {
                   FilterActivity.language=list.get(position).toString();
                   FilterActivity.txt_language.setVisibility(View.VISIBLE);
                   FilterActivity.txt_language.setText("Language : "+list.get(position).toString());
               }
               notifyDataSetChanged();
           }
       });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_title;
        ImageView img_icon;
        LinearLayout lin_click;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_title=(TextView)itemView.findViewById(R.id.txt_class);
            img_icon=(ImageView)itemView.findViewById(R.id.img_icon);
            lin_click=(LinearLayout)itemView.findViewById(R.id.lin_click);
        }
    }
}
