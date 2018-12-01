package landmaster.cartblanche.entity;

import java.util.*;

import com.google.common.collect.*;

import landmaster.cartblanche.util.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.datasync.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class EntityBannerCart extends EntityMinecart {
	private static final DataParameter<Integer> ROTATION = EntityDataManager.createKey(EntityBannerCart.class,
			DataSerializers.VARINT);
	private static final DataParameter<EnumDyeColor> BASE_COLOR = EntityDataManager.createKey(EntityBannerCart.class,
			DataSerializerEnum.DYE_COLOR);
	private static final DataParameter<NBTTagCompound[]> PATTERNS = EntityDataManager.createKey(EntityBannerCart.class,
			DataSerializerArray.COMPOUND_TAG);
	private List<BannerPattern> patternList;
	private List<EnumDyeColor> colorList;
	private String patternResourceLocation;
	
	@Override
	protected void entityInit() {
		super.entityInit();
		getDataManager().register(ROTATION, 0);
		getDataManager().register(BASE_COLOR, EnumDyeColor.BLACK);
		getDataManager().register(PATTERNS, new NBTTagCompound[0]);
	}
	
	public EntityBannerCart(World worldIn) {
		super(worldIn);
	}
	
	public EntityBannerCart(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}
	
	public EntityBannerCart setBaseColor(EnumDyeColor color) {
		getDataManager().set(BASE_COLOR, color);
		return this;
	}
	
	public EntityBannerCart setPatterns(NBTTagCompound[] patterns) {
		getDataManager().set(PATTERNS, patterns);
		return this;
	}
	
	@Override
	public Type getType() {
		return null;
	}
	
	public ItemStack getBannerItem() {
		ItemStack stack = ItemBanner.makeBanner(getDataManager().get(BASE_COLOR),
				Arrays.stream(getDataManager().get(PATTERNS)).collect(NBTTagList::new, NBTTagList::appendTag,
						(list0, list1) -> list1.forEach(list0::appendTag)));
		return stack;
	}
	
	@Override
	public void killMinecart(DamageSource source) {
		super.killMinecart(source);
		
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.entityDropItem(getBannerItem(), 0);
		}
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("Rotation", getDataManager().get(ROTATION));
		compound.setInteger("Base", getDataManager().get(BASE_COLOR).getDyeDamage());
		compound.setTag("Patterns", Arrays.stream(getDataManager().get(PATTERNS)).collect(NBTTagList::new,
				NBTTagList::appendTag, (list0, list1) -> list1.forEach(list0::appendTag)));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		getDataManager().set(ROTATION, compound.getInteger("Rotation"));
		getDataManager().set(BASE_COLOR, EnumDyeColor.byDyeDamage(compound.getInteger("Base")));
		getDataManager().set(PATTERNS, Iterables.<NBTTagCompound>toArray((Iterable) compound.getTagList("Patterns", 10),
				NBTTagCompound.class));
	}
	
	@Override
	public IBlockState getDefaultDisplayTile() {
		return Blocks.STANDING_BANNER.getDefaultState().withProperty(BlockBanner.ROTATION,
				getDataManager().get(ROTATION));
	}
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (super.processInitialInteract(player, hand))
			return true;
		if (!this.world.isRemote) {
			getDataManager().set(ROTATION, (getDataManager().get(ROTATION) + 1) % 16);
		}
		return true;
	}
	
	public int getRotation() {
		return getDataManager().get(ROTATION);
	}
	
	public List<BannerPattern> getPatternList() {
		this.initializeBannerData();
		return this.patternList;
	}
	
	public List<EnumDyeColor> getColorList() {
		this.initializeBannerData();
		return this.colorList;
	}
	
	public String getPatternResourceLocation() {
		this.initializeBannerData();
		return this.patternResourceLocation;
	}
	
	private void initializeBannerData() {
		if (this.patternList == null || this.colorList == null || this.patternResourceLocation == null) {
			this.patternList = Lists.<BannerPattern>newArrayList();
			this.colorList = Lists.<EnumDyeColor>newArrayList();
			this.patternList.add(BannerPattern.BASE);
			this.colorList.add(this.getDataManager().get(BASE_COLOR));
			this.patternResourceLocation = "b" + this.getDataManager().get(BASE_COLOR).getDyeDamage();
			
			NBTTagCompound[] patterns = this.getDataManager().get(PATTERNS);
			for (int i = 0; i < patterns.length; ++i) {
				NBTTagCompound nbttagcompound = patterns[i];
				BannerPattern bannerpattern = BannerPattern.byHash(nbttagcompound.getString("Pattern"));
				
				if (bannerpattern != null) {
					this.patternList.add(bannerpattern);
					int j = nbttagcompound.getInteger("Color");
					this.colorList.add(EnumDyeColor.byDyeDamage(j));
					this.patternResourceLocation = this.patternResourceLocation + bannerpattern.getHashname() + j;
				}
			}
		}
	}
}
