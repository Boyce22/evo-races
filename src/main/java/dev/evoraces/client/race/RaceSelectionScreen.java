package dev.evoraces.client.race;

import dev.evoraces.EvoRaces;
import dev.evoraces.race.Race;
import dev.evoraces.race.RaceRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.util.List;

public class RaceSelectionScreen extends Screen {
    private static final int RACE_CARD_WIDTH = 220;
    private static final int RACE_CARD_HEIGHT = 180;
    private static final int ATTR_BAR_WIDTH = 100;
    private static final int ATTR_BAR_HEIGHT = 10;
    
    private final List<Race> races;
    private int selectedIndex = 0;
    private final Screen parent;
    private ButtonWidget prevButton;
    private ButtonWidget nextButton;
    private ButtonWidget selectButton;
    private ButtonWidget skipButton;
    
    public RaceSelectionScreen(Screen parent) {
        super(Text.literal("Escolha sua Raça"));
        this.parent = parent;
        
        List<Race> raceList;
        List<Race> loadedRaces = RaceDataCache.getInstance().getRaces();
        if (loadedRaces != null && !loadedRaces.isEmpty()) {
            raceList = loadedRaces;
        } else {
            try {
                raceList = List.of(
                    RaceRegistry.getInstance().getRace("human"),
                    RaceRegistry.getInstance().getRace("elf"),
                    RaceRegistry.getInstance().getRace("dwarf"),
                    RaceRegistry.getInstance().getRace("undead"),
                    RaceRegistry.getInstance().getRace("fairy")
                );
            } catch (Exception e) {
                raceList = List.of();
            }
        }
        this.races = raceList;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int cardX = (width - RACE_CARD_WIDTH) / 2;
        int cardY = height / 2 - 80;
        
        prevButton = ButtonWidget.builder(Text.literal("<"), btn -> {
            selectedIndex = (selectedIndex - 1 + races.size()) % races.size();
            updateButtons();
        }).dimensions(cardX - 40, cardY + RACE_CARD_HEIGHT / 2 - 15, 30, 30).build();
        
        nextButton = ButtonWidget.builder(Text.literal(">"), btn -> {
            selectedIndex = (selectedIndex + 1) % races.size();
            updateButtons();
        }).dimensions(cardX + RACE_CARD_WIDTH + 10, cardY + RACE_CARD_HEIGHT / 2 - 15, 30, 30).build();
        
        selectButton = ButtonWidget.builder(
            Text.literal("Confirmar"),
            btn -> confirmSelection()
        ).dimensions(width / 2 - 100, cardY + RACE_CARD_HEIGHT + 40, 200, 25).build();
        
        skipButton = ButtonWidget.builder(
            Text.literal("Pular (Usar Humano)"),
            btn -> skipSelection()
        ).dimensions(width / 2 - 100, cardY + RACE_CARD_HEIGHT + 75, 200, 20).build();
        
        this.addDrawableChild(prevButton);
        this.addDrawableChild(nextButton);
        this.addDrawableChild(selectButton);
        this.addDrawableChild(skipButton);
        
        updateButtons();
    }
    
    private void updateButtons() {
        if (prevButton != null) {
            prevButton.active = races.size() > 1;
        }
        if (nextButton != null) {
            nextButton.active = races.size() > 1;
        }
    }
    
    private void confirmSelection() {
        Race selected = races.get(selectedIndex);
        RaceSelectionManager.getInstance().setSelectedRace(selected);
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getServer() != null) {
            File saveDir = client.getServer().getRunDirectory();
            RaceSelectionManager.getInstance().saveRaceSelection(saveDir, selected.getId());
        }
        
