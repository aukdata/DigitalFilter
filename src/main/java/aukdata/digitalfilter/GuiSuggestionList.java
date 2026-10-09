package aukdata.digitalfilter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.text.GuiTextField;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * Drop-down shown under a filter's text field listing tag names / mod IDs that match what has been typed so far. Tab or a click accepts the
 * highlighted entry, up/down change it.
 */
public class GuiSuggestionList extends GuiElement {

    private static final int ROW_HEIGHT = 10;
    private static final int MAX_ROWS = 6;
    private static final int MAX_SUGGESTIONS = 100;

    private final GuiTextField textField;
    private final Supplier<List<String>> candidateSupplier;
    private List<String> candidates;
    private final List<String> suggestions = new ArrayList<>();
    private String lastText;
    private int selected;
    private int scroll;

    public GuiSuggestionList(IGuiWrapper gui, GuiTextField textField, Supplier<List<String>> candidateSupplier) {
        super(gui, textField.getRelativeX(), textField.getRelativeY() + textField.getHeight(), textField.getWidth(), 0);
        this.textField = textField;
        this.candidateSupplier = candidateSupplier;
        visible = false;
    }

    @Override
    public void tick() {
        super.tick();
        refresh();
    }

    private void refresh() {
        String text = textField.getText();
        if (!text.equals(lastText)) {
            lastText = text;
            updateSuggestions(text.toLowerCase(Locale.ROOT));
        }
        visible = !suggestions.isEmpty() && textField.isTextFieldFocused();
        height = Math.min(MAX_ROWS, suggestions.size()) * ROW_HEIGHT + 2;
    }

    private void updateSuggestions(String query) {
        suggestions.clear();
        selected = 0;
        scroll = 0;
        //Wildcard patterns are matched by Mekanism itself, so there is nothing sensible to suggest
        if (query.isEmpty() || query.contains("*") || query.contains("?")) {
            return;
        }
        if (candidates == null) {
            candidates = candidateSupplier.get();
        }
        List<String> matches = new ArrayList<>();
        for (String candidate : candidates) {
            if (!candidate.equals(query) && candidate.contains(query)) {
                matches.add(candidate);
            }
        }
        matches.sort(Comparator.<String>comparingInt(candidate -> rank(candidate, query)).thenComparing(Comparator.naturalOrder()));
        suggestions.addAll(matches.subList(0, Math.min(MAX_SUGGESTIONS, matches.size())));
    }

    /**
     * Prefix matches first, then matches at the start of a path segment (after ':' or '/'), then anything containing the query.
     */
    private static int rank(String candidate, String query) {
        if (candidate.startsWith(query)) {
            return 0;
        }
        int index = candidate.indexOf(query);
        while (index > 0) {
            char before = candidate.charAt(index - 1);
            if (before == ':' || before == '/' || before == '_') {
                return 1;
            }
            index = candidate.indexOf(query, index + 1);
        }
        return 2;
    }

    private void accept(int index) {
        if (index >= 0 && index < suggestions.size()) {
            textField.setText(suggestions.get(index));
            textField.setFocused(true);
            refresh();
        }
    }

    private void select(int index) {
        int size = suggestions.size();
        selected = Math.floorMod(index, size);
        if (selected < scroll) {
            scroll = selected;
        } else if (selected >= scroll + MAX_ROWS) {
            scroll = selected - MAX_ROWS + 1;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        refresh();
        if (visible) {
            if (keyCode == GLFW.GLFW_KEY_TAB) {
                accept(selected);
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                select(selected + 1);
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_UP) {
                select(selected - 1);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (visible && isMouseOver(mouseX, mouseY)) {
            scroll = Math.max(0, Math.min(suggestions.size() - MAX_ROWS, scroll - (int) Math.signum(delta)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        accept(rowAt(mouseY));
    }

    private int rowAt(double mouseY) {
        int row = (int) ((mouseY - getY() - 1) / ROW_HEIGHT);
        return row >= 0 && row < MAX_ROWS ? scroll + row : -1;
    }

    @Override
    public void renderForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderForeground(guiGraphics, mouseX, mouseY);
        guiGraphics.pose().pushPose();
        //Stay above the item icons drawn by other elements
        guiGraphics.pose().translate(0, 0, 300);
        guiGraphics.fill(relativeX, relativeY, relativeX + width, relativeY + height, 0xF0101010);
        int hovered = isMouseOver(mouseX, mouseY) ? rowAt(mouseY) : -1;
        int rows = Math.min(MAX_ROWS, suggestions.size() - scroll);
        for (int row = 0; row < rows; row++) {
            int index = scroll + row;
            int y = relativeY + 1 + row * ROW_HEIGHT;
            if (index == selected || index == hovered) {
                guiGraphics.fill(relativeX + 1, y, relativeX + width - 1, y + ROW_HEIGHT, 0xFF3C3C3C);
            }
            drawTextScaledBound(guiGraphics, suggestions.get(index), relativeX + 2, y + 1, index == selected ? 0xFFFFFF55 : 0xFFE0E0E0, width - 4);
        }
        guiGraphics.pose().popPose();
    }

    @Override
    public void drawBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //Everything is drawn in the foreground so it overlaps the rest of the window
    }
}
