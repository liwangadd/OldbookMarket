package com.yunjian.util;

public class Book {
	private String bookname;
	
	private String bookstatus;

	public String getBookname() {
		return bookname;
	}

	public void setBookname(String bookname) {
		this.bookname = bookname;
	}

	public String getBookstatus() {
		return bookstatus;
	}

	public void setBookstatus(String bookstatus) {
		this.bookstatus = bookstatus;
	}
	
	public Book(String name,String status){
		bookname = name;
		bookstatus = status;
	}

}
