package com.dwarslooper.cactus.client.systems.emoji;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmojiManager {
   private static long fileLastUpdate;
   private static final Set<EmojiCode> emojiCodes;
   private static final Set<EmojiCode> defaultEmojis = new HashSet(List.of(new EmojiCode[]{new EmojiCode("blackflag", "⚐"), new EmojiCode("box", "☐"), new EmojiCode("boxyes", "☑"), new EmojiCode("boxno", "☒"), new EmojiCode("yes", "✔"), new EmojiCode("no", "✘"), new EmojiCode("square1", "\u23f9"), new EmojiCode("square2", "□"), new EmojiCode("circle1", "\u23fa"), new EmojiCode("circle2", "○"), new EmojiCode("cross", "❌"), new EmojiCode("heart1", "❤"), new EmojiCode("heart2", "❣"), new EmojiCode("triangle1", "▲"), new EmojiCode("triangle2", "△"), new EmojiCode("diamond1", "◆"), new EmojiCode("diamond2", "◇"), new EmojiCode("loopedsquare", "⌘"), new EmojiCode("target", "◎"), new EmojiCode("star1", "★"), new EmojiCode("star2", "☆"), new EmojiCode("star3", "⭐"), new EmojiCode("fire", "\ud83d\udd25"), new EmojiCode("unknown", "�"), new EmojiCode("house", "⌂"), new EmojiCode("clock", "⌚"), new EmojiCode("warning", "⚠"), new EmojiCode("electric", "⚡"), new EmojiCode("mail", "✉"), new EmojiCode("pencil", "✎"), new EmojiCode("degree", "°"), new EmojiCode("hourglass", "⌛"), new EmojiCode("skull", "☠"), new EmojiCode("cactus", "\ud83c\udf35"), new EmojiCode("suitspade1", "♠"), new EmojiCode("suitspade2", "♤"), new EmojiCode("suitheart1", "♥"), new EmojiCode("suitheart2", "♡"), new EmojiCode("suitclub1", "♣"), new EmojiCode("suitclub2", "♧"), new EmojiCode("suitdiamond1", "♦"), new EmojiCode("suitdiamond2", "♢"), new EmojiCode("note1", "♩"), new EmojiCode("note2", "♪"), new EmojiCode("note3", "♫"), new EmojiCode("note4", "♬"), new EmojiCode("flat", "♭"), new EmojiCode("sharp", "♮"), new EmojiCode("dice1", "⚀"), new EmojiCode("dice2", "⚁"), new EmojiCode("dice3", "⚂"), new EmojiCode("dice4", "⚃"), new EmojiCode("dice5", "⚄"), new EmojiCode("dice6", "⚅"), new EmojiCode("z0", "▀"), new EmojiCode("z1", "▄"), new EmojiCode("z2", "█"), new EmojiCode("z3", "▌"), new EmojiCode("z4", "▐"), new EmojiCode("z5", "░"), new EmojiCode("z6", "▒"), new EmojiCode("z7", "▓"), new EmojiCode("za", "ⓐ"), new EmojiCode("zb", "ⓑ"), new EmojiCode("zc", "ⓒ"), new EmojiCode("zd", "ⓓ"), new EmojiCode("ze", "ⓔ"), new EmojiCode("zf", "ⓕ"), new EmojiCode("zg", "ⓖ"), new EmojiCode("zh", "ⓗ"), new EmojiCode("zi", "ⓘ"), new EmojiCode("zj", "ⓙ"), new EmojiCode("zk", "ⓚ"), new EmojiCode("zl", "ⓛ"), new EmojiCode("zm", "ⓜ"), new EmojiCode("zn", "ⓝ"), new EmojiCode("zo", "ⓞ"), new EmojiCode("zp", "ⓟ"), new EmojiCode("zq", "ⓠ"), new EmojiCode("zr", "ⓡ"), new EmojiCode("zs", "ⓢ"), new EmojiCode("zt", "ⓣ"), new EmojiCode("zu", "ⓤ"), new EmojiCode("zv", "ⓥ"), new EmojiCode("zw", "ⓦ"), new EmojiCode("zx", "ⓧ"), new EmojiCode("zy", "ⓨ"), new EmojiCode("zz", "ⓩ"), new EmojiCode("za_", "Ⓐ"), new EmojiCode("zb_", "Ⓑ"), new EmojiCode("zc_", "Ⓒ"), new EmojiCode("zd_", "Ⓓ"), new EmojiCode("ze_", "Ⓔ"), new EmojiCode("zf_", "Ⓕ"), new EmojiCode("zg_", "Ⓖ"), new EmojiCode("zh_", "Ⓗ"), new EmojiCode("zi_", "Ⓘ"), new EmojiCode("zj_", "Ⓙ"), new EmojiCode("zk_", "Ⓚ"), new EmojiCode("zl_", "Ⓛ"), new EmojiCode("zm_", "Ⓜ"), new EmojiCode("zn_", "Ⓝ"), new EmojiCode("zo_", "Ⓞ"), new EmojiCode("zp_", "Ⓟ"), new EmojiCode("zq_", "Ⓠ"), new EmojiCode("zr_", "Ⓡ"), new EmojiCode("zs_", "Ⓢ"), new EmojiCode("zt_", "Ⓣ"), new EmojiCode("zu_", "Ⓤ"), new EmojiCode("zv_", "Ⓥ"), new EmojiCode("zw_", "Ⓦ"), new EmojiCode("zx_", "Ⓧ"), new EmojiCode("zy_", "Ⓨ"), new EmojiCode("zz_", "Ⓩ"), new EmojiCode("face1", "(°_°)"), new EmojiCode("face2", "(ㆆ_ㆆ)"), new EmojiCode("face3", "(O_O)"), new EmojiCode("face4", "(ಠ_ಠ)"), new EmojiCode("face5", "(｡◕‿‿◕｡)"), new EmojiCode("face6", "(-‿-)"), new EmojiCode("face7", "(◠‿◠)"), new EmojiCode("face8", "(✿◠‿◠)"), new EmojiCode("face9", "(°o•)"), new EmojiCode("bear1", "ʕ·ᴥ·ʔ"), new EmojiCode("bear2", "ʕっ·ᴥ·ʔっ"), new EmojiCode("bear3", "ʕ♥ᴥ♥ʔ"), new EmojiCode("bird", "(°v°)"), new EmojiCode("cat", "(°ᴥ°)"), new EmojiCode("dog", "(◕ᴥ◕ʋ)"), new EmojiCode("cheer1", "※\\(^o^)/※"), new EmojiCode("cheer2", "*(^_^)*"), new EmojiCode("cool1", "(⌐■_■)"), new EmojiCode("cool2", "(•_•) ( •_•)>⌐■-■ (⌐■_■)"), new EmojiCode("creep", "ԅ(≖‿≖ԅ)"), new EmojiCode("cry", "(╥﹏╥)"), new EmojiCode("dance1", "ᕕ(⌐■_■)ᕗ"), new EmojiCode("dance2", "ᕕ( ᐛ ) ᕗ"), new EmojiCode("table1", "(╯°□°)╯︵ ┻━┻"), new EmojiCode("table2", "(╯°□°)╯︵ ʇɟɐɹɔǝuᴉɯ"), new EmojiCode("table3", "(╯°□°)╯︵ ƃuɐɾoɯ"), new EmojiCode("table4", "┳━┳ノ(°_°ノ)"), new EmojiCode("shrug1", "¯\\_(ツ)_/¯"), new EmojiCode("shrug2", "¯\\(°_o)/¯"), new EmojiCode("gimme", "༼ つ◕_◕༽つ"), new EmojiCode("lol", "L(°O°L)"), new EmojiCode("zoidberg", "(V)(°,,,°)(V)"), new EmojiCode("fly", "─=≡Σ((つ•ω•)つ"), new EmojiCode("sus", "ඞ")}));

   public static Set<EmojiCode> getEmojis() {
      return emojiCodes;
   }

   public static void reloadEmojis() {
      File file = new File(CactusConstants.DIRECTORY, "emojis.json");
      if (file.exists() && file.lastModified() != fileLastUpdate) {
         emojiCodes.clear();
         emojiCodes.addAll(defaultEmojis);

         try {
            FileReader reader = new FileReader(file);
            JsonObject object = (JsonObject)CactusClient.GSON.fromJson(reader, JsonObject.class);
            object.asMap().forEach((string, jsonElement) -> {
               emojiCodes.add(new EmojiCode(string.trim(), jsonElement.getAsString()));
            });
            fileLastUpdate = file.lastModified();
            CactusClient.getLogger().info("Re-loaded emojis file");
         } catch (Exception var3) {
            CactusClient.getLogger().error("Failed to load custom emojis. Is the JSON syntax correct?");
         }
      }

   }

   static {
      emojiCodes = new HashSet(defaultEmojis);
   }
}
