package Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;

import Adapter.Wishlist_Adapter;
import Config.BaseURL;
import shoparounds.com.MainActivity;
import shoparounds.com.R;
import util.DatabaseCartHandler;
import util.DatabaseHandlerWishList;
import util.Session_management;


public class Wishlist_fragment extends Fragment {

    private static String TAG = Shop_Now_fragment.class.getSimpleName();
    private Bundle savedInstanceState;
    private DatabaseHandlerWishList db_wish;
   public static RecyclerView rv_wishlist;
    DatabaseCartHandler db_cart;
    ProgressDialog loadingBar;
    public static RelativeLayout no_prod_image;
 Session_management session_management;
 String user_id;
    public Wishlist_fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_wishlist, container, false );
        setHasOptionsMenu( true );

        ((MainActivity) getActivity()).setTitle( getResources().getString( R.string.wishlist ) );
        rv_wishlist = view.findViewById( R.id.rv_wishlist );
        no_prod_image = view.findViewById( R.id.no_prod_image );
        rv_wishlist.setLayoutManager( new LinearLayoutManager( getActivity() ) );
        db_cart=new DatabaseCartHandler(getActivity());
        session_management=new Session_management(getActivity());
        //db = new DatabaseHandler(getActivity());
        user_id=session_management.getUserDetails().get(BaseURL.KEY_ID);
        db_wish = new DatabaseHandlerWishList( getActivity() );

        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);

        ArrayList<HashMap<String, String>> map = db_wish.getWishlistAll(user_id);


       if(map.size()<=0)
       {
           if(map.size()<=0)
           {
               rv_wishlist.setVisibility(View.GONE);
               no_prod_image.setVisibility(View.VISIBLE);
           }
       }
        Wishlist_Adapter adapter = new Wishlist_Adapter( map,no_prod_image,getActivity() );

        rv_wishlist.setAdapter( adapter );
        adapter.notifyDataSetChanged();



        return view;
    }



//    private void showClearDialog() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder( getActivity() );
//        alertDialog.setMessage( getResources().getString( R.string.sure_del ) );
//        alertDialog.setNegativeButton( getResources().getString( R.string.cancle ), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        } );
//        alertDialog.setPositiveButton( getResources().getString( R.string.yes ), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                // clear cart data
//                db_wish.clearWishtable();
//                ArrayList<HashMap<String, String>> map = db_wish.getWishtableAll();
//                Wishlist_Adapter adapter = new Wishlist_Adapter(  map,getActivity() );
//                rv_wishlist.setAdapter( adapter );
//                adapter.notifyDataSetChanged();
//
//                //updateData();
//
//                dialogInterface.dismiss();
//            }
//        } );
//
//        alertDialog.show();
//
//
//    }


    @Override
    public void onResume() {
        super.onResume();
        // register reciver
        getActivity().registerReceiver(mCart, new IntentFilter("Grocery_cart"));
        getActivity().registerReceiver( mWish,new IntentFilter( "Grocery_wish" ) );

    }

    // broadcast reciver for receive data
    private BroadcastReceiver mCart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("cart")) {
                updateData();
            }
        }
    };

    private void updateData() {

        ((MainActivity) getActivity()).setCartCounter("" + db_cart.getCartCount());
    }


    @Override
    public void onPause() {
        super.onPause();
        // unregister reciver
        getActivity().unregisterReceiver(mCart);
        getActivity().unregisterReceiver( mWish );
    }
    private BroadcastReceiver mWish= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("wish")) {
                updateWishData();
            }
        }
    };

    private void updateWishData() {

        ((MainActivity) getActivity()).setWishCounter("" + db_wish.getWishlistCount(user_id));
    }


}
