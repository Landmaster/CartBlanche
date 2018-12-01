package landmaster.cartblanche.util;

import java.io.*;
import java.util.function.*;

import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.network.datasync.*;

public class DataSerializerArray<T> implements DataSerializer<T[]> {
	public static final DataSerializerArray<NBTTagCompound> COMPOUND_TAG
		= new DataSerializerArray<>(DataSerializers.COMPOUND_TAG, NBTTagCompound[]::new);
	
	static {
		DataSerializers.registerSerializer(COMPOUND_TAG);
	}
	
	private final DataSerializer<T> underlying;
	private final IntFunction<T[]> newFunc;
	
	public DataSerializerArray(DataSerializer<T> underlying, IntFunction<T[]> newFunc) {
		this.underlying = underlying;
		this.newFunc = newFunc;
	}
	
	@Override
	public void write(PacketBuffer buf, T[] value) {
		buf.writeVarInt(value.length);
		for (T elem: value) {
			underlying.write(buf, elem);
		}
	}
	
	@Override
	public T[] read(PacketBuffer buf) throws IOException {
		T[] arr = newFunc.apply(buf.readVarInt());
		for (int i=0; i<arr.length; ++i) {
			arr[i] = underlying.read(buf);
		}
		return null;
	}
	
	@Override
	public DataParameter<T[]> createKey(int id) {
		return new DataParameter<>(id, this);
	}
	
	@Override
	public T[] copyValue(T[] value) {
		return value.clone();
	}
	
}
