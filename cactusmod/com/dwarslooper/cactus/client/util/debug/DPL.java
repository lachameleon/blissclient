package com.dwarslooper.cactus.client.util.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DPL {
   public static final Logger LOG = LoggerFactory.getLogger(DPL.class);
   private static String name;
   private static int calls;
   private static int pos;
   private static int expect;

   public static void prepare(String label, int atLeast) {
      name = label;
      pos = 0;
      expect = atLeast;
   }

   public static void next() {
      call();
      ++pos;
   }

   public static void call() {
      LOG.info("[{}] Triggered position {}", name, pos);
      ++calls;
   }

   public static boolean end() {
      boolean reachedExpect = pos >= expect;
      LOG.info("[{}] Done (calls={}, pos={}, reachedExpect={})", new Object[]{name, calls, pos, reachedExpect});
      return reachedExpect;
   }
}
