package com.edstem.date_time_operators.repository;

import com.edstem.date_time_operators.model.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
	List<TimeEntry> findByEmployeeIdAndStartTimeLessThanAndEndTimeGreaterThan(Long employeeId, Instant endTime, Instant startTime);

	List<TimeEntry> findByEmployeeIdAndStartTimeBetween(Long employeeId, Instant startTime, Instant endTime);
}
