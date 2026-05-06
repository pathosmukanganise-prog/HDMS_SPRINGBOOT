package com.helpdesk.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TicketComment {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;
private Long ticketId;
private String author;
@Column(length=2000)
private String message;
private LocalDateTime createdAt;
public Long getId(){return id;}
public Long getTicketId(){return ticketId;} public void setTicketId(Long t){this.ticketId=t;}
public String getAuthor(){return author;} public void setAuthor(String a){this.author=a;}
public String getMessage(){return message;} public void setMessage(String m){this.message=m;}
public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime c){this.createdAt=c;}
}
