package com.example.dav_io.repository;

import com.example.dav_io.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByAdminId(Long adminId);
    List<TimeSlot> findByAdminIdAndIsAvailableTrue(Long adminId);  //
}