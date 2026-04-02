package com.dwarslooper.cactus.client.compat.server.specific;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.compat.server.specific.impl.tjc.TjcHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ServerSpecific {
   private static final Map<String, IServerHandler> serverHandlers = new HashMap();
   public static IServerHandler currentHandler;

   public static void registerGeneric() {
      registerServerSpecific(".*thejocraft\\.net.*", new TjcHandler());
   }

   public static void joinServer(String address) {
      CactusClient.getLogger().info("Checking address: {}", address);
      Iterator var1 = serverHandlers.entrySet().iterator();

      Entry entry;
      do {
         if (!var1.hasNext()) {
            CactusClient.getLogger().info("No server specific module found for {}", address);
            close();
            return;
         }

         entry = (Entry)var1.next();
      } while(!address.matches((String)entry.getKey()));

      CactusClient.getLogger().info("Server specific module found for {}", address);
      currentHandler = (IServerHandler)entry.getValue();
      currentHandler.handleActive();
   }

   public static void close() {
      currentHandler.handleInactive();
      currentHandler = IServerHandler.NONE;
   }

   public static void registerServerSpecific(String addressRegex, IServerHandler handler) {
      serverHandlers.put(addressRegex, handler);
   }

   static {
      currentHandler = IServerHandler.NONE;
   }
}
