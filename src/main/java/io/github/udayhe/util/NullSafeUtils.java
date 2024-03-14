package io.github.udayhe.util;

import lombok.experimental.UtilityClass;

import java.util.function.Function;

/**
 * @author udayhegde
 */
@UtilityClass
public class NullSafeUtils {

    public static <I, O> O nullSafeGet(I input, Function<I, O> fn, O defaultValue) {
        try {
            final var result = fn.apply(input);
            return result == null ? defaultValue : result;
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return defaultValue;
        }
    }
}
