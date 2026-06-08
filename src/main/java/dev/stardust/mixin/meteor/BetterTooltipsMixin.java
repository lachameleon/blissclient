package dev.stardust.mixin.meteor;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Category;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import meteordevelopment.meteorclient.events.game.ItemStackTooltipEvent;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;

/**
 * @author Tas [0xTas] <root@0xTas.dev>
 **/
@Mixin(value = BetterTooltips.class, remap = false)
public class BetterTooltipsMixin extends Module {
    @Shadow
    @Final
    private SettingGroup sgOther;

    @Shadow
    @Final
    private Setting<Boolean> openContents;

    public BetterTooltipsMixin(Category category, String name, String description, String... aliases) {
        super(category, name, description, aliases);
    }

    @Unique
    private @Nullable Setting<Boolean> rawDamageTag = null;
    @Unique
    private @Nullable Setting<Boolean> trueDurability = null;
    @Unique
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private @Nullable Setting<Boolean> peekGhostItems = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addTrueDurabilitySetting(CallbackInfo ci) {
        trueDurability = sgOther.add(new BoolSetting.Builder()
            .name("true-durability")
            .description("Show the raw damage value of an item.")
            .defaultValue(false)
            .build()
        );
        rawDamageTag = sgOther.add(
            new BoolSetting.Builder()
                .name("raw-damage-tag")
                .description("Show the raw Damage tag of an item.")
                .defaultValue(false)
                .build()
        );
        // See PeekScreenMixin.java
        peekGhostItems = sgOther.add(
            new BoolSetting.Builder()
                .name("peek-ghost-items")
                .description("Left-click on an item in the Peek Screen to add a client-side-only variant to your hotbar.")
                .defaultValue(false)
                .onChanged(it -> {
                    if (it) this.openContents.set(true);
                })
                .build()
        );
    }

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    private void appendDurabilityTooltip(ItemStackTooltipEvent event, CallbackInfo ci) {
        if (!event.itemStack().isDamageableItem()) return;

        int maxDamage = event.itemStack().getMaxDamage();
        int damage = event.itemStack().getOrDefault(DataComponents.DAMAGE, event.itemStack().getDamageValue());

        if (rawDamageTag != null && rawDamageTag.get()) {
            event.appendEnd(Component.literal("§7Damage§3: §a§o" + damage + " §8[§7Max§3: §a§o" + maxDamage + "§8]"));
        }
        if (trueDurability != null && trueDurability.get()) {
            int durability = maxDamage - damage;
            event.appendEnd(Component.literal("§7Durability§3: §a§o" + durability + " §8[§7Max§3: §a§o" + maxDamage + "§8]"));
        }
    }
}
