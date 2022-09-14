package snapps.relics;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Main implements ModInitializer {
    public static final Item SUPER_HEAVY_BOW = new Item(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1));
    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("relics", "super_heavy_bow"), SUPER_HEAVY_BOW);
    }
}
