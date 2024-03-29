package com.proxyip.select.common.enums;

import java.util.Arrays;

/**
 * 枚举工具类
 *
 * @author libo
 */
public class EnumUtils {

    /**
     * 校验是否是合法枚举值
     *
     * @param tClass 枚举类类型 eg: NodeTypeEnum.class
     * @param code   代码
     * @return true or false
     */
    public static <T extends DictEnum> boolean isValidEnumCode(Class<T> tClass, String code) {
        return isValidEnumCode(tClass.getEnumConstants(), code);
    }

    /**
     * 校验是否是合法枚举值
     *
     * @param enums 枚举值 eg: NodeTypeEnum.values()
     * @param code  代码
     * @return true or false
     */
    public static <T extends DictEnum> boolean isValidEnumCode(T[] enums, String code) {
        if (enums.length <= 0 || code == null) {
            return false;
        }
        return Arrays.stream(enums).anyMatch(x -> code.equals(x.getCode()));
    }

    /**
     * 根据代码获取枚举
     *
     * @param enums 枚举值 eg: NodeTypeEnum.values()
     * @param code  code
     * @return 枚举对象
     */
    public static <T extends DictEnum> T getEnumByCode(T[] enums, String code) {
        if (enums.length <= 0 || code == null) {
            return null;
        }
        return Arrays.stream(enums).filter(x -> code.equals(x.getCode())).findAny().orElse(null);
    }

    /**
     * 根据代码获取枚举
     *
     * @param tClass 枚举值类型 eg: NodeTypeEnum.class
     * @param code   code
     * @return 枚举对象
     */
    public static <T extends DictEnum> T getEnumByCode(Class<T> tClass, String code) {
        return getEnumByCode(tClass.getEnumConstants(), code);
    }
}
