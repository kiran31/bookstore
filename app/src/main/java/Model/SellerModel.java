package Model;

public class SellerModel {
    String user_id,user_name,user_email,user_fullname;

    public SellerModel() {
    }

    public SellerModel(String user_id, String user_name, String user_email, String user_fullname) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_fullname = user_fullname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_fullname() {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname) {
        this.user_fullname = user_fullname;
    }
}
