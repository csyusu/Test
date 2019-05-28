package webreport

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.sql.SQLContext
import tools.oracleConf

object JdbcRddTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SparksqlTest").setMaster("local")
    val sc = new SparkContext(conf)
    val rdd = new JdbcRDD(
      sc,
      /*() => {
        Class.forName("oracle.jdbc.driver.OracleDriver").newInstance()
        DriverManager.getConnection("jdbc:oracle:thin:@10.79.0.26:1521/MDWYMSDB", "MDWADM", "mdwadm1234")
      },*/
      oracleConf.getConnection,
      "SELECT ID,EQP_ID FROM TEST WHERE ? <=ID AND ID <= ?",
      1, 5, 1,
      resultSet => (
        resultSet.getInt("ID"),
        resultSet.getString(2)
      )
    )
    rdd.collect().foreach(println)
    sc.stop()

   // val sqlContest = new SQLContext(sc)
    //val dataFrame = sqlContest.read.json("H:\\BDP\\scala\\webreport\\src\\main\\scala\\webreport\\data.txt")
   // println(dataFrame.schema)
  }
}
