package util;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import Config.BaseURL;
import Model.Socity_model;
import Model.SuggestGetSet;

public class JsonParseSuggestion {
    double current_latitude,current_longitude;
    public JsonParseSuggestion(){}
    public JsonParseSuggestion(double current_latitude, double current_longitude){
        this.current_latitude=current_latitude;
        this.current_longitude=current_longitude;
    }
    public List<Socity_model>getParseJsonSociety(String name)
    {
        List<Socity_model> listData = new ArrayList<>();
        try {
            String temp=name.replace(" ", "%20");
            URL js = new URL(BaseURL.GET_SOCITY_URL+"?pincode="+"%"+temp+"%");
            URLConnection jc = js.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
            String line = reader.readLine();
            JSONObject jsonResponse = new JSONObject(line);
            JSONArray jsonArray = jsonResponse.getJSONArray("");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject r = jsonArray.getJSONObject(i);
                /*String socity_id;
                String socity_name;
                String pincode;

                String delivery_charge;*/
                listData.add(new Socity_model(r.getString("socity_id"),r.getString("socity_name"),r.getString("pincode"),r.getString("delivery_charge")));
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return listData;
    }
    public List<SuggestGetSet> getParseJsonWCF(String sName)
    {
        List<SuggestGetSet> ListData = new ArrayList<SuggestGetSet>();
        try {
            String temp=sName.replace(" ", "%20");
            URL js = new URL(BaseURL.BASE_URL+"index.php/api/get_products_suggestion?product_name="+"%"+temp+"%");
            URLConnection jc = js.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
            String line = reader.readLine();
            JSONObject jsonResponse = new JSONObject(line);
            JSONArray jsonArray = jsonResponse.getJSONArray("data");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject r = jsonArray.getJSONObject(i);
                ListData.add(new SuggestGetSet(r.getString("product_id"),r.getString("product_name"),r.getString("price")));
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return ListData;

    }

}