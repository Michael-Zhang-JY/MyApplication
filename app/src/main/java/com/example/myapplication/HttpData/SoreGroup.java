package com.example.myapplication.HttpData;

public class SoreGroup {
    private int groupId;
    private String groupName;

    @Override
    public String toString() {
        return "SoreGroup{" +
                "groupId=" + groupId +
                ", groupName=" + groupName +
                '}';
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
