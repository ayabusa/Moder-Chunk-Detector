package modern_chunk_detector;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModernChunkDetectorClient implements ClientModInitializer {

	public static CopyOnWriteArrayList<ChunkPos> chunk_to_render = new CopyOnWriteArrayList<ChunkPos>();
	private static KeyBinding toogle_key;
	private static boolean is_mod_enabled = true;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		System.out.println("[Modern Chunk Detector] Mod Initialized!");

		toogle_key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"toogle_key.modern_chunk_detector", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_O, // The keycode of the key
				"modern_chunk_detector.ayabusa" // The translation key of the keybinding's category.
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toogle_key.wasPressed()) {
				if(is_mod_enabled){
					client.player.sendMessage(Text.literal("§b[§dModern chunk detector§b] §fChunk detection is §cOFF"), false);
					is_mod_enabled=false;
				}else {
					client.player.sendMessage(Text.literal("§b[§dModern chunk detector§b] §fChunk detection is §aON"), false);
					is_mod_enabled=true;
				}
			}
		});

		ClientChunkEvents.CHUNK_LOAD.register((ClientWorld world, WorldChunk chunk)->{
			/*ChunkPos chunk_position = chunk.getPos();
			int start_x = chunk_position.getStartX();
			int start_z = chunk_position.getStartZ();*/
			/*BlockState block_state = chunk.getBlockState(new BlockPos(start_x, 99, start_z));
			String block_name = block_state.getBlock().getName().toString();
			if(block_name.contains("block.minecraft.copper_ore")){
				System.out.println("[Modern Chunk Detector] Copper ore detected");
			}*/
			/*for(int x = 0; x <= 15; x++) {
				for (int y = 8; y <= 63; y++) {
					for (int z = 0; z <= 15; z++) {
						BlockState block_state = chunk.getBlockState(new BlockPos(start_x+x, y, start_z+z));
						String block_name = block_state.getBlock().getName().toString();
						if(block_name.contains("block.minecraft.copper_ore") || block_name.contains("block.minecraft.ancient_debris")){
							System.out.println("[Modern Chunk Detector] Found modern chunk at: "+chunk_position);
							chunk_to_render.add(chunk_position);
							return;
						}
					}
				}
			}*/
			ChunkScanner scanner = new ChunkScanner(chunk);
			scanner.start();
		});

		ClientChunkEvents.CHUNK_UNLOAD.register((ClientWorld world, WorldChunk chunk)->{
				chunk_to_render.remove(chunk.getPos());
		});

		WorldRenderEvents.BEFORE_DEBUG_RENDER.register((WorldRenderContext context)->{
			if(is_mod_enabled) {
				MatrixStack matrices = context.matrixStack();
				VertexConsumerProvider vertexConsumers = context.consumers();
				double cameraX = context.camera().getPos().x;
				double cameraY = context.camera().getPos().y;
				double cameraZ = context.camera().getPos().z;

				for (Object i : chunk_to_render) {
					render(matrices, vertexConsumers, cameraX, cameraY, cameraZ, (ChunkPos) i);
				}
			}
		});
	}

	private final MinecraftClient client = MinecraftClient.getInstance();
	private static final int DARK_CYAN = ColorHelper.Argb.getArgb(255, 0, 155, 155);
	private static final int YELLOW = ColorHelper.Argb.getArgb(255, 255, 255, 0);

	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ, ChunkPos chunkPos) {
		Entity entity = this.client.gameRenderer.getCamera().getFocusedEntity();
		float f = (float)((double)this.client.world.getBottomY() - cameraY);
		float g = (float)((double)this.client.world.getTopY() - cameraY);

		float h = (float)((double)chunkPos.getStartX() - cameraX);
		float i = (float)((double)chunkPos.getStartZ() - cameraZ);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(1.0));
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();

		int j;
		int k;
		for(j = -16; j <= 32; j += 16) {
			for(k = -16; k <= 32; k += 16) {
				/*
				vertexConsumer.vertex(matrix4f, h + (float)j, f, i + (float)k).color(1.0F, 0.0F, 0.0F, 0.0F);
				vertexConsumer.vertex(matrix4f, h + (float)j, f, i + (float)k).color(1.0F, 0.0F, 0.0F, 0.5F);
				vertexConsumer.vertex(matrix4f, h + (float)j, g, i + (float)k).color(1.0F, 0.0F, 0.0F, 0.5F);
				vertexConsumer.vertex(matrix4f, h + (float)j, g, i + (float)k).color(1.0F, 0.0F, 0.0F, 0.0F);
				 */
			}
		}

		for(j = 2; j < 16; j += 2) {
			/*
			k = j % 4 == 0 ? DARK_CYAN : YELLOW;
			vertexConsumer.vertex(matrix4f, h + (float)j, f, i).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h + (float)j, f, i).color(k);
			vertexConsumer.vertex(matrix4f, h + (float)j, g, i).color(k);
			vertexConsumer.vertex(matrix4f, h + (float)j, g, i).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h + (float)j, f, i + 16.0F).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h + (float)j, f, i + 16.0F).color(k);
			vertexConsumer.vertex(matrix4f, h + (float)j, g, i + 16.0F).color(k);
			vertexConsumer.vertex(matrix4f, h + (float)j, g, i + 16.0F).color(1.0F, 1.0F, 0.0F, 0.0F);*/
		}

		for(j = 2; j < 16; j += 2) {
			/*
			k = j % 4 == 0 ? DARK_CYAN : YELLOW;
			vertexConsumer.vertex(matrix4f, h, f, i + (float)j).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h, f, i + (float)j).color(k);
			vertexConsumer.vertex(matrix4f, h, g, i + (float)j).color(k);
			vertexConsumer.vertex(matrix4f, h, g, i + (float)j).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h + 16.0F, f, i + (float)j).color(1.0F, 1.0F, 0.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h + 16.0F, f, i + (float)j).color(k);
			vertexConsumer.vertex(matrix4f, h + 16.0F, g, i + (float)j).color(k);
			vertexConsumer.vertex(matrix4f, h + 16.0F, g, i + (float)j).color(1.0F, 1.0F, 0.0F, 0.0F);*/
		}

		float l;
		for(j = this.client.world.getBottomY(); j <= this.client.world.getTopY(); j += 2) {

			l = (float)((double)j - cameraY);
			int m = j % 8 == 0 ? DARK_CYAN : YELLOW;
			vertexConsumer.vertex(matrix4f, h, l, i).color(1.0F, 0F, 1.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h, l, i).color(1.0F, 0F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h, l, i + 16.0F).color(1.0F, 0F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h + 16.0F, l, i + 16.0F).color(1.0F, 0F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h + 16.0F, l, i).color(1.0F, 0F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h, l, i).color(1.0F, 0F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h, l, i).color(1.0F, 0F, 1.0F, 0.0F);
		}

		vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(2.0));

		for(j = 0; j <= 16; j += 16) {
			for(k = 0; k <= 16; k += 16) {
				vertexConsumer.vertex(matrix4f, h + (float)j, f, i + (float)k).color(1.0F, 0F, 1.0F, 0.0F);
				vertexConsumer.vertex(matrix4f, h + (float)j, f, i + (float)k).color(1.0F, 0F, 1.0F, 1.0F);
				vertexConsumer.vertex(matrix4f, h + (float)j, g, i + (float)k).color(1.0F, 0F, 1.0F, 1.0F);
				vertexConsumer.vertex(matrix4f, h + (float)j, g, i + (float)k).color(1.0F, 0F, 1.0F, 0.0F);
			}
		}

		for(j = this.client.world.getBottomY(); j <= this.client.world.getTopY(); j += 16) {
			l = (float)((double)j - cameraY);
			vertexConsumer.vertex(matrix4f, h, l, i).color(1.0F, 0F, 1.0F, 0.0F);
			vertexConsumer.vertex(matrix4f, h, l, i).color(1.0F, 0F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h, l, i + 16.0F).color(1.0F, 0F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h + 16.0F, l, i + 16.0F).color(1.0F, 0F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h + 16.0F, l, i).color(1.0F, 0F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h, l, i).color(1.0F, 0F, 1.0F, 1.0F);
			vertexConsumer.vertex(matrix4f, h, l, i).color(1.0F, 0F, 1.0F, 0.0F);
		}

	}
}