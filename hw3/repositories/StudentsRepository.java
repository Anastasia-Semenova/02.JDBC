package hw3.repositories;

import hw3.models.Student;
import hw3.repositories.CrudRepository;

import java.util.List;


public interface StudentsRepository extends CrudRepository<Student> {
    List<Student> findAllByAge(int age);
}

