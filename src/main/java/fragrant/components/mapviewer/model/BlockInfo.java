package fragrant.components.mapviewer.model;

import net.minecraft.block.Block;
import java.util.HashMap;
import java.awt.Color;
import java.util.Map;

import fragrant.components.mapviewer.core.BlockColorMapping;

public class BlockInfo {
    public final boolean isVisible;
    public final int depth;
    private static final Map<String, Color> blockColors = new HashMap<>();

    public BlockInfo(Block block, boolean isVisible, int depth) {
        this.isVisible = isVisible;
        this.depth = depth;
    }

    public static void initializeBlockColors() {
        BlockColorMapping.BLOCK_COLORS.forEach((blockId, hexColor) -> {
            if (!isTransparentBlock(blockId)) {
                String hex = hexColor.replace("#", "");
                int rgb = Integer.parseInt(hex, 16);
                Color color = new Color(
                        (rgb >> 16) & 0xFF,
                        (rgb >> 8) & 0xFF,
                        rgb & 0xFF);
                blockColors.put(blockId, color);
            }
        });
    }

    public static boolean isTransparentBlock(String blockId) {
        return blockId.equals("air") ||
               blockId.equals("cave_air") ||
               blockId.equals("void_air") ||
               blockId.equals("grass") ||
               blockId.equals("tall_grass") ||
               blockId.equals("crimson_roots") ||
               blockId.equals("crimson_fungus") ||
               blockId.equals("red_mushroom") ||
               blockId.equals("brown_mushroom") ||
               blockId.equals("dandelion") ||
               blockId.equals("poppy") ||
               blockId.equals("blue_orchid") ||
               blockId.equals("allium") ||
               blockId.equals("azure_bluet") ||
               blockId.equals("tulip") ||
               blockId.equals("oxeye_daisy") ||
               blockId.equals("cornflower") ||
               blockId.equals("lily_of_the_valley") ||
               blockId.equals("torchflower") ||
               blockId.equals("peony") ||
               blockId.equals("soul_campfire") ||
               blockId.equals("fire") ||
               blockId.equals("heavy_weighted_pressure_plate") ||
               blockId.equals("soul_fire_lantern") ||
               blockId.equals("sapling") ||
               blockId.equals("wheat") ||
               blockId.equals("carrots") ||
               blockId.equals("potatoes") ||
               blockId.equals("beetroots") ||
               blockId.equals("snow") ||
               blockId.equals("orange_carpet") ||
               blockId.equals("magenta_carpet") ||
               blockId.equals("white_carpet") ||
               blockId.equals("gray_carpet") ||
               blockId.equals("light_gray_carpet") ||
               blockId.equals("cyan_carpet") ||
               blockId.equals("purple_carpet") ||
               blockId.equals("blue_carpet") ||
               blockId.equals("brown_carpet") ||
               blockId.equals("black_carpet") ||
               blockId.equals("moss_carpet") ||
               blockId.equals("vine") ||
               blockId.equals("cave_vines") ||
               blockId.equals("twisting_vines") ||
               blockId.equals("wall_torch") ||
               blockId.equals("rail");
    }

    public static Color getBlockColor(String blockId) {
        return blockColors.getOrDefault(blockId, hexToColor(BlockColorMapping.getColor(blockId)));
    }

    private static Color hexToColor(String hexColor) {
        if (hexColor == null)
            return Color.MAGENTA;
        String hex = hexColor.replace("#", "");
        try {
            int rgb = Integer.parseInt(hex, 16);
            return new Color(
                    (rgb >> 16) & 0xFF,
                    (rgb >> 8) & 0xFF,
                    rgb & 0xFF);
        } catch (NumberFormatException e) {
            return Color.MAGENTA;
        }
    }

    public int depth() {
        return depth;
    }

    public boolean isVisible() {
        return isVisible;
    }
}