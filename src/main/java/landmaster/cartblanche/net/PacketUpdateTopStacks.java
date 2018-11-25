package landmaster.cartblanche.net;

import java.util.*;

import io.netty.buffer.*;
import landmaster.cartblanche.entity.*;
import net.minecraft.client.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PacketUpdateTopStacks implements IMessage {
	private int entityId;
	private List<ItemStack> list;
	
	public PacketUpdateTopStacks() {
		list = NonNullList.create();
	}
	
	public static IMessage onMessage(PacketUpdateTopStacks pkt, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Entity ent = Minecraft.getMinecraft().world.getEntityByID(pkt.entityId);
			if (ent instanceof EntityIronChestCart) {
				((EntityIronChestCart)ent).setTopStacks(pkt.list);
			}
		});
		return null;
	}
	
	public PacketUpdateTopStacks(int entityId, List<ItemStack> list) {
		this.entityId = entityId;
		this.list = list;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityId = buf.readInt();
		this.list = NonNullList.withSize(buf.readInt(), ItemStack.EMPTY);
		for (int i=0; i<this.list.size(); ++i) {
			this.list.set(i, ByteBufUtils.readItemStack(buf));
		}
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeInt(this.list.size());
		for (ItemStack stack: this.list) {
			ByteBufUtils.writeItemStack(buf, stack);
		}
	}
	
}
