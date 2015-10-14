package formality.content.database.mysql;

import formality.content.database.CCSS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marshall
 * Date: 10/5/12
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class DbCCSS {


    public static List<CCSS> getStandards (Connection conn) throws SQLException {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        try {
            List<CCSS> l = new ArrayList<CCSS>();
            String q = "select id,code,grade from CCSS order by grade asc";
            stmt = conn.prepareStatement(q);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int id= rs.getInt(1);
                String code = rs.getString(2);
                int gr = rs.getInt(3);
                l.add(new CCSS(id,code,gr));
            }
            return l;
        }
        finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
        }
    }
}
