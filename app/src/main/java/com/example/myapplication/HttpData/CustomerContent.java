package com.example.myapplication.HttpData;

public class CustomerContent {
    private int businessId;
    private int customerGroupId;
    private int groupId;
    private CustomerDTO customerDTO;

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public int getCustomerGroupId() {
        return customerGroupId;
    }

    public void setCustomerGroupId(int customerGroupId) {
        this.customerGroupId = customerGroupId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public CustomerDTO getCustomerDTO() {
        return customerDTO;
    }

    public void setCustomerDTO(CustomerDTO customerDTO) {
        this.customerDTO = customerDTO;
    }

    @Override
    public String toString() {
        return "CustomerContent{" +
                "businessId=" + businessId +
                ", customerGroupId=" + customerGroupId +
                ", groupId=" + groupId +
                ", customerDTO=" + customerDTO +
                '}';
    }
}
