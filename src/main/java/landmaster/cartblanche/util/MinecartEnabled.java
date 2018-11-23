package landmaster.cartblanche.util;

import java.util.function.*;

import com.google.gson.*;

import landmaster.cartblanche.item.*;
import net.minecraft.util.*;
import net.minecraftforge.common.crafting.*;

public class MinecartEnabled implements IConditionFactory {
	
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		String minecart = JsonUtils.getString(json, "minecart");
		try {
			return ItemModMinecart.Type.valueOf(minecart).config;
		} catch (IllegalArgumentException e) {
			return () -> false;
		}
	}
	
}
