package formality.admin;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Feb 16, 2011
 * Time: 4:42:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteStudents extends StudentAdmin {

    public static void main(String[] args) {
        DeleteStudents s = new DeleteStudents();
        try {
            Connection conn= s.getConnection();
            // delete the users in the range (inclusive)
            s.deleteStudents(conn,1827,1832);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
