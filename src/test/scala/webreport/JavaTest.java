package webreport;/*
    @author    YuSu
    @createTime    2019-04-23
   */


import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;

public class JavaTest {
        //调用swap时，会生成局部变量 a b ，因为a b 存储的是对象的地址，因此调用.setId更改了局部变量a指向地址的内容，a =b 修改了局部变量a的内容
        private static void swap(Person a, Person b) {
            a.setId(20);
            a=b;
        }
        //java除8种基本数据类型外，对象变量都是存储的内存地址
        private void variableTest(){
            Person a = new Person(23, "a");
            Person b = new Person(22, "b");
            System.out.println("交换前\na:" + a.getId());
            swap(a, b);
            System.out.println("交换后\na:" +a.getId());
        }
        private void mapTest(){
            Map<String,Integer> map = new HashMap<String, Integer>(2);
            map.put("a",1);
            map.put("a",map.get("a")+1);
            map.put("b",2);
            map.put("abd",4);
            for(Map.Entry entry:map.entrySet()){
                System.out.println(entry.hashCode());

            }
        }
        private void md5Test(){
            String encryKey="+- ~!?";
            System.out.println(DigestUtils.md5Hex(encryKey));
            System.out.println(DigestUtils.md5(encryKey));
        }

        public static void main(String[] args) {
            JavaTest javaTest = new JavaTest();

        }


    }
