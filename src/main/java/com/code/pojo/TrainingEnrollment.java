package com.code.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TrainingEnrollment {
    private Integer id;
    private String name;
    private Integer event_id;
    private Integer emp_id;
    private LocalDateTime enroll_time;
    private TrainingEvent trainingEvent;
}
