package vlove.web.test;

import org.wicketstuff.annotation.mount.MountPath;

import vlove.web.BasePage;

@MountPath(path="/test")
public class ScriptUpdatePage extends BasePage {
	public ScriptUpdatePage() {
		add(new OutputPushPanel("panel"));
	}
}