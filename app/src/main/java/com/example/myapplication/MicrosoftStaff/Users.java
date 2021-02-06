package com.example.myapplication.MicrosoftStaff;


public class Users {
    private int ID, SiteID;
    private String Alias, LMAlias, GMAlias, EmployeeNO, MSUserID, DisplayName, Surname, GivenName, JobTitle, Mail,
            OfficeLocation, PreferredLanguage, UserPrincipalName, Department, PostalCode, StreetAddress, BusinessPhone, BadgeNumber,
            QueryMail, QueryUserPrincipalName;
    private boolean IsActive, isTemp;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSiteID() {
        return SiteID;
    }

    public void setSiteID(int siteID) {
        SiteID = siteID;
    }

    public String getAlias() {
        return Alias;
    }

    public void setAlias(String alias) {
        Alias = alias;
    }

    public String getLMAlias() {
        return LMAlias;
    }

    public void setLMAlias(String LMAlias) {
        this.LMAlias = LMAlias;
    }

    public String getGMAlias() {
        return GMAlias;
    }

    public void setGMAlias(String GMAlias) {
        this.GMAlias = GMAlias;
    }

    public String getEmployeeNO() {
        return EmployeeNO;
    }

    public void setEmployeeNO(String employeeNO) {
        EmployeeNO = employeeNO;
    }

    public String getMSUserID() {
        return MSUserID;
    }

    public void setMSUserID(String MSUserID) {
        this.MSUserID = MSUserID;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getGivenName() {
        return GivenName;
    }

    public void setGivenName(String givenName) {
        GivenName = givenName;
    }

    public String getJobTitle() {
        return JobTitle;
    }

    public void setJobTitle(String jobTitle) {
        JobTitle = jobTitle;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public String getOfficeLocation() {
        return OfficeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        OfficeLocation = officeLocation;
    }

    public String getPreferredLanguage() {
        return PreferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        PreferredLanguage = preferredLanguage;
    }

    public String getUserPrincipalName() {
        return UserPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        UserPrincipalName = userPrincipalName;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public String getStreetAddress() {
        return StreetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        StreetAddress = streetAddress;
    }

    public String getBusinessPhone() {
        return BusinessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        BusinessPhone = businessPhone;
    }

    public String getBadgeNumber() {
        return BadgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        BadgeNumber = badgeNumber;
    }

    public String getQueryMail() {
        return QueryMail;
    }

    public void setQueryMail(String queryMail) {
        QueryMail = queryMail;
    }

    public String getQueryUserPrincipalName() {
        return QueryUserPrincipalName;
    }

    public void setQueryUserPrincipalName(String queryUserPrincipalName) {
        QueryUserPrincipalName = queryUserPrincipalName;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    @Override
    public String toString() {
        return "Users{" +
                "ID=" + ID +
                ", SiteID=" + SiteID +
                ", Alias='" + Alias + '\'' +
                ", LMAlias='" + LMAlias + '\'' +
                ", GMAlias='" + GMAlias + '\'' +
                ", EmployeeNO='" + EmployeeNO + '\'' +
                ", MSUserID='" + MSUserID + '\'' +
                ", DisplayName='" + DisplayName + '\'' +
                ", Surname='" + Surname + '\'' +
                ", GivenName='" + GivenName + '\'' +
                ", JobTitle='" + JobTitle + '\'' +
                ", Mail='" + Mail + '\'' +
                ", OfficeLocation='" + OfficeLocation + '\'' +
                ", PreferredLanguage='" + PreferredLanguage + '\'' +
                ", UserPrincipalName='" + UserPrincipalName + '\'' +
                ", Department='" + Department + '\'' +
                ", PostalCode='" + PostalCode + '\'' +
                ", StreetAddress='" + StreetAddress + '\'' +
                ", BusinessPhone='" + BusinessPhone + '\'' +
                ", BadgeNumber='" + BadgeNumber + '\'' +
                ", QueryMail='" + QueryMail + '\'' +
                ", QueryUserPrincipalName='" + QueryUserPrincipalName + '\'' +
                ", IsActive=" + IsActive +
                ", isTemp=" + isTemp +
                '}';
    }
}
