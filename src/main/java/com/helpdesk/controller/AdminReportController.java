package com.helpdesk.controller;

import com.helpdesk.model.Ticket;
import com.helpdesk.service.TicketService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {
    private final TicketService ticketService;

    public AdminReportController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public String reports(Model model) {
        model.addAttribute("statusMetrics", ticketService.getStatusMetrics());
        model.addAttribute("createdByMetrics", ticketService.getCreatedByMetrics());
        model.addAttribute("assignedToMetrics", ticketService.getAssignedToMetrics());
        model.addAttribute("tickets", ticketService.getAll());
        return "admin-reports";
    }

    @GetMapping("/tickets.csv")
    @ResponseBody
    public ResponseEntity<String> ticketsCsv() {
        StringBuilder csv = new StringBuilder("ID,Title,Created By,Assigned To,Status,Attachment\n");
        for (Ticket ticket : ticketService.getAll()) {
            csv.append(csvValue(ticket.getId()))
                    .append(',')
                    .append(csvValue(ticket.getTitle()))
                    .append(',')
                    .append(csvValue(ticket.getCreatedBy()))
                    .append(',')
                    .append(csvValue(ticket.getAssignedTo()))
                    .append(',')
                    .append(csvValue(ticket.getStatus()))
                    .append(',')
                    .append(csvValue(ticket.getAttachmentName()))
                    .append('\n');
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ticket-report-" + LocalDate.now() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString());
    }

    private String csvValue(Object value) {
        if (value == null) {
            return "";
        }
        String text = value.toString();
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
