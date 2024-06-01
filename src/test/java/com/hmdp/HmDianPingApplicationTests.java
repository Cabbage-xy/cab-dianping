package com.hmdp;

import com.hmdp.service.impl.ShopServiceImpl;

//ju4
//import org.junit.Test;
//ju5
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import javax.annotation.Resource;

@SpringBootTest(classes = {HmDianPingApplication.class})
class HmDianPingApplicationTests {
    @Resource
    private ShopServiceImpl shopService;

    @Test
    void testSaveShop() throws InterruptedException {
        shopService.saveShop2Redis(1L, 10L);
    }
}
