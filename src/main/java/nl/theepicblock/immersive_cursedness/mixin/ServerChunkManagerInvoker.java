package nl.theepicblock.immersive_cursedness.mixin;

import net.minecraft.server.world.OptionalChunk;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerChunkManager.class)
public interface ServerChunkManagerInvoker {
    @Invoker("getChunkFuture")
    CompletableFuture<OptionalChunk<Chunk>> ic$callGetChunkFuture(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);
}
