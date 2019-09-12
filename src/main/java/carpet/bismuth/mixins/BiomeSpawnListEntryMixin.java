package carpet.bismuth.mixins;

import carpet.bismuth.interfaces.IBiomeSpawnListEntry;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Biome.SpawnListEntry.class)
public abstract class BiomeSpawnListEntryMixin extends WeightedRandom.Item implements IBiomeSpawnListEntry
{
    public BiomeSpawnListEntryMixin(int itemWeightIn)
    {
        super(itemWeightIn);
    }
    
    @Override
    public int getWeight()
    {
        return this.itemWeight;
    }
}
