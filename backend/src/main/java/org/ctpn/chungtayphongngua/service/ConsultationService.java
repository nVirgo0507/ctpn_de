package org.ctpn.chungtayphongngua.service;

import org.ctpn.chungtayphongngua.entity.*;
import org.ctpn.chungtayphongngua.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Consultation Service
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Business logic for Consultation Booking System per Document FR-013
 */
@Service
@Transactional
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private ConsultantAvailabilityRepository availabilityRepository;

    @Autowired
    private ConsultantAvailabilityExceptionRepository exceptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // Consultant Management Methods

    /**
     * Get all consultants with their profiles
     * FR-013: Consultant profile search implementation
     */
    public List<User> getAllConsultants() {
        return userRepository.findConsultantsWithProfile();
    }

    /**
     * Search consultants by specialization
     * FR-013: Filtering by specialization requirement
     */
    public List<User> searchConsultantsBySpecialization(String specialization) {
        return userRepository.findConsultantsWithProfile().stream()
                .filter(user -> user.getUserProfile() != null
                        && user.getUserProfile().getSpecializations().contains(specialization))
                .collect(Collectors.toList());
    }

    /**
     * Get consultant availability for a specific week
     * FR-013: Real-time availability calendar display
     */
    public List<ConsultantAvailability> getConsultantAvailability(Long consultantId, LocalDate startDate,
            LocalDate endDate) {
        return availabilityRepository.findByUserUserIdOrderByDayOfWeekAscStartTimeAsc(consultantId);
    }

    /**
     * Check if consultant is available at specific date and time
     * FR-013: Real-time availability checking
     */
    public boolean isConsultantAvailable(Long consultantId, LocalDateTime proposedDateTime) {
        LocalDate date = proposedDateTime.toLocalDate();
        LocalTime time = proposedDateTime.toLocalTime();
        int dayOfWeek = date.getDayOfWeek().getValue() % 7; // Convert to 0=Sunday format

        // Check regular availability
        Optional<ConsultantAvailability> availability = availabilityRepository
                .findConsultantAvailabilityAtTime(consultantId, dayOfWeek, time);

        if (!availability.isPresent()) {
            return false; // No regular availability
        }

        // Check for exceptions
        Optional<ConsultantAvailabilityException> exception = exceptionRepository
                .findExceptionAtDateTime(consultantId, date, time);

        if (exception.isPresent()) {
            return false; // Has exception (unavailable or busy)
        }

        // Check for existing bookings
        LocalDateTime endTime = proposedDateTime.plusMinutes(60); // Default 1-hour duration
        List<Consultation> conflicts = consultationRepository
                .findConflictingConsultations(consultantId, proposedDateTime, endTime);

        return conflicts.isEmpty();
    }

    /**
     * Get available time slots for consultant on specific date
     * FR-013: Available time slots display
     */
    public List<LocalTime> getAvailableTimeSlots(Long consultantId, LocalDate date) {
        List<LocalTime> availableSlots = new ArrayList<>();
        int dayOfWeek = date.getDayOfWeek().getValue() % 7;

        // Get regular availability for this day
        List<ConsultantAvailability> dailyAvailability = availabilityRepository
                .findByUserUserIdAndDayOfWeekAndIsAvailableTrueOrderByStartTimeAsc(consultantId, dayOfWeek);

        for (ConsultantAvailability availability : dailyAvailability) {
            LocalTime currentSlot = availability.getStartTime();
            LocalTime endTime = availability.getEndTime();

            // Generate 1-hour slots
            while (currentSlot.plusHours(1).isBefore(endTime) || currentSlot.plusHours(1).equals(endTime)) {
                LocalDateTime proposedDateTime = LocalDateTime.of(date, currentSlot);

                if (isConsultantAvailable(consultantId, proposedDateTime)) {
                    availableSlots.add(currentSlot);
                }

                currentSlot = currentSlot.plusHours(1);
            }
        }

        return availableSlots;
    }

    // Booking Management Methods

    /**
     * Book a consultation
     * FR-013: Booking confirmation with Google Meet link generation
     */
    public Consultation bookConsultation(Long consultantId, Long memberId, LocalDateTime scheduledAt, String notes) {
        // Validate consultant exists and is a consultant
        Optional<User> consultant = userRepository.findById(consultantId);
        if (!consultant.isPresent()) {
            throw new RuntimeException("Tư vấn viên không tồn tại");
        }

        // Validate member exists
        Optional<User> member = userRepository.findById(memberId);
        if (!member.isPresent()) {
            throw new RuntimeException("Thành viên không tồn tại");
        }

        // Check availability
        if (!isConsultantAvailable(consultantId, scheduledAt)) {
            throw new RuntimeException("Tư vấn viên không có sẵn trong thời gian này");
        }

        // Validate scheduling rules (e.g., no past dates, advance booking requirements)
        if (scheduledAt.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new RuntimeException("Phải đặt lịch trước ít nhất 1 giờ");
        }

        // Create consultation
        Consultation consultation = new Consultation(consultant.get(), member.get(), scheduledAt);
        consultation.setNotes(notes);

        // Generate Google Meet link placeholder (to be integrated with Google Calendar
        // API)
        consultation.setGoogleMeetLink("https://meet.google.com/placeholder-" + System.currentTimeMillis());

        return consultationRepository.save(consultation);
    }

    /**
     * Cancel consultation
     * FR-013: Cancellation policy enforcement (24-hour notice)
     */
    public Consultation cancelConsultation(Long consultationId, Long userId, String reason) {
        Optional<Consultation> consultationOpt = consultationRepository.findById(consultationId);
        if (!consultationOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy lịch tư vấn");
        }

        Consultation consultation = consultationOpt.get();

        // Check if user has permission to cancel
        if (!consultation.getConsultant().getUserId().equals(userId) &&
                !consultation.getMember().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy lịch tư vấn này");
        }

        // Check 24-hour notice requirement
        if (consultation.getScheduledAt().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new RuntimeException("Phải hủy trước ít nhất 24 giờ");
        }

        consultation.setStatus("cancelled");
        consultation.setNotes(consultation.getNotes() + "\nLý do hủy: " + reason);

        return consultationRepository.save(consultation);
    }

    /**
     * Complete consultation and submit rating
     * FR-013: Consultation rating system post-session completion
     */
    public Consultation completeConsultation(Long consultationId, Integer rating, String feedback) {
        Optional<Consultation> consultationOpt = consultationRepository.findById(consultationId);
        if (!consultationOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy lịch tư vấn");
        }

        Consultation consultation = consultationOpt.get();

        if (!"scheduled".equals(consultation.getStatus())) {
            throw new RuntimeException("Chỉ có thể hoàn thành các cuộc tư vấn đã được lên lịch");
        }

        consultation.setStatus("completed");
        consultation.setCompletedAt(LocalDateTime.now());
        consultation.setRating(rating);
        consultation.setFeedback(feedback);

        return consultationRepository.save(consultation);
    }

    // Query Methods

    /**
     * Get user's consultations
     */
    public List<Consultation> getUserConsultations(Long userId) {
        List<Consultation> memberConsultations = consultationRepository
                .findByMemberUserIdOrderByScheduledAtDesc(userId);
        List<Consultation> consultantConsultations = consultationRepository
                .findByConsultantUserIdOrderByScheduledAtDesc(userId);

        List<Consultation> allConsultations = new ArrayList<>();
        allConsultations.addAll(memberConsultations);
        allConsultations.addAll(consultantConsultations);

        return allConsultations;
    }

    /**
     * Get upcoming consultations for user
     */
    public List<Consultation> getUpcomingConsultations(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Consultation> memberUpcoming = consultationRepository.findUpcomingConsultationsForMember(userId, now);
        List<Consultation> consultantUpcoming = consultationRepository.findUpcomingConsultationsForConsultant(userId,
                now);

        List<Consultation> allUpcoming = new ArrayList<>();
        allUpcoming.addAll(memberUpcoming);
        allUpcoming.addAll(consultantUpcoming);

        return allUpcoming;
    }

    /**
     * Get consultations needing rating
     */
    public List<Consultation> getUnratedConsultations(Long memberId) {
        return consultationRepository.findUnratedCompletedConsultations(memberId);
    }

    /**
     * Get consultant statistics
     */
    public Object[] getConsultantStats(Long consultantId) {
        return consultationRepository.getConsultantStats(consultantId);
    }

    // Admin Methods

    /**
     * Get all consultations with pagination
     */
    public Page<Consultation> getAllConsultations(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return consultationRepository.findAll(pageable);
    }

    /**
     * Get consultation statistics
     */
    public Object[] getSystemConsultationStats() {
        long scheduled = consultationRepository.countByStatus("scheduled");
        long completed = consultationRepository.countByStatus("completed");
        long cancelled = consultationRepository.countByStatus("cancelled");

        return new Object[] { scheduled, completed, cancelled };
    }

    /**
     * Seed default availability for all consultants
     * Default: Mon-Fri, 09:00 - 17:00
     */
    public void seedAvailabilityForConsultants() {
        seedConsultants(); // Ensure consultants exist
        List<User> consultants = userRepository.findConsultantsWithProfile();

        for (User consultant : consultants) {
            // Check if consultant already has availability
            List<ConsultantAvailability> existing = availabilityRepository
                    .findByUserUserIdOrderByDayOfWeekAscStartTimeAsc(consultant.getUserId());

            if (existing.isEmpty()) {
                // Add default availability: Mon(1) to Fri(5)
                for (int day = 1; day <= 5; day++) {
                    ConsultantAvailability morning = new ConsultantAvailability(
                            consultant, day, LocalTime.of(9, 0), LocalTime.of(12, 0));
                    availabilityRepository.save(morning);

                    ConsultantAvailability afternoon = new ConsultantAvailability(
                            consultant, day, LocalTime.of(13, 30), LocalTime.of(17, 0));
                    availabilityRepository.save(afternoon);
                }
            }
        }
    }

    /**
     * Seed dummy consultants if none exist
     */
    public void seedConsultants() {
        if (!userRepository.findConsultantsWithProfile().isEmpty()) {
            return;
        }

        Role consultantRole = roleRepository.findByRoleName("CONSULTANT")
                .orElseThrow(() -> new RuntimeException("Role CONSULTANT not found"));

        // Create 3 dummy consultants
        createDummyConsultant(
                "Dr. Nguyen Van A", "nguyenvana@ctpn.org", "0901234567",
                "Chuyên gia tâm lý với 10 năm kinh nghiệm.",
                Arrays.asList("Tư vấn nghiện chất", "Tâm lý học đường"),
                10, 4.8, consultantRole);

        createDummyConsultant(
                "Ms. Tran Thi B", "tranthib@ctpn.org", "0902345678",
                "Thạc sĩ công tác xã hội, chuyên về trị liệu gia đình.",
                Arrays.asList("Trị liệu gia đình", "Hỗ trợ thanh thiếu niên"),
                8, 4.9, consultantRole);

        createDummyConsultant(
                "Mr. Le Van C", "levanc@ctpn.org", "0903456789",
                "Chuyên gia tư vấn phục hồi chức năng và tái hòa nhập cộng đồng.",
                Arrays.asList("Phục hồi chức năng", "Can thiệp khủng hoảng"),
                12, 4.7, consultantRole);
    }

    private void createDummyConsultant(String name, String email, String phone, String bio,
            List<String> specializations, int experience, double rating, Role role) {
        if (userRepository.existsByEmail(email))
            return;

        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setIsVerified(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Add Role
        UserRole userRole = new UserRole(user, role);
        user.getUserRoles().add(userRole);

        // Add Profile
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setBio(bio);
        profile.setExperienceYears(experience);
        profile.setRating(rating);
        profile.setTotalReviews(10 + (int) (Math.random() * 50));
        profile.setTotalSessions(50 + (int) (Math.random() * 100));
        profile.setConsultant(true);

        // Add Specializations
        for (String specName : specializations) {
            profile.getSpecializations().add(specName);
        }

        user.setUserProfile(profile);
        userRepository.save(user);
    }
}