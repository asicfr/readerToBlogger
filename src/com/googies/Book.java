
package com.googies;

import java.util.List;

public class Book{
   	private String author;
   	private String direction;
   	private String id;
   	private List items;
   	private List self;
   	private String title;
   	private Number updated;

 	public String getAuthor(){
		return this.author;
	}
	public void setAuthor(String author){
		this.author = author;
	}
 	public String getDirection(){
		return this.direction;
	}
	public void setDirection(String direction){
		this.direction = direction;
	}
 	public String getId(){
		return this.id;
	}
	public void setId(String id){
		this.id = id;
	}
 	public List getItems(){
		return this.items;
	}
	public void setItems(List items){
		this.items = items;
	}
 	public List getSelf(){
		return this.self;
	}
	public void setSelf(List self){
		this.self = self;
	}
 	public String getTitle(){
		return this.title;
	}
	public void setTitle(String title){
		this.title = title;
	}
 	public Number getUpdated(){
		return this.updated;
	}
	public void setUpdated(Number updated){
		this.updated = updated;
	}
}
