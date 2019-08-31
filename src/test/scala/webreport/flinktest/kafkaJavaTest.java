package webreport.flinktest;/*
    @author    YuSu
    @createTime    2019-08-27
   */

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.Properties;

public class kafkaJavaTest {
    public static void main(String[] args) throws Exception{
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers","10.121.44.174:6667,10.121.44.176:6667,10.121.44.177:6667");
        properties.setProperty("group.id","test");

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // create a checkpoint every 5 seconds
        env.enableCheckpointing(5000);
        // create a Kafka streaming source consumer for Kafka 0.10.x
        FlinkKafkaConsumer010 kafkaConsumer = new FlinkKafkaConsumer010(
                "test",
                new SimpleStringSchema(),
                properties);

        DataStream<String> messageStream  = env.addSource(kafkaConsumer);
        messageStream.print();
        env.execute("KafkaTest");
    }
}
