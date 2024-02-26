package com.proxyip.select.enums.dict;

import com.proxyip.select.enums.DictEnum;

import java.util.Locale;

/**
 * <p>
 * CountryEnum
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/2/22 18:35
 */
public enum CountryEnum implements DictEnum {

    /**
     * 国家代号
     */
    US("US", "美国"),
    UK("UK", "英国"),
    //    UK("GB", "英国", "uk"),
    NL("NL", "荷兰"),
    DE("DE", "德国"),
    HK("HK", "香港"),
    SG("SG", "新加坡"),
    KR("KR", "韩国"),
    JP("JP", "日本"),

    ;

    private final String code;
    private final String desc;
    private final String lowCode;

    CountryEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
        this.lowCode = code.toLowerCase(Locale.ROOT);
    }

    CountryEnum(String code, String desc, String lowCode) {
        this.code = code;
        this.desc = desc;
        this.lowCode = lowCode;
    }

    public String getLowCode() {
        return lowCode;
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
