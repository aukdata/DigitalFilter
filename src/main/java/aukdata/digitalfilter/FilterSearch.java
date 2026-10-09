package aukdata.digitalfilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import mekanism.common.base.TagCache;
import mekanism.common.content.filter.IFilter;
import mekanism.common.content.filter.IItemStackFilter;
import mekanism.common.content.filter.IModIDFilter;
import mekanism.common.content.filter.ITagFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public final class FilterSearch {

    //Filters are replaced with new instances when edited, so caching by instance is safe
    private static final Map<IFilter<?>, List<String>> CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    private FilterSearch() {
    }

    /**
     * Every whitespace separated term in the query must be contained (case-insensitively) in one of the filter's searchable texts.
     */
    public static boolean matches(IFilter<?> filter, String query) {
        String[] terms = query.toLowerCase(Locale.ROOT).trim().split("\\s+");
        List<String> texts = CACHE.computeIfAbsent(filter, FilterSearch::collectTexts);
        for (String term : terms) {
            if (term.isEmpty()) {
                continue;
            }
            boolean found = false;
            for (String text : texts) {
                if (text.contains(term)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private static List<String> collectTexts(IFilter<?> filter) {
        List<String> texts = new ArrayList<>();
        if (filter instanceof IItemStackFilter<?> itemFilter) {
            addStack(texts, itemFilter.getItemStack());
        } else if (filter instanceof ITagFilter<?> tagFilter) {
            String tagName = tagFilter.getTagName();
            if (tagName != null) {
                texts.add(tagName);
                texts.add("#" + tagName);
                //Also match the names of the blocks in the tag, e.g. "iron ore" finds forge:ores/iron
                for (ItemStack stack : TagCache.getBlockTagStacks(tagName).stacks()) {
                    addStack(texts, stack);
                }
            }
        } else if (filter instanceof IModIDFilter<?> modIDFilter) {
            String modID = modIDFilter.getModID();
            if (modID != null) {
                texts.add(modID);
                texts.add("@" + modID);
                ModList.get().getModContainerById(modID).ifPresent(container -> texts.add(container.getModInfo().getDisplayName()));
            }
        }
        texts.replaceAll(text -> text.toLowerCase(Locale.ROOT));
        return texts;
    }

    private static void addStack(List<String> texts, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        texts.add(stack.getHoverName().getString());
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id != null) {
            texts.add(id.toString());
        }
    }
}
