package formality.admin;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: Feb 14, 2011
 * Time: 9:49:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class InsertStudentInitialFrameworkData extends StudentAdmin {

    public static void main(String[] args) {
        InsertStudentInitialFrameworkData d = new InsertStudentInitialFrameworkData();
        try {
            Connection conn = d.getConnection();
            d.insertStudentFrameworkDataToDb(conn,1696);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
}
