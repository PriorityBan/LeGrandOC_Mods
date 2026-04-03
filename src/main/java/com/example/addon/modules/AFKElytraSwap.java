package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;

public class AFKElytraSwap extends Module {

    private int swapCooldown = 0;

    public AFKElytraSwap() {
        super(AddonTemplate.CATEGORY, "afk-Elytra-Swap", "Auto swaps elytra at low durability to next best durability Elytra.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        if (swapCooldown > 0) {
            swapCooldown--;
            return;
        }

        ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);

        if (chest.getItem() != Items.ELYTRA) return;

        int damage = chest.getDamage();
        int max = chest.getMaxDamage();

        float durability = 1.0f - ((float) damage / max);

        if (durability <= 0.05f) {
            swapElytra();
        }
    }

    private void swapElytra() {
        int bestSlot = -1;
        float bestDurability = 0f;

        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);

            if (stack.getItem() == Items.ELYTRA) {
                int damage = stack.getDamage();
                int max = stack.getMaxDamage();

                float durability = 1.0f - ((float) damage / max);

                // skip very low durability elytra (prevents swap loops)
                if (durability <= 0.05f) continue;

                if (durability > bestDurability) {
                    bestDurability = durability;
                    bestSlot = i;
                }
            }
        }

        if (bestSlot != -1) {
            InvUtils.move().from(bestSlot).toArmor(2);
            swapCooldown = 40;
        }
    }
}
