package webreport.sparktest

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client.{ConnectionFactory, Get, Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HConstants, TableName}
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object HbaseTest {

  def hbaseApiRead(){

    val conf = HBaseConfiguration.create()
    conf.set(HConstants.ZOOKEEPER_QUORUM,"bdpzookeeper.boeb9.com")
    conf.set(HConstants.ZOOKEEPER_ZNODE_PARENT, "/hbase")
    val conn = ConnectionFactory.createConnection(conf)
    val table = conn.getTable(TableName.valueOf("MDW:DWR_LOT"))
    val get = new Get(Bytes.toBytes("000|B9|9AAA930439"))
    val result = table.get(get)
    if(result.getRow.length>0){
      println(result.listCells())
    }
    else{
      println("empty")
    }
    table.close()
    conn.close()
  }
  def hBaseConf(): Configuration ={
    val table = "MDW:DWR_LOT"
    val family = "CF"
    val columnLot = "LOT_ID"
    val columnFactory = "FACTORY"
    val column = s"$family:$columnLot $family:$columnFactory"
    val startRowKey = "000|B9|9AAA930439"
    val endRowKey = "fff|B9|VFQUD20600B002"
    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.property.clientPort", "2181")
    //conf.set("spark.executor.memory", "3000m")
    conf.set("hbase.zookeeper.quorum", "bdpzookeeper.boeb9.com")
    conf.set("zookeeper.znode.parent", "/hbase")
    conf.set(TableInputFormat.INPUT_TABLE, table)
    //conf.set(TableInputFormat.SCAN_COLUMN_FAMILY,family)
    //若指定column family，则指定columns无效
    conf.set(TableInputFormat.SCAN_COLUMNS, column)
    conf.set(TableInputFormat.SCAN_ROW_START, startRowKey)
    conf.set(TableInputFormat.SCAN_ROW_STOP, endRowKey)
    conf
  }
  def hadoopRddReadTest(): Unit = {
    val spark = SparkSession.builder().master("local[*]").appName(s"${this.getClass.getSimpleName}").getOrCreate()
    val sc = spark.sparkContext
    val conf = hBaseConf()
    val hBaseRdd: RDD[(ImmutableBytesWritable, Result)] = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result])
    //读取HBase，rawCells遍历方式
    hBaseRdd.foreachPartition(partition => {
      partition.foreach({ case (k, result) => {
        val kd = new String(k.get(), k.getOffset, k.getLength)
        //val kd = Bytes.toString(k.get())
        val vd = result.rawCells()
        for (cell <- vd) {
          val rowKey = new String(cell.getRowArray, cell.getRowOffset, cell.getRowLength)
          val family = new String(cell.getFamilyArray, cell.getFamilyOffset, cell.getFamilyLength)
          val qualifier = new String(cell.getQualifierArray, cell.getQualifierOffset, cell.getQualifierLength)
          val value = new String(cell.getValueArray, cell.getValueOffset, cell.getValueLength)
          println("kd" + kd, "rowkey" + rowKey, "fm" + family, "qu" + qualifier, "va" + value)
        }
      }
      })
    })
    //读取HBase信息,getValue方式
    hBaseRdd.foreach { case (_, result) => {
      val key: String = Bytes.toString(result.getRow)
      val Lot = Bytes.toString(result.getValue("CF".getBytes(), "LOT_ID".getBytes()))
      val Factory = Bytes.toString(result.getValue("CF".getBytes(), "FACTORY".getBytes()))
      println(s"key is $key the Lot is $Lot,the Factory is $Factory")
    }
    }
    spark.close()
  }
  def hadoopRddWriteTest: Unit = {
    val spark = SparkSession.builder().master("local[*]").appName(s"${this.getClass.getSimpleName}").getOrCreate()
    val sc = spark.sparkContext
    val conf = hBaseConf()
    //写入数据到hbase
    //初始化jobconf，TableOutputFormat必须是org.apache.hadoop.hbase.mapred包下的！
    val jobConf = new JobConf(conf)
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, "MDW:DWR_LOT")

    val dataRdd = sc.makeRDD(Array("111111111,lotid,15", "111111111,lotid,16", "111111111,lotid,17")).repartition(3)
    val rdd = dataRdd.map(_.split(',')).map(arr => {
      val put = new Put(Bytes.toBytes(arr(0)))
      put.addColumn(Bytes.toBytes("CF"), Bytes.toBytes("LOT_ID"), Bytes.toBytes(arr(1)))
      put.addColumn(Bytes.toBytes("CF"), Bytes.toBytes("FACTORY"), Bytes.toBytes(arr(2)))
      (new ImmutableBytesWritable, put)
    })
    rdd.repartition(1).saveAsHadoopDataset(jobConf)
    Thread.sleep(600000)
    spark.stop()
  }
  def newApiHadoopRddWriteTest: Unit = {
    val spark = SparkSession.builder().master("local[*]").appName(s"${this.getClass.getSimpleName}").getOrCreate()
    val sc = spark.sparkContext
    sc.hadoopConfiguration.set("hbase.zookeeper.quorum","slave1,slave2,slave3")
    sc.hadoopConfiguration.set("hbase.zookeeper.property.clientPort", "2181")
    sc.hadoopConfiguration.set(TableOutputFormat.OUTPUT_TABLE, "MDW:DWR_LOT")
    val job = Job.getInstance(sc.hadoopConfiguration)
    job.setOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setOutputValueClass(classOf[Result])
    //job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])
    val dataRdd = sc.makeRDD(Array("111111111,lotid,15", "111111111,lotid,16", "111111111,lotid,17")).repartition(3)
    val rdd = dataRdd.map(_.split(',')).map(arr => {
      val put = new Put(Bytes.toBytes(arr(0)))
      put.addColumn(Bytes.toBytes("CF"), Bytes.toBytes("LOT_ID"), Bytes.toBytes(arr(1)))
      put.addColumn(Bytes.toBytes("CF"), Bytes.toBytes("FACTORY"), Bytes.toBytes(arr(2)))
      (new ImmutableBytesWritable, put)
    })
    rdd.repartition(3)
    rdd.saveAsNewAPIHadoopDataset(job.getConfiguration)
    Thread.sleep(600000)
    spark.stop()
  }
