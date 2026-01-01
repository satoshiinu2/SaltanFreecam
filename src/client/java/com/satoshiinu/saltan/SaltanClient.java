package com.satoshiinu.saltan;

import com.satoshiinu.saltan.command.SaltanFreecamConfigCommand;
import com.satoshiinu.saltan.config.SaltanConfigManager;
import com.satoshiinu.saltan.imixin.client.ICameraGetter;
import com.satoshiinu.saltan.render.CamPointRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.function.Supplier;

public class SaltanClient implements ClientModInitializer {
	static public MinecraftClient client;
	static public String MODID = "saltan-freecam";
	static private final Identifier HUD_LAYER = Identifier.of(MODID, "overlay_hud");
	static public SaltanConfigManager configManager = new SaltanConfigManager();

    public static KeyBinding.Category SALTAN_CATEGORY = KeyBinding.Category.create(Identifier.of("key.categories.saltan"));
    static private KeyBinding keyToggleFreecam;
	static private KeyBinding keyToggleMoveCam;
	static private KeyBinding keyViewPlayerCam;
	static private KeyBinding keyGoPrevPos;
	// special future
	static private KeyBinding keyNightVision;

	static private Vec3d velocity = Vec3d.ZERO;
	static private Vec3d freecamPos=Vec3d.ZERO;
	static private Vec3d freecamPrevVisualPos = Vec3d.ZERO;
	@Nullable
	static private Vec3d freecamPrevSessionPos = null;

	static private float freecamYaw;
	static private float freecamPrevVisualYaw;
	static private float freecamPitch;
	static private float freecamPrevVisualPitch;
	static private float freecamPrevSessionPitch = Float.NaN;
	static private float freecamPrevSessionYaw = Float.NaN;
	static private boolean freecamEnabled;
	static private boolean hasFreecamEnabled;
	static private boolean freecamCamMoveLocked;
	static public CamPointRenderer camPointRenderer;

	@Override
	public void onInitializeClient() {
		client = MinecraftClient.getInstance();
		camPointRenderer = new CamPointRenderer();

		keyToggleFreecam = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.saltan.toggleFreecam", GLFW.GLFW_KEY_G, SALTAN_CATEGORY));
		keyToggleMoveCam = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.saltan.toggleMovePlayer", -1, SALTAN_CATEGORY));
		keyViewPlayerCam = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.saltan.viewPlayer", GLFW.GLFW_KEY_Y, SALTAN_CATEGORY));
		keyGoPrevPos = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.saltan.goPrevPos", GLFW.GLFW_KEY_H, SALTAN_CATEGORY));
		keyNightVision = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.saltan.night_vision", GLFW.GLFW_KEY_N, SALTAN_CATEGORY));

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext) -> SaltanFreecamConfigCommand.register(dispatcher));

		configManager.loadConfig();

		ClientTickEvents.START_CLIENT_TICK.register(minecraftClient -> {
			freecamPrevVisualPos = getVisualPos();
			freecamPrevVisualPitch = getVisualPitch();
			freecamPrevVisualYaw = getVisualYaw();

			while (keyToggleFreecam.wasPressed()){
				toggleFreecam();
			}

			if(freecamEnabled) {
				while (keyToggleMoveCam.wasPressed()) {
					toggleFreecamLock();
				}

				while (keyGoPrevPos.wasPressed()) {
					restorePrevSessionPos();
				}
			}else{
				while (keyToggleMoveCam.wasPressed()) {
				}

				while (keyGoPrevPos.wasPressed()) {
					if (freecamPrevSessionPos != null) toggleFreecam(true);
					restorePrevSessionPos();
				}
			}


			while (keyNightVision.wasPressed()) {
				toggleNightVision();
			}

			handleCamMove();
		});

		ClientLifecycleEvents.CLIENT_STARTED.register(minecraftClient -> {
			velocity = new Vec3d(0,0,0);
			freecamPrevSessionPos = null;
		});
		ClientLifecycleEvents.CLIENT_STOPPING.register(minecraftClient -> {
			freecamEnabled = false;
			freecamCamMoveLocked = false;
			hasFreecamEnabled = false;
			velocity = new Vec3d(0,0,0);
			freecamPos = null;
			freecamPrevVisualPos = null;
			freecamPrevSessionPos = null;
			freecamPitch = 0;
			freecamPrevVisualPitch = 0;
			freecamPrevSessionPitch = 0;
			freecamYaw = 0;
			freecamPrevVisualYaw = 0;
			freecamPrevSessionYaw = 0;
		});


        HudElementRegistry.addFirst(HUD_LAYER, this::renderHud);
