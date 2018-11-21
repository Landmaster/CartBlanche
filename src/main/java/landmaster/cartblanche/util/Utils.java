package landmaster.cartblanche.util;

import java.lang.invoke.*;
import java.lang.reflect.*;

public class Utils {
	public static MethodHandles.Lookup getImplLookup() throws NoSuchFieldException, IllegalAccessException {
		Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
        field.setAccessible(true);
        return (MethodHandles.Lookup) field.get(null);
    }
}
