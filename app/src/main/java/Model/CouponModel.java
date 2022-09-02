package Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 23,December,2019
 */
public class CouponModel {
    String id,coupon_name,coupon_code,valid_from,valid_to,validity_type,product_name,discount_type,discount_value,cart_value,uses_restriction;

    public CouponModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoupon_name() {
        return coupon_name;
    }

    public void setCoupon_name(String coupon_name) {
        this.coupon_name = coupon_name;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getValid_from() {
        return valid_from;
    }

    public void setValid_from(String valid_from) {
        this.valid_from = valid_from;
    }

    public String getValid_to() {
        return valid_to;
    }

    public void setValid_to(String valid_to) {
        this.valid_to = valid_to;
    }

    public String getValidity_type() {
        return validity_type;
    }

    public void setValidity_type(String validity_type) {
        this.validity_type = validity_type;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discount_type) {
        this.discount_type = discount_type;
    }

    public String getDiscount_value() {
        return discount_value;
    }

    public void setDiscount_value(String discount_value) {
        this.discount_value = discount_value;
    }

    public String getCart_value() {
        return cart_value;
    }

    public void setCart_value(String cart_value) {
        this.cart_value = cart_value;
    }

    public String getUses_restriction() {
        return uses_restriction;
    }

    public void setUses_restriction(String uses_restriction) {
        this.uses_restriction = uses_restriction;
    }
}
