package webreport

import org.apache.spark.sql.SparkSession

/*
    @author    YuSu
    @createTime    2019-05-14
   */
object HiveTest {
    def main(args: Array[String]): Unit = {
      //val warehouseLocation = "D:\\workspaces\\idea\\hadoop"

      val spark =
        SparkSession.builder()
          .appName("HiveTest")
          .master("local[2]")
          //拷贝hdfs-site.xml不用设置，如果使用本地hive，可通过该参数设置metastore_db的位置
          //.config("spark.sql.warehouse.dir", warehouseLocation)
          .enableHiveSupport() //开启支持hive
          .getOrCreate()

      //spark.sparkContext.setLogLevel("WARN") //设置日志输出级别
      import spark.implicits._
      import spark.sql

      sql("show databases").show
      sql("use mdw")
      sql("select * from mdw.dwr_gls_hist limit 1").show()
      //Thread.sleep(150 * 1000)
      spark.stop()
    }
}
