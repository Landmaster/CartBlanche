package landmaster.cartblanche.util;

import java.lang.invoke.*;
import java.lang.reflect.*;

import com.google.common.base.Throwables;

public class Utils {
	public static final MethodHandles.Lookup IMPL_LOOKUP;
	static {
		try {
			Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
	        field.setAccessible(true);
	        IMPL_LOOKUP = (MethodHandles.Lookup)MethodHandles.lookup().unreflectGetter(field).invokeExact();
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
}
