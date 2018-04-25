package in.bugzy.data.remote.model;


public class Request {
   /**
    * It is not required any more because we are now using
    * the new api format from manuscript, which has
    * this cmd in the URL itself
    */
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
