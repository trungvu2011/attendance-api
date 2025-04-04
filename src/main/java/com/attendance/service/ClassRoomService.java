package com.attendance.service;


import com.attendance.entities.ClassRoom;
import com.attendance.repositories.ClassRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClassRoomService {
    private final ClassRoomRepository classRoomRepository;

    @Autowired
    public ClassRoomService(ClassRoomRepository classRoomRepository){ this.classRoomRepository = classRoomRepository;}


    public List<ClassRoom> getAllClassRoom() { return classRoomRepository.findAll();}

    public Optional<ClassRoom> getClassRoomById(UUID classId){return classRoomRepository.findById(classId);}

    public ClassRoom createClassRoom (ClassRoom classRoom){return classRoomRepository.save(classRoom);}

    public ClassRoom updateClassRoom (ClassRoom classRoom){return classRoomRepository.save(classRoom);}

    public void deleteClassRoom (UUID classId){ classRoomRepository.deleteById(classId);}
}
