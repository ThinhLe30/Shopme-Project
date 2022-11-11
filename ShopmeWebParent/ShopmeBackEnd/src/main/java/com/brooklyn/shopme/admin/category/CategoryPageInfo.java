package com.brooklyn.shopme.admin.category;

public class CategoryPageInfo {
	private int totalPages;
	private long totalElement;
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalElement() {
		return totalElement;
	}
	public void setTotalElement(long totalElement) {
		this.totalElement = totalElement;
	}
	public CategoryPageInfo(int totalPages, long totalElement) {
		this.totalPages = totalPages;
		this.totalElement = totalElement;
	}
	public CategoryPageInfo() {
	}
	
}
