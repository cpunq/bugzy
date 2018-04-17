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
}
