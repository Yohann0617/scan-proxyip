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
