package com.bluestacks.bugzy.models;

public abstract class Request {
   private String cmd;

   public Request(String cmd) {
      this.cmd = cmd;
   }

   public String getCmd() {
      return cmd;
   }

   public void setCmd(String cmd) {
      this.cmd = cmd;
   }
}
