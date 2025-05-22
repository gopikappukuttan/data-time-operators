package com.edstem.date_time_operators.service;

import com.edstem.date_time_operators.contract.TimeEntryRequest;
import com.edstem.date_time_operators.contract.TimeEntryResponse;
import com.edstem.date_time_operators.model.TimeEntry;
import com.edstem.date_time_operators.repository.TimeEntryRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeEntryService {
	private final TimeEntryRepository timeEntryRepository;

	public TimeEntryService(TimeEntryRepository timeEntryRepository) {
		this.timeEntryRepository = timeEntryRepository;
	}

	public TimeEntryResponse createTimeEntry(TimeEntryRequest request) {
		Instant startTime = request.getStartTime().toInstant();
		Instant endTime = request.getEndTime().toInstant();
		if (endTime.isBefore(startTime)) {
			throw new IllegalArgumentException("End time cannot be before start time");
		}
		TimeEntry entry = TimeEntry.builder()
				.employeeId(request.getEmployeeId())
				.projectId(request.getProjectId())
				.startTime(startTime)
				.endTime(endTime)
				.description(request.getDescription())
				.build();

		TimeEntry saved = timeEntryRepository.save(entry);

		return toResponse(saved, request.getStartTime().getZone());
	}


	private TimeEntryResponse toResponse(TimeEntry entity, ZoneId userZone) {
		return TimeEntryResponse.builder()
				.id(entity.getId())
				.employeeId(entity.getEmployeeId())
				.projectId(entity.getProjectId())
				.startTime(entity.getStartTime().atZone(ZoneOffset.UTC).withZoneSameInstant(userZone))
				.endTime(entity.getEndTime().atZone(ZoneOffset.UTC).withZoneSameInstant(userZone))
				.description(entity.getDescription())
				.build();
	}

	public List<TimeEntryResponse> findOverlapping(Long employeeId, ZonedDateTime start, ZonedDateTime end) {
		Instant startUtc = start.toInstant();
		Instant endUtc = end.toInstant();

		List<TimeEntry> overlapping = timeEntryRepository.findByEmployeeIdAndStartTimeLessThanAndEndTimeGreaterThan(
				employeeId, endUtc, startUtc);

		return overlapping.stream()
				.map(te -> toResponse(te, start.getZone()))
				.collect(Collectors.toList());
	}

	public Duration calculateTotalDuration(Long employeeId, ZonedDateTime rangeStart, ZonedDateTime rangeEnd) {
		Instant startUtc = rangeStart.toInstant();
		Instant endUtc = rangeEnd.toInstant();

		List<TimeEntry> entries = timeEntryRepository.findByEmployeeIdAndStartTimeBetween(employeeId, startUtc, endUtc);

		Duration total = Duration.ZERO;
		for (TimeEntry entry : entries) {
			Instant entryStart = entry.getStartTime().isBefore(startUtc) ? startUtc : entry.getStartTime();
			Instant entryEnd = entry.getEndTime().isAfter(endUtc) ? endUtc : entry.getEndTime();
			total = total.plus(Duration.between(entryStart, entryEnd));
		}
		return total;
	}

	public List<TimeEntryResponse> generateReport(Long employeeId, ZonedDateTime rangeStart, ZonedDateTime rangeEnd) {
		Instant startUtc = rangeStart.toInstant();
		Instant endUtc = rangeEnd.toInstant();

		List<TimeEntry> entries = timeEntryRepository.findByEmployeeIdAndStartTimeBetween(employeeId, startUtc, endUtc);

		return entries.stream()
				.map(te -> toResponse(te, rangeStart.getZone()))
				.collect(Collectors.toList());
	}

}
