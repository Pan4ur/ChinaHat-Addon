package thunder.addon.chinahat.modules;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import thunder.hack.core.Managers;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.impl.SettingGroup;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ChinaHat extends Module {
    public ChinaHat() {
        super("ChinaHat", Category.RENDER);
    }

    private final Setting<Float> radius = new Setting<>("Radius", 0.55f, 0.1f, 1f);
    private final Setting<ColorMode> colorMode = new Setting<>("ColorMode", ColorMode.Sync);
    private final Setting<ColorSetting> color = new Setting<>("ColorMode", new ColorSetting(Color.GRAY.getRGB()), v -> colorMode.is(ColorMode.Custom));
    private final Setting<SettingGroup> selection = new Setting<>("Selection", new SettingGroup(false, 0));
    private final Setting<Boolean> self = new Setting<>("Self", true).addToGroup(selection);
    private final Setting<Boolean> friends = new Setting<>("Friends", true).addToGroup(selection);
    private final Setting<Boolean> others = new Setting<>("Others", true).addToGroup(selection);

    private enum ColorMode {
        Sync, Custom
    }

    public void onRender3D(MatrixStack stack) {
        for (PlayerEntity pl : Managers.ASYNC.getAsyncPlayers()) {
            if (shouldSkip(pl))
                continue;

            float offsetY = mc.player.getInventory().getStack(39).isEmpty() ? 0.2f : 0.26f;

            float lineX = (float) (pl.prevX + (pl.getX() - pl.prevX) * Render3DEngine.getTickDelta());
            float lineY = (float) (pl.prevY + (pl.getY() - pl.prevY) * Render3DEngine.getTickDelta() + pl.getEyeHeight(pl.getPose()) + offsetY);
            float lineZ = (float) (pl.prevZ + (pl.getZ() - pl.prevZ) * Render3DEngine.getTickDelta());

            float x = lineX - (float) mc.getEntityRenderDispatcher().camera.getPos().getX();
            float y = lineY - (float) mc.getEntityRenderDispatcher().camera.getPos().getY();
            float z = lineZ - (float) mc.getEntityRenderDispatcher().camera.getPos().getZ();

            stack.push();
            RenderSystem.disableCull();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);


            stack.translate(x, y - 0.5f, z);
            stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(pl.getYaw()));
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pl.getPitch()));
            stack.translate(-x, -(y - 0.5f), -z);


            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

            for (int i = 0; i <= 32; i++) {
                int c = colorMode.is(ColorMode.Sync) ? Render2DEngine.applyOpacity(HudEditor.getColor(i * 12).getRGB(), 0.7f) : color.getValue().getColor();
                bufferBuilder.vertex(stack.peek().getPositionMatrix(), x, y + 0.2f, z).color(c);

                float x2 = (float) (x - Math.sin(i * Math.PI * 2f / 32f) * radius.getValue());
                float z2 = (float) (z + Math.cos(i * Math.PI * 2f / 32f) * radius.getValue());

                bufferBuilder.vertex(stack.peek().getPositionMatrix(), x2, y, z2).color(c);
            }

            Render2DEngine.endBuilding(bufferBuilder);
            Render3DEngine.endRender();
            RenderSystem.enableCull();
            RenderSystem.disableDepthTest();
            stack.pop();
        }
    }

    private boolean shouldSkip(PlayerEntity pl) {
        if (pl == mc.player) {
            if (mc.options.getPerspective() == Perspective.FIRST_PERSON)
                return true;

            return !self.getValue();
        }

        if (Managers.FRIEND.isFriend(pl))
            return !friends.getValue();

        return !others.getValue();
    }
}
