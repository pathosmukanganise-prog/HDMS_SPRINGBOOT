package com.helpdesk.controller;
import com.helpdesk.service.TicketService;
import com.helpdesk.service.UserAccountService;
import com.helpdesk.model.Ticket;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
@Controller
public class TicketController {
private final TicketService service;
private final UserAccountService userAccountService;
private final Path uploadDir = Paths.get("uploads");
public TicketController(TicketService s, UserAccountService u){this.service=s;this.userAccountService=u;}
@GetMapping("/")
public String home(@RequestParam(required=false) String q, Model m, Authentication auth){
 m.addAttribute("tickets",service.searchTickets(q));
 m.addAttribute("username",auth.getName());
 m.addAttribute("isAdmin",auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
 m.addAttribute("assignableUsers", userAccountService.getUsernames());
 m.addAttribute("myTicketMetrics", service.getStatusMetricsForUser(auth.getName()));
 m.addAttribute("q", q);
 return "tickets";
}
@GetMapping("/track")
public String track(@RequestParam(required=false) String q, Model m, Authentication auth){
 m.addAttribute("tickets",service.searchTrackedTickets(auth.getName(), q));
 m.addAttribute("username",auth.getName());
 m.addAttribute("myTicketMetrics", service.getStatusMetricsForUser(auth.getName()));
 m.addAttribute("q", q);
 return "track-tickets";
}
@GetMapping("/new")
public String form(Model m){m.addAttribute("ticket",new Ticket());return "create-ticket";}
@GetMapping("/tickets/{id}")
public String detail(@PathVariable Long id, Model m, Authentication auth){
 Ticket ticket = service.getById(id).orElse(null);
 if(ticket == null){return "redirect:/";}
 boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
 if(!service.canAccess(ticket, auth.getName(), admin)){return "redirect:/";}
 m.addAttribute("ticket", ticket);
 m.addAttribute("comments", service.getComments(id));
 m.addAttribute("username", auth.getName());
 m.addAttribute("isAdmin", admin);
 return "ticket-detail";
}
@GetMapping("/tickets/{id}/edit")
public String editForm(@PathVariable Long id, Model m, Authentication auth){
 boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
 if(!admin){return "redirect:/";}
 Ticket ticket = service.getById(id).orElse(null);
 if(ticket == null){return "redirect:/";}
 m.addAttribute("ticket", ticket);
 m.addAttribute("assignableUsers", userAccountService.getUsernames());
 return "edit-ticket";
}
@PostMapping("/save")
public String save(@ModelAttribute Ticket t, @RequestParam("attachment") MultipartFile attachment, Authentication auth) throws IOException{
 t.setStatus("OPEN");
 t.setCreatedBy(auth.getName());
 if(!attachment.isEmpty()){
  Files.createDirectories(uploadDir);
  String originalName = StringUtils.cleanPath(attachment.getOriginalFilename());
  originalName = Paths.get(originalName).getFileName().toString();
  String storedName = UUID.randomUUID() + "-" + originalName;
  Path target = uploadDir.resolve(storedName).normalize();
  Files.copy(attachment.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
  t.setAttachmentName(originalName);
  t.setAttachmentPath(target.toString());
 }
 service.save(t);
 return "redirect:/";
}
@GetMapping("/tickets/{id}/attachment")
@ResponseBody
public ResponseEntity<Resource> attachment(@PathVariable Long id, Authentication auth) throws MalformedURLException{
 Ticket t = service.getById(id).orElseThrow();
 boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
 boolean allowed = admin || auth.getName().equals(t.getCreatedBy()) || auth.getName().equals(t.getAssignedTo());
 if(!allowed || t.getAttachmentPath() == null){return ResponseEntity.notFound().build();}
 Resource resource = new UrlResource(Paths.get(t.getAttachmentPath()).toUri());
 if(!resource.exists()){return ResponseEntity.notFound().build();}
 return ResponseEntity.ok()
  .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + t.getAttachmentName() + "\"")
  .body(resource);
}
@PostMapping("/tickets/{id}/resolve")
public String resolve(@PathVariable Long id, Authentication auth){
 boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
 service.resolve(id, auth.getName(), admin);
 return "redirect:/";
}
@PostMapping("/tickets/{id}/delete")
public String delete(@PathVariable Long id, Authentication auth){
 boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
 service.delete(id, auth.getName(), admin);
 return "redirect:/";
}
@PostMapping("/tickets/{id}/assign")
public String assign(@PathVariable Long id, @RequestParam String assignedTo, Authentication auth){
 boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
 service.assign(id, assignedTo, admin);
 return "redirect:/";
}
@PostMapping("/tickets/{id}/edit")
public String update(@PathVariable Long id,
 @RequestParam String title,
 @RequestParam String description,
 @RequestParam String status,
 @RequestParam(required=false) String assignedTo,
 Authentication auth){
 boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
 service.update(id, title, description, status, assignedTo, admin);
 return "redirect:/";
}
@PostMapping("/tickets/{id}/comments")
public String comment(@PathVariable Long id, @RequestParam String message, Authentication auth){
 boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
 service.addComment(id, auth.getName(), admin, message);
 return "redirect:/tickets/" + id;
}
}
