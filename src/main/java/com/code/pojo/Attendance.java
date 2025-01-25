package com.code.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attendance {
    private Integer id;
    private Integer emp_id;
    private LocalDateTime punch_time;
    private LocalTime scheduled_punch_time;
    private Boolean is_late;
    private LocalDateTime create_time;
    private LocalDateTime update_time;


}
