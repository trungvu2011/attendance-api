package com.attendance.dto;

import com.attendance.entities.Exam;
import com.attendance.entities.Room;
import com.attendance.entities.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamDTO {
    private String name;
    private String subject;
    private String semester;
    private LocalDate date;
    private Integer scheduleId;
    private UUID roomId;

    public Exam toEntity(Room room, Schedule schedule) {
        Exam exam = new Exam();
        exam.setName(this.name);
        exam.setSubject(this.subject);
        exam.setSemester(this.semester);
        exam.setDate(this.date);
        exam.setRoom(room);
        exam.setSchedule(schedule);
        return exam;
    }
}