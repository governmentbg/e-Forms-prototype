package bg.bulsi.eforms.jsf.bean;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component("currentLayout")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class CurrentLayout {
	private String layout;

	@PostConstruct
	public void init() {
		setDefaultLayout();
	}

	public String getLayout() {
		return layout;
	}

	public void setHorizontalLayout() {
		layout = "/layouts/template.xhtml";
	}

	public void setDefaultLayout() {
		layout = "/layouts/template.xhtml";
	}
}
