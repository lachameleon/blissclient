package com.dwarslooper.cactus.client.event.impl;

public final class WorldLeftEvent {
   private final WorldLeftEvent.Reason reason;
   public static final WorldLeftEvent DISCONNECTED;
   public static final WorldLeftEvent RECONFIGURING;

   private WorldLeftEvent(WorldLeftEvent.Reason reason) {
      this.reason = reason;
   }

   public WorldLeftEvent.Reason getReason() {
      return this.reason;
   }

   static {
      DISCONNECTED = new WorldLeftEvent(WorldLeftEvent.Reason.DISCONNECT);
      RECONFIGURING = new WorldLeftEvent(WorldLeftEvent.Reason.RECONFIGURE);
   }

   public static enum Reason {
      DISCONNECT,
      RECONFIGURE;

      // $FF: synthetic method
      private static WorldLeftEvent.Reason[] $values() {
         return new WorldLeftEvent.Reason[]{DISCONNECT, RECONFIGURE};
      }
   }
}
