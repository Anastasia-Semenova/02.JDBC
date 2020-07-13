package hw3.repositories;

import hw3.models.Mentor;
import hw3.models.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class StudentsRepositoryJdbcImpl implements StudentsRepository {

    //language=SQL
    private static final String SQL_SELECT_BY_ID = "select * from student where id = ";
    private static final String SQL_SELECT_STUDENTS_BY_AGE = "select * from student where age = ";
    private static final String SQL_SELECT_MENTORS_BY_STUDENT_ID = "select * from mentor where mentor.student_id = ";
    private static final String SQL_INSERT_STUDENT = "insert into student " + "(first_name, last_name, age, group_number) values ('%s','%s',%d,%d)";
    private static final String SQL_UPDATE_STUDENT = "update student set " + "first_name = '%s', last_name = '%s'," +
            " age = %d, group_number = %d where id = %d";
    private static final String SQL_SELECT_STUDENTS = "select * from student";

    private Connection connection;

    public StudentsRepositoryJdbcImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Student> findAllByAge(int age) {

        Statement statement1 = null;
        ResultSet result1 = null;
        Statement statement2;
        ResultSet result2;

        List<Student> studentModels = new ArrayList<>();

        try {

            statement1 = connection.createStatement();
            result1 = statement1.executeQuery(SQL_SELECT_STUDENTS_BY_AGE + age);

            while (result1.next()) {
                Student studentModel = new Student(
                        result1.getLong("id"),
                        result1.getString("first_name"),
                        result1.getString("last_name"),
                        result1.getInt("age"),
                        result1.getInt("group_number")
                );

                statement2 = connection.createStatement();
                result2 = statement2.executeQuery(SQL_SELECT_MENTORS_BY_STUDENT_ID + studentModel.getId());

                List<Mentor> mentorModels = new ArrayList<>();
                while (result2.next()) {
                    Mentor mentorModel = new Mentor(
                            result2.getLong("id"),
                            result2.getString("first_name"),
                            result2.getString("last_name"),
                            studentModel
                    );
                    mentorModels.add(mentorModel);
                }
                studentModel.setMentors(mentorModels);
                studentModels.add(studentModel);
            }
            return studentModels;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result1 != null) {
                try {
                    result1.close();
                } catch (SQLException e) {
                    //ignore
                }
            }
            if (statement1 != null) {
                try {
                    statement1.close();
                } catch (SQLException e) {
                    //ignore
                }
            }
        }

    }

    // Необходимо вытащить список всех студентов, при этом у каждого студента должен быть проставлен список менторов
    // у менторов в свою очередь ничего проставлять (кроме имени, фамилии, id не надо)
    // student1(id, firstName, ..., mentors = [{id, firstName, lastName, null}, {}, ), student2, student3
    // все сделать одним запросом
    @Override
    public List<Student> findAll() {
        Statement statement1 = null;
        ResultSet result1 = null;
        Statement statement2;
        ResultSet result2;

        List<Student> studentModels = new ArrayList<>();

        try {

            statement1 = connection.createStatement();
            result1 = statement1.executeQuery(SQL_SELECT_STUDENTS);

            while (result1.next()) {
                Student studentModel = new Student(
                        result1.getLong("id"),
                        result1.getString("first_name"),
                        result1.getString("last_name"),
                        result1.getInt("age"),
                        result1.getInt("group_number")
                );

                statement2 = connection.createStatement();
                result2 = statement2.executeQuery(SQL_SELECT_MENTORS_BY_STUDENT_ID + studentModel.getId());

                List<Mentor> mentor = new ArrayList<>();
                while (result2.next()) {
                    Mentor mentorModel = new Mentor(
                            result2.getLong("id"),
                            result2.getString("first_name"),
                            result2.getString("last_name"),
                            studentModel
                    );
                    mentor.add(mentorModel);
                }
                studentModel.setMentors(mentor);
                studentModels.add(studentModel);
            }
            return studentModels;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result1 != null) {
                try {
                    result1.close();
                } catch (SQLException e) {
                    //ignore
                }
            }
            if (statement1 != null) {
                try {
                    statement1.close();
                } catch (SQLException e) {
                    //ignore
                }
            }
        }

    }



    @Override
    public Student findById(Long id) {
        Statement statement = null;
        ResultSet result = null;

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(SQL_SELECT_BY_ID + id);
            Student student;
            if (result.next()) {
                student = new Student(
                        result.getLong("id"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getInt("age"),
                        result.getInt("group_number")
                );
            } else return null;
            result = statement.executeQuery(SQL_SELECT_MENTORS_BY_STUDENT_ID + id);
            List<Mentor> mentor = new ArrayList<>();
            while (result.next()) {
                Mentor mentorModel = new Mentor(
                        result.getLong("id"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        student
                );
                mentor.add(mentorModel);
            }
            student.setMentors(mentor);
            return student;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    // просто вызывается insert для сущности
    // student = Student(null, 'Марсель', 'Сидиков', 26, 915)
    // studentsRepository.save(student);
    // // student = Student(3, 'Марсель', 'Сидиков', 26, 915)
    @Override
    public void save(Student entity) {

        Statement statement = null;
        ResultSet result = null;

        try {

            statement = connection.createStatement();

            String insert = String.format(SQL_INSERT_STUDENT,
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getAge(),
                    entity.getGroupNumber()
            );
            statement.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);
            result = statement.getGeneratedKeys();
            result.next();
            long id = result.getInt(1);
            entity.setId(id);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    //ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    //ignore
                }
            }
        }

    }

    // для сущности, у которой задан id выполнить обновление всех полей

    // student = Student(3, 'Марсель', 'Сидиков', 26, 915)
    // student.setFirstName("Игорь")
    // student.setLastName(null);
    // studentsRepository.update(student);
    // (3, 'Игорь', null, 26, 915)

    @Override
    public void update(Student entity) {

        Statement statement = null;

        try {

            statement = connection.createStatement();

            String update = String.format(SQL_UPDATE_STUDENT,
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getAge(),
                    entity.getGroupNumber(),
                    entity.getId()
            );
            statement.executeUpdate(update);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    //ignore
                }
            }
        }
    }
}

