package com.yunjian.view;

public class Node {
		public String data;
		public Node next;
		
		Node(){
			next = null;
		};
		
		Node(String s){
			data = s;
			next = null;
		}
}
