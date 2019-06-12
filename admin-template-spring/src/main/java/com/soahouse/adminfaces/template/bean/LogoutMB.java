package com.soahouse.adminfaces.template.bean;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.soahouse.adminfaces.template.util.Constants;


@Component("logoutMB")
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class LogoutMB {

    @Autowired
    AdminConfig adminConfig;

    public void doLogout() throws IOException {
        String loginPage = adminConfig.getLoginPage();
        if (loginPage == null || "".equals(loginPage)) {
            loginPage = Constants.DEFAULT_INDEX_PAGE;
        }
        if (!loginPage.startsWith("/")) {
            loginPage = "/" + loginPage;
        }
        if(FacesContext.getCurrentInstance().getExternalContext().getSession(false)!=null){
            //FacesContext.getCurrentInstance().getExternalContext().getSession(false).invalidate();
        }
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath() + loginPage);
    }

}