//  def bulkLoadTest(): Unit ={
//    val sparkConf = new SparkConf().setAppName("HBaseReadTest").setMaster("local[2]")
//    val sc = new SparkContext(sparkConf)
//
//    val tableName = "imooc_course_clickcount"
//    val quorum = "localhost"
//    val port = "2181"
//
//    // 配置相关信息
//    val conf = HBaseUtils.getHBaseConfiguration(quorum,port,tableName)
//    conf.set(TableOutputFormat.OUTPUT_TABLE,tableName)
//
//    val table = HBaseUtils.getTable(conf,tableName)
//
//    val job = Job.getInstance(conf)
//    job.setMapOutputKeyClass(classOf[ImmutableBytesWritable])
//    job.setMapOutputKeyClass(classOf[KeyValue])
//
//    HFileOutputFormat2.configureIncrementalLoadMap(job, table)
//
//
//    // inputRDD data
//    val indataRDD = sc.makeRDD(Array("20180723_02,13","20180723_03,13","20180818_03,13"))
//    val rdd = indataRDD.map(x => {
//      val arr = x.split(",")
//      val kv = new KeyValue(Bytes.toBytes(arr(0)),"info".getBytes,"clict_count".getBytes,arr(1).getBytes)
//      (new ImmutableBytesWritable(Bytes.toBytes(arr(0))),kv)
//    })
//
//    // 保存Hfile to HDFS
//    rdd.saveAsNewAPIHadoopFile("hdfs://localhost:8020/tmp/hbase",classOf[ImmutableBytesWritable],classOf[KeyValue],classOf[HFileOutputFormat2],conf)
//
//    // Bulk写Hfile to HBase
//    val bulkLoader = new LoadIncrementalHFiles(conf)
//    bulkLoader.doBulkLoad(new Path("hdfs://localhost:8020/tmp/hbase"),table)
//
//  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[*]").appName(s"${this.getClass.getSimpleName}").getOrCreate()
    val sc = spark.sparkContext
    val conf = hBaseConf()
    val hBaseRdd: RDD[(ImmutableBytesWritable, Result)] = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result])
    println(hBaseRdd.count())
    spark.close()
  }
}
