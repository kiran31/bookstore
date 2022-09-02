package Model;

import android.app.Activity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;



public class Deal_Of_Day_model {

    String product_id;
    String product_name;
    String category_id;
    String product_description;
    String deal_price;
    String start_date;
    String start_time;
    String end_date;
    String end_time;
    String price;
    String product_image;
    String product_name_arb;
    String product_description_arb;
    String status;
    String in_stock;
    String unit_value;
    String unit;
    String increament;
    String rewards;
    String stock;
    String title;
    String mrp;
    String seller_id;
    String book_class;
    String language;
    String subject;

    public Deal_Of_Day_model(String product_id, String product_name, String category_id, String product_description, String deal_price, String start_date, String start_time, String end_date, String end_time, String price, String product_image, String product_name_arb, String product_description_arb, String status, String in_stock, String unit_value, String unit, String increament, String rewards, String stock, String title, String mrp, String seller_id, String book_class, String language, String subject) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.category_id = category_id;
        this.product_description = product_description;
        this.deal_price = deal_price;
        this.start_date = start_date;
        this.start_time = start_time;
        this.end_date = end_date;
        this.end_time = end_time;
        this.price = price;
        this.product_image = product_image;
        this.product_name_arb = product_name_arb;
        this.product_description_arb = product_description_arb;
        this.status = status;
        this.in_stock = in_stock;
        this.unit_value = unit_value;
        this.unit = unit;
        this.increament = increament;
        this.rewards = rewards;
        this.stock = stock;
        this.title = title;
        this.mrp = mrp;
        this.seller_id = seller_id;
        this.book_class = book_class;
        this.language = language;
        this.subject = subject;
    }

    public String getBook_class() {
        return book_class;
    }

    public void setBook_class(String book_class) {
        this.book_class = book_class;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getDeal_price() {
        return deal_price;
    }

    public void setDeal_price(String deal_price) {
        this.deal_price = deal_price;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_name_arb() {
        return product_name_arb;
    }

    public void setProduct_name_arb(String product_name_arb) {
        this.product_name_arb = product_name_arb;
    }

    public String getProduct_description_arb() {
        return product_description_arb;
    }

    public void setProduct_description_arb(String product_description_arb) {
        this.product_description_arb = product_description_arb;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIn_stock() {
        return in_stock;
    }

    public void setIn_stock(String in_stock) {
        this.in_stock = in_stock;
    }

    public String getUnit_value() {
        return unit_value;
    }

    public void setUnit_value(String unit_value) {
        this.unit_value = unit_value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIncreament() {
        return increament;
    }

    public void setIncreament(String increament) {
        this.increament = increament;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }
}
