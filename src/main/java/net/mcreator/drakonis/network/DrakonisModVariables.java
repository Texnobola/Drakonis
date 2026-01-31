package net.mcreator.drakonis.network;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;

import net.mcreator.drakonis.DrakonisMod;

import java.util.function.Supplier;

@EventBusSubscriber
public class DrakonisModVariables {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, DrakonisMod.MODID);
	public static final Supplier<AttachmentType<PlayerVariables>> PLAYER_VARIABLES = ATTACHMENT_TYPES.register("player_variables", () -> AttachmentType.serializable(() -> new PlayerVariables()).build());

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		DrakonisMod.addNetworkMessage(PlayerVariablesSyncMessage.TYPE, PlayerVariablesSyncMessage.STREAM_CODEC, PlayerVariablesSyncMessage::handleData);
	}

	@SubscribeEvent
	public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player)
			PacketDistributor.sendToPlayer(player, new PlayerVariablesSyncMessage(player.getData(PLAYER_VARIABLES)));
	}

	@SubscribeEvent
	public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer player)
			PacketDistributor.sendToPlayer(player, new PlayerVariablesSyncMessage(player.getData(PLAYER_VARIABLES)));
	}

	@SubscribeEvent
	public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player)
			PacketDistributor.sendToPlayer(player, new PlayerVariablesSyncMessage(player.getData(PLAYER_VARIABLES)));
	}

	@SubscribeEvent
	public static void onPlayerTickUpdateSyncPlayerVariables(PlayerTickEvent.Post event) {
		if (event.getEntity() instanceof ServerPlayer player && player.getData(PLAYER_VARIABLES)._syncDirty) {
			PacketDistributor.sendToPlayer(player, new PlayerVariablesSyncMessage(player.getData(PLAYER_VARIABLES)));
			player.getData(PLAYER_VARIABLES)._syncDirty = false;
		}
	}

	@SubscribeEvent
	public static void clonePlayer(PlayerEvent.Clone event) {
		PlayerVariables original = event.getOriginal().getData(PLAYER_VARIABLES);
		PlayerVariables clone = new PlayerVariables();
		if (!event.isWasDeath()) {
			clone.player_torch_active = original.player_torch_active;
			clone.emberDominionActive = original.emberDominionActive;
			clone.emberDominionLastToggle = original.emberDominionLastToggle;
			clone.emberDominionTickCounter = original.emberDominionTickCounter;
			clone.isHoldingConcentration = original.isHoldingConcentration;
		}
		event.getEntity().setData(PLAYER_VARIABLES, clone);
	}

	public static class PlayerVariables implements INBTSerializable<CompoundTag> {
		boolean _syncDirty = false;
		public boolean player_torch_active = false;
		public boolean emberDominionActive = false;
		public boolean isHoldingEmberDominion = false;
		public long emberDominionActivationTime = 0;
		public long emberDominionStartTime = 0;
		public long emberDominionLastToggle = 0;
		public int emberDominionTickCounter = 0;
		public boolean fireBlastCharging = false;
		public long fireBlastChargeTime = 0;
		public long dragonStrikeICDUntil = 0;
		public boolean isHoldingConcentration = false;
		public boolean concentrationAnimationPlayed = false;
		public boolean dragonConcentrationActivated = false;
		public long dragonConcentrationActivationTime = 0;
		public long dragonConcentrationStartTime = 0;
		public long dragonConcentrationUntil = 0;
		public long dragonConcentrationToggleCooldown = 0;
		public long dragonConcentrationOnProcCooldown = 0;
		public int dragonConsecutiveCount = 0;
		public long dragonConsecutiveWindowUntil = 0;
		public long dragonImmortalityUntil = 0;
		public double dragonStrikeProcChance = 0.05;
		public boolean hasSelectedLanguage = false;
		public long fireStoneMainHandCookStart = 0;
		public long fireStoneOffHandCookStart = 0;
		public boolean hasReceivedInstructionBook = false;
		public boolean isSnowGolemSummoning = false;
		public long snowGolemSummonStartTime = 0;
		public long snowGolemSummonCooldown = 0;

		@Override
		public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
			CompoundTag nbt = new CompoundTag();
			nbt.putBoolean("player_torch_active", player_torch_active);
			nbt.putBoolean("emberDominionActive", emberDominionActive);
			nbt.putBoolean("isHoldingEmberDominion", isHoldingEmberDominion);
			nbt.putLong("emberDominionActivationTime", emberDominionActivationTime);
			nbt.putLong("emberDominionStartTime", emberDominionStartTime);
			nbt.putLong("emberDominionLastToggle", emberDominionLastToggle);
			nbt.putInt("emberDominionTickCounter", emberDominionTickCounter);
			nbt.putBoolean("fireBlastCharging", fireBlastCharging);
			nbt.putLong("fireBlastChargeTime", fireBlastChargeTime);
			nbt.putLong("dragonStrikeICDUntil", dragonStrikeICDUntil);
			nbt.putBoolean("isHoldingConcentration", isHoldingConcentration);
			nbt.putBoolean("concentrationAnimationPlayed", concentrationAnimationPlayed);
			nbt.putBoolean("dragonConcentrationActivated", dragonConcentrationActivated);
			nbt.putLong("dragonConcentrationActivationTime", dragonConcentrationActivationTime);
			nbt.putLong("dragonConcentrationStartTime", dragonConcentrationStartTime);
			nbt.putLong("dragonConcentrationUntil", dragonConcentrationUntil);
			nbt.putLong("dragonConcentrationToggleCooldown", dragonConcentrationToggleCooldown);
			nbt.putLong("dragonConcentrationOnProcCooldown", dragonConcentrationOnProcCooldown);
			nbt.putInt("dragonConsecutiveCount", dragonConsecutiveCount);
			nbt.putLong("dragonConsecutiveWindowUntil", dragonConsecutiveWindowUntil);
			nbt.putLong("dragonImmortalityUntil", dragonImmortalityUntil);
			nbt.putDouble("dragonStrikeProcChance", dragonStrikeProcChance);
			nbt.putBoolean("hasSelectedLanguage", hasSelectedLanguage);
			nbt.putLong("fireStoneMainHandCookStart", fireStoneMainHandCookStart);
			nbt.putLong("fireStoneOffHandCookStart", fireStoneOffHandCookStart);
			nbt.putBoolean("hasReceivedInstructionBook", hasReceivedInstructionBook);
			nbt.putBoolean("isSnowGolemSummoning", isSnowGolemSummoning);
			nbt.putLong("snowGolemSummonStartTime", snowGolemSummonStartTime);
			nbt.putLong("snowGolemSummonCooldown", snowGolemSummonCooldown);
			return nbt;
		}

		@Override
		public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
			player_torch_active = nbt.getBoolean("player_torch_active");
			emberDominionActive = nbt.getBoolean("emberDominionActive");
			isHoldingEmberDominion = nbt.getBoolean("isHoldingEmberDominion");
			emberDominionActivationTime = nbt.getLong("emberDominionActivationTime");
			emberDominionStartTime = nbt.getLong("emberDominionStartTime");
			emberDominionLastToggle = nbt.getLong("emberDominionLastToggle");
			emberDominionTickCounter = nbt.getInt("emberDominionTickCounter");
			fireBlastCharging = nbt.getBoolean("fireBlastCharging");
			fireBlastChargeTime = nbt.getLong("fireBlastChargeTime");
			dragonStrikeICDUntil = nbt.getLong("dragonStrikeICDUntil");
			isHoldingConcentration = nbt.getBoolean("isHoldingConcentration");
			concentrationAnimationPlayed = nbt.getBoolean("concentrationAnimationPlayed");
			dragonConcentrationActivated = nbt.getBoolean("dragonConcentrationActivated");
			dragonConcentrationActivationTime = nbt.getLong("dragonConcentrationActivationTime");
			dragonConcentrationStartTime = nbt.getLong("dragonConcentrationStartTime");
			dragonConcentrationUntil = nbt.getLong("dragonConcentrationUntil");
			dragonConcentrationToggleCooldown = nbt.getLong("dragonConcentrationToggleCooldown");
			dragonConcentrationOnProcCooldown = nbt.getLong("dragonConcentrationOnProcCooldown");
			dragonConsecutiveCount = nbt.getInt("dragonConsecutiveCount");
			dragonConsecutiveWindowUntil = nbt.getLong("dragonConsecutiveWindowUntil");
			dragonImmortalityUntil = nbt.getLong("dragonImmortalityUntil");
			dragonStrikeProcChance = nbt.getDouble("dragonStrikeProcChance");
			if (dragonStrikeProcChance == 0) dragonStrikeProcChance = 0.05;
			hasSelectedLanguage = nbt.getBoolean("hasSelectedLanguage");
			fireStoneMainHandCookStart = nbt.getLong("fireStoneMainHandCookStart");
			fireStoneOffHandCookStart = nbt.getLong("fireStoneOffHandCookStart");
			hasReceivedInstructionBook = nbt.getBoolean("hasReceivedInstructionBook");
			isSnowGolemSummoning = nbt.getBoolean("isSnowGolemSummoning");
			snowGolemSummonStartTime = nbt.getLong("snowGolemSummonStartTime");
			snowGolemSummonCooldown = nbt.getLong("snowGolemSummonCooldown");
		}

		public void syncPlayerVariables(net.minecraft.world.entity.player.Player player) {
			if (player instanceof ServerPlayer serverPlayer)
				PacketDistributor.sendToPlayer(serverPlayer, new PlayerVariablesSyncMessage(this));
		}

		public void markSyncDirty() {
			_syncDirty = true;
		}
	}

	public record PlayerVariablesSyncMessage(PlayerVariables data) implements CustomPacketPayload {
		public static final Type<PlayerVariablesSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DrakonisMod.MODID, "player_variables_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVariablesSyncMessage> STREAM_CODEC = StreamCodec
				.of((RegistryFriendlyByteBuf buffer, PlayerVariablesSyncMessage message) -> buffer.writeNbt(message.data().serializeNBT(buffer.registryAccess())), (RegistryFriendlyByteBuf buffer) -> {
					PlayerVariablesSyncMessage message = new PlayerVariablesSyncMessage(new PlayerVariables());
					message.data.deserializeNBT(buffer.registryAccess(), buffer.readNbt());
					return message;
				});

		@Override
		public Type<PlayerVariablesSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final PlayerVariablesSyncMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.enqueueWork(() -> context.player().getData(PLAYER_VARIABLES).deserializeNBT(context.player().registryAccess(), message.data.serializeNBT(context.player().registryAccess()))).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}