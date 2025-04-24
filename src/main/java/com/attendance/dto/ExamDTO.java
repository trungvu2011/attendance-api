package com.attendance.dto;

import com.attendance.entities.Exam;
import com.attendance.entities.Room;
import com.attendance.entities.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDTO {
    private UUID examId;
    private String name;
    private String subject;
    private String semester;
    private LocalDate date;
    private ScheduleDTO schedule;
    private RoomDetail room;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleDTO {
        private Integer scheduleId;
        private LocalTime startTime;
        private LocalTime endTime;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomDetail {
        private UUID roomId;
        private String name;
        private String building;
    }

    public static ExamDTO fromEntity(Exam exam) {
        ExamDTO dto = new ExamDTO();
        dto.setExamId(exam.getExamId());
        dto.setName(exam.getName());
        dto.setSubject(exam.getSubject());
        dto.setSemester(exam.getSemester());
        dto.setDate(exam.getDate());

        if (exam.getSchedule() != null) {
            Schedule schedule = exam.getSchedule();
            ScheduleDTO scheduleDTO = new ScheduleDTO(
                    schedule.getScheduleId(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getName());
            dto.setSchedule(scheduleDTO);
        }

        if (exam.getRoom() != null) {
            Room room = exam.getRoom();
            RoomDetail roomDetail = new RoomDetail(
                    room.getRoomId(),
                    room.getName(),
                    room.getBuilding());
            dto.setRoom(roomDetail);
        }

        return dto;
    }
}