package landmaster.cartblanche.util;

import java.io.*;

import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.network.datasync.*;

public class DataSerializerEnum<T extends Enum<T>> implements DataSerializer<T> {
	public static final DataSerializerEnum<EnumDyeColor> DYE_COLOR = new DataSerializerEnum<>(EnumDyeColor.class);
	
	static {
		DataSerializers.registerSerializer(DYE_COLOR);
	}
	
	private final T[] enumValues;
	
	public DataSerializerEnum(Class<T> clazz) {
		enumValues = clazz.getEnumConstants();
	}
	
	@Override
	public void write(PacketBuffer buf, T value) {
		buf.writeVarInt(value.ordinal());
	}
	
	@Override
	public T read(PacketBuffer buf) throws IOException {
		return enumValues[buf.readVarInt()];
	}
	
	@Override
	public DataParameter<T> createKey(int id) {
		return new DataParameter<>(id, this);
	}
	
	@Override
	public T copyValue(T value) {
		return value;
	}
	
}
