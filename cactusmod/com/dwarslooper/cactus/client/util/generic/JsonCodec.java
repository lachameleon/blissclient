package com.dwarslooper.cactus.client.util.generic;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;

public class JsonCodec<T> {
   private static final Gson gson = new Gson();
   private final Map<String, JsonCodec.FieldCodec<?>> fields = new HashMap();
   private final Function<Map<String, Object>, T> constructor;
   private final Function<T, Map<String, Object>> extractor;

   public JsonCodec(Function<Map<String, Object>, T> constructor, Function<T, Map<String, Object>> extractor) {
      this.constructor = constructor;
      this.extractor = extractor;
   }

   public <V> JsonCodec<T> field(String name, Class<V> type) {
      Map var10000 = this.fields;
      Function var10005 = (v) -> {
         return v;
      };
      Objects.requireNonNull(type);
      var10000.put(name, new JsonCodec.FieldCodec(type, var10005, type::cast));
      return this;
   }

   public <V> JsonCodec<T> field(String name, Class<V> type, Function<V, Object> encoder, Function<Object, V> decoder) {
      this.fields.put(name, new JsonCodec.FieldCodec(type, encoder, decoder));
      return this;
   }

   public JsonObject encode(T object) {
      Map<String, Object> values = (Map)this.extractor.apply(object);
      JsonObject jsonObject = new JsonObject();
      Iterator var4 = values.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<String, Object> entry = (Entry)var4.next();
         String fieldName = (String)entry.getKey();
         Object value = entry.getValue();
         if (this.fields.containsKey(fieldName) && value != null) {
            JsonCodec.FieldCodec<?> codec = (JsonCodec.FieldCodec)this.fields.get(fieldName);
            Object encodedValue = codec.encode(value);
            jsonObject.add(fieldName, gson.toJsonTree(encodedValue));
         }
      }

      return jsonObject;
   }

   public T decode(String json) {
      return this.decode((JsonObject)gson.fromJson(json, JsonObject.class));
   }

   public T decode(JsonObject json) {
      Map<String, Object> values = new HashMap();
      Iterator var3 = this.fields.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<String, JsonCodec.FieldCodec<?>> entry = (Entry)var3.next();
         String fieldName = (String)entry.getKey();
         JsonCodec.FieldCodec<?> codec = (JsonCodec.FieldCodec)entry.getValue();
         if (json.has(fieldName)) {
            JsonElement element = json.get(fieldName);
            Object rawValue = gson.fromJson(element, codec.type);
            Object decodedValue = codec.decode(rawValue);
            values.put(fieldName, decodedValue);
         }
      }

      return this.constructor.apply(values);
   }

   private static class FieldCodec<V> {
      final Class<V> type;
      final Function<Object, Object> encoder;
      final Function<Object, V> decoder;

      FieldCodec(Class<V> type, Function<V, Object> encoder, Function<Object, V> decoder) {
         this.type = type;
         this.encoder = (v) -> {
            return encoder.apply(v);
         };
         this.decoder = decoder;
      }

      Object encode(Object value) {
         return this.encoder.apply(value);
      }

      V decode(Object value) {
         return this.decoder.apply(value);
      }
   }
}
