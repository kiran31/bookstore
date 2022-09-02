package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import shoparounds.com.R;

public class SortAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> list;

    public SortAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get( i );
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);

        View row=layoutInflater.inflate( R.layout.row_spinner,viewGroup,false);

        TextView sortName=(TextView)row.findViewById(R.id.tv_sp);

         sortName.setText(list.get(i) );

        return row;
    }
}
