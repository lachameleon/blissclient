package com.dwarslooper.cactus.client.addon.v2;

import java.util.LinkedList;
import java.util.List;

public class SimpleRegistry implements ContentRegistry<Object> {
   private final List<Object> instances = new LinkedList();

   public void register(Object value) {
      this.instances.add(value);
   }

   public List<Object> getAll() {
      return this.instances;
   }
}
