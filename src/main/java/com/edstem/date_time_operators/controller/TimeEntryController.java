package com.edstem.date_time_operators.controller;

import com.edstem.date_time_operators.contract.TimeEntryRequest;
import com.edstem.date_time_operators.contract.TimeEntryResponse;
import com.edstem.date_time_operators.service.TimeEntryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/time-entries")
public class TimeEntryController {
	private final TimeEntryService timeEntryService;

	public TimeEntryController(TimeEntryService timeEntryService) {
		this.timeEntryService = timeEntryService;
	}

	@PostMapping
	public TimeEntryResponse createEntry(@RequestBody TimeEntryRequest request) {
		return timeEntryService.createTimeEntry(request);
	}


	@GetMapping("/overlaps")
	public List<TimeEntryResponse> getOverlaps(
			@RequestParam Long employeeId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTime,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime) {

		return timeEntryService.findOverlapping(employeeId, startTime, endTime);
	}

	@GetMapping("/total-duration")
	public String getTotalDuration(
			@RequestParam Long employeeId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startTime,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime) {

		Duration duration = timeEntryService.calculateTotalDuration(employeeId, startTime, endTime);

		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();

		return String.format("Total hours: %d hours and %d minutes", hours, minutes);
	}

	@GetMapping("/report")
	public List<TimeEntryResponse> generateReport(
			@RequestParam Long employeeId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {

		return timeEntryService.generateReport(employeeId, startDate, endDate);
	}
}
