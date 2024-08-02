[ZH](https://github.com/Yohann0617/scan-proxyip/blob/master/README_ZH.md) | [EN](https://github.com/Yohann0617/scan-proxyip/blob/master/README.md)

# scan-proxyip

> A service used to regularly parse proxy domain name A records. The resolved proxyip will be automatically added to the domain name DNS record hosted in Cloudflare according to the location of the computer room. It can cooperate with Cloudflare's Worker to achieve stable and scientific Internet access. Currently, it only supports the Linux environment, supports direct startup of the local Java environment, and also supports one-click deployment of Docker (arm64/amd64 architecture).

## Docker one-click deployment (recommended🏆)

Parameter introduction:

- `cloudflare-cfg.api-token`: api token created by Cloudflare.
- `cloudflare-cfg.proxy-domain-prefix`: The domain name prefix you want to add to Cloudflare.
- `cloudflare-cfg.root-domain`: The main domain name hosted in Cloudflare.
- `cloudflare-cfg.zone-id`: The zoneId of the main domain name hosted in Cloudflare.
- `dns-cfg.power-on-exec`: Whether to perform a DNS record update task immediately when the service starts.
- `dns-cfg.cron`: Cron expression of scheduled tasks.
- `dns-cfg.dns-server`: DNS server address. Google: 8.8.8.8; OpenDNS: 208.67.222.222 or 208.67.220.220; cloudflare: 1.1.1.1
- `dns-cfg.proxy-domain`: proxy domain name.
- `dns-cfg.upload-api`: personal network disk api. (can not be configured)
- `dns-cfg.geoip-auth`: token created by GeoIP2. (No need to configure) Address: [https://www.maxmind.com](https://www.maxmind.com) Create a License Key, splice the Account ID and License key into `Account ID:License key` and use base64 Encoding results, quota is 1000 queries/day, if not configured, the free API will be used by default (average accuracy)
```bash
# create a mount database file
mkdir -p /root/proxyip && touch /root/proxyip/scan.db
```

```bash
docker run -d --net=host --restart=always \
-v /root/proxyip/scan.db:/app/scan.db \
-e cloudflare-cfg.api-token='xxx' \
-e cloudflare-cfg.proxy-domain-prefix='proxyip' \
-e cloudflare-cfg.root-domain='abc.com' \
-e cloudflare-cfg.zone-id='xxx' \
-e dns-cfg.power-on-exec='true' \
-e dns-cfg.cron='0 30 8 * * ?' \
-e dns-cfg.dns-server='1.1.1.1' \
-e dns-cfg.geoip-auth='xxx' \
-e dns-cfg.upload-api='https://abc.com/api' \
--name proxyip yohannfan/yohann-proxyip:latest
```

## java -jar run directly

### 1. Install or update dependent packages:

```bash
yum install -y bind-utils curl epel-release \
&& yum install -y jq \
&& yum clean all
```

### 2. Modify the application.yml configuration file

Create the `application.yml` configuration file in the same directory as the jar package and modify the relevant configurations. Refer to `application.yml` in the source code.

### 3. Start the service in the background:

```bash
nohup jar -jar select-1.0.0.jar > /var/log/scan-proxyip.log &
```
## API interface

### 1. Query the country where the IP belongs

```bash
curl -X POST "Your URL/api/proxyIp/getIpInfo" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer your_token_here" \
--data '{"ip":"1.1.1.1"}'
```

## Stargazers over time

[![Stargazers over time](https://starchart.cc/Yohann0617/scan-proxyip.svg)](https://starchart.cc/Yohann0617/scan-proxyip)
