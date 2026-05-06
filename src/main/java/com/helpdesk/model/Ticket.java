package com.helpdesk.model;
import jakarta.persistence.*;
@Entity
public class Ticket {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;
private String title; private String description; private String status; private String createdBy; private String assignedTo; private String attachmentName; private String attachmentPath;
public Long getId(){return id;} 
public String getTitle(){return title;} public void setTitle(String t){this.title=t;}
public String getDescription(){return description;} public void setDescription(String d){this.description=d;}
public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
public String getCreatedBy(){return createdBy;} public void setCreatedBy(String c){this.createdBy=c;}
public String getAssignedTo(){return assignedTo;} public void setAssignedTo(String a){this.assignedTo=a;}
public String getAttachmentName(){return attachmentName;} public void setAttachmentName(String a){this.attachmentName=a;}
public String getAttachmentPath(){return attachmentPath;} public void setAttachmentPath(String a){this.attachmentPath=a;}
}
