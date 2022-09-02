package Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import Fragment.My_Pending_Order;
import Fragment.My_Past_Order;
import Fragment.My_cancel_order_fragment;


public class PagerOrderAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerOrderAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                My_Pending_Order tab1 = new My_Pending_Order();
                return tab1;
            case 1:
                My_Past_Order tab2 = new My_Past_Order();
                return tab2;
                case 2:
                My_cancel_order_fragment tab3 = new My_cancel_order_fragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}