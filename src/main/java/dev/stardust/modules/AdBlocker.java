package dev.stardust.modules;

import java.util.List;
import java.util.ArrayList;
import meteordevelopment.meteorclient.systems.modules.Categories;
import dev.stardust.util.MsgUtil;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.StringListSetting;

/**
 * @author Tas [0xTas] <root@0xTas.dev>
 **/
public class AdBlocker extends Module {
    public AdBlocker() { super(Categories.Misc, "AdBlocker", "Blocks advertisers in chat."); }

    public enum IgnoreStyle {
        None, Ignore, HardIgnore
    }

    private final Setting<IgnoreStyle> ignoreStyle = settings.getDefaultGroup().add(
        new EnumSetting.Builder<IgnoreStyle>()
            .name("ignore-advertisers")
            .description("Whether to ignore accounts which trigger the blocked patterns filter.")
            .defaultValue(IgnoreStyle.Ignore)
            .build()
    );
    private final Setting<List<String>> patterns = settings.getDefaultGroup().add(
        new StringListSetting.Builder()
            .name("blocked-patterns")
            .description("Chat messages matching any of these patterns will be blocked, and ignore preferences applied to the culprit.")
            .defaultValue(
                List.of(
                    "thishttp", "discord.com", "discord.gg", "gg/", "com/", "/invite/", "% off",
                    ".store", "cheapest price", "cheapest kit", "cheap price", "cheap kit", "use code", "at checkout",
                    "join now", "rusherhack.org", "nox2b", ".shop"
                )
            )
            .build()
    );

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPacketReceive(PacketEvent.Receive event) {
        if (mc.getConnection() == null) return;
        if (!(event.packet instanceof ClientboundSystemChatPacket packet)) return;

        if (packet.content() == null) return;
        String content = packet.content().getString();
        for (String pattern : patterns.get()) {
            if (pattern.isBlank()) continue;
            if (content.toLowerCase().contains(pattern.toLowerCase())) {
                event.cancel(); // fuck yo packets
                if (!ignoreStyle.get().equals(IgnoreStyle.None)) {
                    String name = getNameFromMessage(content);

                    String cmd;
                    if (ignoreStyle.get().equals(IgnoreStyle.Ignore)) {
                        cmd = "ignore";
                    } else {
                        cmd = "ignorehard";
                    }

                    if (name.isBlank()) {
                        cmd = "ignoredeathmsgs";
                        List<String> responsible = new ArrayList<>();
                        extractNamesFromDeathMessage(packet.content(), responsible);
                        for (String culprit : responsible) {
                            if (chatFeedback) {
                                MsgUtil.sendModuleMsg(
                                    "Ignoring death-message advertiser \"§c" + culprit + "§7\"§a..!",
                                    this.name
                                );
                            }
                            mc.getConnection().getConnection().send(new ServerboundChatCommandPacket(cmd + " " + culprit));
                        }
                    } else {
                        mc.getConnection().getConnection().send(new ServerboundChatCommandPacket(cmd + " " + name));
                    }
                }
                break;
            }
        }
    }

    private String getNameFromMessage(String message) {
        String name = "";
        String[] parts = message.split(" ");
        if (parts.length >= 3 && parts[1].equals("whispers:")) name = parts[0];
        else if (parts[0].startsWith("<") && parts[0].endsWith(">")) name = parts[0].substring(1, parts[0].length() - 1);

        return name;
    }

    private void extractNamesFromDeathMessage(Component msg, List<String> names) {
        if (msg.getStyle().getHoverEvent() != null) {
            HoverEvent event = msg.getStyle().getHoverEvent();
            if (event instanceof HoverEvent.ShowText showText) {
                Component value = showText.value();
                if (value != null && value.getString().startsWith("Message ")) {
                    names.add(value.getString().substring(8).trim());
                }
            }
        }

        for (Component sibling : msg.getSiblings()) {
            extractNamesFromDeathMessage(sibling, names);
        }
    }
}
