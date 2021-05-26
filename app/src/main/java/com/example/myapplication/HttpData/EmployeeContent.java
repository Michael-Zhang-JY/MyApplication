package com.example.myapplication.HttpData;

public class EmployeeContent {
    private String card;
    private String contact;
    private String employeeName;
    private String employeeState;
    private String label;
    private int wechatUserId;
    private int employeeId;
    private CardCustomer customer;

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeState() {
        return employeeState;
    }

    public void setEmployeeState(String employeeState) {
        this.employeeState = employeeState;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getWechatUserId() {
        return wechatUserId;
    }

    public void setWechatUserId(int wechatUserId) {
        this.wechatUserId = wechatUserId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public CardCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(CardCustomer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "EmployeeContent{" +
                "card='" + card + '\'' +
                ", contact='" + contact + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", employeeState='" + employeeState + '\'' +
                ", label='" + label + '\'' +
                ", wechatUserId=" + wechatUserId +
                ", employeeId=" + employeeId +
                ", customer=" + customer +
                '}';
    }
}
