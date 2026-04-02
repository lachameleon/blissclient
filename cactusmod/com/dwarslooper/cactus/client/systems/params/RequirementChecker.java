package com.dwarslooper.cactus.client.systems.params;

public class RequirementChecker {
   public static boolean check(String requirementString) {
      if (requirementString != null && !requirementString.isEmpty()) {
         String[] conditions = requirementString.split(";");
         String[] var2 = conditions;
         int var3 = conditions.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String condition = var2[var4];
            String[] parts = condition.split(":", 2);
            if (parts.length == 2) {
               String typeStr = parts[0].trim().toUpperCase();
               String[] values = parts[1].split(",");

               SupportRequirement type;
               try {
                  type = SupportRequirement.valueOf(typeStr);
               } catch (IllegalArgumentException var18) {
                  continue;
               }

               boolean matched = false;
               String[] var11 = values;
               int var12 = values.length;

               for(int var13 = 0; var13 < var12; ++var13) {
                  String value = var11[var13];
                  value = value.trim();
                  boolean negated = value.startsWith("!");
                  String checkValue = negated ? value.substring(1) : value;
                  boolean result = type.check(checkValue);
                  if (negated) {
                     result = !result;
                  }

                  if (result) {
                     matched = true;
                     break;
                  }
               }

               if (!matched) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static String processConditionalString(String input) {
      if (input != null && input.startsWith("<")) {
         int endIndex = input.indexOf(62);
         if (endIndex == -1) {
            return input;
         } else {
            String condition = input.substring(1, endIndex);
            String content = input.substring(endIndex + 1);
            return !check(condition) ? null : content;
         }
      } else {
         return input;
      }
   }
}
