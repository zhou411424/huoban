package com.example.huoban.assistant.model;

import java.io.Serializable;

/**
 * 监理Menu bean
 * 
 */
public class MenuSupervisorBean implements Serializable {

	private static final long serialVersionUID = 1892454002185132421L;

	private int tip_id;
	private String menu_title;

	public int getTip_id() {
		return tip_id;
	}

	public void setTip_id(int tip_id) {
		this.tip_id = tip_id;
	}

	public String getMenu_title() {
		return menu_title;
	}

	public void setMenu_title(String menu_title) {
		this.menu_title = menu_title;
	}

}
