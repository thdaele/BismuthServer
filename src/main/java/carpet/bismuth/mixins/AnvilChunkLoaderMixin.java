package carpet.bismuth.mixins;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(AnvilChunkLoader.class)
public abstract class AnvilChunkLoaderMixin
{
    @Shadow private boolean flushing;
    
    @Shadow @Final private static Logger LOGGER;
    
    @Shadow @Final private File chunkSaveLocation;
    
    @Shadow protected abstract void writeChunkData(ChunkPos pos, NBTTagCompound compound) throws IOException;
    
    
    private ChunkPos copyOfChunkPos1;
    private ChunkPos copyOfChunkPos2;
    
    /**
     * A map containing chunks to be written to disk (but not those that are currently in the process of being written).
     * Key is the chunk position, value is the NBT to write.
     */
    private final Map<ChunkPos, NBTTagCompound> chunksToSave = new HashMap();
    private final Map<ChunkPos, NBTTagCompound> chunksInWrite = new HashMap();
    
    // Insert new chunk into pending queue, replacing any older one at the same position
    synchronized private void queueChunkToRemove(ChunkPos pos, NBTTagCompound data)
    {
        chunksToSave.put(pos, data);
    }
    
    // Fetch another chunk to save to disk and atomically move it into
    // the queue of chunk(s) being written.
    synchronized private Map.Entry<ChunkPos, NBTTagCompound> fetchChunkToWrite()
    {
        if (chunksToSave.isEmpty())
            return null;
        Iterator<Map.Entry<ChunkPos, NBTTagCompound>> iter = chunksToSave.entrySet().iterator();
        Map.Entry<ChunkPos, NBTTagCompound> entry = iter.next();
        iter.remove();
        chunksInWrite.put(entry.getKey(), entry.getValue());
        return entry;
    }
    
    // Once the write for a chunk is completely committed to disk,
    // this method discards it
    synchronized private void retireChunkToWrite(ChunkPos pos, NBTTagCompound data)
    {
        chunksInWrite.remove(pos);
    }
    
    // Check these data structures for a chunk being reloaded
    synchronized private NBTTagCompound reloadChunkFromRemoveQueues(ChunkPos pos)
    {
        NBTTagCompound data = chunksToSave.get(pos);
        if (data != null)
            return data;
        return chunksInWrite.get(pos);
    }
    
    @Inject(method = "loadChunk", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void copyChunkPos(World arg0, int x, int z, CallbackInfoReturnable<Chunk> cir, ChunkPos chunkpos)
    {
        this.copyOfChunkPos1 = chunkpos;
    }
    
    @Redirect(method = "loadChunk", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object onLoadChunk(Map map, Object key)
    {
        return reloadChunkFromRemoveQueues(copyOfChunkPos1);
    }
    
    @Inject(
            method = "isChunkGeneratedAt",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void copyPos(int x, int z, CallbackInfoReturnable<Boolean> cir, ChunkPos chunkpos)
    {
        this.copyOfChunkPos2 = chunkpos;
    }
    
    @Redirect(method = "isChunkGeneratedAt", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object onIsChunkGeneratedAt(Map map, Object key)
    {
        return reloadChunkFromRemoveQueues(copyOfChunkPos2);
    }
    
    @Inject(method = "addChunkToPending", at = @At("HEAD"), cancellable = true)
    private void onAddChunkToPending(ChunkPos pos, NBTTagCompound compound, CallbackInfo ci)
    {
        if (!chunksInWrite.containsKey(pos))
        {
            queueChunkToRemove(pos, compound);
        }
    
        ThreadedFileIOBase.getThreadedIOInstance().queueIO((AnvilChunkLoader) (Object) this);
        ci.cancel();
    }
    
    /**
     * @author DeadlyMC
     */
    @Overwrite
    public boolean writeNextIO()
    {
        /*
        if (this.chunksToRemove.isEmpty())
        {
            if (this.savingExtraData)
            {
                LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)this.chunkSaveLocation.getName());
            }

            return false;
        }
        else
        {
        */
    
        // ChunkPos chunkpos = this.chunksToRemove.keySet().iterator().next();
    
        Map.Entry<ChunkPos, NBTTagCompound> entry = fetchChunkToWrite();
        if (entry == null)
        {
            // If none left, here's code for some message that will never
            // be executed since there is no "extra data."
            if (this.flushing)
            {
                LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[]{this.chunkSaveLocation.getName()});
            }
        
            return false;
        }
    
        // New for MC-119971
        ChunkPos chunkpos = entry.getKey();
        NBTTagCompound nbttagcompound = entry.getValue();
    
    
        // boolean lvt_3_1_;
        
        /*
        try
        {
            this.chunksBeingSaved.add(chunkpos);
            NBTTagCompound nbttagcompound = this.chunksToRemove.remove(chunkpos);
    
            if (nbttagcompound != null)
            {
        */
        try
        {
            this.writeChunkData(chunkpos, nbttagcompound);
        }
        catch (Exception exception)
        {
            LOGGER.error("Failed to save chunk", (Throwable)exception);
        }
        /*
        }
            lvt_3_1_ = true;
        }
        finally
        {
            this.chunksBeingSaved.remove(chunkpos);
        }
        */
        retireChunkToWrite(chunkpos, nbttagcompound);
    
        // return lvt_3_1_;
        return true;
        // }  // CM fixed indentation
    }
}
