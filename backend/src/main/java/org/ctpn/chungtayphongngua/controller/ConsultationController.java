package org.ctpn.chungtayphongngua.controller;

import org.ctpn.chungtayphongngua.dto.response.ConsultantDTO;
import org.ctpn.chungtayphongngua.dto.response.UserProfileDTO;
import org.ctpn.chungtayphongngua.entity.*;
import org.ctpn.chungtayphongngua.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/consultations")
@CrossOrigin(origins = "*")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @GetMapping("/consultants")
    public ResponseEntity<List<ConsultantDTO>> getAllConsultants() {
        List<User> consultants = consultationService.getAllConsultants();
        List<ConsultantDTO> consultantDTOs = consultants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(consultantDTOs);
    }

    @GetMapping("/consultants/search")
    public ResponseEntity<List<ConsultantDTO>> searchConsultants(
            @RequestParam(required = false) String specialization) {

        List<User> consultants = specialization != null && !specialization.isEmpty()
                ? consultationService.searchConsultantsBySpecialization(specialization)
                : consultationService.getAllConsultants();

        List<ConsultantDTO> consultantDTOs = consultants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(consultantDTOs);
    }

    private ConsultantDTO convertToDto(User user) {
        UserProfile userProfile = user.getUserProfile();
        UserProfileDTO userProfileDTO = null;
        if (userProfile != null) {
            userProfileDTO = new UserProfileDTO(
                    userProfile.getBio(),
                    userProfile.getSpecializations(),
                    null, // Qualifications removed from UserProfile
                    userProfile.getExperienceYears(),
                    userProfile.getRating()
            );
        }
        return new ConsultantDTO(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                userProfileDTO
        );
    }

    // Other controller methods...
    @GetMapping("/consultants/{consultantId}/availability")
    public ResponseEntity<List<ConsultantAvailability>> getConsultantAvailability(
            @PathVariable Long consultantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<ConsultantAvailability> availability = consultationService.getConsultantAvailability(consultantId, startDate, endDate);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/consultants/{consultantId}/slots")
    public ResponseEntity<List<LocalTime>> getAvailableTimeSlots(
            @PathVariable Long consultantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<LocalTime> slots = consultationService.getAvailableTimeSlots(consultantId, date);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/consultants/{consultantId}/check")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long consultantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {

        boolean available = consultationService.isConsultantAvailable(consultantId, dateTime);
        return ResponseEntity.ok(available);
    }

    @PostMapping("/book")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> bookConsultation(@RequestBody BookConsultationRequest request, Authentication auth) {
        try {
            Long memberId = 1L; // This should be retrieved from the authenticated user
            Consultation consultation = consultationService.bookConsultation(
                    request.getConsultantId(), memberId, request.getScheduledAt(), request.getNotes()
            );
            return ResponseEntity.ok(consultation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{consultationId}/cancel")
    @PreAuthorize("hasRole('MEMBER') or hasRole('CONSULTANT')")
    public ResponseEntity<?> cancelConsultation(
            @PathVariable Long consultationId,
            @RequestBody CancelConsultationRequest request,
            Authentication auth) {
        try {
            Long userId = 1L; // This should be retrieved from the authenticated user
            Consultation consultation = consultationService.cancelConsultation(
                    consultationId, userId, request.getReason()
            );
            return ResponseEntity.ok(consultation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{consultationId}/complete")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> completeConsultation(
            @PathVariable Long consultationId,
            @RequestBody CompleteConsultationRequest request) {
        try {
            Consultation consultation = consultationService.completeConsultation(
                    consultationId, request.getRating(), request.getFeedback()
            );
            return ResponseEntity.ok(consultation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/my-consultations")
    @PreAuthorize("hasRole('MEMBER') or hasRole('CONSULTANT')")
    public ResponseEntity<List<Consultation>> getMyConsultations(Authentication auth) {
        Long userId = 1L; // This should be retrieved from the authenticated user
        List<Consultation> consultations = consultationService.getUserConsultations(userId);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('MEMBER') or hasRole('CONSULTANT')")
    public ResponseEntity<List<Consultation>> getUpcomingConsultations(Authentication auth) {
        Long userId = 1L; // This should be retrieved from the authenticated user
        List<Consultation> consultations = consultationService.getUpcomingConsultations(userId);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/unrated")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<Consultation>> getUnratedConsultations(Authentication auth) {
        Long memberId = 1L; // This should be retrieved from the authenticated user
        List<Consultation> consultations = consultationService.getUnratedConsultations(memberId);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/consultants/{consultantId}/stats")
    @PreAuthorize("hasRole('CONSULTANT') or hasRole('MANAGER')")
    public ResponseEntity<Object[]> getConsultantStats(@PathVariable Long consultantId) {
        Object[] stats = consultationService.getConsultantStats(consultantId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Page<Consultation>> getAllConsultations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "scheduledAt") String sort) {
        Page<Consultation> consultations = consultationService.getAllConsultations(page, size, sort);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Object[]> getSystemStats() {
        Object[] stats = consultationService.getSystemConsultationStats();
        return ResponseEntity.ok(stats);
    }

    // DTOs
    public static class BookConsultationRequest {
        private Long consultantId;
        private LocalDateTime scheduledAt;
        private String notes;
        public Long getConsultantId() { return consultantId; }
        public void setConsultantId(Long id) { this.consultantId = id; }
        public LocalDateTime getScheduledAt() { return scheduledAt; }
        public void setScheduledAt(LocalDateTime dt) { this.scheduledAt = dt; }
        public String getNotes() { return notes; }
        public void setNotes(String n) { this.notes = n; }
    }
    public static class CancelConsultationRequest {
        private String reason;
        public String getReason() { return reason; }
        public void setReason(String r) { this.reason = r; }
    }
    public static class CompleteConsultationRequest {
        private Integer rating;
        private String feedback;
        public Integer getRating() { return rating; }
        public void setRating(Integer r) { this.rating = r; }
        public String getFeedback() { return feedback; }
        public void setFeedback(String f) { this.feedback = f; }
    }
}
