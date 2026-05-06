package com.helpdesk.service;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.TicketComment;
import com.helpdesk.repository.TicketCommentRepository;
import com.helpdesk.repository.TicketRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class TicketService {
private final TicketRepository repo;
private final TicketCommentRepository commentRepo;
public TicketService(TicketRepository r, TicketCommentRepository c){this.repo=r;this.commentRepo=c;}
public List<Ticket> getAll(){return repo.findAll();}
public List<Ticket> getTrackedTickets(String username){return repo.findByCreatedByOrAssignedTo(username, username);}
public List<Ticket> searchTickets(String query){return filterTickets(getAll(), query);}
public List<Ticket> searchTrackedTickets(String username, String query){return filterTickets(getTrackedTickets(username), query);}
private List<Ticket> filterTickets(List<Ticket> tickets, String query){
 String cleanQuery = query == null ? "" : query.trim().toLowerCase();
 if(cleanQuery.isBlank()){return tickets;}
 return tickets.stream().filter(t -> matchesTicket(t, cleanQuery)).toList();
}
private boolean matchesTicket(Ticket t, String query){
 return contains(t.getId(), query)
  || contains(t.getTitle(), query)
  || contains(t.getDescription(), query)
  || contains(t.getCreatedBy(), query)
  || contains(t.getAssignedTo(), query)
  || contains(t.getAttachmentName(), query)
  || contains(t.getStatus(), query);
}
private boolean contains(Object value, String query){
 return value != null && value.toString().toLowerCase().contains(query);
}
public Map<String,Long> getStatusMetricsForUser(String username){
 List<Ticket> tickets = getTrackedTickets(username);
 long open = tickets.stream().filter(t -> "OPEN".equals(t.getStatus())).count();
 long resolved = tickets.stream().filter(t -> "RESOLVED".equals(t.getStatus())).count();
 Map<String,Long> metrics = new LinkedHashMap<>();
 metrics.put("total", (long) tickets.size());
 metrics.put("open", open);
 metrics.put("resolved", resolved);
 return metrics;
}
public Map<String,Long> getStatusMetrics(){
 List<Ticket> tickets = getAll();
 long open = tickets.stream().filter(t -> "OPEN".equals(t.getStatus())).count();
 long resolved = tickets.stream().filter(t -> "RESOLVED".equals(t.getStatus())).count();
 Map<String,Long> metrics = new LinkedHashMap<>();
 metrics.put("total", (long) tickets.size());
 metrics.put("open", open);
 metrics.put("resolved", resolved);
 return metrics;
}
public Map<String,Long> getCreatedByMetrics(){
 Map<String,Long> metrics = new TreeMap<>();
 for(Ticket ticket : getAll()){
  String key = ticket.getCreatedBy() == null || ticket.getCreatedBy().isBlank() ? "Unknown" : ticket.getCreatedBy();
  metrics.put(key, metrics.getOrDefault(key, 0L) + 1);
 }
 return metrics;
}
public Map<String,Long> getAssignedToMetrics(){
 Map<String,Long> metrics = new TreeMap<>();
 for(Ticket ticket : getAll()){
  String key = ticket.getAssignedTo() == null || ticket.getAssignedTo().isBlank() ? "Unassigned" : ticket.getAssignedTo();
  metrics.put(key, metrics.getOrDefault(key, 0L) + 1);
 }
 return metrics;
}
public Optional<Ticket> getById(Long id){return repo.findById(id);}
public void save(Ticket t){repo.save(t);}
public boolean canAccess(Ticket t, String username, boolean admin){
 return admin || username.equals(t.getCreatedBy()) || username.equals(t.getAssignedTo());
}
public List<TicketComment> getComments(Long ticketId){return commentRepo.findByTicketIdOrderByCreatedAtAsc(ticketId);}
public boolean addComment(Long id, String username, boolean admin, String message){
 Optional<Ticket> ticket = repo.findById(id);
 if(ticket.isEmpty()){return false;}
 if(!canAccess(ticket.get(), username, admin)){return false;}
 if(message == null || message.trim().isBlank()){return false;}
 TicketComment comment = new TicketComment();
 comment.setTicketId(id);
 comment.setAuthor(username);
 comment.setMessage(message.trim());
 comment.setCreatedAt(LocalDateTime.now());
 commentRepo.save(comment);
 return true;
}
public boolean update(Long id, String title, String description, String status, String assignedTo, boolean admin){
 if(!admin){return false;}
 Optional<Ticket> ticket = repo.findById(id);
 if(ticket.isEmpty()){return false;}
 Ticket t = ticket.get();
 t.setTitle(title);
 t.setDescription(description);
 t.setStatus("RESOLVED".equals(status) ? "RESOLVED" : "OPEN");
 t.setAssignedTo(assignedTo == null || assignedTo.isBlank() ? null : assignedTo);
 repo.save(t);
 return true;
}
public boolean resolve(Long id, String username, boolean admin){
 Optional<Ticket> ticket = repo.findById(id);
 if(ticket.isEmpty()){return false;}
 Ticket t = ticket.get();
 if(!admin && !username.equals(t.getCreatedBy())){return false;}
 t.setStatus("RESOLVED");
 repo.save(t);
 return true;
}
public boolean delete(Long id, String username, boolean admin){
 Optional<Ticket> ticket = repo.findById(id);
 if(ticket.isEmpty()){return false;}
 Ticket t = ticket.get();
 if(!admin && !username.equals(t.getCreatedBy())){return false;}
 commentRepo.deleteAll(commentRepo.findByTicketIdOrderByCreatedAtAsc(id));
 repo.delete(t);
 return true;
}
public boolean assign(Long id, String assignedTo, boolean admin){
 if(!admin){return false;}
 Optional<Ticket> ticket = repo.findById(id);
 if(ticket.isEmpty()){return false;}
 Ticket t = ticket.get();
 t.setAssignedTo(assignedTo);
 repo.save(t);
 return true;
}
}
