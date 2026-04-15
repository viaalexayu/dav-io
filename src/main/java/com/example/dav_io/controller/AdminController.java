package com.example.dav_io.controller;

import com.example.dav_io.model.TimeSlot;
import com.example.dav_io.model.User;
import com.example.dav_io.repository.TimeSlotRepository;
import com.example.dav_io.repository.UserRepository;
import com.example.dav_io.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AppointmentService appointmentService;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;

    public AdminController(AppointmentService appointmentService,
                           TimeSlotRepository timeSlotRepository,
                           UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.timeSlotRepository = timeSlotRepository;
        this.userRepository = userRepository;
    }

    // Admin dashboard — all bookings + own slots
    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        model.addAttribute("appointments", appointmentService.getAllAppointments());
        model.addAttribute("slots", timeSlotRepository.findByAdminId(admin.getId()));

        return "admin";
    }

    // Accept a booking
    @PostMapping("/confirm/{id}")
    public String confirm(@PathVariable Long id) {
        appointmentService.confirmAppointment(id);
        return "redirect:/admin?confirmed";
    }

    // Reject a booking
    @PostMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return "redirect:/admin?cancelled";
    }

    // Add a new time slot (linked to logged-in admin)
    @PostMapping("/slots/add")
    public String addSlot(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate slotDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @AuthenticationPrincipal UserDetails userDetails) {

        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        List<TimeSlot> existing = timeSlotRepository.findByAdminId(admin.getId());
        for (TimeSlot s : existing) {
            if (s.getSlotDate().equals(slotDate)) {
                if (startTime.isBefore(s.getEndTime()) && endTime.isAfter(s.getStartTime())) {
                    return "redirect:/admin?overlapError";
                }
            }
        }

        TimeSlot slot = new TimeSlot();
        slot.setAdmin(admin);
        slot.setSlotDate(slotDate);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setIsAvailable(true);
        timeSlotRepository.save(slot);
        return "redirect:/admin?slotAdded";
    }

    // Delete a time slot (only if it belongs to this admin)
    @PostMapping("/slots/delete/{id}")
    public String deleteSlot(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        timeSlotRepository.findById(id).ifPresent(slot -> {
            if (slot.getAdmin().getId().equals(admin.getId())) {
                timeSlotRepository.deleteById(id);
            }
        });
        return "redirect:/admin?slotDeleted";
    }
}