/**
 * 
 */
package com.ft.hack.cobweb.domain;

import java.util.List;

/**
 * @author anurag.kapur
 *
 */
public class SearchResult {

	private String title;
	private String url;
	private String type = "article";
	private List<String> belongs;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getBelongs() {
		return belongs;
	}
	public void setBelongs(List<String> belongs) {
		this.belongs = belongs;
	}
}
