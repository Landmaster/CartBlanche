package landmaster.cartblanche.entity;

import java.util.*;

import javax.annotation.*;

import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.datasync.*;
import net.minecraft.potion.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.items.*;
import net.minecraftforge.items.wrapper.*;

public class EntityBeaconCart extends EntityMinecartContainer implements ISidedInventory {
	private List<ItemStack> beaconMaterials = new ArrayList<>();
	
	private final List<TileEntityBeacon.BeamSegment> beamSegments = new ArrayList<>();
	private long beamRenderCounter;
	private float beamRenderScale;
	
	private Potion primaryEffect, secondaryEffect;
	
	private boolean isComplete;
	
	private static final DataParameter<Integer> LEVELS = EntityDataManager.createKey(EntityJukeboxCart.class, DataSerializers.VARINT);
	private static final int LEVELS_UNINIT = (int)Byte.MAX_VALUE;
	
	@Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(LEVELS, LEVELS_UNINIT);
    }
	
	public EntityBeaconCart(World worldIn) {
		super(worldIn);
		this.ignoreFrustumCheck = true;
	}
	
	public EntityBeaconCart(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
		this.ignoreFrustumCheck = true;
	}
	
	public EntityBeaconCart setBeaconMaterials(List<ItemStack> beaconMaterials) {
		this.beaconMaterials = beaconMaterials;
		return this;
	}
	
	public int getLevels() {
		if (this.getDataManager().get(LEVELS) == LEVELS_UNINIT) {
			int levels = -1;
			int len = 0;
			do {
				++levels;
				len += (2 * levels + 3) * (2 * levels + 3);
			} while (len <= beaconMaterials.size());
			this.getDataManager().set(LEVELS, Math.min(levels, 4));
		}
		return this.getDataManager().get(LEVELS);
	}
	
	@Override
	public void killMinecart(DamageSource source) {
		super.killMinecart(source);
		
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.dropItemWithOffset(Item.getItemFromBlock(Blocks.BEACON), 1, 0.0F);
			InventoryHelper.dropInventoryItems(world, this, this);
			for (ItemStack stack : beaconMaterials) {
				InventoryHelper.spawnItemStack(world, posX, posY, posZ, stack);
			}
		}
	}
	
	@Override
	public int getSizeInventory() {
		return 1;
	}
	
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerBeacon(playerInventory, this);
	}
	
	@Override
	public String getGuiID() {
		return "minecraft:beacon";
	}
	
	@Override
	public Type getType() {
		return null; // unused
	}
	
	@Override
	public IBlockState getDefaultDisplayTile() {
		return Blocks.BEACON.getDefaultState();
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.primaryEffect = TileEntityBeacon.isBeaconEffect(compound.getInteger("Primary"));
		this.secondaryEffect = TileEntityBeacon.isBeaconEffect(compound.getInteger("Secondary"));
		compound.getTagList("BeaconMaterials", 10)
				.forEach(stackNBT -> beaconMaterials.add(new ItemStack((NBTTagCompound) stackNBT)));
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("Primary", Potion.getIdFromPotion(this.primaryEffect));
		compound.setInteger("Secondary", Potion.getIdFromPotion(this.secondaryEffect));
		compound.setTag("BeaconMaterials", beaconMaterials.stream().map(ItemStack::serializeNBT)
				.collect(NBTTagList::new, NBTTagList::appendTag, (list0, list1) -> list1.forEach(list0::appendTag)));
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() != null && stack.getItem().isBeaconPayment(stack);
	}
	
	public int getField(int id) {
		switch (id) {
		case 0:
			return this.getLevels();
		case 1:
			return Potion.getIdFromPotion(this.primaryEffect);
		case 2:
			return Potion.getIdFromPotion(this.secondaryEffect);
		default:
			return 0;
		}
	}
	
	public void setField(int id, int value) {
		switch (id) {
		case 0:
			// this.levels = value;
			break;
		case 1:
			this.primaryEffect = TileEntityBeacon.isBeaconEffect(value);
			break;
		case 2:
			this.secondaryEffect = TileEntityBeacon.isBeaconEffect(value);
			break;
		}
	}
	
	public int getFieldCount() {
		return 3;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.world.getTotalWorldTime() % 80L == 0L) {
			this.updateBeacon();
		}
	}
	
	public void updateBeacon() {
		this.updateSegmentColors();
		this.addEffectsToPlayers();
	}
	
	private void addEffectsToPlayers() {
		if (this.isComplete && this.getLevels() > 0 && !this.world.isRemote && this.primaryEffect != null) {
			double d0 = (double) (this.getLevels() * 10 + 10);
			int i = 0;
			
			if (this.getLevels() >= 4 && this.primaryEffect == this.secondaryEffect) {
				i = 1;
			}
			
			int j = (9 + this.getLevels() * 2) * 20;
			double k = this.posX;
			double l = this.posY;
			double i1 = this.posZ;
			AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double) k, (double) l, (double) i1, (double) (k + 1),
					(double) (l + 1), (double) (i1 + 1))).grow(d0).expand(0.0D, (double) this.world.getHeight(), 0.0D);
			List<EntityPlayer> list = this.world.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
			
			for (EntityPlayer entityplayer : list) {
				entityplayer.addPotionEffect(new PotionEffect(this.primaryEffect, j, i, true, true));
			}
			
			if (this.getLevels() >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect != null) {
				for (EntityPlayer entityplayer1 : list) {
					entityplayer1.addPotionEffect(new PotionEffect(this.secondaryEffect, j, 0, true, true));
				}
			}
		}
	}
	
	private void updateSegmentColors() {
		double i = this.posX;
		double j = this.posY;
		double k = this.posZ;
		
		this.beamSegments.clear();
		this.isComplete = true;
		TileEntityBeacon.BeamSegment tileentitybeacon$beamsegment = new TileEntityBeacon.BeamSegment(
				EnumDyeColor.WHITE.getColorComponentValues());
		this.beamSegments.add(tileentitybeacon$beamsegment);
		boolean flag = true;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		
		for (int i1 = (int) (j + 0.5) + 1; i1 < 256; ++i1) {
			IBlockState iblockstate = this.world.getBlockState(blockpos$mutableblockpos.setPos(i, i1, k));
			float[] afloat;
			
			if (iblockstate.getBlock() == Blocks.STAINED_GLASS) {
				afloat = ((EnumDyeColor) iblockstate.getValue(BlockStainedGlass.COLOR)).getColorComponentValues();
			} else {
				if (iblockstate.getBlock() != Blocks.STAINED_GLASS_PANE) {
					if (iblockstate.getLightOpacity(world, blockpos$mutableblockpos) >= 15
							&& iblockstate.getBlock() != Blocks.BEDROCK) {
						this.isComplete = false;
						this.beamSegments.clear();
						break;
					}
					float[] customColor = iblockstate.getBlock().getBeaconColorMultiplier(iblockstate, this.world,
							blockpos$mutableblockpos, this.getPosition());
					if (customColor != null)
						afloat = customColor;
					else {
						tileentitybeacon$beamsegment.incrementHeight();
						continue;
					}
				} else
					afloat = ((EnumDyeColor) iblockstate.getValue(BlockStainedGlassPane.COLOR))
							.getColorComponentValues();
			}
			
			if (!flag) {
				afloat = new float[] { (tileentitybeacon$beamsegment.getColors()[0] + afloat[0]) / 2.0F,
						(tileentitybeacon$beamsegment.getColors()[1] + afloat[1]) / 2.0F,
						(tileentitybeacon$beamsegment.getColors()[2] + afloat[2]) / 2.0F };
			}
			
			if (Arrays.equals(afloat, tileentitybeacon$beamsegment.getColors())) {
				tileentitybeacon$beamsegment.incrementHeight();
			} else {
				tileentitybeacon$beamsegment = new TileEntityBeacon.BeamSegment(afloat);
				this.beamSegments.add(tileentitybeacon$beamsegment);
			}
			
			flag = false;
		}
		
		if (this.getLevels() == 0) {
			this.isComplete = false;
		}
	}
	
	public List<TileEntityBeacon.BeamSegment> getBeamSegments() {
		return this.beamSegments;
	}
	
	public float shouldBeamRender() {
		if (!this.isComplete) {
			return 0.0F;
		} else {
			int i = (int) (this.world.getTotalWorldTime() - this.beamRenderCounter);
			this.beamRenderCounter = this.world.getTotalWorldTime();
			
			if (i > 1) {
				this.beamRenderScale -= (float) i / 40.0F;
				
				if (this.beamRenderScale < 0.0F) {
					this.beamRenderScale = 0.0F;
				}
			}
			
			this.beamRenderScale += 0.025F;
			
			if (this.beamRenderScale > 1.0F) {
				this.beamRenderScale = 1.0F;
			}
			
			return this.beamRenderScale;
		}
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[0];
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) new SidedInvWrapper(this, facing);
		}
		return super.getCapability(capability, facing);
	}
}
