package com.example.x.bunuscustomer.retrofit.buyQR;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mobi app on 04.07.2017.
 */

public class QrCompanyUser {

    @SerializedName("col_buy")
    @Expose
    private String colBuy;
    @SerializedName("col_bal")
    @Expose
    private String colBal;
    @SerializedName("col_friend")
    @Expose
    private String colFriend;

    @SerializedName("sum_buy")
    @Expose
    private String sum_buy;

    @SerializedName("status_company_user")
    @Expose
    private String status_company_user;


    public String getColBuy() {
        return colBuy;
    }

    public void setColBuy(String colBuy) {
        this.colBuy = colBuy;
    }

    public String getColBal() {
        return colBal;
    }

    public void setColBal(String colBal) {
        this.colBal = colBal;
    }

    public String getColFriend() {
        return colFriend;
    }

    public void setColFriend(String colFriend) {
        this.colFriend = colFriend;
    }

    public String getSum_buy() {
        return sum_buy;
    }

    public void setSum_buy(String sum_buy) {
        this.sum_buy = sum_buy;
    }

    public String getStatus_company_user() {
        return status_company_user;
    }

    public void setStatus_company_user(String status_company_user) {
        this.status_company_user = status_company_user;
    }
}
