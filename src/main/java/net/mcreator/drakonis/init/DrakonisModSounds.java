package net.mcreator.drakonis.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.mcreator.drakonis.DrakonisMod;

public class DrakonisModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, DrakonisMod.MODID);
    
    public static final DeferredHolder<SoundEvent, SoundEvent> HAKARI_DANCE = REGISTRY.register("hakari_dance",
        () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("drakonis", "hakari_dance")));
}
