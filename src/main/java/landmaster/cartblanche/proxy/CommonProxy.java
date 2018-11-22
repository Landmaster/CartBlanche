package landmaster.cartblanche.proxy;

import landmaster.cartblanche.entity.EntityJukeboxCart;
import net.minecraft.item.*;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class CommonProxy {
	public void initEntityRendering() {}
	public void registerItemRenderer(Item item, int meta, String id) {}
	public void registerItemRenderer(Item item, int meta, String id, String variant) {}
	public void stopSound(Object sound) {}
	public Object playJukeboxCartSound(SoundEvent soundIn, SoundCategory categoryIn, EntityJukeboxCart cart) {
		return null;
	}
	public boolean isSoundPlaying(Object sound) {
		return false;
	}
}
