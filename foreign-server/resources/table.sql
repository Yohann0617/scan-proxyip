create table if not exists `proxy_ip`
(
    id         varchar(256) not null, --'id'
    country    varchar(64)  null,     --'国家代码'
    ip         varchar(64)  not null, --'ip地址'
    ping_value INT          null,     --'ping值'
    primary key ("id")
);--代理ip表

CREATE INDEX idx_country ON proxy_ip (country);
CREATE INDEX idx_ip ON proxy_ip (ip);