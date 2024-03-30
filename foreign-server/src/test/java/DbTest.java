import com.proxyip.select.SelectApplication;
import com.proxyip.select.bean.params.ProxyIpPageParams;
import com.proxyip.select.business.IProxyIpBusiness;
import com.proxyip.select.common.bean.ProxyIp;
import com.proxyip.select.common.service.IProxyIpService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @projectName: scan-proxyip
 * @package: PACKAGE_NAME
 * @className: DbTest
 * @author: Yohann
 * @date: 2024/3/30 15:48
 */
@SpringBootTest(classes = SelectApplication.class)
public class DbTest {

    @Resource
    private IProxyIpBusiness proxyIpBusiness;
    @Resource
    private IProxyIpService proxyIpService;

    @Test
    void contextLoads() {
//        ProxyIpPageParams params = new ProxyIpPageParams("", 1, 5);
//        Page<ProxyIp> page = proxyIpBusiness.pageList(params);
//        page.getRecords().forEach(System.out::println);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProxyIp> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 5);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProxyIp> proxyIpPage = proxyIpService.page(page);
        proxyIpPage.getRecords().forEach(System.out::println);
    }
}
