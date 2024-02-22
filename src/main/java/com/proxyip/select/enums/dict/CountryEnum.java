package com.proxyip.select.enums.dict;

import com.proxyip.select.enums.DictEnum;

/**
 * <p>
 * CountryEnum
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/2/22 18:35
 */
public enum CountryEnum implements DictEnum {

    US("US","美国"),
    UK("GB","英国"),
    NL("NL","荷兰"),
    DE("DE","德国"),
    HK("HK","香港"),
    SG("SG","新加坡"),
    KR("KR","韩国"),
    JP("JP","日本")

    ;

    private final String code;
    private final String desc;

    CountryEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
