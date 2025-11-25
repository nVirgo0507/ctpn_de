package org.ctpn.chungtayphongngua;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("org.ctpn.chungtayphongngua.entity")
@EnableJpaRepositories("org.ctpn.chungtayphongngua.repository")
public class ChungTayPhongNguaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChungTayPhongNguaApplication.class, args);
    }
} 