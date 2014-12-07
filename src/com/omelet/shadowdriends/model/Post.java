package com.omelet.shadowdriends.model;

public class Post {
    private int post_id, user_id;
    private String title, subtitle, status, profilePic, timeStamp;
    private int agreeCount, disagreeCount;

    public Post() {
    }

    public Post(int post_id,int user_id, String title, String subtitle, String status,
                    String profilePic, String timeStamp, int agreeCount, int disagreeCount) {
        super();
        this.post_id = post_id;
        this.user_id = user_id;
        this.title = title;
        this.subtitle= subtitle;
        this.status = status;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.agreeCount = agreeCount;
        this.disagreeCount = disagreeCount;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int id) {
        this.post_id = id;
    }
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int id) {
        this.user_id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }





    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String status) {
        this.subtitle = subtitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getAgreeCount() {
        return agreeCount;
    }

    public void setAgreeCount(int agreeCount) {
        this.agreeCount = agreeCount;
    }
    public int getDisagreeCount() {
        return disagreeCount;
    }

    public void setDisagreeCount(int disagreeCount) {
        this.disagreeCount = disagreeCount;
    }

}
