package com.dwarslooper.cactus.client.systems.config;

import com.google.gson.JsonObject;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TreeSerializerFilter {
   private final String path;
   private final Set<String> include;
   private final TreeSerializerFilter.PathValidatorFunction validator;
   public static TreeSerializerFilter ALL;
   public static TreeSerializerFilter NEGATED;

   public TreeSerializerFilter() {
      this(TreeSerializerFilter.PathValidatorFunction.DEFAULT);
   }

   public TreeSerializerFilter(TreeSerializerFilter.PathValidatorFunction validator) {
      this("", new HashSet(), validator);
   }

   public TreeSerializerFilter(String path, Set<String> include, TreeSerializerFilter.PathValidatorFunction validator) {
      this.path = path;
      this.include = include;
      this.validator = validator;
   }

   public TreeSerializerFilter includes(String path) {
      this.include.add(path);
      return this;
   }

   public TreeSerializerFilter resolve(String path) {
      return new TreeSerializerFilter(this.path.isEmpty() ? path : this.path + "." + path, this.include, this.validator);
   }

   public boolean saves(String element) {
      String fullPath = this.path.isEmpty() ? element : this.path + "." + element;
      return this.validator.validate(this.include, fullPath);
   }

   public void checkSerializeAndPass(String element, BiConsumer<String, JsonObject> adder, Function<TreeSerializerFilter, JsonObject> pipeline) {
      if (this.saves(element)) {
         JsonObject object = (JsonObject)pipeline.apply(this.resolve(element));
         adder.accept(element, object);
      }

   }

   public String getPath() {
      return this.path;
   }

   static {
      ALL = new TreeSerializerFilter(TreeSerializerFilter.PathValidatorFunction.ALL);
      NEGATED = new TreeSerializerFilter(TreeSerializerFilter.PathValidatorFunction.NEGATED);
   }

   @FunctionalInterface
   public interface PathValidatorFunction {
      TreeSerializerFilter.PathValidatorFunction ALL = (include, element) -> {
         return true;
      };
      TreeSerializerFilter.PathValidatorFunction DEFAULT = (include, element) -> {
         return include.stream().anyMatch((path) -> {
            return element.equals(path) || element.startsWith(path + ".") || path.startsWith(element + ".");
         });
      };
      TreeSerializerFilter.PathValidatorFunction NEGATED = (include, element) -> {
         return include.stream().noneMatch((path) -> {
            return element.equals(path) || element.startsWith(path + ".") || path.startsWith(element + ".");
         });
      };

      boolean validate(Set<String> var1, String var2);
   }
}
