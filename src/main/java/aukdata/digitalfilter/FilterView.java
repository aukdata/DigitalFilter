package aukdata.digitalfilter;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import mekanism.common.content.filter.IFilter;
import mekanism.common.content.filter.IItemStackFilter;
import mekanism.common.content.filter.IModIDFilter;
import mekanism.common.content.filter.ITagFilter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Display-only state filter and sort order for the Digital Miner filter list. Neither changes the miner's real filter order.
 */
public final class FilterView {

    private FilterView() {
    }

    public enum State {
        ALL,
        ENABLED,
        DISABLED;

        public boolean test(IFilter<?> filter) {
            return switch (this) {
                case ALL -> true;
                case ENABLED -> filter.isEnabled();
                case DISABLED -> !filter.isEnabled();
            };
        }

        public Component label() {
            return Component.translatable("digitalfilter.state.short." + name().toLowerCase(Locale.ROOT));
        }

        public Component description() {
            return Component.translatable("digitalfilter.state." + name().toLowerCase(Locale.ROOT));
        }
    }

    public enum Sort {
        REGISTERED,
        MOD,
        NAME;

        public Component label() {
            return Component.translatable("digitalfilter.sort.short." + name().toLowerCase(Locale.ROOT));
        }

        public Component description() {
            return Component.translatable("digitalfilter.sort." + name().toLowerCase(Locale.ROOT));
        }

        /**
         * Sorts indices into the filter list; ties keep the registration order.
         */
        public void sort(List<Integer> indices, List<? extends IFilter<?>> filters) {
            Comparator<Integer> comparator = switch (this) {
                case REGISTERED -> null;
                case MOD -> Comparator.<Integer, String>comparing(i -> modKey(filters.get(i))).thenComparing(i -> nameKey(filters.get(i)));
                case NAME -> Comparator.comparing(i -> nameKey(filters.get(i)));
            };
            if (comparator != null) {
                indices.sort(comparator.thenComparingInt(i -> i));
            }
        }
    }

    public static <E extends Enum<E>> E cycle(E value, int direction) {
        E[] values = value.getDeclaringClass().getEnumConstants();
        return values[Math.floorMod(value.ordinal() + direction, values.length)];
    }

    private static String nameKey(IFilter<?> filter) {
        String name = "";
        if (filter instanceof IItemStackFilter<?> itemFilter) {
            name = itemFilter.getItemStack().getHoverName().getString();
        } else if (filter instanceof ITagFilter<?> tagFilter && tagFilter.getTagName() != null) {
            name = tagFilter.getTagName();
        } else if (filter instanceof IModIDFilter<?> modIDFilter && modIDFilter.getModID() != null) {
            String modID = modIDFilter.getModID();
            name = ModList.get().getModContainerById(modID).map(container -> container.getModInfo().getDisplayName()).orElse(modID);
        }
        return name.toLowerCase(Locale.ROOT);
    }

    private static String modKey(IFilter<?> filter) {
        if (filter instanceof IItemStackFilter<?> itemFilter) {
            ItemStack stack = itemFilter.getItemStack();
            ResourceLocation id = stack.isEmpty() ? null : ForgeRegistries.ITEMS.getKey(stack.getItem());
            return id == null ? "" : id.getNamespace();
        } else if (filter instanceof ITagFilter<?> tagFilter && tagFilter.getTagName() != null) {
            String tagName = tagFilter.getTagName();
            int colon = tagName.indexOf(':');
            return colon < 0 ? "minecraft" : tagName.substring(0, colon);
        } else if (filter instanceof IModIDFilter<?> modIDFilter && modIDFilter.getModID() != null) {
            return modIDFilter.getModID();
        }
        return "";
    }
}
