package net.mcreator.drakonis.procedures;

import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;

import net.mcreator.drakonis.network.DrakonisModVariables;
import net.mcreator.drakonis.DrakonisMod;

@EventBusSubscriber(value = Dist.DEDICATED_SERVER)
public class FirstJoinInstructionBookProcedure {
    
    @SubscribeEvent
    public static void onPlayerJoinServer(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            var data = player.getData(DrakonisModVariables.PLAYER_VARIABLES);
            
            // Check if player has received instruction book
            if (!data.hasReceivedInstructionBook) {
                DrakonisMod.LOGGER.info("[DRAKONIS] First join detected for player: " + player.getName().getString());
                
                // Create the instruction book with content
                ItemStack instructionBook = createInstructionBook();
                
                // Try to add to inventory, or drop if full
                if (!player.getInventory().add(instructionBook)) {
                    player.drop(instructionBook, false);
                    DrakonisMod.LOGGER.info("[DRAKONIS] Inventory full, dropped instruction book for: " + player.getName().getString());
                } else {
                    DrakonisMod.LOGGER.info("[DRAKONIS] Given instruction book to player: " + player.getName().getString());
                }
                
                // Mark that the player has received the book
                data.hasReceivedInstructionBook = true;
                data.syncPlayerVariables(player);
                
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                        "§6§l[ДРАКОНИC] §7Вы получили книгу инструкций! / Сиз билим китобини ўлдингиз!"
                    ), 
                    false
                );
            }
        }
    }
    
    private static ItemStack createInstructionBook() {
        ItemStack book = new ItemStack(Items.WRITABLE_BOOK);
        ListTag pages = new ListTag();
        
        // Page 1: Title (Russian)
        pages.add(net.minecraft.nbt.StringTag.valueOf(
            "§6§l========== ДРАКОНИC ==========\n\n" +
            "§0§lКнига Инструкций\n\n" +
            "§7Добро пожаловать в мир Драконис!\n\n" +
            "§0Эта книга содержит важную информацию о магических силах."
        ));
        
        // Page 2: Welcome (Uzbek)
        pages.add(net.minecraft.nbt.StringTag.valueOf(
            "§6§l========== ДРАКОНИС ==========\n\n" +
            "§0§lБилим Китоби\n\n" +
            "§7Дракониc олимига хуш келибсиз!\n\n" +
            "§0Бу китоб сизга волшебный кучалар ва қобиятлар тўғрисида маълумот беради."
        ));
        
        // Page 3: Fire Stone (Russian)
        pages.add(net.minecraft.nbt.StringTag.valueOf(
            "§c§l🔥 Оловтоши (Огненный Камень) 🔥\n\n" +
            "§7Это основной артефакт для огненной магии.\n\n" +
            "§0Свойства:\n" +
            "§6• §0Держите в руке\n" +
            "§6• §0Активирует способности\n" +
            "§6• §0Защита от огня"
        ));
        
        // Page 4: Fire Stone (Uzbek)
        pages.add(net.minecraft.nbt.StringTag.valueOf(
            "§c§l🔥 Оловтошининг Қобиятлари 🔥\n\n" +
            "§0§lЭмбер Доминион (F):\n" +
            "§7• Қувватли отень портлашуви\n" +
            "§7• Атроф дюшманларини зарарланди\n\n" +
            "§0§lОт Портлашуси (О):\n" +
            "§7• Йўнальмаси ўйнига"
        ));
        
        // Page 5: All Stones
        pages.add(net.minecraft.nbt.StringTag.valueOf(
            "§5§l⚔️ БАРЧА СОНГЛАР ⚔️\n\n" +
            "§0§lҚара (Dark): Қаронгулик\n" +
            "§0§lОровчи (Light): Ёруитиш\n" +
            "§0§lОловтошi (Fire): Ўтень\n" +
            "§0§lСувтошi (Water): Сув\n" +
            "§0§lЙертошi (Earth): Ер"
        ));
        
        // Page 6: Controls
        pages.add(net.minecraft.nbt.StringTag.valueOf(
            "§9§l⌨️ БОШҚАРИШ ⌨️\n\n" +
            "§0§lF - Ember Dominion\n" +
            "§0§lO - Concentration\n" +
            "§0§lP - Dragon Strike\n\n" +
            "§7Параметрлар → Назорат"
        ));
        
        // Page 7: Tips (Uzbek)
        pages.add(net.minecraft.nbt.StringTag.valueOf(
            "§e§l💡 МАСЛАХАТЛАР 💡\n\n" +
            "§0§l1. Адама эхтиётга\n" +
            "§0§l2. Озиқ қилтирани\n" +
            "§0§l3. Ўзга ўйим\n" +
            "§0§l4. Кўйка жабрасидан"
        ));
        
        // Page 8: Final (Uzbek)
        pages.add(net.minecraft.nbt.StringTag.valueOf(
            "§b§l🌟 ОХИРГИ 🌟\n\n" +
            "§0Сўч шахрида:\n" +
            "§7• Сўзучи кўпроқ қўва\n" +
            "§7• Миқдори ёхшилик\n" +
            "§7• Қўл кўфи устуни\n" +
            "§7Давомлаштиринг!"
        ));
        
        // Use reflection or internal method to set tag (NeoForge 1.21.1 way)
        CompoundTag tag = new CompoundTag();
        tag.put("pages", pages);
        tag.putInt("resolved", 1);
        
        // For NeoForge 1.21.1, we need to use the proper API
        // Try using the ItemStack constructor parameter or reflection
        try {
            java.lang.reflect.Method setTagMethod = ItemStack.class.getDeclaredMethod("m_150863_", CompoundTag.class);
            setTagMethod.setAccessible(true);
            setTagMethod.invoke(book, tag);
        } catch (Exception e) {
            DrakonisMod.LOGGER.error("Failed to set book tag: " + e.getMessage());
        }
        
        return book;
    }
}
