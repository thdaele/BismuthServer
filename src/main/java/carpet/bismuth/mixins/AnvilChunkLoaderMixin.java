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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(AnvilChunkLoader.class)
public abstract class AnvilChunkLoaderMixin {
	@Shadow
	@Final
	private static Logger LOGGER;

	private final Map<ChunkPos, NBTTagCompound> chunksToSave = new HashMap();
	private final Map<ChunkPos, NBTTagCompound> chunksInWrite = new HashMap();
	@Shadow
	private boolean flushing;
	@Shadow
	@Final
	private File chunkSaveLocation;
	private ChunkPos copyOfChunkPos1;
	private ChunkPos copyOfChunkPos2;

	@Shadow
	protected abstract void writeChunkData(ChunkPos pos, NBTTagCompound compound);

	synchronized private void queueChunkToRemove(ChunkPos pos, NBTTagCompound data) {
		chunksToSave.put(pos, data);
	}

	synchronized private Map.Entry<ChunkPos, NBTTagCompound> fetchChunkToWrite() {
		if (this.chunksToSave.isEmpty()) {
			return null;
		}

		Iterator<Map.Entry<ChunkPos, NBTTagCompound>> iter = this.chunksToSave.entrySet().iterator();
		Map.Entry<ChunkPos, NBTTagCompound> entry = iter.next();
		iter.remove();
		this.chunksInWrite.put(entry.getKey(), entry.getValue());
		return entry;
	}

	synchronized private void retireChunkToWrite(ChunkPos pos) {
		this.chunksInWrite.remove(pos);
	}

	synchronized private NBTTagCompound reloadChunkFromRemoveQueues(ChunkPos pos) {
		NBTTagCompound data = this.chunksToSave.get(pos);
		if (data != null) {
			return data;
		}

		return this.chunksInWrite.get(pos);
	}

	@Inject(method = "loadChunk", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
	private void copyChunkPos(World arg0, int x, int z, CallbackInfoReturnable<Chunk> cir, ChunkPos chunkpos) {
		this.copyOfChunkPos1 = chunkpos;
	}

	@Redirect(method = "loadChunk", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
	private Object onLoadChunk(Map map, Object key) {
		return reloadChunkFromRemoveQueues(this.copyOfChunkPos1);
	}

	@Inject(method = "isChunkGeneratedAt", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
	private void copyPos(int x, int z, CallbackInfoReturnable<Boolean> cir, ChunkPos chunkpos) {
		this.copyOfChunkPos2 = chunkpos;
	}

	@Redirect(method = "isChunkGeneratedAt", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
	private Object onIsChunkGeneratedAt(Map map, Object key) {
		return reloadChunkFromRemoveQueues(this.copyOfChunkPos2);
	}

	@Inject(method = "addChunkToPending", at = @At("HEAD"), cancellable = true)
	private void onAddChunkToPending(ChunkPos pos, NBTTagCompound compound, CallbackInfo ci) {
		if (!this.chunksInWrite.containsKey(pos)) {
			queueChunkToRemove(pos, compound);
		}

		ThreadedFileIOBase.getThreadedIOInstance().queueIO((AnvilChunkLoader) (Object) this);
		ci.cancel();
	}

	/**
	 * @author DeadlyMC
	 */
	@Overwrite
	public boolean writeNextIO() {
		Map.Entry<ChunkPos, NBTTagCompound> entry = fetchChunkToWrite();
		if (entry == null) {
			if (this.flushing) {
				LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[]{this.chunkSaveLocation.getName()});
			}

			return false;
		}

		ChunkPos chunkpos = entry.getKey();
		NBTTagCompound nbttagcompound = entry.getValue();

		try {
			this.writeChunkData(chunkpos, nbttagcompound);
		} catch (Exception exception) {
			LOGGER.error("Failed to save chunk", exception);
		}

		retireChunkToWrite(chunkpos);

		return true;
	}
}
