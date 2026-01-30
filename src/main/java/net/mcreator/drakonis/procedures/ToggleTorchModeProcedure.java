package net.mcreator.drakonis.procedures;

import top.theillusivec4.curios.api.CuriosApi;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import net.mcreator.drakonis.init.DrakonisModItems;

public class ToggleTorchModeProcedure {
    public static void execute(Entity entity) {
        if (entity == null || !(entity instanceof LivingEntity)) return;

        LivingEntity livingEntity = (LivingEntity) entity;
        
        // Check if Olov Toshi is equipped in any curios slot
        var curiosInv = CuriosApi.getCuriosInventory(livingEntity);
        if (curiosInv.isPresent()) {
            boolean hasOlovToshi = curiosInv.get().findFirstCurio(DrakonisModItems.OLOVTOSHI.get()).isPresent();
            
            if (hasOlovToshi) {
                BlockPos pos = livingEntity.blockPosition();
                if (livingEntity.level().isEmptyBlock(pos)) {
                    livingEntity.level().setBlock(pos, Blocks.TORCH.defaultBlockState(), 3);
                }
            }
        }
    }
}