package com.proxyip.select;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "continent",
        "country",
        "registered_country",
        "traits"
})
public class GeoIPResponse {

    @JsonProperty("continent")
    private Continent continent;
    @JsonProperty("country")
    private Country country;
    @JsonProperty("registered_country")
    private RegisteredCountry registeredCountry;
    @JsonProperty("traits")
    private Traits traits;

    @JsonProperty("continent")
    public Continent getContinent() {
        return continent;
    }

    @JsonProperty("continent")
    public void setContinent(Continent continent) {
        this.continent = continent;
    }

    @JsonProperty("country")
    public Country getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(Country country) {
        this.country = country;
    }

    @JsonProperty("registered_country")
    public RegisteredCountry getRegisteredCountry() {
        return registeredCountry;
    }

    @JsonProperty("registered_country")
    public void setRegisteredCountry(RegisteredCountry registeredCountry) {
        this.registeredCountry = registeredCountry;
    }

    @JsonProperty("traits")
    public Traits getTraits() {
        return traits;
    }

    @JsonProperty("traits")
    public void setTraits(Traits traits) {
        this.traits = traits;
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "code",
        "geoname_id",
        "names"
})
class Continent {

    @JsonProperty("code")
    private String code;
    @JsonProperty("geoname_id")
    private Integer geonameId;
    @JsonProperty("names")
    private Names names;

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("geoname_id")
    public Integer getGeonameId() {
        return geonameId;
    }

    @JsonProperty("geoname_id")
    public void setGeonameId(Integer geonameId) {
        this.geonameId = geonameId;
    }

    @JsonProperty("names")
    public Names getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(Names names) {
        this.names = names;
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ja",
        "pt-BR",
        "ru",
        "zh-CN",
        "de",
        "en",
        "es",
        "fr"
})

class Names {

    @JsonProperty("ja")
    private String ja;
    @JsonProperty("pt-BR")
    private String ptBR;
    @JsonProperty("ru")
    private String ru;
    @JsonProperty("zh-CN")
    private String zhCN;
    @JsonProperty("de")
    private String de;
    @JsonProperty("en")
    private String en;
    @JsonProperty("es")
    private String es;
    @JsonProperty("fr")
    private String fr;

    @JsonProperty("ja")
    public String getJa() {
        return ja;
    }

    @JsonProperty("ja")
    public void setJa(String ja) {
        this.ja = ja;
    }

    @JsonProperty("pt-BR")
    public String getPtBR() {
        return ptBR;
    }

    @JsonProperty("pt-BR")
    public void setPtBR(String ptBR) {
        this.ptBR = ptBR;
    }

    @JsonProperty("ru")
    public String getRu() {
        return ru;
    }

    @JsonProperty("ru")
    public void setRu(String ru) {
        this.ru = ru;
    }

    @JsonProperty("zh-CN")
    public String getZhCN() {
        return zhCN;
    }

    @JsonProperty("zh-CN")
    public void setZhCN(String zhCN) {
        this.zhCN = zhCN;
    }

    @JsonProperty("de")
    public String getDe() {
        return de;
    }

    @JsonProperty("de")
    public void setDe(String de) {
        this.de = de;
    }

    @JsonProperty("en")
    public String getEn() {
        return en;
    }

    @JsonProperty("en")
    public void setEn(String en) {
        this.en = en;
    }

    @JsonProperty("es")
    public String getEs() {
        return es;
    }

    @JsonProperty("es")
    public void setEs(String es) {
        this.es = es;
    }

    @JsonProperty("fr")
    public String getFr() {
        return fr;
    }

    @JsonProperty("fr")
    public void setFr(String fr) {
        this.fr = fr;
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "iso_code",
        "geoname_id",
        "names"
})
class Country {

    @JsonProperty("iso_code")
    private String isoCode;
    @JsonProperty("geoname_id")
    private Integer geonameId;
    @JsonProperty("names")
    private Names_ names;

    @JsonProperty("iso_code")
    public String getIsoCode() {
        return isoCode;
    }

    @JsonProperty("iso_code")
    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    @JsonProperty("geoname_id")
    public Integer getGeonameId() {
        return geonameId;
    }

    @JsonProperty("geoname_id")
    public void setGeonameId(Integer geonameId) {
        this.geonameId = geonameId;
    }

    @JsonProperty("names")
    public Names_ getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(Names_ names) {
        this.names = names;
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ja",
        "pt-BR",
        "ru",
        "zh-CN",
        "de",
        "en",
        "es",
        "fr"
})
class Names_ {

    @JsonProperty("ja")
    private String ja;
    @JsonProperty("pt-BR")
    private String ptBR;
    @JsonProperty("ru")
    private String ru;
    @JsonProperty("zh-CN")
    private String zhCN;
    @JsonProperty("de")
    private String de;
    @JsonProperty("en")
    private String en;
    @JsonProperty("es")
    private String es;
    @JsonProperty("fr")
    private String fr;

