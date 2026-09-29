package com.manacommunity.homeservices.repository;

import com.manacommunity.homeservices.model.entity.HomeServiceAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository("homeServiceAttendanceRepository")
public interface HomeServiceAttendanceRepository extends JpaRepository<HomeServiceAttendanceEntity, String> {
    List<HomeServiceAttendanceEntity> findByBookingIdOrderByServiceDateDesc(String bookingId);
    List<HomeServiceAttendanceEntity> findByWorkerIdAndServiceDate(String workerId, LocalDate serviceDate);
    List<HomeServiceAttendanceEntity> findByResidentUserIdAndServiceDate(String residentUserId, LocalDate serviceDate);
    List<HomeServiceAttendanceEntity> findByResidentUserIdOrderByServiceDateDesc(String residentUserId);
    List<HomeServiceAttendanceEntity> findByWorkerIdAndServiceDateBetween(String workerId, LocalDate startDate, LocalDate endDate);
    List<HomeServiceAttendanceEntity> findByBookingIdAndServiceDateBetween(String bookingId, LocalDate startDate, LocalDate endDate);
}
