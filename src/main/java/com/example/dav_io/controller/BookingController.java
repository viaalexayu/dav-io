package com.example.dav_io.controller;

import com.example.dav_io.model.User;
import com.example.dav_io.repository.AppointmentRepository;
import com.example.dav_io.repository.TimeSlotRepository;
import com.example.dav_io.repository.UserRepository;
import com.example.dav_io.service.AppointmentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookingController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final AppointmentRepository appointmentRepository; // ← ADD THIS

    public BookingController(AppointmentService appointmentService,
                             UserRepository userRepository,
                             TimeSlotRepository timeSlotRepository,
                             AppointmentRepository appointmentRepository) { // ← ADD THIS
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.appointmentRepository = appointmentRepository; // ← ADD THIS
    }

    @GetMapping("/book/{adminId}")
    public String bookPage(@PathVariable Long adminId, Model model) {
        model.addAttribute("admin", userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found")));
        model.addAttribute("slots",
                timeSlotRepository.findByAdminIdAndIsAvailableTrue(adminId));
        return "book";
    }

    @PostMapping("/book")
    public String submitBooking(@RequestParam Long slotId,
                                @RequestParam(required = false) String notes,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            appointmentService.bookAppointment(user.getId(), slotId, notes);
            return "redirect:/my-bookings?success";
        } catch (RuntimeException e) {
            return "redirect:/?error=" + e.getMessage();
        }
    }

    @GetMapping("/my-bookings")
    public String myBookings(@AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // Admin sees bookings made WITH them
            model.addAttribute("appointments",
                    appointmentRepository.findBySlot_Admin_Id(user.getId()));
            model.addAttribute("isAdmin", true);
        } else {
            // User sees their own bookings
            model.addAttribute("appointments",
                    appointmentService.getUserAppointments(user.getId()));
            model.addAttribute("isAdmin", false);
        }
        return "my-bookings";
    }
}