package com.example.batch.demobatch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.batch.job.enabled=false"})
class DemoBatchApplicationTests {

    @Test
    void contextLoads() {
    }

}
