package com.bluestacks.bugzy.models;

public class Error {
   protected String message;
   protected String detail;
   protected String code;

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getDetail() {
      return detail;
   }

   public void setDetail(String detail) {
      this.detail = detail;
   }

   public String getCode() {
      return code;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public void setCode(int code) {
      this.code = code + "";
   }
}
