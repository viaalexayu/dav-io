package com.example.dav_io.repository;

import com.example.dav_io.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUser_Id(Long userId);
    List<Appointment> findAllByOrderByBookedAtDesc();
    List<Appointment> findBySlot_Admin_Id(Long adminId);
}