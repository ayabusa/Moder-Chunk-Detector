package modern_chunk_detector;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkScanner extends Thread{
    WorldChunk chunk;
    public ChunkScanner(WorldChunk c){
        chunk = c;
    }

    public void run() {
        ChunkPos chunk_position = chunk.getPos();
        int start_x = chunk_position.getStartX();
        int start_z = chunk_position.getStartZ();

        for(int x = 0; x <= 15; x++) {
            for (int y = 8; y <= 63; y++) {
                for (int z = 0; z <= 15; z++) {
                    BlockState block_state = chunk.getBlockState(new BlockPos(start_x+x, y, start_z+z));
                    String block_name = block_state.getBlock().getName().toString();
                    if(block_name.contains("block.minecraft.copper_ore") || block_name.contains("block.minecraft.ancient_debris")){
                        System.out.println("[Modern Chunk Detector] Found modern chunk at: "+chunk_position);
                        ModernChunkDetectorClient.chunk_to_render.add(chunk_position);
                        return;
                    }
                }
            }
        }
    }
}
