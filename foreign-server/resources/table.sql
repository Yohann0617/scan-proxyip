create table if not exists `proxy_ip`
(
    id          varchar(256) not null,                                   --'id'
    country     varchar(64)  null,                                       --'国家代码'
    ip          varchar(64)  not null,                                   --'ip地址'
    ping_value  INT          null,                                       --'ping值'
    create_time datetime default (datetime('now', 'localtime')) not null,-- '记录创建时间'
    primary key ("id")
);--代理ip表

CREATE INDEX idx_country ON proxy_ip (country);
CREATE INDEX idx_ip ON proxy_ip (ip);
CREATE INDEX idx_ping_value ON proxy_ip (ping_value);
CREATE INDEX idx_create_time ON proxy_ip (create_time DESC);