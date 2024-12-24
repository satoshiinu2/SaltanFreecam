package com.satoshiinu.saltan;

import com.mojang.blaze3d.systems.RenderSystem;
import com.satoshiinu.saltan.render.CamPointRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;
import java.util.function.Supplier;

public class SaltanFreecamClient implements ClientModInitializer {

	static public MinecraftClient client;
	static public String MODID = "saltan-freecam";
	static private KeyBinding keyToggleFreecam;
	static private KeyBinding keyToggleMoveCam;
	static private KeyBinding keyGoPrevPos;
	// special future
	static private KeyBinding keyNightVision;
	static public boolean nightVisionEnabled;
	@Nullable
	static public Vec3d velocity = null;
	@Nullable
	static public Vec3d freecamPos = null;
	@Nullable
	static public Vec3d freecamPrevPos = null;
	@Nullable
	static public Vec3d freecamPrevSessionPos = null;
	static public CamHudRenderType freecamHudRenderType = CamHudRenderType.WHEN_ENABLED;
	static public boolean freecamPointRenderEnabled = true;
	static public float freecamYaw;
	static public float freecamYawPrev;
	static public float freecamPitch;
	static public float freecamPitchPrev;
	static public boolean freecamEnabled;
	static public boolean hasFreecamEnabled;
	static private boolean canMoveCamera;
	static public CamPointRenderer camPointRenderer;
	@Override
	public void onInitializeClient() {
		client = MinecraftClient.getInstance();
		camPointRenderer = new CamPointRenderer();

		keyToggleFreecam = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.saltan.toggleFreecam", GLFW.GLFW_KEY_G, "key.categories.saltan"));
		keyToggleMoveCam = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.saltan.toggleMovePlayer", GLFW.GLFW_KEY_Y, "key.categories.saltan"));
		keyGoPrevPos = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.saltan.goPrevPos", GLFW.GLFW_KEY_H, "key.categories.saltan"));
		keyNightVision = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.saltan.nightvision", GLFW.GLFW_KEY_N, "key.categories.saltan"));

		ClientTickEvents.START_CLIENT_TICK.register(minecraftClient -> {
			freecamPrevPos = freecamPos;
			while (keyToggleFreecam.wasPressed()){
				freecamEnabled = !freecamEnabled;
				hasFreecamEnabled = true;
				if(freecamEnabled) {
					onFreecamEnabled();
				}else{
					onFreecamDisabled();
				}
			}

			while (keyToggleMoveCam.wasPressed()) {
				canMoveCamera = !canMoveCamera;
				if(canMoveCamera) {
					log(Text.translatable("saltan.freecam.unlocked"));
				} else {
					log(Text.translatable("saltan.freecam.locked"));
				}
			}

			while (keyGoPrevPos.wasPressed()) {
				if(!freecamEnabled)continue;
				if(freecamPrevSessionPos != null) {
					freecamPos = freecamPrevSessionPos;
					freecamPrevSessionPos = null;
					log(Text.translatable("saltan.freecam.restore.success"));
				} else {
					log(Text.translatable("saltan.freecam.restore.failed"));
				}
			}


			while (keyNightVision.wasPressed()) {
				nightVisionEnabled = !nightVisionEnabled;
				if(nightVisionEnabled) {
					log(Text.translatable("saltan.nightvision.enabled"));
				} else {
					log(Text.translatable("saltan.nightvision.disabled"));
				}
			}

			if(velocity == null)velocity = Vec3d.ZERO;
			if(freecamPos == null)freecamPos = Vec3d.ZERO;

			if(canMoveCamera()) {
				handleCamMove();
			}

			velocity = velocity.multiply(0.9);

			freecamPos = freecamPos.add(velocity);

		});

		ClientLifecycleEvents.CLIENT_STARTED.register(minecraftClient -> {
			velocity = new Vec3d(0,0,0);
		});
		ClientLifecycleEvents.CLIENT_STOPPING.register(minecraftClient -> {
			freecamEnabled = false;
			canMoveCamera = false;
			hasFreecamEnabled = false;
			freecamPos = null;
			freecamPrevPos = null;
			freecamPrevSessionPos = null;
			freecamPitch = 0;
			freecamPitchPrev = 0;
			freecamYaw = 0;
			freecamYawPrev = 0;
		});
		
		HudRenderCallback.EVENT.register(this::renderHud);

		WorldRenderEvents.LAST.register(this::renderCamPos);
	}


	public float CAM_MOVE_SPEED = 0.1f;
	public float CAM_SPRINT_RATIO = 4f;

