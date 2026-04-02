package com.dwarslooper.cactus.client.util.generic;

import java.util.TreeMap;

public class RomanNumber {
   private static final TreeMap<Integer, String> map = new TreeMap();

   public static String toRoman(int number) {
      int l = (Integer)map.floorKey(number);
      if (number == l) {
         return (String)map.get(number);
      } else {
         String var10000 = (String)map.get(l);
         return var10000 + toRoman(number - l);
      }
   }

   static {
      map.put(1000, "M");
      map.put(900, "CM");
      map.put(500, "D");
      map.put(400, "CD");
      map.put(100, "C");
      map.put(90, "XC");
      map.put(50, "L");
      map.put(40, "XL");
      map.put(10, "X");
      map.put(9, "IX");
      map.put(5, "V");
      map.put(4, "IV");
      map.put(1, "I");
   }
}
