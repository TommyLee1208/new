package com.code.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Salary {
    private Integer emp_id;
    private BigDecimal base_salary;
    private BigDecimal full_attendance_bonus;
    private LocalDateTime create_time;
    private LocalDateTime update_time;


}
