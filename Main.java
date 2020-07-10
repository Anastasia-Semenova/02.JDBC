import java.sql.*;

public class Main {

    private static final String URL = "jdbc:postgresql://localhost:5432/itis_second";
    private static final String USER = "postgres";
    private static final String PASSWORD = "glotai)900";



    public static void main(String[] args) throws SQLException {
        SimpleDataSource dataSource=new SimpleDataSource();
        Connection connection = dataSource.openConnection(URL, USER, PASSWORD);

        Statement statement = connection.createStatement();
        ResultSet resultSet= statement.executeQuery("select * from serial");

        while(resultSet.next()){
            System.out.println("ID" + resultSet.getInt("id"));
            System.out.println("Name" + resultSet.getString("name"));
            System.out.println("Creator" + resultSet.getString("creator") );
            System.out.println("Year" + resultSet.getInt("year"));
        }
        System.out.println("---------------------");
        resultSet.close();

        resultSet= statement.executeQuery("select serial.id from serial join company on " +
                        "serial.id=company.serial_id");
        while (resultSet.next()){
            System.out.println("ID" + resultSet.getInt("id"));
        }


        connection.close();
    }
}
