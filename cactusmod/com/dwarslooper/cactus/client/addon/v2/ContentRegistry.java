package com.dwarslooper.cactus.client.addon.v2;

import java.util.List;

public interface ContentRegistry<T> {
   void register(T var1);

   List<T> getAll();
}
