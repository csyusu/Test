package webreport;/*
    @author    YuSu
    @createTime    2019-04-23
   */


import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.janino.Java;

import java.nio.charset.Charset;
import java.rmi.server.UID;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class JavaTest  implements interfaceTest{
    volatile boolean flag = true;

    /**
     * 调用swap时，会生成局部变量 a b ，因为a b 存储的是对象的地址，
     * 因此调用.setId更改了局部变量a指向地址的内容，
     * a =b 修改了局部变量a的值，不影响原值
     */
    private void swap(Person a, Person b) {
        a.setId(20);
        a=b;
    }

    /**
     * java除8种基本数据类型外，对象变量都是存储的内存地址
     */
    public void variableTest(){
        Person a = new Person(23, "a");
        Person b = new Person(22, "b");
        System.out.println("交换前\na:" + a.getId());
        swap(a, b);
        System.out.println("交换后\na:" +a.getId());
    }

    /**
     * 父类引用指向了子类对象，叫做协变+T
     */
    public void extendsTest(){
            JavaParent parent = new JavaParent("P",20);
            JavaChild child = new JavaChild("C",20);
            JavaParent[] parents = new JavaParent[10];
            //父类的引用可以指向子类对象，子类引用不能指向父类对象。
            parents[0] = child;
            System.out.println(parents[0].getName());
            System.out.println("Parent:"+parent.getClass());
            System.out.println("Child"+child.getClass());
        }

    /**
     * float实际存储略大于实际值
     * 根据IEEE标准，float0.3实际存储略大于0.3，float(0.3)>double(0.3)
     */
    public void floatTest(){
            float a = 0.3f;
            //float :0.30000001192092896000
            System.out.printf("float :%.20f\n",a);
            //double :0.30000000000000000000
            System.out.printf("double :%.20f\n",0.3);
            //字面量0.3，默认为double类型，比较时把float类型转变为double类型，float后面补0，因此float 0.3>double 0.3
            System.out.println("float>double:"+(a>0.3));
        }

    /**
     * HBase rowKey设计 Md5取前几位或hashcode取模，当取模数m=2的N次方时，取模可以优化为对m-1做位与（参考hashMap实现方式）
     */
    public void md5Test(){
            System.out.println("Md5:"+DigestUtils.md5Hex("9ABA8X0762-1"));
            int m =16;
            System.out.println("9ABA8X0762-1".hashCode()%m);
            System.out.println("9ABA8X0762-1".hashCode()&(m-1));
        }

    /**
     *  包装器自动拆装箱
     *  实际调用Integer u = Integer.valueOf(9)。
     *  Integer数值范围在[-128,127]会引用已维护的数组，不会新建对象，因此对[-128,127]自动拆装箱会指向相同地址。
     *   if (i >= -128 && i <= 127)
     *             return IntegerCache.cache[i + (-IntegerCache.low)];
     */
    public void wrapperTest(){
            Integer s = new Integer(9);
            Integer t = new Integer(9);
            Integer u =9;
            int v = 9;
            System.out.println(s==t);
            System.out.println(u==v);
        }

    /**
     * 多线程同步锁测试
     */
    public void synchronizedTest(){
            SynchronizedClass synchronizedClass = new SynchronizedClass();

            new Thread(()->{
                for(int n=1;n<4;n++){
                    System.out.println("Thread 1");
                    synchronizedClass.printA();
                }
            }).start();
            new Thread(()->{
                for(int n=1;n<4;n++){
                    System.out.println("Thread 2");
                    synchronizedClass.printB();
                }
            }).start();
        }

    /**
     * Java8 Stream流处理
     */
    public void streamTest(){
            List<Integer> in = new ArrayList<Integer>(1000);
            for(int n =0;n<1000;n++){
                in.add(n);
            }
            in.add(998);
            Map<Integer,Long> map = in.stream().filter(x->{
//                System.out.print("filter: ");
                return x%2==0;
            }).map((x)->{
                return x+1;
            }).collect(Collectors.groupingBy((x)->{return 1;},Collectors.counting()));
            map.forEach((k,v)->{
                System.out.println("k:"+k+"v:"+v);
            });
//                    .forEach(x-> System.out.println(x));
        }

    /**
     * Java8 时间函数
     */
    public void dateTimeTest(){
        //当前时间减一分钟
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(1);
        String dateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(dateTime);
        //把小时改为指定小时
        LocalDateTime localDateTime2 = LocalDateTime.now().withHour(13);
        //计算两个时间差
        Duration duration = Duration.between(localDateTime2,localDateTime);
        //舍入法
        System.out.println("Hour:"+duration.toHours());
        System.out.println("Minute:"+duration.toMinutes());
    }

    public void test(String a){

    }
        public static void main(String[] args) throws Exception {
            JavaTest javaTest = new JavaTest();
//            javaTest.streamTest();

            HashMap<String,String> map = new HashMap<>();
            HashMap<String,String> map2 = new HashMap<>();
            String a ="a";
            String b ="b";
            map.put(a,a);
            map2.put("a",a);
            map.forEach((k,v)->{
                System.out.println(k.getClass().getName()+k);
            });
            map2.forEach((k,v)->{
                System.out.println(k.getClass().getName()+k);
            });
        }
}
