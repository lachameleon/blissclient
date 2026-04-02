package com.dwarslooper.cactus.client.util.exception;

public class FunnyException extends Exception {
   public FunnyException(String s) {
      super(s);
   }

   public FunnyException(Throwable t) {
      super(t);
   }

   public FunnyException(String s, Throwable t) {
      super(s, t);
   }
}
