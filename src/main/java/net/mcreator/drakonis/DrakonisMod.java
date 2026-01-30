package net.mcreator.drakonis;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Tuple;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.drakonis.network.DrakonisModVariables;
import net.mcreator.drakonis.init.DrakonisModTabs;
import net.mcreator.drakonis.init.DrakonisModItems;
import net.mcreator.drakonis.init.DrakonisModCuriosCompat;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;

@Mod("drakonis")
public class DrakonisMod {
	public static final Logger LOGGER = LogManager.getLogger(DrakonisMod.class);
	public static final String MODID = "drakonis";

	public DrakonisMod(IEventBus modEventBus) {
		// Start of user code block mod constructor
		// End of user code block mod constructor
		NeoForge.EVENT_BUS.register(this);
		modEventBus.addListener(this::registerNetworking);
		
		// Register network messages
		addNetworkMessage(net.mcreator.drakonis.network.FirstpassiveMessage.TYPE, 
			net.mcreator.drakonis.network.FirstpassiveMessage.STREAM_CODEC, 
			net.mcreator.drakonis.network.FirstpassiveMessage::handleData);
		addNetworkMessage(net.mcreator.drakonis.network.EmberDominionToggleMessage.TYPE,
			net.mcreator.drakonis.network.EmberDominionToggleMessage.STREAM_CODEC,
			net.mcreator.drakonis.network.EmberDominionToggleMessage::handleData);
		addNetworkMessage(net.mcreator.drakonis.network.FireBlastChargeMessage.TYPE,
			net.mcreator.drakonis.network.FireBlastChargeMessage.STREAM_CODEC,
			net.mcreator.drakonis.network.FireBlastChargeMessage::handleData);
		addNetworkMessage(net.mcreator.drakonis.network.DragonConcentrationToggleMessage.TYPE,
			net.mcreator.drakonis.network.DragonConcentrationToggleMessage.STREAM_CODEC,
			net.mcreator.drakonis.network.DragonConcentrationToggleMessage::handleData);
		addNetworkMessage(net.mcreator.drakonis.network.LanguageSelectMessage.TYPE,
			net.mcreator.drakonis.network.LanguageSelectMessage.STREAM_CODEC,
			net.mcreator.drakonis.network.LanguageSelectMessage::handleData);
		addNetworkMessage(net.mcreator.drakonis.network.PlayPlayerAnimationMessage.TYPE,
			net.mcreator.drakonis.network.PlayPlayerAnimationMessage.STREAM_CODEC,
			net.mcreator.drakonis.network.PlayPlayerAnimationMessage::handleData);
		if (ModList.get().isLoaded("curios")) {
			modEventBus.addListener(DrakonisModCuriosCompat::registerCapabilities);
		}

		DrakonisModItems.REGISTRY.register(modEventBus);

		DrakonisModTabs.REGISTRY.register(modEventBus);
		DrakonisModVariables.ATTACHMENT_TYPES.register(modEventBus);

		// Start of user code block mod init
		// End of user code block mod init
	}

	// Start of user code block mod methods
	// End of user code block mod methods
	private static boolean networkingRegistered = false;
	private static final Map<CustomPacketPayload.Type<?>, NetworkMessage<?>> MESSAGES = new HashMap<>();

	private record NetworkMessage<T extends CustomPacketPayload>(StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
	}

	public static <T extends CustomPacketPayload> void addNetworkMessage(CustomPacketPayload.Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
		if (networkingRegistered)
			throw new IllegalStateException("Cannot register new network messages after networking has been registered");
		MESSAGES.put(id, new NetworkMessage<>(reader, handler));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void registerNetworking(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(MODID);
		MESSAGES.forEach((id, networkMessage) -> registrar.playBidirectional(id, ((NetworkMessage) networkMessage).reader(), ((NetworkMessage) networkMessage).handler()));
		networkingRegistered = true;
	}

	private static final Collection<Tuple<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new Tuple<>(action, tick));
	}

	@SubscribeEvent
	public void tick(ServerTickEvent.Post event) {
		List<Tuple<Runnable, Integer>> actions = new ArrayList<>();
		workQueue.forEach(work -> {
			work.setB(work.getB() - 1);
			if (work.getB() == 0)
				actions.add(work);
		});
		actions.forEach(e -> e.getA().run());
		workQueue.removeAll(actions);
	}

	@SubscribeEvent
	public void onPlayerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
		if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
			net.mcreator.drakonis.procedures.EmberDominionTickProcedure.execute(player);
			net.mcreator.drakonis.procedures.DragonConcentrationTickProcedure.execute(player);
			net.mcreator.drakonis.procedures.FireBlastChargingTickProcedure.execute(player);
		}
		net.mcreator.drakonis.procedures.FireStoneCookingProcedure.onPlayerTick(event);
	}

	public static class CuriosApiHelper {
		private static final EntityCapability<IItemHandler, Void> CURIOS_INVENTORY = EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath("curios", "item_handler"), IItemHandler.class);

		public static IItemHandler getCuriosInventory(Player player) {
			if (ModList.get().isLoaded("curios")) {
				return player.getCapability(CURIOS_INVENTORY);
			}
			return null;
		}

		public static boolean isCurioItem(ItemStack itemstack) {
			return BuiltInRegistries.ITEM.getTagNames().filter(tagKey -> tagKey.location().getNamespace().equals("curios")).anyMatch(itemstack::is);
		}
	}
}