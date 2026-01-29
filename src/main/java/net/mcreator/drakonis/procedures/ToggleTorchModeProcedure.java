package net.mcreator.drakonis.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

public class ToggleTorchModeProcedure {
    public static void execute(Entity entity) {
        if (entity == null) return;

        // Get the exact block position where the player is standing
        BlockPos pos = entity.blockPosition();
        
        // Place the torch at the player's feet
        // The '3' tells the game to update neighbors and notify the client
        entity.level().setBlock(pos, Blocks.TORCH.defaultBlockState(), 3);
    }
}