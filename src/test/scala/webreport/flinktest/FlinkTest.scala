package webreport.flinktest

import java.util

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.spark.api.java.function.MapFunction

/*
    @author    YuSu
    @createTime    2019-08-31
   */
object FlinkTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tuple2 = (("12.2.2.49","16.1.1.41",138L,145L,6L,33L,"10.121.45.218"),(0.0,100.0,100.0,100.0,1L,1567145687000L))
    val list = new util.ArrayList[(Tuple7[String, String, Long, Long, Long, Long, String], Tuple6[Double, Double, Double, Double,Long, Long])]
    list.add(tuple2)
    print(tuple2)
    val message2 = env.fromElements(1,2,3)

    val message = env.fromCollection(list)
//    message.map(new MapFunction[((String, String, Long, Long, Long, Long, String),(Double, Double, Double, Double,Long, Long))] {
//      override def map: Unit ={
//
//      }
//    })
  }

}