    @JsonProperty("ja")
    public String getJa() {
        return ja;
    }

    @JsonProperty("ja")
    public void setJa(String ja) {
        this.ja = ja;
    }

    @JsonProperty("pt-BR")
    public String getPtBR() {
        return ptBR;
    }

    @JsonProperty("pt-BR")
    public void setPtBR(String ptBR) {
        this.ptBR = ptBR;
    }

    @JsonProperty("ru")
    public String getRu() {
        return ru;
    }

    @JsonProperty("ru")
    public void setRu(String ru) {
        this.ru = ru;
    }

    @JsonProperty("zh-CN")
    public String getZhCN() {
        return zhCN;
    }

    @JsonProperty("zh-CN")
    public void setZhCN(String zhCN) {
        this.zhCN = zhCN;
    }

    @JsonProperty("de")
    public String getDe() {
        return de;
    }

    @JsonProperty("de")
    public void setDe(String de) {
        this.de = de;
    }

    @JsonProperty("en")
    public String getEn() {
        return en;
    }

    @JsonProperty("en")
    public void setEn(String en) {
        this.en = en;
    }

    @JsonProperty("es")
    public String getEs() {
        return es;
    }

    @JsonProperty("es")
    public void setEs(String es) {
        this.es = es;
    }

    @JsonProperty("fr")
    public String getFr() {
        return fr;
    }

    @JsonProperty("fr")
    public void setFr(String fr) {
        this.fr = fr;
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "iso_code",
        "geoname_id",
        "names"
})
class RegisteredCountry {

    @JsonProperty("iso_code")
    private String isoCode;
    @JsonProperty("geoname_id")
    private Integer geonameId;
    @JsonProperty("names")
    private Names__ names;

    @JsonProperty("iso_code")
    public String getIsoCode() {
        return isoCode;
    }

    @JsonProperty("iso_code")
    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    @JsonProperty("geoname_id")
    public Integer getGeonameId() {
        return geonameId;
    }

    @JsonProperty("geoname_id")
    public void setGeonameId(Integer geonameId) {
        this.geonameId = geonameId;
    }

    @JsonProperty("names")
    public Names__ getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(Names__ names) {
        this.names = names;
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "en",
        "es",
        "fr",
        "ja",
        "pt-BR",
        "ru",
        "zh-CN",
        "de"
})
class Names__ {

    @JsonProperty("en")
    private String en;
    @JsonProperty("es")
    private String es;
    @JsonProperty("fr")
    private String fr;
    @JsonProperty("ja")
    private String ja;
    @JsonProperty("pt-BR")
    private String ptBR;
    @JsonProperty("ru")
    private String ru;
    @JsonProperty("zh-CN")
    private String zhCN;
    @JsonProperty("de")
    private String de;

    @JsonProperty("en")
    public String getEn() {
        return en;
    }

    @JsonProperty("en")
    public void setEn(String en) {
        this.en = en;
    }

    @JsonProperty("es")
    public String getEs() {
        return es;
    }

    @JsonProperty("es")
    public void setEs(String es) {
        this.es = es;
    }

    @JsonProperty("fr")
    public String getFr() {
        return fr;
    }

    @JsonProperty("fr")
    public void setFr(String fr) {
        this.fr = fr;
    }

    @JsonProperty("ja")
    public String getJa() {
        return ja;
    }

    @JsonProperty("ja")
    public void setJa(String ja) {
        this.ja = ja;
    }

    @JsonProperty("pt-BR")
    public String getPtBR() {
        return ptBR;
    }

    @JsonProperty("pt-BR")
    public void setPtBR(String ptBR) {
        this.ptBR = ptBR;
    }

    @JsonProperty("ru")
    public String getRu() {
        return ru;
    }

    @JsonProperty("ru")
    public void setRu(String ru) {
        this.ru = ru;
    }

    @JsonProperty("zh-CN")
    public String getZhCN() {
        return zhCN;
    }

    @JsonProperty("zh-CN")
    public void setZhCN(String zhCN) {
        this.zhCN = zhCN;
    }

    @JsonProperty("de")
    public String getDe() {
        return de;
    }

    @JsonProperty("de")
    public void setDe(String de) {
        this.de = de;
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ip_address",
        "network"
})
class Traits {

    @JsonProperty("ip_address")
    private String ipAddress;
    @JsonProperty("network")
    private String network;

    @JsonProperty("ip_address")
    public String getIpAddress() {
        return ipAddress;
    }

    @JsonProperty("ip_address")
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @JsonProperty("network")
    public String getNetwork() {
        return network;
    }

    @JsonProperty("network")
    public void setNetwork(String network) {
        this.network = network;
    }
}
