package com.dd.auth.api.controller;

import com.dd.auth.api.entity.ChannelInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RefreshScope
public class AuthController {

    @Value("${channel.local: No Value}")
    private String local;

    @Value("${channel.owner: No Owner}")
    private String owner;

    @Value("${channel.link: No link}")
    private String link;

    @Value("${spring.h2.console.path: No path}")
    private String path;

    @GetMapping("/data-info")
    public ChannelInfo getDataInfo() {
        return new ChannelInfo(local, owner, link, path);
    }
}
