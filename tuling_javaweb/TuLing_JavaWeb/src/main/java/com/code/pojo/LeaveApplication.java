package com.code.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveApplication {
    private Integer id;
    private Integer emp_id;
    private Integer leave_type;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private Integer approved_by_manager;
    private LocalDateTime approve_time;
    private LocalDateTime apply_time;
    private String remarks;

}
