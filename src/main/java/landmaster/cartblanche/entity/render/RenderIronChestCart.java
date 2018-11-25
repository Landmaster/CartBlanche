package landmaster.cartblanche.entity.render;

import java.util.*;

import com.google.common.primitives.*;

import cpw.mods.ironchest.common.blocks.chest.*;
import landmaster.cartblanche.entity.*;
import net.minecraft.block.state.*;
import net.minecraft.client.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.item.*;

public class RenderIronChestCart extends RenderMinecart<EntityIronChestCart> {
	private ModelChest model;
	private Random random;
	private RenderEntityItem itemRenderer;
	
	private static final float halfPI = (float) (Math.PI / 2D);
	
	private static final float[][] shifts = { { 0.3F, 0.45F, 0.3F }, { 0.7F, 0.45F, 0.3F }, { 0.3F, 0.45F, 0.7F },
			{ 0.7F, 0.45F, 0.7F }, { 0.3F, 0.1F, 0.3F }, { 0.7F, 0.1F, 0.3F }, { 0.3F, 0.1F, 0.7F },
			{ 0.7F, 0.1F, 0.7F }, { 0.5F, 0.32F, 0.5F } };
	
	private static final EntityItem customitem = new EntityItem(null);
	
	public RenderIronChestCart(RenderManager manager) {
		super(manager);
		this.model = new ModelChest();
		this.random = new Random();
	}
	
	@Override
	public void doRender(EntityIronChestCart entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	
	@Override
	protected void renderCartContents(EntityIronChestCart entity, float partialTicks, IBlockState p_188319_3_) {
		super.renderCartContents(entity, partialTicks, p_188319_3_);
		
		//float x = 0.0f, y = 0.0f, z = 0.0f;
		
		//EnumFacing facing = EnumFacing.NORTH;
		IronChestType type = entity.getChestType();
		
		this.bindTexture(type.modelTexture);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 1, 0);
		
		GlStateManager.pushMatrix();
		
		if (type == IronChestType.CRYSTAL) {
			GlStateManager.disableCull();
		}
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		//GlStateManager.translate((float) x, (float) y + 1F, (float) z + 1F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		
		GlStateManager.rotate(270F, 0F, 1F, 0F);
		
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		
		float lidangle = 0;
		
		lidangle = 1F - lidangle;
		lidangle = 1F - lidangle * lidangle * lidangle;
		
		if (type.isTransparent()) {
			GlStateManager.scale(1F, 0.99F, 1F);
		}
		
		this.model.chestLid.rotateAngleX = -lidangle * halfPI;
		// Render the chest itself
		this.model.renderAll();
		
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		
		if (type.isTransparent() && entity.getPositionVector()
				.squareDistanceTo(Minecraft.getMinecraft().getRenderViewEntity().getPositionVector()) < 128d) {
			this.random.setSeed(254L);
			
			float shiftX;
			float shiftY;
			float shiftZ;
			int shift = 0;
			float blockScale = 0.70F;
			float timeD = (float) (360D * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL) - partialTicks;
			
			if (entity.getTopStacks().get(1).isEmpty()) {
				shift = 8;
				blockScale = 0.85F;
			}
			
			GlStateManager.pushMatrix();
			//GlStateManager.translate((float) x, (float) y, (float) z);
			GlStateManager.translate(0, 0, -1);
			
			customitem.setWorld(entity.world);
			customitem.hoverStart = 0F;
			
			for (ItemStack item : entity.getTopStacks()) {
				if (shift > shifts.length || shift > 8) {
					break;
				}
				
				if (item.isEmpty()) {
					shift++;
					continue;
				}
				
				shiftX = shifts[shift][0];
				shiftY = shifts[shift][1];
				shiftZ = shifts[shift][2];
				shift++;
				
				GlStateManager.pushMatrix();
				GlStateManager.translate(shiftX, shiftY, shiftZ);
				GlStateManager.rotate(timeD, 0F, 1F, 0F);
				GlStateManager.scale(blockScale, blockScale, blockScale);
				
				customitem.setItem(item);
				
				if (this.itemRenderer == null) {
					this.itemRenderer = new RenderEntityItem(Minecraft.getMinecraft().getRenderManager(),
							Minecraft.getMinecraft().getRenderItem()) {
						@Override
						public int getModelCount(ItemStack stack) {
							return SignedBytes.saturatedCast(Math.min(stack.getCount() / 32, 15) + 1);
						}
						
						@Override
						public boolean shouldBob() {
							return false;
						}
						
						@Override
						public boolean shouldSpreadItems() {
							return true;
						}
					};
				}
				
				this.itemRenderer.doRender(customitem, 0D, 0D, 0D, 0F, partialTicks);
				
				GlStateManager.popMatrix();
			}
			
			GlStateManager.popMatrix();
		}
    }
}
