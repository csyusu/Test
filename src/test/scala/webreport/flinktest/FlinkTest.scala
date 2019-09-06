package webreport.flinktest


import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector


/*
    @author    YuSu
    @createTime    2019-08-31
   */
object FlinkTest {
  /**
  ConnectedStreams测试
   */
  def connectTest(env:StreamExecutionEnvironment): Unit ={
    val stream = env.readTextFile("D:\\Subversion\\Test\\src\\test\\scala\\webreport\\wordcount.txt")
    val streamMap = stream.flatMap(item =>item.split(" "))
    val streamCollect = env.fromCollection(List(1,2,3,4))
    val streamConnect = streamMap.connect(streamCollect)
    streamConnect.map(item=>println(item), item=>println(item))
  }

  /**
    * 自定义eventTime时间戳、waterMark延迟时间offset、自定义key选择器
    * @param env
    */
  def waterMarkTest(env:StreamExecutionEnvironment): Unit ={
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    //指定waterMark offset为1s
    val stream = env.fromElements((1,"A"),(2,"B"),(3,"B"),(3000,"C")).assignTimestampsAndWatermarks(
      new BoundedOutOfOrdernessTimestampExtractor[(Int, String)](Time.seconds(1)) {
        //自定义时间戳提取器
        override def extractTimestamp(element: (Int, String)): Long = {
          element._1.toLong
        }
      }
    )
    //自定义key选择器
    val result = stream.keyBy( item=>{item._2.toLowerCase})
      .window(
      TumblingEventTimeWindows.of(Time.seconds(1))
    ).sum(0)
    result.print()
  }

  /**
    * 侧输出测试
    * @param env
    */
  def sideTest(env:StreamExecutionEnvironment): Unit ={
    //定义侧输出标签：类型，id
    val sideTag = OutputTag[String]("sideTag")
    val data = env.fromElements((1,"A"),(2,"B"),(3,"B"),(3000,"C"))
      //侧输出需要使用ProcessFunction、CoProcessFunction、ProcessWindowFunction、ProcessAllWindowFunction等函数发生数据
      .process(new ProcessFunction[(Int,String),(Int,String)] {
        override def processElement(value: (Int, String), ctx: ProcessFunction[(Int, String), (Int, String)]#Context, out: Collector[(Int,String)])= {
          //正常输出
          if(value._1<10){
            out.collect(value)
          }
          //侧输出
          else {
            ctx.output(sideTag,s"侧输出:${value._2}")
          }
        }
      })
    println("正常输出：")
    data.print()
    println("侧输出：")
    data.getSideOutput(sideTag).print()
  }
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置基于事件时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setParallelism(2)
    // create a checkpoint every 5 seconds
    env.enableCheckpointing(5000)
    //指定checkpoint语义
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    sideTest(env)
    env.execute("FlinkTest")

  }
}
