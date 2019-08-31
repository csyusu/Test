package webreport.sparktest

import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import webreport.sparktest.tools.GetOracleDataframe

/*
    @author    YuSu
    @createTime    2019-06-18
   */
object ArrayRepYield {
  def main(args:Array[String]): Unit ={
    val spark = SparkSession.builder().appName("ArrayRepYield")
      //.master("local[*]")
      .config("spark.debug.maxToStringFields",10000)
      .config("spark.sql.shuffle.partitions",300)
      .getOrCreate()

    import spark.sql
    val repYield = sql("""select g.glass_id,p.pnl_id,d.br_defect
                          |from yms.dwr_eda_ar_rep_gls g
                          |inner join yms.dwr_eda_ar_rep_pnl p
                          |on g.txn_time=p.txn_time
                          |inner join yms.dwr_eda_ar_rep_dft d
                          |on p.txn_time=d.txn_time
                          |and p.pnl_id=d.pnl_id
                          |where g.end_time>'20190617_060000'
                          |and g.end_time<'20190618_060000'
                          |and g.date_part>='20190617'
                          |""".stripMargin)
      repYield.createOrReplaceTempView("repYield")
    val gls = sql(
      """
        |select gls_id,unit_id,shift_timekey
        |from mdw.dwr_gls_hist
        |where unit_id in ('9ATPE01-PCVD-CHA',
        |                           '9ATPE01-PCVD-CHB',
        |                           '9ATPE01-PCVD-CHC',
        |                           '9ATPE01-PCVD-CHD',
        |                           '9ATPE01-PCVD-CHE',
        |                           '9ATPE02-PCVD-CHA',
        |                           '9ATPE02-PCVD-CHB',
        |                           '9ATPE02-PCVD-CHC',
        |                           '9ATPE02-PCVD-CHD',
        |                           '9ATPE02-PCVD-CHE',
        |                           '9ATPE03-PCVD-CHA',
        |                           '9ATPE03-PCVD-CHB',
        |                           '9ATPE03-PCVD-CHC',
        |                           '9ATPE03-PCVD-CHD',
        |                           '9ATPE03-PCVD-CHE',
        |                           '9ATPE04-PCVD-CHA',
        |                           '9ATPE04-PCVD-CHB',
        |                           '9ATPE04-PCVD-CHC',
        |                           '9ATPE04-PCVD-CHD',
        |                           '9ATPE04-PCVD-CHE',
        |                           '9ATPE05-PCVD-CHA',
        |                           '9ATPE05-PCVD-CHB',
        |                           '9ATPE05-PCVD-CHC',
        |                           '9ATPE05-PCVD-CHD',
        |                           '9ATPE05-PCVD-CHE',
        |                           '9ATPE06-PCVD-CHA',
        |                           '9ATPE06-PCVD-CHB',
        |                           '9ATPE06-PCVD-CHC',
        |                           '9ATPE06-PCVD-CHD',
        |                           '9ATPE06-PCVD-CHE',
        |                           '9ATPE07-PCVD-CHA',
        |                           '9ATPE07-PCVD-CHB',
        |                           '9ATPE07-PCVD-CHC',
        |                           '9ATPE07-PCVD-CHD',
        |                           '9ATPE07-PCVD-CHE',
        |                           '9ATPE08-PCVD-CHA',
        |                           '9ATPE08-PCVD-CHB',
        |                           '9ATPE08-PCVD-CHC',
        |                           '9ATPE08-PCVD-CHD',
        |                           '9ATPE08-PCVD-CHE',
        |                           '9ATPE09-PCVD-CHA',
        |                           '9ATPE09-PCVD-CHB',
        |                           '9ATPE09-PCVD-CHC',
        |                           '9ATPE09-PCVD-CHD',
        |                           '9ATPE09-PCVD-CHE',
        |                           '9ATPE10-PCVD-CHA',
        |                           '9ATPE10-PCVD-CHB',
        |                           '9ATPE10-PCVD-CHC',
        |                           '9ATPE10-PCVD-CHD',
        |                           '9ATPE10-PCVD-CHE',
        |                           '9ATPE11-PCVD-CHA',
        |                           '9ATPE11-PCVD-CHB',
        |                           '9ATPE11-PCVD-CHC',
        |                           '9ATPE11-PCVD-CHD',
        |                           '9ATPE11-PCVD-CHE',
        |                           '9ATPE12-PCVD-CHA',
        |                           '9ATPE12-PCVD-CHB',
        |                           '9ATPE12-PCVD-CHC',
        |                           '9ATPE12-PCVD-CHD',
        |                           '9ATPE12-PCVD-CHE',
        |                           '9ATPE13-PCVD-CHA',
        |                           '9ATPE13-PCVD-CHB',
        |                           '9ATPE13-PCVD-CHC',
        |                           '9ATPE13-PCVD-CHD',
        |                           '9ATPE13-PCVD-CHE',
        |                           '9ATPE14-PCVD-CHA',
        |                           '9ATPE14-PCVD-CHB',
        |                           '9ATPE14-PCVD-CHC',
        |                           '9ATPE14-PCVD-CHD',
        |                           '9ATPE14-PCVD-CHE',
        |                           '9ATPE15-PCVD-CHA',
        |                           '9ATPE15-PCVD-CHB',
        |                           '9ATPE15-PCVD-CHC',
        |                           '9ATPE15-PCVD-CHD',
        |                           '9ATPE15-PCVD-CHE',
        |                           '9ATPE16-PCVD-CHA',
        |                           '9ATPE16-PCVD-CHB',
        |                           '9ATPE16-PCVD-CHC',
        |                           '9ATPE16-PCVD-CHD',
        |                           '9ATPE16-PCVD-CHE')
        |     AND FACTORY = 'ARRAY'
        |     AND OPER_CODE IN
        |         ('4320-00', '4340-00', '5320-00', '5340-00', '8320-00')
      """.stripMargin)
      gls.createOrReplaceTempView("gls")
    val result = sql(
      """
        |select g.unit_id,d.br_defect,
        |count(g.gls_id) over(partition by unit_id) as defect_qty,
        |count(g.gls_id) over() as total_qty,
        |from_unixtime(unix_timestamp(), 'YYYY-MM-dd HH:mm:ss') as systime
        |from gls g inner join repYield d
        |on g.gls_id=d.glass_id
      """.stripMargin).persist(StorageLevel.MEMORY_AND_DISK)
    result.explain()
    result.show(20)
    try{
      result.write.mode("overwrite").jdbc(GetOracleDataframe.url,"test1",GetOracleDataframe.conn)
    }
    catch{
      case e:Exception => println(e)
    }
    finally {
      spark.close()
    }
  }
}
