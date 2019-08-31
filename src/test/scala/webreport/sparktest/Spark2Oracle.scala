package webreport.sparktest

import java.util.Properties

import org.apache.spark.{SparkConf, sql}

/*
    @author    YuSu
    @createTime    2019-05-13
   */
object Spark2Oracle {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Spark2Test").setMaster("local[*]")
    val spark= new sql.SparkSession.Builder().config(conf).getOrCreate()
    val oracleUrl="""jdbc:oracle:thin:@//10.79.0.26:1521/mdwymsdb"""
    val table="""(select * from dwr_pnl where pnl_id='AAAC790618A1B2') a"""
    val connProps = new Properties()
    connProps.put("user","mdwadm")
    connProps.put("password","mdwadm1234")
    spark.read.jdbc(oracleUrl,table,connProps).show()
  }

}
