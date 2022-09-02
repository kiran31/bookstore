package Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import Model.Socity_model;
import shoparounds.com.R;


public class Socity_adapter extends RecyclerView.Adapter<Socity_adapter.MyViewHolder>
         {

    private ArrayList<Socity_model> modelList;
    private ArrayList<Socity_model> mFilteredList;

    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tv_socity_name);
        }
    }

    public Socity_adapter(ArrayList<Socity_model> modelList) {
        this.modelList = modelList;
        this.mFilteredList = modelList;
    }

    @Override
    public Socity_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_socity_rv, parent, false);

        context = parent.getContext();

        return new Socity_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Socity_adapter.MyViewHolder holder, int position) {
        Socity_model mList = mFilteredList.get(position);

        holder.title.setText(mList.getSocity_name()+" - "+mList.getPincode());
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

//    @Override
//    public Filter getFilter() {
//
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//               // String charString = charSequence.toString();
//                ArrayList<Socity_model> filteredList = new ArrayList<>();
//
//                if (charSequence==null || charSequence.length()==0) {
//                   // Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show();
//                    mFilteredList.addAll(modelList);
//                } else {
//                    String filterPattern=charSequence.toString().toLowerCase().trim();
//                  //  Toast.makeText(context, String.valueOf(modelList.size()), Toast.LENGTH_SHORT).show();
//                    for (Socity_model androidVersion : modelList) {
//                        if (androidVersion.getPincode().toLowerCase().contains(filterPattern) || androidVersion.getPincode().contains(charSequence)) {
//                                filteredList.add(androidVersion);
//                        }
//                    }
//
//
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = filteredList;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                mFilteredList.clear();
//                if(charSequence.equals(""))
//                {
//
//                    mFilteredList=modelList;
//                    Toast.makeText(context,""+filterResults+"m:-- \n"+modelList.size(),Toast.LENGTH_SHORT).show();
//
//                    //  mFilteredList = (ArrayList<Socity_model>) filterResults.values;
//                    notifyDataSetChanged();
//
//                }
//                else
//                {
//                    mFilteredList.addAll((ArrayList<Socity_model>)filterResults.values);
//                    //  mFilteredList = (ArrayList<Socity_model>) filterResults.values;
//                    notifyDataSetChanged();
//
//                }
//
//
//
//            }
//        };
//    }

             public void filterList(ArrayList<Socity_model> filteredList)
             {
            mFilteredList=filteredList;
            notifyDataSetChanged();
             }

}
