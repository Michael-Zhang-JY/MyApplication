package com.example.myapplication.HttpData;

public class CardCustomer {
    private String buyState;
    private String customerName;
    private String customerState;
    private int customerId;
    private int customerUserId;
    private int tenantId;

    public String getBuyState() {
        return buyState;
    }

    public void setBuyState(String buyState) {
        this.buyState = buyState;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerState() {
        return customerState;
    }

    public void setCustomerState(String customerState) {
        this.customerState = customerState;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerUserId() {
        return customerUserId;
    }

    public void setCustomerUserId(int customerUserId) {
        this.customerUserId = customerUserId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "CardCustomer{" +
                "buyState='" + buyState + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerState='" + customerState + '\'' +
                ", customerId=" + customerId +
                ", customerUserId=" + customerUserId +
                ", tenantId=" + tenantId +
                '}';
    }
}
