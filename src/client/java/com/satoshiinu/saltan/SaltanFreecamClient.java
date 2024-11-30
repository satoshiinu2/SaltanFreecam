package com.satoshiinu.saltan;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.text.DecimalFormat;

public class SaltanFreecamClient implements ClientModInitializer {
	static public MinecraftClient client;
	static private KeyBinding keyToggleFreecam;
	static private KeyBinding keyToggleMoveCam;
	static private KeyBinding keyGoPrevPos;
	static public Vec3d velocity = new Vec3d(0,0,0);
	static public Vec3d freecamPos = new Vec3d(0,0,0);
	static public Vec3d freecamPrevPos = new Vec3d(0,0,0);
	static public Vec3d freecamPrevSessionPos = null;
	static public float freecamYaw;
	static public float freecamYawPrev;
	static public float freecamPitch;
	static public float freecamPitchPrev;
	static public boolean freecamEnabled;
	static private boolean canMoveCamera;
	@Override
	public void onInitializeClient() {
		client = MinecraftClient.getInstance();

		keyToggleFreecam = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.saltan.toggleFreecam", GLFW.GLFW_KEY_G, "key.categories.saltan"));
		keyToggleMoveCam = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.saltan.toggleMovePlayer", GLFW.GLFW_KEY_Y, "key.categories.saltan"));
		keyGoPrevPos = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.saltan.goPrevPos", GLFW.GLFW_KEY_P, "key.categories.saltan"));

		ClientTickEvents.START_CLIENT_TICK.register(minecraftClient -> {
			freecamPrevPos = freecamPos;
			while (keyToggleFreecam.wasPressed()){
				freecamEnabled = !freecamEnabled;
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
				if(freecamPrevSessionPos != null) {
					freecamPos = freecamPrevSessionPos;
					log(Text.translatable("saltan.freecam.restore.success"));
				} else {
					log(Text.translatable("saltan.freecam.restore.failed"));
				}
			}

			if(canMoveCamera()) {
				handleCamMove();
			}

			velocity = velocity.multiply(0.9);

			freecamPos = freecamPos.add(velocity);

		});

		HudRenderCallback.EVENT.register(this::onRender);
	}

	private void handleCamMove() {
//		client.options.forwardKey
		float moveSpeed = 0.1f;
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
	private void onRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		TextRenderer textRenderer = client.textRenderer;
		VertexConsumerProvider.Immediate vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();

		if(true || freecamEnabled){

			String posStr = String.format("freecam pos: x: %s y: %s z: %s",numFormat(freecamPos.x),numFormat(freecamPos.y),numFormat(freecamPos.z));

			textRenderer.draw(posStr,0,0,-1,true, drawContext.getMatrices().peek().getPositionMatrix(),vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH,0,-1);
		}
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

	public static Vec3d getLerpedPos(float tickDelta){
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

}