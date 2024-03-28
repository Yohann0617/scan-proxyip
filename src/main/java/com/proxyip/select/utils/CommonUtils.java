package com.proxyip.select.utils;

import java.util.Collection;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.utils
 * @className: CommonUtils
 * @author: Yohann
 * @date: 2024/3/28 21:19
 */
public class CommonUtils {

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }
}
