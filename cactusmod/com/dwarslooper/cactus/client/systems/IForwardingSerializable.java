package com.dwarslooper.cactus.client.systems;

import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.google.gson.JsonObject;

public interface IForwardingSerializable<T, E> extends ISerializable<T> {
   ISerializable<T> delegate();

   default JsonObject toJson(TreeSerializerFilter filter) {
      return this.delegate().toJson(filter);
   }

   default T fromJson(JsonObject object) {
      return this.delegate().fromJson(object);
   }
}
