package com.example.dav_io.service;

import com.example.dav_io.model.*;
import com.example.dav_io.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              TimeSlotRepository timeSlotRepository,
                              NotificationRepository notificationRepository,
                              UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Appointment bookAppointment(Long userId, Long slotId, String notes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!slot.getIsAvailable()) {
            throw new RuntimeException("Slot already booked");
        }

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setSlot(slot);
        appointment.setNotes(notes);
        appointment.setStatus("PENDING");

        slot.setIsAvailable(false);
        timeSlotRepository.save(slot);

        Appointment saved = appointmentRepository.save(appointment);
        sendNotification(user, saved, "Your booking is pending approval.");
        return saved;
    }

    @Transactional
    public void confirmAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus("CONFIRMED");
        appointmentRepository.save(appointment);
        sendNotification(appointment.getUser(), appointment,
                "Your booking on " + appointment.getSlot().getSlotDate() +
                        " at " + appointment.getSlot().getStartTime() + " has been confirmed!");
    }

    @Transactional
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus("CANCELLED");
        TimeSlot slot = appointment.getSlot();
        slot.setIsAvailable(true);
        timeSlotRepository.save(slot);
        appointmentRepository.save(appointment);
        sendNotification(appointment.getUser(), appointment,
                "Sorry, your booking on " + appointment.getSlot().getSlotDate() +
                        " has been cancelled.");
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAllByOrderByBookedAtDesc();
    }

    public List<Appointment> getUserAppointments(Long userId) {
        return appointmentRepository.findByUser_Id(userId);
    }

    private void sendNotification(User user, Appointment appointment, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setAppointment(appointment);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }
}