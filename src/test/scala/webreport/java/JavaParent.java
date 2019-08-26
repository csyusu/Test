package webreport.java;/*
    @author    YuSu
    @createTime    2019-06-10
   */

public class JavaParent {
    private String name;
    private int age;
    public int getAge(){
        return age;
    }
    public JavaParent(String name,int age){
        this.name=name;
        this.age=age;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
