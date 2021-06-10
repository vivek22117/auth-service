package com.dd.auth.api.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class AppInfoIndicator implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("Application", "auth-service");
        builder.withDetail("Author", "Vivek Mishra");
        builder.withDetail("Version", "v1");
        builder.withDetail("Description", "Application to authenticate and authorize request!");
        builder.build();
    }
}
