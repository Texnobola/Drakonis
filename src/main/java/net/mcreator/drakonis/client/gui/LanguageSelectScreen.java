package net.mcreator.drakonis.client.gui;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.Minecraft;

import net.mcreator.drakonis.network.LanguageSelectMessage;

public class LanguageSelectScreen extends Screen {
    public LanguageSelectScreen() {
        super(Component.translatable("gui.drakonis.language_select"));
    }

    @Override
    protected void init() {
        super.init();
        
        int buttonWidth = 200;
        int buttonHeight = 20;
        int centerX = this.width / 2 - buttonWidth / 2;
        int startY = this.height / 2 - 40;
        
        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.drakonis.language.english"),
            btn -> selectLanguage("en_us"))
            .bounds(centerX, startY, buttonWidth, buttonHeight)
            .build());
        
        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.drakonis.language.russian"),
            btn -> selectLanguage("ru_ru"))
            .bounds(centerX, startY + 30, buttonWidth, buttonHeight)
            .build());
        
        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.drakonis.language.uzbek"),
            btn -> selectLanguage("uz_uz"))
            .bounds(centerX, startY + 60, buttonWidth, buttonHeight)
            .build());
    }

    private void selectLanguage(String langCode) {
        PacketDistributor.sendToServer(new LanguageSelectMessage(langCode));
        this.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
