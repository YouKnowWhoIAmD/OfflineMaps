package com.junjunguo.pocketmaps.geocoding;
/**
 * Created by Google Deveoper Student Club
 * Varshit Ratna(leader)
 * Devaraj Akhil(Core team)
 */

public class CityMatcher
{
  String lines[];
  boolean isNumeric[];

  public CityMatcher(String searchS)
  {
    lines = searchS.replace('\n', ' ').split(" ");
    isNumeric = new boolean[lines.length];
    for (int i=0; i<lines.length; i++)
    {
      isNumeric[i] = isNumeric(lines[i]);
      if (!isNumeric[i])
      {
        lines[i] = lines[i].toLowerCase();
      }
    }
  }
  
  public boolean isMatching(String value, boolean valueNumeric)
  {
    if (value.isEmpty()) { return false; }
    if (!valueNumeric) { value = value.toLowerCase(); }
    boolean result = false;
    for (int i=0; i<lines.length; i++)
    {
      if (lines[i].isEmpty()) { continue; }
      if (valueNumeric && isNumeric[i])
      {
        if (value.equals(lines[i])) { result = true; break; }
      }
      if (!valueNumeric && !isNumeric[i])
      {
        if (lines[i].length() < 3)
        {
          if (value.equals(lines[i])) { result = true; break; }
        }
        if (value.contains(lines[i])) { result = true; break; }
      }
    }
    return result;
  }
  
  /** Ignores ',' and '.' on check. **/
  public static boolean isNumeric(String s)
  {
    try
    {
      Integer.parseInt(s.replace('.', '0').replace(',', '0'));
      return true;
    }
    catch (NumberFormatException e)
    {
      return false;
    }
  }
}
