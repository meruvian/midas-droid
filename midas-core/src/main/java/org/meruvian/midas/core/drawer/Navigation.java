package org.meruvian.midas.core.drawer;

public class Navigation {
	public enum NavigationType {
		TITLE, MENU
	}
	
	private String name;
	private NavigationType type = NavigationType.MENU;
	private int image;
	
	public Navigation(String name, NavigationType type) {
		this.name = name;
		this.type = type;
	}
	
	public Navigation(String name, int image, NavigationType type) {
		this.name = name;
		this.setImage(image);
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NavigationType getType() {
		return type;
	}

	public void setType(NavigationType type) {
		this.type = type;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}
}
