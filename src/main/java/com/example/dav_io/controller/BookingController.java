package com.example.dav_io.controller;

import com.example.dav_io.model.User;
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

    public BookingController(AppointmentService appointmentService,
                             UserRepository userRepository,
                             TimeSlotRepository timeSlotRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    // Show available slots for a specific admin
    @GetMapping("/book/{adminId}")
    public String bookPage(@PathVariable Long adminId, Model model) {
        model.addAttribute("admin", userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found")));
        model.addAttribute("slots",
                timeSlotRepository.findByAdminIdAndIsAvailableTrue(adminId));
        return "book";
    }

    // Submit booking
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

    // User sees their own bookings
    @GetMapping("/my-bookings")
    public String myBookings(@AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("appointments",
                appointmentService.getUserAppointments(user.getId()));
        return "my-bookings";
    }
}