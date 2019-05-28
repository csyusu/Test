package webreport.tools

import java.sql.DriverManager

object oracleConf {
  def getConnection()={
    Class.forName("oracle.jdbc.driver.OracleDriver").newInstance()
    DriverManager.getConnection("jdbc:oracle:thin:@10.79.0.26:1521/MDWYMSDB", "MDWADM", "mdwadm1234")
  }
}