//		HudLayerRegistrationCallback.EVENT.register(layers -> layers.attachLayerAfter(IdentifiedLayer.EXPERIENCE_LEVEL,IdentifiedLayer.of(HUD_LAYER,this::renderHud)));
//		HudRenderCallback.EVENT.register(this::renderHud);

		WorldRenderEvents.BEFORE_ENTITIES.register(this::renderWorldContent);

	}

	public static boolean toggleFreecam() {
		return toggleFreecam(!freecamEnabled);
	}
	public static boolean toggleFreecam(boolean enabled){
		if(freecamEnabled == enabled)return false;
		freecamEnabled = enabled;
		hasFreecamEnabled = true;
		if(freecamEnabled) {
			onFreecamEnabled();
		}else{
			onFreecamDisabled();
		}
		return true;
	}
	public static boolean toggleFreecamLock() {
		return toggleFreecamLock(!freecamCamMoveLocked);
	}
	public static boolean toggleFreecamLock(boolean locked){
		if(!freecamEnabled)return false;
		freecamCamMoveLocked = locked;
		if (freecamCamMoveLocked) {
			log(Text.translatable("saltan.freecam.unlocked"));
		} else {
			log(Text.translatable("saltan.freecam.locked"));
		}
		return true;
	}
	public static boolean restorePrevSessionPos(){
		if(!freecamEnabled)return false;

		if (freecamPrevSessionPos != null) {
			Vec3d tempPos = freecamPos;
			float tempPitch = freecamPitch;
			float tempYaw = freecamYaw;

			freecamPos = freecamPrevSessionPos;
			freecamPitch = freecamPrevSessionPitch;
			freecamYaw = freecamPrevSessionYaw;

			if(configManager.freecamSwapLastSession.getValue()) {
				freecamPrevSessionPos = tempPos;
				freecamPrevSessionPitch = tempPitch;
				freecamPrevSessionYaw = tempYaw;
			}

			log(Text.translatable("saltan.freecam.restore.success"));
			return true;
		} else {
			log(Text.translatable("saltan.freecam.restore.failed"));
			return false;
		}
	}

	public static boolean toggleNightVision() {
		return toggleNightVision(!configManager.nightVisionEnabled.getValue());
	}
	public static boolean toggleNightVision(boolean enabled){
		if(configManager.nightVisionEnabled.setValue(enabled)) {
			log(Text.translatable("saltan.night_vision.enabled"));
		} else {
			log(Text.translatable("saltan.night_vision.disabled"));
		}
		return true;
	}

	private static void handleCamMove(){
		Camera camera = client.gameRenderer.getCamera();
		if(velocity == null)velocity = Vec3d.ZERO;
		if(freecamPos == null)return;

		if(shouldControlAltCam()) {
			handleCamInputMove();
		}

		velocity = velocity.multiply(0.9);

		freecamPos = freecamPos.add(velocity);
	}
	private static void handleCamInputMove() {
		assert velocity != null;
		float moveSpeed = configManager.freecamMoveSpeed.getValue();
		if(client.options.sprintKey.isPressed())moveSpeed *= configManager.freecamSprintSpeedMultiplier.getValue();
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
        velocity = velocity.add(rotateMovementInput(new Vec3d(sidewaySpd,upwordSpd,forwardSpd),moveSpeed,client.gameRenderer.getCamera().getYaw()));

	}

	private static void onFreecamEnabled() {
		Camera camera = client.gameRenderer.getCamera();
		freecamPos = camera.getCameraPos();
		freecamYaw = camera.getYaw();
		freecamPitch = camera.getPitch();
		freecamPrevVisualPos = freecamPos;
		freecamPrevVisualYaw = freecamYaw;
		freecamPrevVisualPitch = freecamPitch;

		freecamCamMoveLocked = false;
		log(Text.translatable("saltan.freecam.enabled",keyViewPlayerCam.getBoundKeyLocalizedText()));
	}
	private static void onFreecamDisabled() {
		freecamPrevSessionPos = freecamPos;
		freecamPrevSessionPitch = freecamPitch;
		freecamPrevSessionYaw = freecamYaw;

		log(Text.translatable("saltan.freecam.disabled"));
	}
	private static final DecimalFormat numFormat = new DecimalFormat("##0.0");
	private static String numFormat(double number){
		return numFormat.format(number);
	}
	private void renderHud(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		TextRenderer textRenderer = client.textRenderer;
		VertexConsumerProvider.Immediate vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();

		if(freecamPos != null && configManager.freecamPosHudType.getValue().shouldRender() ){
			Text posText = Text.translatable("saltan.freecam.pos",numFormat(freecamPos.x),numFormat(freecamPos.y),numFormat(freecamPos.z));
			boolean left = configManager.freecamPosHudLeft.getValue();
			boolean top = configManager.freecamPosHudTop.getValue();
			int offsetX = configManager.freecamPosHudOffsetX.getValue();
			int offsetY = configManager.freecamPosHudOffsetY.getValue();


			renderText(drawContext,textRenderer,vertexConsumers,posText,left,top,offsetX,offsetY);
	}
	}

	public void renderText(DrawContext drawContext,TextRenderer textRenderer,VertexConsumerProvider.Immediate vertexConsumers,Text text, boolean left,boolean top,int offsetX,int offsetY){
		final int space = 9;
	 	final int padding = 2;

		int guiWidth = drawContext.getScaledWindowWidth();
		int guiHeight = drawContext.getScaledWindowHeight();
		int fontWidth = textRenderer.getWidth(text.getString());
		int drawX = left ? padding : -padding + guiWidth - fontWidth;
		int drawY = top ? padding : -padding + guiHeight - space;

		drawX += offsetX;
		drawY += offsetY;

        drawContext.drawText(textRenderer, text, drawX, drawY,-1,false);
//		textRenderer.draw(text,drawX,drawY,-1,true, drawContext.getMatrices().peek().getPositionMatrix(),vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH,0,-1);
	}

	private boolean renderWorldContent(WorldRenderContext context) {
		MinecraftClient client = MinecraftClient.getInstance();
		MatrixStack matrices = context.matrices();
		OrderedRenderCommandQueue cmdQueue = context.commandQueue();
		if(client.player == null)return true;

		matrices.push();
        Vec3d actCamPos = client.gameRenderer.getCamera().getCameraPos();
        matrices.translate(-actCamPos.x, -actCamPos.y, -actCamPos.z);

        final float halfSize = 0.3f / 2;

        if (freecamPrevSessionPos != null && configManager.freecamPointEnabled.getValue()) {
            Color color = configManager.freecamPointColor.getValue();
            camPointRenderer.renderCamPos(matrices, cmdQueue, new Box(-halfSize,-halfSize,-halfSize,halfSize,halfSize,halfSize), freecamPrevSessionPos, color);
        }

		matrices.pop();
        return true;
	}


	/**
	 * When camera locked, player can move
	 * When camera unlocked, player cannot move
	 * @return If camera is locked return true
	 */
	public static boolean isCameraLocked(){
		if(freecamEnabled)return freecamCamMoveLocked;
        else return true;// if freecam is disabled, lock camera to don't move camera
	}

	public static boolean isFreecamEnabled(){
		return freecamEnabled;
	}

	public static boolean shouldForcePlayerView(){
		return keyViewPlayerCam.isPressed();
	}
	public static boolean shouldViewAltCam(){
		return isFreecamEnabled() && !shouldForcePlayerView();
	}
	public static boolean shouldControlAltCam(){
		return shouldViewAltCam() && !freecamCamMoveLocked;
	}

	public static boolean isNightVisionEnabled(){
		return configManager.nightVisionEnabled.getValue();
	}
	public static boolean isHasFreecamEnabled(){
		return hasFreecamEnabled;
	}

	public static Vec3d getVelocity() {
		return velocity;
	}

	public static @Nullable Vec3d getFreecamPos() {
		return freecamPos;
	}

	public static @Nullable Vec3d getFreecamPrevVisualPos() {
		return freecamPrevVisualPos;
	}

	public static float getFreecamPitch() {
		return freecamPitch;
	}

	public static float getFreecamPrevVisualPitch() {
		return freecamPrevVisualPitch;
	}

	public static float getFreecamYaw() {
		return freecamYaw;
	}

	public static float getFreecamPrevVisualYaw() {
		return freecamPrevVisualYaw;
	}

	public static @Nullable Vec3d getFreecamPrevSessionPos() {
		return freecamPrevSessionPos;
	}

	public static float getFreecamPrevSessionPitch() {
		return freecamPrevSessionPitch;
	}

	public static float getFreecamPrevSessionYaw() {
		return freecamPrevSessionYaw;
	}

	protected static Vec3d rotateMovementInput(Vec3d movementInput, float speed, float yaw) {
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

	public static Vec3d getVisualPos(){
		ICameraGetter camera = (ICameraGetter)client.gameRenderer.getCamera();
		return shouldViewAltCam() ? freecamPos : camera.saltan_freecam$getOriginalPos();
	}
	public static float getVisualYaw(){
		ICameraGetter camera = (ICameraGetter)client.gameRenderer.getCamera();
		return shouldViewAltCam() ? freecamYaw : camera.saltan_freecam$getOriginalYaw();
	}
	public static float getVisualPitch(){
		ICameraGetter camera = (ICameraGetter)client.gameRenderer.getCamera();
		return shouldViewAltCam() ? freecamPitch : camera.saltan_freecam$getOriginalPitch();
	}


	@Nullable
	public static Vec3d getLerpedVisualPos(float tickDelta){
		if(freecamPrevVisualPos == null)return null;
		return freecamPrevVisualPos.lerp(getVisualPos(),tickDelta);
	}
	public static float getLerpedVisualYaw(float tickDelta){
		return MathHelper.lerp(tickDelta, freecamPrevVisualYaw, getVisualYaw());
	}
	public static float getLerpedVisualPitch(float tickDelta){
		return MathHelper.lerp(tickDelta, freecamPrevVisualPitch, getVisualPitch());
	}

	public static void changeLookDirection(double cursorDeltaX, double cursorDeltaY){
		float y = (float)cursorDeltaY * 0.15F;
		float x = (float)cursorDeltaX * 0.15F;
		freecamPitch += y;
		freecamYaw += x;
		freecamPrevVisualPitch += y;
		freecamPrevVisualYaw += x;
		if(configManager.freecamClampYaw.getValue()){
			freecamPitch = Math.clamp(freecamPitch % 360.0F, -90.0F, 90.0F);
			freecamPrevVisualPitch = Math.clamp(freecamPrevVisualPitch % 360.0F, -90.0F, 90.0F);
		}
	}

	public static boolean actionbarLogMode = true;
	public static void log(Text text){
		if(client.player == null) return;
		Text prefix = actionbarLogMode ? Text.empty() : Text.translatable("saltan.prefix");

		client.player.sendMessage(prefix.copy().append(text),actionbarLogMode);
	}

	public enum CamHudRenderType {
		NEVER(()->false),
		WHEN_ENABLED(()->isFreecamEnabled()),
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