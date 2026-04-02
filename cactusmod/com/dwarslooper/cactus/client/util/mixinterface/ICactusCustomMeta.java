package com.dwarslooper.cactus.client.util.mixinterface;

import com.google.gson.JsonObject;

public interface ICactusCustomMeta {
   void setCactusCustomData(JsonObject var1);

   JsonObject getCactusCustomData();
}
