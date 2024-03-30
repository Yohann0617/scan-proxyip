package com.proxyip.select.bean.params;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.bean.params
 * @className: ProxyIpPageParams
 * @author: Yohann
 * @date: 2024/3/30 14:37
 */
public class ProxyIpPageParams {

    private String keyword;
    private long currentPage;
    private long pageSize;

    public ProxyIpPageParams() {
    }

    public ProxyIpPageParams(String keyword, long currentPage, long pageSize) {
        this.keyword = keyword;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "ProxyIpPageParams{" +
                "keyword='" + keyword + '\'' +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                '}';
    }
}