	private void handleCamMove() {
		assert velocity != null;
		float moveSpeed = CAM_MOVE_SPEED;
		if(client.options.sprintKey.isPressed())moveSpeed *= CAM_SPRINT_RATIO;
		float forwardSpd = 0;
		float upwordSpd = 0;
		float sidewaySpd = 0;

		if(client.options.leftKey.isPressed())
			sidewaySpd += 1.0f;
		if(client.options.rightKey.isPressed())
			sidewaySpd -= 1.0f;
		if(client.options.jumpKey.isPressed())
			upwordSpd += 1.0f;
		if(client.options.sneakKey.isPressed())
			upwordSpd -= 1.0f;
		if(client.options.forwardKey.isPressed())
			forwardSpd += 1.0f;
		if(client.options.backKey.isPressed())
			forwardSpd -= 1.0f;
        velocity = velocity.add(movementInputToVelocity(new Vec3d(sidewaySpd,upwordSpd,forwardSpd),moveSpeed,client.gameRenderer.getCamera().getYaw()));

	}

	private void onFreecamEnabled() {
		Camera camera = client.gameRenderer.getCamera();
		freecamPos = camera.getPos();
		freecamYaw = camera.getYaw();
		freecamPitch = camera.getPitch();
		freecamPrevPos = freecamPos;
		freecamYawPrev = freecamYaw;
		freecamPitchPrev = freecamPitch;

		canMoveCamera = true;
		log(Text.translatable("saltan.freecam.enabled"));
	}
	private void onFreecamDisabled() {
		freecamPrevSessionPos = freecamPos;

		log(Text.translatable("saltan.freecam.disabled"));
	}
	private final DecimalFormat numFormat = new DecimalFormat("##0.0");
	private String numFormat(double number){
		return numFormat.format(number);
	}
	private void renderHud(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		TextRenderer textRenderer = client.textRenderer;
		VertexConsumerProvider.Immediate vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();

		if(freecamPos != null && (freecamHudRenderType.shouldRender())){
			Text posText = Text.translatable("saltan.freecam.pos",numFormat(freecamPos.x),numFormat(freecamPos.y),numFormat(freecamPos.z));

			textRenderer.draw(posText,0,0,-1,true, drawContext.getMatrices().peek().getPositionMatrix(),vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH,0,-1);
		}
	}

	private void renderCamPos(WorldRenderContext context) {
		MinecraftClient client = MinecraftClient.getInstance();
		MatrixStack matrices = context.matrixStack();
		VertexConsumerProvider consumers = context.consumers();
		if(matrices == null)return;
		if(consumers == null)return;
		if(client.player == null)return;

		matrices.push();
		Vec3d actCamPos = context.camera().getPos();
		matrices.translate(-actCamPos.x, -actCamPos.y, -actCamPos.z);

		float width = client.player.getWidth();
		float height = client.player.getHeight();
		float offset = client.player.getStandingEyeHeight();

//		if(camPointShouldRender())
//			camPointRenderer.renderCamPos(matrices,consumers,new Box(-width/2, -offset, -width/2,width/2, height - offset, width/2),freecamPos);
		if(freecamPrevSessionPos != null)
			camPointRenderer.renderCamPos(matrices,consumers,new Box(-width/2, -offset, -width/2,width/2, height - offset, width/2),freecamPrevSessionPos);

		matrices.pop();
	}
	public boolean camPointShouldRender(){
		return !freecamEnabled && hasFreecamEnabled && freecamPointRenderEnabled;
	}

	public static boolean canMoveCamera(){
		if(freecamEnabled)return canMoveCamera;
        else return false;
	}
	protected static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
		double d = movementInput.lengthSquared();
		if (d < 1.0E-7) {
			return Vec3d.ZERO;
		} else {
			Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply((double)speed);
			float f = MathHelper.sin(yaw * 0.017453292F);
			float g = MathHelper.cos(yaw * 0.017453292F);
			return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
		}
	}

	@Nullable
	public static Vec3d getLerpedPos(float tickDelta){
		if(freecamPrevPos == null)return null;
		return freecamPrevPos.lerp(freecamPos,tickDelta);
	}
	public static float getLerpedYaw(float tickDelta){
		return MathHelper.lerp(tickDelta, freecamYawPrev, freecamYaw);
	}
	public static float getLerpedPitch(float tickDelta){
		return MathHelper.lerp(tickDelta, freecamPitchPrev, freecamPitch);
	}

	public static void changeLookDirection(double cursorDeltaX, double cursorDeltaY){
		float y = (float)cursorDeltaY * 0.15F;
		float x = (float)cursorDeltaX * 0.15F;
		freecamPitch += y;
		freecamYaw += x;
		freecamPitchPrev += y;
		freecamYawPrev += x;
	}

	public static boolean actionbarLogMode = true;
	public static void log(Text text){
		if(client.player == null) return;
		Text prefix = actionbarLogMode ? Text.literal("") : Text.translatable("saltan.prefix");

		client.player.sendMessage(prefix.copy().append(text),actionbarLogMode);
	}

	public enum CamHudRenderType {
		NEVER(()->false),
		WHEN_ENABLED(()->freecamEnabled),
		ALWAYS(()->true);

		private final Supplier<Boolean> shouldRenderedSupplier;
		CamHudRenderType(Supplier<Boolean> shouldRenderedSupplier) {
			this.shouldRenderedSupplier = shouldRenderedSupplier;
		}

		public boolean shouldRender() {
			return shouldRenderedSupplier.get();
		}
	}
}