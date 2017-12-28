package com.alkalynx.alanfal.alanfal;

/**
 * Created by SaputraPC on 12-Dec-17.
 */

public class NotificationModel {

    public String notifUserID;
    public String notifUserTitle;
    public String notifUserName;
    public long notifUserDate;

    public NotificationModel() {
    }

    public NotificationModel(String notifUserID, String notifUserTitle, String notifUserName, long notifUserDate) {
        this.notifUserID = notifUserID;
        this.notifUserTitle = notifUserTitle;
        this.notifUserName = notifUserName;
        this.notifUserDate = notifUserDate;
    }

    public String getNotifUserID() {
        return notifUserID;
    }

    public void setNotifUserID(String notifUserID) {
        this.notifUserID = notifUserID;
    }

    public String getNotifUserTitle() {
        return notifUserTitle;
    }

    public void setNotifUserTitle(String notifUserTitle) {
        this.notifUserTitle = notifUserTitle;
    }

    public String getNotifUserName() {
        return notifUserName;
    }

    public void setNotifUserName(String notifUserName) {
        this.notifUserName = notifUserName;
    }

    public long getNotifUserDate() {
        return notifUserDate;
    }

    public void setNotifUserDate(long notifUserDate) {
        this.notifUserDate = notifUserDate;
    }
}
