package com.code.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingEvent {
    private Integer id;
    private String event_name;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private LocalDateTime create_time;
    private String location;
    private Integer enrolled_count;
    private Set<TrainingEnrollment> enrollments;
}
