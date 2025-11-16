package org.lovetropics.installer.util;

@FunctionalInterface
public interface ThrowingFunction<T, R> {

    R apply(T t) throws Exception;

}
