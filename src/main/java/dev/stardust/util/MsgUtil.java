package dev.stardust.util;

import java.util.Map;
import java.util.HashMap;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import meteordevelopment.meteorclient.systems.modules.Module;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.systems.modules.Modules;

/**
 * @author Tas [@0xTas] <root@0xTas.dev>
 **/
public class MsgUtil {
    private final static Map<String, String> modulePrefixes = new HashMap<>();

    public static String getPrefix() {
        return ChatFormatting.DARK_GRAY + "<" + StardustUtil.rCC() +
            ChatFormatting.ITALIC + "✨" + ChatFormatting.DARK_GRAY + ">";
    }

    public static String getRawPrefix() {
        return "[Stardust]";
    }

    public static String getRawPrefix(String module) {
        return "[" + module + "]";
    }

    public static void initModulePrefixes() {
        Modules modules = Modules.get();
        if (modules == null) return;

        for (Module module : modules.getAll()) {
            if (!module.getClass().getName().startsWith("dev.stardust.modules.")) continue;
            String name = module.name;
            String color = StardustUtil.rCC();
            modulePrefixes.put(name, color);
        }
    }

    public static String getModulePrefix(String module) {
        if (!modulePrefixes.containsKey(module)) {
            return String.valueOf(ChatFormatting.DARK_GRAY) + '[' + StardustUtil.rCC() +
                ChatFormatting.ITALIC + Utils.nameToTitle(module) + ChatFormatting.DARK_GRAY + ']';
        } else {
            return String.valueOf(ChatFormatting.DARK_GRAY) + '[' + modulePrefixes.get(module) +
                ChatFormatting.ITALIC + Utils.nameToTitle(module) + ChatFormatting.DARK_GRAY + ']';
        }
    }

    public static void sendRawMsg(String msg) {
        if (mc.player == null) return;
        mc.player.sendSystemMessage(Component.literal(msg));
    }

    public static void sendMsg(String msg) {
        if (mc.player == null) return;

        try {
            StringBuilder sb = new StringBuilder();
            mc.player.sendSystemMessage(Component.literal(sb.append(getPrefix()).append(' ').append(ChatFormatting.GRAY).append(msg).toString()));
        } catch (Exception ignored) {}
    }

    public static void sendMsg(String msg, Style style) {
        if (mc.player == null) return;

        try {
            String message = getPrefix() + ' ' + ChatFormatting.GRAY + msg;
            mc.player.sendSystemMessage(Component.literal(message).setStyle(style));
        } catch (Exception ignored) {}
    }

    public static void sendModuleMsg(String msg, String module) {
        if (mc.player == null) return;

        try {
            StringBuilder sb = new StringBuilder();
            mc.player.sendSystemMessage(Component.literal(sb.append(getModulePrefix(module)).append(' ').append(ChatFormatting.GRAY).append(msg).toString()));
        } catch (Exception ignored) {}
    }

    public static void sendModuleMsg(String msg, Style style, String module) {
        if (mc.player == null) return;

        try {
            String message = getModulePrefix(module) + ' ' + ChatFormatting.GRAY + msg;
            mc.player.sendSystemMessage(Component.literal(message).setStyle(style));
        } catch (Exception ignored) {}
    }

    public static void updateMsg(String msg, int hashcode) {
        if (mc.player == null) return;

        try {
            StringBuilder sb = new StringBuilder();
            ((IChatHud) mc.gui.getChat()).meteor$add(
                Component.literal(sb.append(getPrefix()).append(' ').append(ChatFormatting.GRAY).append(msg).toString()), hashcode
            );
        } catch (Exception ignored) {}
    }

    public static void updateModuleMsg(String msg, String module, int hashcode) {
        if (mc.player == null) return;

        try {
            StringBuilder sb = new StringBuilder();
            ((IChatHud) mc.gui.getChat()).meteor$add(
                Component.literal(sb.append(getModulePrefix(module)).append(' ').append(ChatFormatting.GRAY).append(msg).toString()), hashcode
            );
        } catch (Exception ignored) {}
    }
}