        this.close();
    }
    
    private void skipSelection() {
        Race human = RaceRegistry.getInstance().getRace("human");
        RaceSelectionManager.getInstance().setSelectedRace(human);
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getServer() != null) {
            File saveDir = client.getServer().getRunDirectory();
            RaceSelectionManager.getInstance().saveRaceSelection(saveDir, "human");
        }
        
        this.close();
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        
        int titleWidth = textRenderer.getWidth("Escolha sua Raça");
        context.drawText(textRenderer, "Escolha sua Raça", width / 2 - titleWidth / 2, 30, 0xFFFFFF, false);
        
        int cardX = (width - RACE_CARD_WIDTH) / 2;
        int cardY = height / 2 - 80;
        
        context.fill(cardX, cardY, cardX + RACE_CARD_WIDTH, cardY + RACE_CARD_HEIGHT, 0xFF2D2D2D);
        context.fill(cardX, cardY, cardX + RACE_CARD_WIDTH, cardY + 3, 0xFF4A90D9);
        context.fill(cardX, cardY + RACE_CARD_HEIGHT - 3, cardX + RACE_CARD_WIDTH, cardY + RACE_CARD_HEIGHT, 0xFF4A90D9);
        context.fill(cardX, cardY, cardX + 3, cardY + RACE_CARD_HEIGHT, 0xFF4A90D9);
        context.fill(cardX + RACE_CARD_WIDTH - 3, cardY, cardX + RACE_CARD_WIDTH, cardY + RACE_CARD_HEIGHT, 0xFF4A90D9);
        
        Race race = races.get(selectedIndex);
        
        context.drawText(textRenderer, race.getName(), cardX + 10, cardY + 15, 0xFFD700, false);
        
        String[] descLines = wrapText(race.getDescription(), RACE_CARD_WIDTH - 20);
        for (int j = 0; j < Math.min(descLines.length, 2); j++) {
            context.drawText(textRenderer, descLines[j], cardX + 10, cardY + 35 + j * 12, 0xAAAAAA, false);
        }
        
        int attrY = cardY + 65;
        renderAttributeBar(context, "Vitalidade", race.getAttributes().getVitality(), cardX + 10, attrY);
        renderAttributeBar(context, "Força", race.getAttributes().getStrength(), cardX + 10, attrY + 15);
        renderAttributeBar(context, "Agilidade", race.getAttributes().getAgility(), cardX + 10, attrY + 30);
        renderAttributeBar(context, "Intelecto", race.getAttributes().getIntellect(), cardX + 10, attrY + 45);
        renderAttributeBar(context, "Resistência", race.getAttributes().getResistance(), cardX + 10, attrY + 60);
        
        context.drawText(textRenderer, String.format("%d / %d", selectedIndex + 1, races.size()), cardX + RACE_CARD_WIDTH / 2 - 20, cardY + RACE_CARD_HEIGHT - 15, 0x888888, false);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderAttributeBar(DrawContext context, String name, int value, int x, int y) {
        context.fill(x, y, x + ATTR_BAR_WIDTH, y + ATTR_BAR_HEIGHT, 0xFF333333);
        
        int fillWidth = (int) ((value / 150.0) * ATTR_BAR_WIDTH);
        int barColor = getAttributeColor(value);
        context.fill(x, y, x + fillWidth, y + ATTR_BAR_HEIGHT, barColor);
        
        context.drawText(textRenderer, name + ": " + value, x + ATTR_BAR_WIDTH + 5, y - 2, 0xFFFFFF, false);
    }
    
    private int getAttributeColor(int value) {
        if (value >= 120) return 0xFFFF6B6B;
        if (value >= 100) return 0xFFFFD93D;
        return 0xFF6BCB77;
    }
    
    private String[] wrapText(String text, int maxWidth) {
        if (text == null || text.isEmpty()) return new String[0];
        
        StringBuilder sb = new StringBuilder();
        String[] words = text.split(" ");
        String currentLine = "";
        
        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            if (textRenderer.getWidth(testLine) <= maxWidth) {
                currentLine = testLine;
            } else {
                if (!currentLine.isEmpty()) {
                    sb.append(currentLine).append("\n");
                }
                currentLine = word;
            }
        }
        if (!currentLine.isEmpty()) {
            sb.append(currentLine);
        }
        
        return sb.toString().split("\n");
    }
    
    @Override
    public void close() {
        this.client.setScreen(parent);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
