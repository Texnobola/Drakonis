package net.mcreator.drakonis.procedures;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

import net.mcreator.drakonis.network.DrakonisModVariables;
import net.mcreator.drakonis.init.DrakonisModItems;

import java.util.Optional;

@EventBusSubscriber
public class FireStoneCookingProcedure {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        
        if (!player.isShiftKeyDown()) return;
        
        // Check Fire Stone
        boolean hasFireStone = false;
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i).is(DrakonisModItems.OLOVTOSHI.get())) {
                hasFireStone = true;
                break;
            }
        }
        if (!hasFireStone && !player.getOffhandItem().is(DrakonisModItems.OLOVTOSHI.get())) {
            var curiosInv = net.mcreator.drakonis.DrakonisMod.CuriosApiHelper.getCuriosInventory(player);
            if (curiosInv != null) {
                for (int i = 0; i < curiosInv.getSlots(); i++) {
                    if (curiosInv.getStackInSlot(i).is(DrakonisModItems.OLOVTOSHI.get())) {
                        hasFireStone = true;
                        break;
                    }
                }
            }
            if (!hasFireStone) return;
        }
        
        var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
        long worldTime = player.level().getGameTime();
        
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        
        if (!mainHand.isEmpty()) {
            processCooking(player, mainHand, data, worldTime, true);
        }
        if (!offHand.isEmpty()) {
            processCooking(player, offHand, data, worldTime, false);
        }
    }
    
    private static void processCooking(Player player, ItemStack stack, DrakonisModVariables.PlayerVariables data, long worldTime, boolean isMainHand) {
        Level level = player.level();
        Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager()
            .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level);
        
        if (recipe.isEmpty()) return;
        
        String key = isMainHand ? "mainHandCookStart" : "offHandCookStart";
        long startTime = isMainHand ? data.fireStoneMainHandCookStart : data.fireStoneOffHandCookStart;
        
        if (startTime == 0) {
            if (isMainHand) {
                data.fireStoneMainHandCookStart = worldTime;
            } else {
                data.fireStoneOffHandCookStart = worldTime;
            }
            data.syncPlayerVariables(player);
            return;
        }
        
        long elapsed = worldTime - startTime;
        
        if (elapsed >= 400) { // 20 seconds = 400 ticks
            ItemStack result = recipe.get().value().getResultItem(level.registryAccess()).copy();
            stack.shrink(1);
            
            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }
            
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.FLAME, 
                    player.getX(), player.getY() + 1, player.getZ(), 
                    10, 0.3, 0.3, 0.3, 0.05);
            }
            
            if (isMainHand) {
                data.fireStoneMainHandCookStart = 0;
            } else {
                data.fireStoneOffHandCookStart = 0;
            }
            data.syncPlayerVariables(player);
        } else if (elapsed % 20 == 0) {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SMOKE, 
                    player.getX(), player.getY() + 1, player.getZ(), 
                    2, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }
}
