package com.example.batch.demobatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
public class CoffeeItemWriter implements ItemWriter<Coffee> {
    @Override
    public void write(List<? extends Coffee> list) throws Exception {
        log.info("Write {}", list);
    }
}
