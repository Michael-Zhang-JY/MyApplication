package com.example.myapplication.MicrosoftStaff;

import java.util.List;

public class Microsoft {
    private boolean Success;
    private List<Users> Users;

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public List<com.example.myapplication.MicrosoftStaff.Users> getUsers() {
        return Users;
    }

    public void setUsers(List<com.example.myapplication.MicrosoftStaff.Users> users) {
        Users = users;
    }

    @Override
    public String toString() {
        return "Microsoft{" +
                "Success=" + Success +
                ", Users=" + Users +
                '}';
    }
}
