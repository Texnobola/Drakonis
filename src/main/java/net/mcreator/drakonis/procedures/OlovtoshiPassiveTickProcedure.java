package net.mcreator.drakonis.procedures;

import top.theillusivec4.curios.api.CuriosApi;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.drakonis.network.DrakonisModVariables;
import net.mcreator.drakonis.init.DrakonisModItems;

public class OlovtoshiPassiveTickProcedure {
    public static void execute(Entity entity) {
        if (entity == null) return;

        // 1. Check if the "Switch" variable is ON
        if (entity.getData(DrakonisModVariables.PLAYER_VARIABLES).player_torch_active) {
            boolean hasItem = false;

            // 2. Check Hands (Main Hand OR Off Hand)
            if (entity instanceof LivingEntity _livEnt) {
                ItemStack main = _livEnt.getMainHandItem();
                ItemStack off = _livEnt.getOffhandItem();
                if (main.getItem() == DrakonisModItems.OLOVTOSHI.get() || off.getItem() == DrakonisModItems.OLOVTOSHI.get()) {
                    hasItem = true;
                }
            }

            // 3. Check Curios Slots (If not found in hands yet)
            // This works for ANY Curios slot (Back, Charm, Belt, etc.)
            if (!hasItem && entity instanceof LivingEntity _livEnt) {
                var curiosInv = CuriosApi.getCuriosInventory(_livEnt);
                if (curiosInv.isPresent()) {
                    // Returns true if Olovtoshi is found in any slot
                    hasItem = curiosInv.get().findFirstCurio(DrakonisModItems.OLOVTOSHI.get()).isPresent();
                }
            }

            // 4. Apply Effect if Item Found
            if (hasItem && entity instanceof LivingEntity _entity) {
                // Apply Glowing for 20 ticks (1 second) so it doesn't flicker
                // Ambient: false, Visible: false (Hides the potion swirls)
                _entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20, 0, false, false));
            }
        }
    }
}