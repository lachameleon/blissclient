package com.dwarslooper.cactus.client.systems;

import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.google.gson.JsonObject;

public interface ISerializable<T> {
   JsonObject toJson(TreeSerializerFilter var1);

   T fromJson(JsonObject var1);
}
