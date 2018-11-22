package landmaster.cartblanche.sound;

import landmaster.cartblanche.entity.*;
import net.minecraft.client.audio.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraftforge.items.*;

@SideOnly(Side.CLIENT)
public class JukeboxCartSound extends MovingSound {
	private EntityJukeboxCart cart;
	
	public JukeboxCartSound(SoundEvent soundIn, SoundCategory categoryIn, EntityJukeboxCart cart) {
		super(soundIn, categoryIn);
		this.cart = cart;
        this.repeat = true;
        this.repeatDelay = 0;
        
        //System.out.println("CREATED: "+this);
	}

	@Override
	public void update() {
		IItemHandler handler = this.cart.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		ItemStack stack = handler != null ? handler.getStackInSlot(0) : ItemStack.EMPTY;
		if (this.cart.isDead
				|| this.cart.isDisabled()
				|| stack.isEmpty()
				|| !(stack.getItem() instanceof ItemRecord)) {
			this.donePlaying = true;
			this.cart.removeSound(this);
		} else {
			this.xPosF = (float)this.cart.posX;
			this.yPosF = (float)this.cart.posY;
			this.zPosF = (float)this.cart.posZ;
		}
	}
	
}
