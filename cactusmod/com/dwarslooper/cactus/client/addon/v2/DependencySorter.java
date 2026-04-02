package com.dwarslooper.cactus.client.addon.v2;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.addon.v2.annotation.Depends;
import com.dwarslooper.cactus.client.addon.v2.annotation.LoadBefore;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.class_6496;

public class DependencySorter {
   protected static List<Addon> sortAddonsByDependency(List<Addon> addons) {
      MutableGraph<String> graph = GraphBuilder.directed().allowsSelfLoops(false).build();
      Map<String, Addon> addonMap = new HashMap();
      Iterator var3 = addons.iterator();

      Addon addon;
      while(var3.hasNext()) {
         addon = (Addon)var3.next();
         graph.addNode(addon.id());
         addonMap.put(addon.id(), addon);
      }

      var3 = addons.iterator();

      while(true) {
         int var9;
         LoadBefore post;
         do {
            if (!var3.hasNext()) {
               List<String> sortedIds = topologicalSort(graph);
               List<Addon> sorted = new ArrayList(sortedIds.size());
               Iterator var14 = sortedIds.iterator();

               while(var14.hasNext()) {
                  String id = (String)var14.next();
                  sorted.add((Addon)addonMap.get(id));
               }

               return sorted;
            }

            addon = (Addon)var3.next();
            Class<?> clazz = addon.lifecycle().getClass();
            Depends pre = (Depends)clazz.getAnnotation(Depends.class);
            if (pre != null) {
               String[] var7 = pre.value();
               int var8 = var7.length;

               for(var9 = 0; var9 < var8; ++var9) {
                  String dep = var7[var9];
                  if (graph.nodes().contains(dep)) {
                     graph.putEdge(dep, addon.id());
                  } else {
                     CactusClient.getInstance().getAddonHandler().getLogger().warn("Addon '{}' depends on '{}', but said mod / addon is not present", addon.id(), dep);
                  }
               }
            }

            post = (LoadBefore)clazz.getAnnotation(LoadBefore.class);
         } while(post == null);

         String[] var17 = post.value();
         var9 = var17.length;

         for(int var18 = 0; var18 < var9; ++var18) {
            String dep = var17[var18];
            if (graph.nodes().contains(dep)) {
               graph.putEdge(addon.id(), dep);
            }
         }
      }
   }

   public static <T> List<T> topologicalSort(Graph<T> graph) {
      Map<T, Set<T>> successors = new HashMap();
      Iterator var2 = graph.nodes().iterator();

      while(var2.hasNext()) {
         T node = var2.next();
         Set<T> successor = graph.successors(node);
         successors.put(node, successor.isEmpty() ? ImmutableSet.of() : new HashSet(successor));
      }

      List<T> result = new ArrayList();
      Set<T> visited = new HashSet();
      Set<T> visiting = new HashSet();
      Iterator var5 = graph.nodes().iterator();

      Object node;
      boolean cycle;
      do {
         if (!var5.hasNext()) {
            return result;
         }

         node = var5.next();
         Objects.requireNonNull(result);
         cycle = class_6496.method_37951(successors, visited, visiting, result::add, node);
      } while(!cycle);

      throw new IllegalStateException("Cycle detected in dependencies involving '%s'".formatted(new Object[]{node}));
   }
}
