package net.mcreator.drakonis.procedures;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import net.mcreator.drakonis.network.DrakonisModVariables;
import net.mcreator.drakonis.init.DrakonisModItems;
import net.mcreator.drakonis.DrakonisMod;

public class EvolutionSummonProcedure {
    private static final long EVOLUTION_COOLDOWN = 600; // 30 seconds
    
    public static void execute(Entity entity) {
        if (entity == null || !(entity instanceof Player player)) return;
        
        Level level = player.level();
        if (level.isClientSide) return;
        
        long worldTime = level.getGameTime();
        var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
        
        // Check cooldown
        if (worldTime < data.evolutionCooldown) {
            long remaining = (data.evolutionCooldown - worldTime) / 20;
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§bEvolution cooldown: " + remaining + "s"));
            return;
        }
        
        // Check for Ice Stone in Curios
        boolean hasIceStone = hasItemInCurios(player, DrakonisModItems.MUZTOSHI.get().getDefaultInstance());
        if (!hasIceStone) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cRequires Ice Stone to evolve!"));
            return;
        }
        
        // Check for ice gloves
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        
        boolean hasIceGloveRight = mainHand.is(DrakonisModItems.ICY_GLOVE_RIGHT.get());
        boolean hasIceGloveLeft = offHand.is(DrakonisModItems.ICY_GLOVE_LEFT.get());
        
        if (!hasIceGloveRight || !hasIceGloveLeft) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cRequires both Ice Gloves to evolve!"));
            return;
        }
        
        if (!data.isEvolvingToArmor) {
            data.isEvolvingToArmor = true;
            data.evolutionStartTime = worldTime;
            data.evolutionCooldown = worldTime + EVOLUTION_COOLDOWN;
            data.markSyncDirty();
            
            DrakonisMod.LOGGER.info("[EVOLUTION] Sending animation packet: drakonis:animation.player.evolution_aura_smooth");
            AnimationHelper.playAnimation(player, "drakonis:animation.player.evolution_aura_smooth", true, false);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§b§l⚡ ULTIMATE EVOLUTION ⚡"));
        }
    }
    
    private static boolean hasItemInCurios(Player player, ItemStack item) {
        var curiosInventory = net.mcreator.drakonis.DrakonisMod.CuriosApiHelper.getCuriosInventory(player);
        if (curiosInventory == null)
            return false;
        
        for (int i = 0; i < curiosInventory.getSlots(); i++) {
            ItemStack stack = curiosInventory.getStackInSlot(i);
            if (ItemStack.isSameItem(stack, item))
                return true;
        }
        return false;
    }
}
