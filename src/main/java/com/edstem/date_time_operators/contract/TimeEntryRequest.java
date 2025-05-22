package com.edstem.date_time_operators.contract;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeEntryRequest {
	private Long id;
	private Long employeeId;
	private Long projectId;
	private ZonedDateTime startTime;
	private ZonedDateTime endTime;
	private String description;
}
