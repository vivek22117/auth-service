package com.dd.auth.api.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelInfo {

    private String local;
    private String owner;
    private String link;
    private String path;

    public ChannelInfo(String local, String owner, String link, String path) {
        this.local = local;
        this.owner = owner;
        this.link = link;
        this.path = path;
    }
}
