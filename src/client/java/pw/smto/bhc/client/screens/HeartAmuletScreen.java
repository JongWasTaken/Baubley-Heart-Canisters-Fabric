package pw.smto.bhc.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import pw.smto.bhc.common.BaubleyHeartCanisters;
import pw.smto.bhc.common.container.HeartAmuletContainer;

public class HeartAmuletScreen extends HandledScreen<HeartAmuletContainer> {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(BaubleyHeartCanisters.MOD_ID, "textures/gui/heart_amulet.png");
    public HeartAmuletScreen(HeartAmuletContainer container, PlayerInventory inventory, Text title) {
        super(container, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        context.drawTexture(BACKGROUND_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

}
