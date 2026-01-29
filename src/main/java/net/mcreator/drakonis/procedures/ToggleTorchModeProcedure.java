package net.mcreator.drakonis.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

public class ToggleTorchModeProcedure {
    public static void execute(Entity entity) {
        if (entity == null) return;

        // Get player location
        BlockPos pos = entity.blockPosition();
        
        // Place a Torch block
        entity.level().setBlock(pos, Blocks.TORCH.defaultBlockState(), 3);
    }
}