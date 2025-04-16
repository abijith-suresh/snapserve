package com.snapserve.userservice.service;

import com.snapserve.userservice.model.Specialist;
import com.snapserve.userservice.repository.SpecialistRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialistServiceImpl implements GenericUserService<Specialist> {

    private SpecialistRepository specialistRepository;

    @Override
    public Specialist createUser(Specialist user) {
        return specialistRepository.save(user);
    }

    @Override
    public Specialist getUserById(String id) {
        return specialistRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
    }

    @Override
    public List<Specialist> getAllUsers() {
        return specialistRepository.findAll();
    }

    @Override
    public Specialist updateUser(String id, Specialist user) {
        user.setId(new ObjectId(id));
        return specialistRepository.save(user);
    }

    @Override
    public void deleteUser(String id) {
        specialistRepository.deleteById(new ObjectId(id));
    }
}