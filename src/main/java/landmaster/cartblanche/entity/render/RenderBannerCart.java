package landmaster.cartblanche.entity.render;

import java.util.*;

import javax.annotation.*;

import landmaster.cartblanche.entity.*;
import net.minecraft.block.state.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

public class RenderBannerCart extends RenderMinecart<EntityBannerCart> {
	private final ModelBanner bannerModel = new ModelBanner();
	
	public RenderBannerCart(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}
	
	@Override
	public void doRender(EntityBannerCart entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		this.bindEntityTexture(entity);
		long i = (long) entity.getEntityId() * 493286711L;
		i = i * i * 4392167121L + i * 98761L;
		float f = (((float) (i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f1 = (((float) (i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f2 = (((float) (i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		GlStateManager.translate(f, f1, f2);
		double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
		double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
		double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
		// double d3 = 0.30000001192092896D;
		Vec3d vec3d = entity.getPos(d0, d1, d2);
		float f3 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
		
		if (vec3d != null) {
			Vec3d vec3d1 = entity.getPosOffset(d0, d1, d2, 0.30000001192092896D);
			Vec3d vec3d2 = entity.getPosOffset(d0, d1, d2, -0.30000001192092896D);
			
			if (vec3d1 == null) {
				vec3d1 = vec3d;
			}
			
			if (vec3d2 == null) {
				vec3d2 = vec3d;
			}
			
			x += vec3d.x - d0;
			y += (vec3d1.y + vec3d2.y) / 2.0D - d1;
			z += vec3d.z - d2;
			Vec3d vec3d3 = vec3d2.add(-vec3d1.x, -vec3d1.y, -vec3d1.z);
			
			if (vec3d3.length() != 0.0D) {
				vec3d3 = vec3d3.normalize();
				entityYaw = (float) (Math.atan2(vec3d3.z, vec3d3.x) * 180.0D / Math.PI);
				f3 = (float) (Math.atan(vec3d3.y) * 73.0D);
			}
		}
		
		GlStateManager.translate((float) x, (float) y + 0.375F, (float) z);
		GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-f3, 0.0F, 0.0F, 1.0F);
		float f5 = (float) entity.getRollingAmplitude() - partialTicks;
		float f6 = entity.getDamage() - partialTicks;
		
		if (f6 < 0.0F) {
			f6 = 0.0F;
		}
		
		if (f5 > 0.0F) {
			GlStateManager.rotate(MathHelper.sin(f5) * f5 * f6 / 10.0F * (float) entity.getRollingDirection(), 1.0F,
					0.0F, 0.0F);
		}
		
		int j = entity.getDisplayTileOffset();
		
		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}
		
		IBlockState iblockstate = entity.getDisplayTile();
		
		{
			GlStateManager.pushMatrix();
			this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			// float f4 = 0.75F;
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			GlStateManager.translate(-0.5F, (float) (j - 8) / 16.0F, 0.5F);
			this.renderCartContents(entity, partialTicks, iblockstate);
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindEntityTexture(entity);
		}
		
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		// this.modelMinecart.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F,
		// 0.0625F);
		GlStateManager.popMatrix();
		
		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}
		
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	
	@Override
	protected void renderCartContents(EntityBannerCart entity, float partialTicks, IBlockState iblockstate) {
		int i = entity.getRotation();
		long j = entity.world.getTotalWorldTime();
		GlStateManager.pushMatrix();
		
		GlStateManager.translate(0.5f, 1, -0.5f);
		float f1 = (float) (i * 360) / 16.0F;
		GlStateManager.rotate(270-f1, 0.0F, 1.0F, 0.0F);
		this.bannerModel.bannerStand.showModel = true;
		
		UUID uuid = entity.getUniqueID();
		float f3 = (float) (uuid.hashCode() % 524287) + (float) j + partialTicks;
		this.bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(f3 * (float) Math.PI * 0.02F))
				* (float) Math.PI;
		GlStateManager.enableRescaleNormal();
		ResourceLocation resourcelocation = this.getBannerResourceLocation(entity);
		/*
		if (entity.ticksExisted % 20 == 0) {
			System.out.println(resourcelocation);
		}*/
		
		if (resourcelocation != null) {
			this.bindTexture(resourcelocation);
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
			this.bannerModel.renderBanner();
			GlStateManager.popMatrix();
		}
		
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}
	
	@Nullable
	private ResourceLocation getBannerResourceLocation(EntityBannerCart entity) {
		return BannerTextures.BANNER_DESIGNS.getResourceLocation(entity.getPatternResourceLocation(),
				entity.getPatternList(), entity.getColorList());
	}
}
