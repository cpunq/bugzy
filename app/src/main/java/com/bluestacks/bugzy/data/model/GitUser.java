package com.bluestacks.bugzy.data.model;


import com.google.gson.annotations.SerializedName;

public class GitUser {
   private String login;
   private String id;
   private int contributions;

   @SerializedName("avatar_url")
   private String avatarUrl;

   @SerializedName("html_url")
   private String htmlUrl;

   public String getLogin() {
      return login;
   }

   public void setLogin(String login) {
      this.login = login;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public int getContributions() {
      return contributions;
   }

   public void setContributions(int contributions) {
      this.contributions = contributions;
   }

   public String getAvatarUrl() {
      return avatarUrl;
   }

   public void setAvatarUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
   }

   public String getHtmlUrl() {
      return htmlUrl;
   }

   public void setHtmlUrl(String htmlUrl) {
      this.htmlUrl = htmlUrl;
   }
}

