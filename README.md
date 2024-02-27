# scan-proxyip

> 一个用来定时解析代理域名A记录的服务，解析到的proxyip会根据机房位置自动添加到Cloudflare中托管的域名DNS记录中，可以配合Cloudflare的Worker实现稳定的科学上网。目前仅支持Linux环境，支持本地Java环境直接启动，也支持Docker一键部署（arm64/amd64架构）。

## Docker一键部署（推荐🏆）

参数介绍：

- `cloudflare-cfg.api-token`：Cloudflare创建的api token。
- `cloudflare-cfg.proxy-domain-prefix`：想要添加到Cloudflare中的域名前缀。
- `cloudflare-cfg.root-domain`：Cloudflare中托管的主域名。
- `cloudflare-cfg.zone-id`：Cloudflare中托管的主域名的zoneId。
- `dns-cfg.power-on-exec`：服务启动是否立即执行一次DNS记录更新任务。
- `dns-cfg.cron`：定时任务Cron表达式。
- `dns-cfg.dns-server`：DNS服务器地址。谷歌：8.8.8.8；OpenDNS：208.67.222.222或208.67.220.220；cloudflare：1.1.1.1
- `dns-cfg.proxy-domain`：代理域名。
- `dns-cfg.upload-api`：个人网盘api。（可以不配置）
- `dns-cfg.geoip-auth`：GeoIP2创建的token。（可以不配置）地址：[https://www.maxmind.com](https://www.maxmind.com) 创建License Key ，将Account ID和License key拼接成 `Account ID:License key` 并用base64编码的结果，额度1000次查询/天，不配置默认使用免费的api（精准度一般）

```bash
docker run -d --net=host --restart=always \
-e cloudflare-cfg.api-token='xxx' \
-e cloudflare-cfg.proxy-domain-prefix='proxyip' \
-e cloudflare-cfg.root-domain='abc.com' \
-e cloudflare-cfg.zone-id='xxx' \
-e dns-cfg.power-on-exec='true' \
-e dns-cfg.cron='0 30 8 * * ?' \
-e dns-cfg.dns-server='1.1.1.1' \
-e dns-cfg.proxy-domain='xxx' \
-e dns-cfg.geoip-auth='xxx' \
-e dns-cfg.upload-api='https://abc.com/api' \
--name proxyip yohannfan/yohann-proxyip:latest
```

## java -jar 直接运行

安装或更新依赖包：

```bash
yum install -y bind-utils curl epel-release \
&& yum install -y jq \
&& yum clean all
```

后台启动服务：

```bash
nohup jar -jar select-1.0.0.jar > /var/log/scan-proxyip.log &
```

