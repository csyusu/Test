package webreport

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("WordCount").setMaster("local");
    val sc = new SparkContext(conf);
    //val list = sc.parallelize(List(1,2,3));
    val file = sc.textFile("H:\\BDP\\scala\\webreport\\src\\JavaTest\\scala\\webreport\\wordcount.txt");
    val rdd = file.map(line => line.split(" "))
      .map(x=>((x(0),x(1)),x(3))).groupByKey().sortByKey(false)
      .map(x => (x._1._1+"-"+x._1._2,x._2.toList.sortWith(_>_)))
    rdd.foreach(
      x=>{
        val buf = new StringBuilder()
        for(a <- x._2){
          buf.append(a)
          buf.append(",")
        }
        buf.deleteCharAt(buf.length()-1)
        println(x._1+" "+buf.toString())
      })
    val counts = file.flatMap(_.split(" ")).map(s => (s,1)).reduceByKey((a,b) => a+b)
    println("count:",counts)
    sc.stop()

  }
}
