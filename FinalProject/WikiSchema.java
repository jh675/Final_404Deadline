import java.sql.*;
public class WikiSchema {
  public static void main(String[] a) throws Exception {
    Class.forName("oracle.jdbc.driver.OracleDriver");
    try (Connection c = DriverManager.getConnection("jdbc:oracle:thin:@43.201.115.54:1521/xe","dev01","dev01")) {
      String q = "SELECT table_name, column_name, data_type FROM user_tab_columns WHERE table_name IN ('WIKI','WIKI_PAGE','WIKI_CONTENT') ORDER BY table_name, column_id";
      try (Statement s = c.createStatement(); ResultSet r = s.executeQuery(q)) {
        while (r.next()) System.out.println(r.getString(1)+"\t"+r.getString(2)+"\t"+r.getString(3));
      }
    }
  }
}
