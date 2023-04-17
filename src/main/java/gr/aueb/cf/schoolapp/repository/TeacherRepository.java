package gr.aueb.cf.schoolapp.repository;

import gr.aueb.cf.schoolapp.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



//works like DAO
@Repository
public interface TeacherRepository extends JpaRepository <Teacher, Long> {
    List<Teacher> findByLastnameStartingWith(String lastname);
    Teacher findTeacherById(Long id);

//    @Query("SELECT T FROM Teacher T WHERE T.lastname LIKE ?1")
//    List<Teacher> getTeachersByLastname(String lastname);
}
