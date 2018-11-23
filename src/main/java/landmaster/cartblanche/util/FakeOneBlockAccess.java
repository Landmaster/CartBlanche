package landmaster.cartblanche.util;

import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;

public class FakeOneBlockAccess implements IBlockAccess {
	private IBlockState state;
	
	public FakeOneBlockAccess(IBlockState state) {
		this.state = state;
	}
	
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return null;
	}
	
	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return 0;
	}
	
	@Override
	public IBlockState getBlockState(BlockPos pos) {
		return pos.equals(BlockPos.ORIGIN) ? this.state : Blocks.AIR.getDefaultState();
	}
	
	@Override
	public boolean isAirBlock(BlockPos pos) {
		return !pos.equals(BlockPos.ORIGIN);
	}
	
	@Override
	public Biome getBiome(BlockPos pos) {
		return Biomes.DEFAULT;
	}
	
	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return 0;
	}
	
	@Override
	public WorldType getWorldType() {
		return WorldType.DEFAULT;
	}
	
	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		return pos.equals(BlockPos.ORIGIN) ? this.state.isSideSolid(this, pos, side) : _default;
	}
	
}
