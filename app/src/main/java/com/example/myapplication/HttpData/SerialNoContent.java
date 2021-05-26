package com.example.myapplication.HttpData;

public class SerialNoContent {
    private int storeId;
    private int adminTenantId;
    private int managerTenantId;
    private int groupId;
    private SoreGroup storeGroup;
    private int deviceId;
    private int chanNum;
    private int chanSize;
    private int storeStatus;
    private int saleStatus;
    private int workStatus;
    private int lineStatus;
    private String storeName;
    private String serialNo;
    private String modelName;
    private String qr;

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getAdminTenantId() {
        return adminTenantId;
    }

    public void setAdminTenantId(int adminTenantId) {
        this.adminTenantId = adminTenantId;
    }

    public int getManagerTenantId() {
        return managerTenantId;
    }

    public void setManagerTenantId(int managerTenantId) {
        this.managerTenantId = managerTenantId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public SoreGroup getStoreGroup() {
        return storeGroup;
    }

    public void setStoreGroup(SoreGroup storeGroup) {
        this.storeGroup = storeGroup;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getChanNum() {
        return chanNum;
    }

    public void setChanNum(int chanNum) {
        this.chanNum = chanNum;
    }

    public int getChanSize() {
        return chanSize;
    }

    public void setChanSize(int chanSize) {
        this.chanSize = chanSize;
    }

    public int getStoreStatus() {
        return storeStatus;
    }

    public void setStoreStatus(int storeStatus) {
        this.storeStatus = storeStatus;
    }

    public int getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(int saleStatus) {
        this.saleStatus = saleStatus;
    }

    public int getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(int workStatus) {
        this.workStatus = workStatus;
    }

    public int getLineStatus() {
        return lineStatus;
    }

    public void setLineStatus(int lineStatus) {
        this.lineStatus = lineStatus;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    @Override
    public String toString() {
        return "SerialNoContent{" +
                "storeId=" + storeId +
                ", adminTenantId=" + adminTenantId +
                ", managerTenantId=" + managerTenantId +
                ", groupId=" + groupId +
                ", storeGroup=" + storeGroup +
                ", deviceId=" + deviceId +
                ", chanNum=" + chanNum +
                ", chanSize=" + chanSize +
                ", storeStatus=" + storeStatus +
                ", saleStatus=" + saleStatus +
                ", workStatus=" + workStatus +
                ", lineStatus=" + lineStatus +
                ", storeName='" + storeName + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", modelName='" + modelName + '\'' +
                ", qr='" + qr + '\'' +
                '}';
    }
}
