package hw3;

import hw2.SimpleDataSource;
import hw3.repositories.StudentsRepository;
import hw3.repositories.StudentsRepositoryJdbcImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/itis.BD";
    private static final String USER = "postgres";
    private static final String PASSWORD = "glotai)900";


    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        StudentsRepository studentsRepository = new StudentsRepositoryJdbcImpl(connection);
        System.out.println(studentsRepository.findById(2L));
        System.out.println(studentsRepository.findAllByAge(19));
        System.out.println(studentsRepository.findAll());
        connection.close();
    }
}
