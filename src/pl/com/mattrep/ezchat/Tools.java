package pl.com.mattrep.ezchat;

public class Tools
{
  public static String fixColors(String msg)
  {
    return msg.replaceAll("&", "§");
  }
}
