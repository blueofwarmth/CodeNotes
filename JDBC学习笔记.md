---
title: JDBC 学习
tags: [数据库, Java, JDBC]
categories: [技术, 记录]
date: 2023-01-23 15:44:52
description: 
---



# 概述

JDBC(Java Database Connectivity) :

在Java代码中, 使用jdbc提供的方法, 可以发送字符串类型的sql语句到数据库管理软件, 并且获取语句执行结果, 进而实现数据库数据的curd

![image-20240125160219558](https://cdn.jsdelivr.net/gh/QyingliBoost/Photo@main/image-20240125160219558.png)



![image-20240125160343840](https://cdn.jsdelivr.net/gh/QyingliBoost/Photo@main/image-20240125160343840.png)

## 小结

* jdbc是Java连接数据库技术的统称
* jdbc是由两个部分组成: 
  * Java提供的jdbc规范
  * 各个数据库厂商实现的驱动jar包
* jdbc技术是一种典型的面向接口编程

### 优势

只需要学习jdbc接口规范, 既可以操作所有数据库软件

项目中切换数据库, 只需要更新第三方数据库驱动jar包, 不需要更改代码

## jdbc核心api和使用路线

### jdbc技术组成

1. jdk下jdbc规范接口, 存储在Java.sql和javax.sql包中的api

为了项目代码的可移植性，可维护性，SUN公司从最初就制定了Java程序连接各种数据库的统一接口规范。这样的话，不管是连接哪一种DBMS软件，Java代码可以保持一致性。

2. 各个数据库厂商提供的驱动jar包

因为各个数据库厂商的DBMS软件各有不同，那么内部如何通过sql实现增、删、改、查等管理数据，只有这个数据库厂商自己更清楚，因此把接口规范的实现交给各个数据库厂商自己实现。

3. jar包

java程序打成的一种压缩包格式，你可以将这些jar包引入你的项目中，然后你可以使用这个java程序中类和方法以及属性了!

### 设计具体核心类和接口

1. DriverManager获取连接；
2. 接着建立连接；
3. PreparedStatement（最常用）发送sql语句；\
4. 若是查询操作，则对应的查询结果放在Result中。

#### DriverManager

1. 将第三方数据库厂商的实现驱动jar注册到程序中

2. 可以根据数据库连接信息获取connection

#### Connection

- 和数据库建立的连接,在连接对象上,可以多次执行数据库curd动作
- 可以获取statement和 preparedstatement,callablestatement对象

#### Statement | PreparedStatement | CallableStatement

* 具体发送SQL语句到数据库管理软件的对象
* 不同发送方式稍有不同

#### Result

* 面向对象思维的产物, 抽象成数据库的查询结果表
* 存储DQL的查询数据库结果的对象
* 需要我们进行解析, 获取具体的数据库数据

![image-20240125162156771](https://cdn.jsdelivr.net/gh/QyingliBoost/Photo@main/image-20240125162156771.png)

# JDBC核心API

![image-20240125165423209](https://cdn.jsdelivr.net/gh/QyingliBoost/Photo@main/image-20240125165423209.png)

  ```java
  package com.database.api.statement;
  import com.mysql.cj.jdbc.Driver;
  import java.sql.*;
  
  public class StatementQueryPart {
      public static void main(String[] args) throws SQLException {
          //1. 注册驱动
          DriverManager.registerDriver(new Driver());
          //2. 连接数据库
          String url = "jdbc:mysql://127.0.0.1:3306/atguigudb";
          String username = "root";
          String password = "qyingli001234";
          Connection connection = DriverManager.getConnection(url, username, password);
  
          //创建statement
          Statement statement = connection.createStatement();
          //发送sql语句, 获取返回结果
          String sql = "select email from employees;";
          ResultSet resultSet = statement.executeQuery(sql);
          //进行结果解析
          while (resultSet.next()) {//有就下一行数据
  //            int id = resultSet.getInt("id");
  //            String account = resultSet.getString("account");
  //            String nickname = resultSet.getString("password");
  //            System.out.println(id + " " + account + " " + nickname);
              String email = resultSet.getString("email");
              System.out.println(email);
          }
          //关闭
          resultSet.close();
          statement.close();
          connection.close();
      }
  }
  
  ```

# 全新JDBC拓展提升

##  自增长主键回显实现

### 功能需求

1. **java程序**获取**插入**数据时mysql维护**自增长**维护的主键**id值**,这就是主键回显
2. 作用: 在多表关联插入数据时,一般主表的主键都是自动生成的,所以在插入数据之前无法知道这条数据的主键,但是从表需要在插入数据之前就绑定主表的主键,这时可以使用主键回显技术:
3. 也就是, 在插入订单的数据时, 用返回主键确认对应的是那一个订单项

![img](https://myphoto-1301444197.cos.ap-chengdu.myqcloud.com/img/202401271942228.png)

### 功能实现

> 继续沿用之前的表数据

```Java
/**
 * 返回插入的主键！
 * 主键：数据库帮助维护的自增长的整数主键！
 * @throws Exception
 */
@Test
public void  returnPrimaryKey() throws Exception{

    //1.注册驱动
    Class.forName("com.mysql.cj.jdbc.Driver");
    //2.获取连接
    Connection connection = DriverManager.getConnection(
                          "jdbc:mysql:///atguigu?user=root&password=qyingli001234");
    //3.编写SQL语句结构
    String sql = "insert into t_user (account,password,nickname) values (?,?,?);";
    //4.创建预编译的statement，传入SQL语句结构
    /**
     * TODO: 第二个参数填入 1 | Statement.RETURN_GENERATED_KEYS
     *       1. 告诉statement携带回数据库生成的主键！
     * 		 PrepareDStatemnt返回Java程序时带上主键
     * 		 2. 获取司机装主键值的结果对象, 一行一列, 获取对应的数据 
     * 		ResultSet resultSet = statement.getGeneratedKeys()
     */
    PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    //5.占位符赋值
    statement.setObject(1,"towgog");
    statement.setObject(2,"123456");
    statement.setObject(3,"二狗子");
    //6.执行SQL语句 【注意：不需要传入SQL语句】 DML
    int i = statement.executeUpdate();
    //7.结果集解析
    if(i > 0) {
        System.out.println("i = " + i);

        //一行一列的数据！里面就装主键值！ id=值, 固定用getGeneratedKeys
        ResultSet resultSet = statement.getGeneratedKeys();
        //移动光标到第一行
        resultSet.next();
        //获取第一列的数据
        int anInt = resultSet.getInt(1);//指向第一列
        System.out.println("anInt = " + anInt);
    }


    //8.释放资源
    statement.close();
    connection.close();
}
```

##  批量数据插入性能提升

### 功能需求

1. 批量数据插入优化
2. 提升大量数据插入效率

### 功能实现

```Java
/**
 *改动了三处：（1）路径（2）必写values，且后面不加;（3）装货addBatch()最后executeBatch();
 * 批量细节：
 *    1.url?rewriteBatchedStatements=true
 *    2.insert 语句必须使用 values
 *    3.语句后面不能添加分号;
 *    4.语句不能直接执行，每次需要装货  addBatch() 最后 executeBatch();
 *
 * 批量插入优化！
 * @throws Exception
 */
@Test
public void  batchInsertYH() throws Exception{

    //1.注册驱动
    Class.forName("com.mysql.cj.jdbc.Driver");
    //2.获取连接
    Connection connection = DriverManager.getConnection
          ("jdbc:mysql:///atguigu?rewriteBatchedStatements=true","root","root");
    //3.编写SQL语句结构
    String sql = "insert into t_user (account,password,nickname) values (?,?,?)";
    //4.创建预编译的statement，传入SQL语句结构
    /**
     * TODO: 第二个参数填入 1 | Statement.RETURN_GENERATED_KEYS
     *       告诉statement携带回数据库生成的主键！
     */
    long start = System.currentTimeMillis();
    PreparedStatement statement = connection.prepareStatement(sql);
    for (int i = 0; i < 10000; i++) {

        //5.占位符赋值
        statement.setObject(1,"ergouzi"+i);
        statement.setObject(2,"lvdandan");
        statement.setObject(3,"驴蛋蛋"+i);
        //6.装车
        statement.addBatch();
    }

    //发车！ 批量操作！
    statement.executeBatch();

    long end = System.currentTimeMillis();

    System.out.println("消耗时间："+(end - start));


    //7.结果集解析

    //8.释放资源
    connection.close();
}
```

## jdbc中数据库事务实现

### 章节目标

使用jdbc代码,添加数据库事务动作!

开启事务

事务提交 / 事务回滚

### 事务概念回顾

#### 事务概念
* 数据库事务就是一种SQL语句执行的缓存机制,不会单条执行完毕就更新数据库数据,最终根据缓
     存内的多条语句执行结果统一判定!
* 一个事务内所有语句都成功及事务成功,我们可以触发commit提交事务来结束事务,更新数据!
* 一个事务内任意一条语句失败,及事务失败,我们可以触发rollback回滚结束事务,
     数据回到事务之前状态!

举个例子: 
           临近高考,你好吃懒做,偶尔还瞎花钱,父母也只会说'==你等着!==',待到高考完毕!
           成绩600+,翻篇,庆祝!
           成绩200+,翻旧账,男女混合双打!
           

#### 优势
  允许我们在失败情况下,数据回归到业务之前的状态! 

* 场景
     **一个业务****涉及多条修改数据库语句!**
     例如: 经典的转账案例,转账业务(加钱和减钱)   
           批量删除(涉及多个删除)
           批量添加(涉及多个插入)     
           

#### 事务特性

  1. 原子性（Atomicity）原子性是指事务是一个不可分割的工作单位，事务中的操作要么都发生，

    要么都不发生。 

  2. 一致性（Consistency）事务必须使数据库从一个一致性状态变换到另外一个一致性状态。

  3. 隔离性（Isolation）事务的隔离性是指一个事务的执行不能被其他事务干扰，

    即一个事务内部的操作及使用的数据对并发的其他事务是隔离的，并发执行的各个事务之间不能互相干扰。

  4. 持久性（Durability）持久性是指一个事务一旦被提交，它对数据库中数据的改变就是永久性的，

    接下来的其他操作和数据库故障不应该对其有任何影响

#### 事务类型

  自动提交 : 每条语句自动存储一个事务中,执行成功自动提交,执行失败自动回滚! (MySQL)
  手动提交:  手动开启事务,添加语句,手动提交或者手动回滚即可!

#### SQL开启事务方式【事务都在一个连接中】
   针对自动提交: 关闭自动提交即可,多条语句添加以后,最终手动提交或者回滚! (推荐)
     

      SET autocommit = off; //关闭当前连接connection自动事务提交方式
      # 只有当前连接有效
      # 编写SQL语句即可
      SQL
      SQL
      SQL
      #手动提交或者回滚 【结束当前的事务】
      COMMIT / ROLLBACK ;  

   手动开启事务: 开启事务代码,添加SQL语句,事务提交或者事务回滚! (不推荐)

#### 呼应jdbc技术


​    

```Java
 try{
    connection.setAutoCommit(false); //关闭自动提交了
    //connection.setAutoCommit(false)也就类型于SET autocommit = off
//注意,只要当前connection对象,进行数据库操作,都不会自动提交事务
//数据库动作!
//statement - 单一的数据库动作 c u r d 
//connection - 操作事务 
connection.commit();
  }catch(Execption e){
    connection.rollback();
  }
```


```sql

- 数据库表数据
```Java
-- 继续在atguigu的库中创建银行表
CREATE TABLE t_bank(
   id INT PRIMARY KEY AUTO_INCREMENT COMMENT '账号主键', #ATUO意味着可以不用传入, 让系统自动sh
   account VARCHAR(20) NOT NULL UNIQUE COMMENT '账号',
   money  INT UNSIGNED COMMENT '金额,不能为负值') ;
   
INSERT INTO t_bank(account,money) VALUES
  ('ergouzi',1000),('lvdandan',1000);
```

- 代码结构设计

  ![img](https://myphoto-1301444197.cos.ap-chengdu.myqcloud.com/img/202401272032153.png)

- jdbc事务实现

  - 测试类

```Java
/**
 * @Author 赵伟风
 * Description: 测试类
 */
public class BankTest {

    @Test
    public void testBank() throws Exception {
        BankService bankService = new BankService();
        bankService.transfer("ergouzi", "lvdandan",
                500);
    }

}
- BankService
/**
 * @Author 赵伟风
 * Description: bank表业务类,添加转账业务
 */
public class BankService {

//一个事物最基本的是在同一个连接中connection，一个转账方法是一个事物，将connection传入dao
//实现层即可，dao层不用关闭connection，由事物统一关闭
    /**
     * 转账业务方法
     * @param addAccount  加钱账号
     * @param subAccount  减钱账号
     * @param money  金额
     */
    public void transfer(String addAccount,String subAccount, int money) throws ClassNotFoundException, SQLException {

        System.out.println("addAccount = " + addAccount + ", subAccount = " + subAccount + ", money = " + money);

        //注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");

        //获取连接
        Connection connection = DriverManager.getConnection
                                ("jdbc:mysql:///atguigu", "root", "root");

        int flag = 0;

        //利用try代码块,调用dao
        try {
            //开启事务(关闭事务自动提交)
            connection.setAutoCommit(false);

            BankDao bankDao = new BankDao();
            //调用加钱 和 减钱
            bankDao.addMoney(addAccount,money,connection);
            System.out.println("--------------");
            bankDao.subMoney(subAccount,money,connection);
            flag = 1;
            //不报错,提交事务
            connection.commit();
        }catch (Exception e){

            //报错回滚事务
            connection.rollback();
            throw e;
        }finally {
            connection.close();
        }

        if (flag == 1){
            System.out.println("转账成功!");
        }else{
            System.out.println("转账失败!");
        }
    }

}
- BankDao：具体操作方法
/**
 * @Author 赵伟风
 * Description: 数据库访问dao类
 */
public class BankDao {

    /**
     * 加钱方法
     * @param account
     * @param money
     * @param connection 业务传递的connection和减钱是同一个! 才可以在一个事务中!
     * @return 影响行数
     */
    public int addMoney(String account, int money,Connection connection) throws ClassNotFoundException, SQLException {


        String sql = "update t_bank set money = money + ? where account = ? ;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        //占位符赋值
        preparedStatement.setObject(1, money);
        preparedStatement.setString(2, account);

        //发送SQL语句
        int rows = preparedStatement.executeUpdate();

        //输出结果
        System.out.println("加钱执行完毕!");

        //关闭资源close
        preparedStatement.close();

        return rows;
    }

    /**
     * 减钱方法
     * @param account
     * @param money
     * @param connection 业务传递的connection和加钱是同一个! 才可以在一个事务中!
     * @return 影响行数
     */
    public int subMoney(String account, int money,Connection connection) throws ClassNotFoundException, SQLException {

        String sql = "update t_bank set money = money - ? where account = ? ;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        //占位符赋值
        preparedStatement.setObject(1, money);
        preparedStatement.setString(2, account);

        //发送SQL语句
        int rows = preparedStatement.executeUpdate();

        //输出结果
        System.out.println("减钱执行完毕!");

        //关闭资源close
        preparedStatement.close();

        return rows;
    }
}
```

# 连接池使用

## 连接池作用

### 传统缺点

1. 不使用数据库连接池，每次都通过DriverManager获取新连接，用完直接抛弃断开，
   连接的利用率太低，太浪费。
2. 对于数据库服务器来说，压力太大了。我们数据库服务器和Java程序对连接数也无法控制
   ，很容易导致数据库服务器崩溃。

我们就希望能管理连接。
- 我们可以建立一个连接池，这个池中可以容纳一定数量的连接对象，一开始，
  我们可以先替用户先创建好一些连接对象，等用户要拿连接对象时，就直接从池中拿，
  不用新建了，这样也可以节省时间。然后用户用完后，放回去，别人可以接着用。
- 可以提高连接的使用率。当池中的现有的连接都用完了，那么连接池可以向服务器申
  请新的连接放到池中。
- 直到池中的连接达到“最大连接数”，就不能在申请新的连接了，如果没有拿到连接的用户只能等待。 

![image-20240130224142261](https://myphoto-1301444197.cos.ap-chengdu.myqcloud.com/img/202401302241527.png)

# 全新JDBC使用优化以及工具类封装

过程：

1. 注册驱动
2. 获取连接
3. 编写SQL语句
4. 创建statement
5. 占位符赋值
6. 发送SQL语句
7. 结果解析 
8. 回收资源

## 1.0版本

我们封装一个工具类,内部包含连接池对象,同时对外提供连接的方法和回收连接的方法!

## 2.0版本

优化工具类v1.0版本,考虑事务的情况下, 

==如何一个线程的不同方法获取同一个连接!==

ThreadLocal的介绍：
线程本地变量：为同一个线程存储共享变量
使用这个工具类可以很简洁地编写出优美的多线程程序。通常用来在在多线程中管理共享数据库连接、
Session等

ThreadLocal用于保存某个线程共享变量，原因是在Java中，每一个线程对象中都有一个
ThreadLocalMap<ThreadLocal, Object>，其key就是一个ThreadLocal，而Object即为该线程的
共享变量。而这个map是通过ThreadLocal的set和get方法操作的。对于同一个static ThreadLocal，
不同线程只能从中get，set，remove自己的变量，而不会影响其他线程的变量。

1、ThreadLocal对象.get: 获取ThreadLocal中当前线程共享变量的值。

2、ThreadLocal对象.set: 设置ThreadLocal中当前线程共享变量的值。

3、ThreadLocal对象.remove: 移除ThreadLocal中当前线程共享变量的值。

![](https://myphoto-1301444197.cos.ap-chengdu.myqcloud.com/img/202402042332496.png)

service.trsanfer()调用了dao.add()方法和dao.sub()方法, 他们共处一个线程, 只需要在调用service时get一个connection放到ThreadLocal里, 之后的add和sub去用就行

#### 2.0工具类

```java
import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
//事物时，Service和dao属于同一线程，不用再传参数了
/*
这个工具类的作用就是用来给所有的SQL操作提供“连接”，和释放连接。
这里使用ThreadLocal的目的是为了让同一个线程，在多个地方getConnection得到的是同一个连接。
这里使用DataSource的目的是为了（1）限制服务器的连接的上限（2）连接的重用性等
 */
public class JDBCTools {
    private static DataSource ds;
    private static ThreadLocal<Connection> tl = new ThreadLocal<>();
    static{//静态代码块，JDBCToolsVersion1类初始化执行
        try {
            Properties pro = new Properties();
            pro.load(ClassLoader.getSystemResourceAsStream("druid.properties"));
            ds = DruidDataSourceFactory.createDataSource(pro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
         Connection connection = tl.get();
         if(connection  == null){//当前线程还没有拿过连接，就给它从数据库连接池拿一个
             connection = ds.getConnection();
             tl.set(connection);
         }
         return connection;
    }

    public static void free() throws SQLException {
        Connection connection = tl.get();
        if(connection != null){
            tl.remove();
            connection.setAutoCommit(true);//避免还给数据库连接池的连接不是自动提交模式（建议）
            connection.close();
        }
    }
}
```

### 高级应用封装BaseDao

前面的封装只做了第一和第八步, 整体依然繁琐

> **基本上每一个数据表都应该有一个对应的DAO接口及其实现类**，发现对所有表的操作（增、删、改、查）代码重复度很高，所以可以**抽取公共代码**，给这些DAO的实现类可以抽取一个公共的父类，我们称为BaseDao

**![image-20240131212433719](https://myphoto-1301444197.cos.ap-chengdu.myqcloud.com/img/202401312124895.png)**

将重复的代码封装到basedao中, 而增删改查中, 

增删和改都是返回int, 调用update, 所以单独所谓一个方法, 查调用query方法, 单独一个. 

即DQL语句和非DQL语句

```Java
public abstract class BaseDao {
    /*
    通用的增、删、改的方法
    String sql：sql
    Object... args：给sql中的?设置的值列表，可以是0~n
     */
    protected int update(String sql,Object... args) throws SQLException {
//        创建PreparedStatement对象，对sql预编译
        Connection connection = JDBCTools.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        //设置?的值
        if(args != null && args.length>0){
            for(int i=0; i<args.length; i++) {
                ps.setObject(i+1, args[i]);//?的编号从1开始，不是从0开始，数组的下标是从0开始
            }
        }

        //执行sql
        int len = ps.executeUpdate();
        ps.close();
        //这里检查下是否开启事务,开启不关闭连接,业务方法关闭!
        //connection.getAutoCommit()为false，不要在这里回收connection,由开启事务的地方回收
        //connection.getAutoCommit()为true，正常回收连接
        //没有开启事务的话,直接回收关闭即可!
        if (connection.getAutoCommit()) {
            //回收
            JDBCTools.free();
        }
        return len;
    }

    /*
    通用的查询多个Javabean对象的方法，例如：多个员工对象，多个部门对象等
    这里的clazz接收的是T类型的Class对象，
    如果查询员工信息，clazz代表Employee.class，
    如果查询部门信息，clazz代表Department.class，
    返回List<T> list
     */
    protected <T> ArrayList<T> query(Class<T> clazz,String sql, Object... args) throws Exception {
        //        创建PreparedStatement对象，对sql预编译
        Connection connection = JDBCTools.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        //设置?的值
        if(args != null && args.length>0){
            for(int i=0; i<args.length; i++) {
                ps.setObject(i+1, args[i]);//?的编号从1开始，不是从0开始，数组的下标是从0开始
            }
        }

        ArrayList<T> list = new ArrayList<>();
        ResultSet res = ps.executeQuery();

        /*
        获取结果集的元数据对象。
        元数据对象中有该结果集一共有几列、列名称是什么等信息
         */
         ResultSetMetaData metaData = res.getMetaData();
        int columnCount = metaData.getColumnCount();//获取结果集列数

        //遍历结果集ResultSet，把查询结果中的一条一条记录，变成一个一个T 对象，放到list中。
        while(res.next()){
            //循环一次代表有一行，代表有一个T对象
            T t = clazz.newInstance();//要求这个类型必须有公共的无参构造

            //把这条记录的每一个单元格的值取出来，设置到t对象对应的属性中。
            for(int i=1; i<=columnCount; i++){
                //for循环一次，代表取某一行的1个单元格的值
                Object value = res.getObject(i);

                //这个值应该是t对象的某个属性值
                //获取该属性对应的Field对象
                //String columnName = metaData.getColumnName(i);//获取第i列的字段名
                //这里再取别名可能没办法对应上
                String columnName = metaData.getColumnLabel(i);//获取第i列的字段名或字段的别名
                Field field = clazz.getDeclaredField(columnName);
                field.setAccessible(true);//这么做可以操作private的属性

                field.set(t, value);
            }

            list.add(t);
        }

        res.close();
        ps.close();
        //这里检查下是否开启事务,开启不关闭连接,业务方法关闭!
        //没有开启事务的话,直接回收关闭即可!
        if (connection.getAutoCommit()) {
            //回收
            JDBCTools.free();
        }
        return list;
    }

    protected <T> T queryBean(Class<T> clazz,String sql, Object... args) throws Exception {
        ArrayList<T> list = query(clazz, sql, args);
        if(list == null || list.size() == 0){
            return null;
        }
        return list.get(0);
    }
}
```

