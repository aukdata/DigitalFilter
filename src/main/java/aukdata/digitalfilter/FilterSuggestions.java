package aukdata.digitalfilter;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

/**
 * Suggestion candidates for the Digital Miner's tag and mod ID filters, which match blocks.
 */
public final class FilterSuggestions {

    private FilterSuggestions() {
    }

    public static List<String> blockTags() {
        ITagManager<Block> tags = ForgeRegistries.BLOCKS.tags();
        if (tags == null) {
            return List.of();
        }
        return tags.getTagNames().map(key -> key.location().toString()).sorted().toList();
    }

    public static List<String> blockModIDs() {
        return ForgeRegistries.BLOCKS.getKeys().stream().map(ResourceLocation::getNamespace).distinct().sorted().toList();
    }
}
