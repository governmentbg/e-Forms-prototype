package com.soahouse.adminfaces.template.bean;

import static com.soahouse.adminfaces.template.util.Assert.has;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component("skinMB")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class SkinMB implements Serializable {

    private String skin;

    @Autowired
    private AdminConfig adminConfig;

    @PostConstruct
    public void init() {
        skin = adminConfig.getSkin();
        if(!has(skin)) {
            skin = "skin-blue";
        }
    }


    public void changeSkin(String skin){
        this.skin = skin;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }
}
