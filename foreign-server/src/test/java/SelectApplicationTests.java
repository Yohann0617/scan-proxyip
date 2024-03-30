import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.proxyip.select.common.bean.ProxyIp;
import com.proxyip.select.common.service.IProxyIpService;
import com.proxyip.select.config.CloudflareCfg;
import com.proxyip.select.config.DnsCfg;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class SelectApplicationTests {

    @Resource
    private DnsCfg dnsCfg;
    @Resource
    private CloudflareCfg cloudflareCfg;
    @Resource
    private IProxyIpService proxyIpService;
    @Test
    void contextLoads() {

    }

}
