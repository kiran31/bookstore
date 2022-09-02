package Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import shoparounds.com.MainActivity;
import shoparounds.com.R;

//import android.support.v4.app.Fragment;


public class Empty_wishlist_fragment extends Fragment {
    private static String TAG = Empty_wishlist_fragment.class.getSimpleName();

    RelativeLayout Shop_now;
Dialog loadingBar ;

    public Empty_wishlist_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate( R.layout.fragment_empty_wishlist, container, false );
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.wishlist));
        loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);

        Shop_now = (RelativeLayout) view.findViewById(R.id.btn_shopnow);
        Shop_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.show();
                android.app.Fragment fm = new Home_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                loadingBar.dismiss();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }
        });
        return view ;
    }

}
