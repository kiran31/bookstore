package Module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import shoparounds.com.MainActivity;
import shoparounds.com.R;
import util.DatabaseCartHandler;
import util.DatabaseHandlerWishList;

public class Module {

Context context;
    public Module() {
    }

    public static void setIntoCart(Activity activity, String cart_id, String product_id, String product_images, String cat_id, String details_product_name, String details_product_price, String details_product_desc, String details_product_rewards, String details_product_unit_price, String details_product_unit,
                                   String details_product_increament, String details_product_inStock, String details_product_title, String details_product_mrp, String seller_id, String book_class, String subject, String language, float qty)
    {
        DatabaseCartHandler db_cart=new DatabaseCartHandler(activity);
        HashMap<String, String> mapProduct=new HashMap<String, String>();
        mapProduct.put("product_id", product_id);
        mapProduct.put("cart_id", cart_id);
        mapProduct.put("product_image",product_images);
        mapProduct.put("cat_id",cat_id);
        mapProduct.put("product_name",details_product_name);
        mapProduct.put("price", details_product_price);
        mapProduct.put("product_description",details_product_desc);
        mapProduct.put("rewards", details_product_rewards);
        mapProduct.put("unit_price",details_product_unit_price );
        mapProduct.put("unit", details_product_unit);
        mapProduct.put("increament",details_product_increament);
        mapProduct.put("stock",details_product_inStock);
        mapProduct.put("title",details_product_title);
        mapProduct.put("mrp",details_product_mrp);
        mapProduct.put("sid",seller_id);
        mapProduct.put("book_class",book_class);
        mapProduct.put("subject",subject);
        mapProduct.put("language",language);


        try {

            boolean tr = db_cart.setCart(mapProduct, qty);
            if (tr == true) {
                MainActivity mainActivity = new MainActivity();
                mainActivity.setCartCounter("" + db_cart.getCartCount());

                //   context.setCartCounter("" + holder.db_cart.getCartCount());
               Toast.makeText(activity, "Added to Cart", Toast.LENGTH_LONG).show();
                int n = db_cart.getCartCount();
                updateintent(activity);
          //      txtTotal.setText("\u20B9"+String.valueOf(db_cart.getTotalAmount()));

            } else if (tr == false) {
                Toast.makeText(activity, "cart updated", Toast.LENGTH_LONG).show();
               // txtTotal.setText("\u20B9"+String.valueOf(db_cart.getTotalAmount()));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }



    }



    public static void updateintent(Activity activity) {
        Intent updates = new Intent("Grocery_cart");
        updates.putExtra("type", "cart");
        activity.sendBroadcast(updates);
    }
    public static  void updatewish(Activity activity)
    {
        Intent updates = new Intent("Grocery_wish");
        updates.putExtra("type", "wish");
        activity.sendBroadcast(updates);

    }

    public String VolleyErrorMessage(VolleyError error)
    {
        String str_error ="";
        if (error instanceof TimeoutError) {
            str_error="Connection Timeout";
        } else if (error instanceof AuthFailureError) {
            str_error="Session Timeout";
            //TODO
        } else if (error instanceof ServerError) {
            str_error="Server not responding please try again later";
            //TODO
        } else if (error instanceof NetworkError) {
            str_error="Server not responding please try again later";
            //TODO
        } else if (error instanceof ParseError) {
            //TODO
            str_error="Something Went Wrong \n Please try again later";
        }else if(error instanceof NoConnectionError){
            str_error="no Internet Connection";
        }

        return str_error;
    }

    public void showToast(String msg,Activity activity)
    {
        LayoutInflater layoutInflater=activity.getLayoutInflater();

        View layout=layoutInflater.inflate(R.layout.toast_layout,(ViewGroup)activity.findViewById(R.id.toast_root));

        ImageView toast_img=(ImageView)layout.findViewById(R.id.toast_img);
        TextView toast_text=(TextView) layout.findViewById(R.id.toast_text);
        toast_text.setText(msg);
        Toast toast=new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void setIntoWish(Activity activity, String product_id, String product_images, String cat_id, String details_product_name,
                                   String details_product_price, String details_product_desc,String details_in_stock,String details_status,
                                   String details_product_rewards, String details_product_unit_value, String details_product_unit,
                                   String details_product_increament, String details_product_inStock, String details_product_title,
                                   String details_product_mrp, String seller_id,String book_class,String subject,String language,String user_id)
    {
        DatabaseHandlerWishList db_cart=new DatabaseHandlerWishList(activity);
        HashMap<String, String> mapProduct=new HashMap<String, String>();
        mapProduct.put("product_id", product_id);
        mapProduct.put("product_image",product_images);
        mapProduct.put("category_id",cat_id);
        mapProduct.put("product_name",details_product_name);
        mapProduct.put("product_description",details_product_desc);
        mapProduct.put("in_stock",details_in_stock);
        mapProduct.put("status",details_status);
        mapProduct.put("price", details_product_price);
        mapProduct.put("rewards", details_product_rewards);
        mapProduct.put("unit_unit",details_product_unit_value );
        mapProduct.put("unit", details_product_unit);
        mapProduct.put("increament",details_product_increament);
        mapProduct.put("stock",details_product_inStock);
        mapProduct.put("title",details_product_title);
        mapProduct.put("mrp",details_product_mrp);
        mapProduct.put("seller_id",seller_id);
        mapProduct.put("book_class",book_class);
        mapProduct.put("subject",subject);
        mapProduct.put("language",language);
        mapProduct.put("user_id",user_id);


        try {

            boolean tr = db_cart.setwishlist(mapProduct);
            if (tr == true) {
                MainActivity mainActivity = new MainActivity();
                mainActivity.setCartCounter("" + db_cart.getWishlistCount(user_id));

                //   context.setCartCounter("" + holder.db_cart.getCartCount());
                Toast.makeText(activity, "Added to WishList" , Toast.LENGTH_LONG).show();
                int n = db_cart.getWishlistCount(user_id);
                updatewish(activity);
                //      txtTotal.setText("\u20B9"+String.valueOf(db_cart.getTotalAmount()));

            } else if (tr == false) {

                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
                // txtTotal.setText("\u20B9"+String.valueOf(db_cart.getTotalAmount()));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }



    }

    public String getFirstImage(String product_images,Context context)
    {
        String first_image="";
        try {
            List<String> image_list=new ArrayList<>();
            JSONArray array = new JSONArray(product_images);
            //Toast.makeText(this,""+product_images,Toast.LENGTH_LONG).show();
            if (product_images.equals(null)) {
                Toast.makeText(context, "There is no image for this product", Toast.LENGTH_LONG).show();
            } else {
                for (int i = 0; i <= array.length() - 1; i++) {
                    image_list.add(array.get(i).toString());

                }

                first_image=image_list.get(0).toString();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
          //  Toast.makeText(context,""+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return first_image;
    }

    }

