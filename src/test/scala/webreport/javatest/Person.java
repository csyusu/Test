package webreport.javatest;/*
    @author    YuSu
    @createTime    2019-04-23
   */

public class Person {
    private int id;
    private String name;

    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
}
