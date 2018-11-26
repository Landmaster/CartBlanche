package landmaster.cartblanche.entity.render;

import landmaster.cartblanche.entity.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.*;

public class RenderNetherChestCart extends RenderMinecart<EntityNetherChestCart> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("netherchest:textures/model/nether_chest.png");
	
	private final ModelChest model = new ModelChest();
	
	public RenderNetherChestCart(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}
	
	@Override
	protected void renderCartContents(EntityNetherChestCart entity, float partialTicks, IBlockState p_188319_3_) {
		super.renderCartContents(entity, partialTicks, p_188319_3_);
		double x = 0.5, y = 0.5, z = -0.5;
		
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		
		this.bindTexture(TEXTURE);
		
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		
		int j = 270; // Rotation
		
		GlStateManager.rotate((float) j, 0.0F, 1.0F, 0.0F);
		model.renderAll();
		
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
