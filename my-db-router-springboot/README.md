# 分库分表路由组件

## 概述

> 首先我们要用到 SpringBoot 的 starter 开发，就是在项目启动的时候，去进行一系列的数据库的初始化配置。
>
> 然后要用到 AOP 面向切面，自定义一个路由注解配置在你想要进行分库分表的接口方法处。当使用这个方法的时候，就会通过 AOP 定义库表索引。当[连接数据库](https://so.csdn.net/so/search?q=连接数据库&spm=1001.2101.3001.7020)的使用 getConnection () 的时候，就会根据库表索引去切换数据源。
>
> 最后，实现 Mybatis 的拦截器，通过反射修改 SQL 语句的表名，至此就完成了整体的流程



在现代大型应用系统中，随着数据量和访问量的不断增加，单一数据库往往会面临性能瓶颈和可扩展性问题。为了提升系统的性能和可扩展性，分库分表（Sharding）是一种常见且有效的策略。以下是对分库分表的详细介绍，包括其概念、优势、常用方案及实现方式。

### 分库分表的概念

- **分库**：将数据分散存储在多个数据库实例中。每个数据库实例存储数据的一部分，从而减少单个数据库的压力。
- **分表**：将一个大表拆分成多个小表，每个小表存储部分数据。这些小表可以位于同一个数据库实例中，也可以分布在多个数据库实例中。

### 分库分表的优势

1. **性能提升**：通过分散数据存储和访问，减少单个数据库实例的负担，提高查询和写入性能。
2. **高可用性**：分库分表后，单个数据库实例故障不会影响整个系统的可用性，可以实现更高的容错性和数据冗余。
3. **可扩展性**：可以通过增加新的数据库实例来扩展存储和处理能力，支持业务的快速增长。
4. **数据隔离**：不同的业务模块或租户的数据可以分开存储，便于管理和维护。

### 常见的分库分表方案

1. **水平拆分（Sharding）**：
   - **按范围拆分**：根据某个字段的范围进行拆分，例如按照用户ID的范围拆分成多个表或库。
   - **按哈希拆分**：对某个字段进行哈希运算，将数据均匀分布到多个表或库中。
   - **按时间拆分**：根据时间将数据拆分成多个表或库，例如每个月的数据存储在不同的表或库中。

2. **垂直拆分**：
   - **按业务模块拆分**：将不同业务模块的数据存储在不同的数据库实例中，例如用户信息和订单信息存储在不同的数据库。
   - **按字段拆分**：将一个表中的列拆分到多个表中，例如将用户表中的基本信息和扩展信息分开存储。

### 分库分表的实现

#### 常用的分库分表中间件和工具

1. **Apache ShardingSphere**：
   - 提供 Sharding-JDBC、Sharding-Proxy 和 Sharding-Sidecar 三个组件，支持多种分片策略、读写分离、分布式事务和数据加密。
   - 示例：
     ```java
     DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new Properties());
     ```

2. **MyCAT**：
   - 开源的分布式数据库中间件，支持 SQL 解析、分库分表、读写分离和跨库查询。
   - 配置示例（schema.xml）：
     ```xml
     <dataHost name="host1" maxCon="1000" minCon="10" balance="0" writeType="0">
         <heartbeat>select user()</heartbeat>
         <writeHost host="hostM1" url="localhost:3306" user="root" password="root">
             <readHost host="hostS1" url="localhost:3307" user="root" password="root"/>
         </writeHost>
     </dataHost>
     ```

3. **Vitess**：
   - 由 YouTube 开发的开源分布式数据库系统，适用于大规模 MySQL 集群管理。
   - 配置示例（yaml）：
     ```yaml
     keyspaces:
       - name: commerce
         shards: ["-80", "80-"]
     ```

4. **Citus**：
   - PostgreSQL 的扩展，提供透明的分布式数据库功能，支持数据分片和并行查询。
   - 示例：
     ```sql
     SELECT create_distributed_table('events', 'user_id');
     ```

### 实践中的考虑因素

1. **分片键的选择**：选择合理的分片键非常重要，它直接影响数据分布的均匀性和查询性能。常见的分片键有用户ID、订单ID等。
2. **分片规则的设计**：根据业务需求设计合理的分片规则，既要保证数据均匀分布，又要尽量减少跨分片查询。
3. **数据迁移和扩容**：在实际应用中，数据量和访问量可能会不断增加，需要考虑如何进行数据迁移和系统扩容，确保系统的可持续发展。
4. **分布式事务**：在分库分表的环境下，事务的管理变得更加复杂，需要使用分布式事务或最终一致性策略来保证数据的一致性。

### 总结

分库分表是解决大型应用系统中数据库性能和可扩展性问题的重要手段。通过合理的分片策略和中间件工具，可以有效提升系统的性能、可用性和管理能力。在实施分库分表时，需要综合考虑业务需求、数据特点和系统架构，设计合适的分片方案，确保系统的高效运行和可持续发展。

### 自定义注解

* 分库分表
* 是否开启分库分表

### 配置类

* 载入注册器, 拦截器, 分库分表策略
* 动态切换数据源
* 加载配置文件

### 分库分表策略

```
1. 获取分库分表数
2. 计算散列的那个库
3. 那个库
4. 那张表
5. 设置结果
```

### AOP类

```
1. 获取路由key
2. 在args中获得key对应的的属性
3. 设置路由策略
4. 返回对象并清楚本地线程中的数据
```

### Mybatis拦截器

```
0. 定义Parttn对象, 匹配sql的表名
1.拿到statementHandler
2. 从 metaObject 中获取delegate.mappedStatement属性，该属性的类型是 MappedStatement
3. 拿到自定义注解, 判断是否需要分库分表
4. 获取sql, 替换内容
 	获取分表信息
 	替换内容
```

### 信息类

* 分表和分库的key, 存储在ThreadLocal中
* 路由的各种属性

## 宏观流程

1.在调用dao层接口时换数据源 -> 更换库

2.在执行sql之前替换原有sql ->更换表

3.执行sql

## 核心类

>   AbstractRoutingDataSource类是用于动态切换数据源
>
>   AbstractRoutingDataSource->determineCurrentLookupKey用于获取数据源的抽象方法
>
>   实现Interceptor接口可用于拦截sql操作
>
>   路由计算:由HashMap扰动函数的散列方式



基于以上点可以知道：

1.配置并读取数据源信息放入AbstractRoutingDataSource

2.在dao层方法执行前换数据源 -> AOP拦截

3.拦截对应sql操作 -> 拦截器

4.执行器



**核心步骤就是分库分表的核心逻辑。**

## 微观流程

1.使用SPI机制扫描注入 `DataSourceAutoConfig`

2.获取并存储配置文件中配置的数据源信息

3.创建 `DynamicDataSource` 代替mybatis中数据源bean，重写determineCurrentLookupKey()方法,设置数据源策略

4.创建 `TransactionTemplate ` ，提供后续声明式事务

5.创建 `DBRouterConfig ` ,用于存储DB的信息(库表数量，路由字段)

6.创建 `IDBRouterStrategy ` 路由策略，供后续可以手动设置路由(一个事务中需要切换多个数据源会导致数据源失效，因此需要先设置路由)

7.创建 `DynamicMybatisPlugin ` 拦截器，用于动态修改sql操作哪张表

8.创建 `DBRouterJoinPoint `aop，用于在不手动设置路由情况下，aop设置路由策略



## 类描述

| 类                     | 作用                                  |
| --------------------- | ----------------------------------- |
| DBRouter注解            | 标记是否需要分库                            |
| DBRouterStrategy注解    | 标记是否需要分表                            |
| DataSourceAutoConfig类 | 配置 一些 bean 信息                       |
| DynamicDataSource类    | 重写determineCurrentLookupKey()，切换数据源 |
| DynamicMybatisPlugin类 | 拦截具体哪种sql,这里拦截的是prepare()方法         |
| IDBRouterStrategy接口   | 提供路由策略接口API                         |
| DBContextHolder类      | 存储具体分哪个库哪张表的上下文->ThreadLocal        |
| DBRouterConfig类       | 存放DB信息的类                            |
| DBRouterJoinPoint类    | AOP用于切DBRouter注解->设置库表信息            |



## 关键类分析

### AbstractRoutingDataSource类

>   该类是用于设置数据源的类

**核心方法**

| 方法                                       | 作用         |
| ---------------------------------------- | ---------- |
| setTargetDataSources(Map<Object, Object> targetDataSources) | 设置额外的数据源   |
| setDefaultTargetDataSource(Object defaultTargetDataSource) | 设置默认使用的数据源 |
| determineTargetDataSource()              | 决定使用哪个数据源  |
| determineCurrentLookupKey()              | 获取数据源的唯一表示 |

在AbstractRoutingDataSource类，存储数据源的数据结构是Map。 key->唯一表示，value：数据源信息

 **determineCurrentLookupKey()**  这个类是用于获取key

因此这个类的玩法是，创建bean替代他并且调用设置数据源的方法，重写determineCurrentLookupKey()方法获取对应的数据源



### Interceptor类

>   实现该类后，可以根据在类上加上注解@Intercepts 具体拦截执行jdbc中哪个行为，具体是XXXHandler

Intercepts中的参数有Signature

```java
public @interface Signature {
  Class<?> type(); // type：具体拦截哪个类
  String method(); // method：类中哪个方法
  Class<?>[] args(); // args：可能有多个一样的方法，拦截方法中的参数(更加一步的指定拦截的方法)
}
```



### StatementHandler

>   该类是用于封装jdbc中的各个操作
>
>   在Intercepts注解中拦截的类一般都是StatementHandler该类

1. 在MyBatis框架中，`StatementHandler` 是一个关键接口，负责处理数据库操作的核心任务。它主要用于执行SQL语句并处理结果集。`StatementHandler` 是MyBatis中执行SQL语句的底层机制，涉及SQL的生成、参数的处理以及结果的映射。

   ### `StatementHandler` 的作用

   1. **SQL的生成**：
      - `StatementHandler` 从Mapped Statement对象中获取SQL语句，并根据需要动态生成SQL。
      
   2. **参数的处理**：
      - 将参数对象转换为SQL语句中的具体参数，处理参数的类型转换和设置。

   3. **SQL的执行**：
      - 负责执行具体的SQL操作，包括查询、更新、插入和删除。

   4. **结果集的处理**：
      - 处理SQL执行后的结果集，将其映射为Java对象或集合。

   ### `StatementHandler` 的实现类

   MyBatis 中有多个 `StatementHandler` 的实现类，分别处理不同类型的SQL操作：

   1. **`SimpleStatementHandler`**：
      - 处理不需要预编译的简单SQL语句。

   2. **`PreparedStatementHandler`**：
      - 处理需要预编译的SQL语句，主要用于处理带参数的SQL。

   3. **`CallableStatementHandler`**：
      - 处理存储过程调用的SQL语句。

   ### `StatementHandler` 的工作流程

   1. **准备语句**：
      - 通过`prepare()`方法创建和初始化一个 `Statement` 对象（如 `Statement`、`PreparedStatement` 或 `CallableStatement`）。

   2. **设置参数**：
      - 通过`parameterize()`方法将参数对象的值设置到SQL语句的参数占位符中。

   3. **执行SQL**：
      - 通过`update()`或`query()`方法执行SQL语句。

   4. **处理结果**：
      - 通过`query()`方法的返回值处理结果集，并将其转换为对应的Java对象。

   ### `StatementHandler` 接口定义

   以下是 `StatementHandler` 接口的一些关键方法：

   ```java
   public interface StatementHandler {
   
       // 准备SQL语句
       Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;
   
       // 设置参数
       void parameterize(Statement statement) throws SQLException;
   
       // 执行更新操作
       int update(Statement statement) throws SQLException;
   
       // 执行查询操作
       <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;
   
       // 处理批处理操作
       void batch(Statement statement) throws SQLException;
   }
   ```

   ### 扩展 `StatementHandler`

   在MyBatis中，`StatementHandler` 可以通过插件机制进行扩展，以添加自定义的功能。例如，可以通过实现 `Interceptor` 接口来拦截和修改 `StatementHandler` 的行为。

   ```java
   @Intercepts({
       @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
       @Signature(type = StatementHandler.class, method = "parameterize", args = {Statement.class}),
       @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
       @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})
   })
   public class MyStatementHandlerInterceptor implements Interceptor {
   
       @Override
       public Object intercept(Invocation invocation) throws Throwable {
           // 自定义拦截逻辑
           return invocation.proceed();
       }
   
       @Override
       public Object plugin(Object target) {
           return Plugin.wrap(target, this);
       }
   
       @Override
       public void setProperties(Properties properties) {
           // 设置插件属性
       }
   }
   ```

   ### 总结

   `StatementHandler` 是MyBatis中处理SQL操作的核心接口，它负责生成SQL、设置参数、执行SQL以及处理结果集。通过实现和扩展 `StatementHandler`，开发者可以定制MyBatis的SQL执行过程，以满足复杂的业务需求和性能优化的目标。在实际应用中，`StatementHandler` 通常配合其他MyBatis组件（如 `Executor` 和 `ResultHandler`）共同工作，以实现高效和灵活的数据访问。

* BaseStatementHandler:用于实现StatementHandler中子类公用的方法
* RoutingStatementHandler：处理具体的组件\
* PreparedStatementHandler:用于处理带有参数的sql
* CallableStatementHandler:用于处理带有存储过程的sql
* SimpleStatementHandler:用于处理不带参数的sql

## 路由策略的计算



```java
 public void doRouter(String dbKeyAttr) {
   		// 所有的表数量,要保证表的数量是2次幂,因为要保证下面获取idx散列更加均匀
        int size = dbRouterConfig.getDbCount() * dbRouterConfig.getTbCount();
   		// 计算路由到哪张表 0 - size 范围，这个idx很重要！！
        int idx = (size - 1) & (dbKeyAttr.hashCode() ^ (dbKeyAttr.hashCode() >>> 16));
		// 计算哪个库,idx / 表的数量 对应的库，+1是因为库是从1号库开始算的
        int dbIdx = idx / dbRouterConfig.getTbCount() + 1;
   		// 计算哪个表,dbIdx - 1是因为上面是+1，因此要重新校验库
   		// dbRouterConfig.getTbCount() * (dbIdx - 1) 这个公式代表当前库前面需要跳过哪些表
   		// idx - dbRouterConfig.getTbCount() * (dbIdx - 1) 得出当前库中的第几张表
        int tbIdx = idx - dbRouterConfig.getTbCount() * (dbIdx - 1);
		

        DBContextHolder.setDBKey(String.format("%02d", dbIdx));
        DBContextHolder.setTBKey(String.format("%03d", tbIdx));
        logger.debug("数据库路由 dbIdx：{} tbIdx：{}",  dbIdx, tbIdx);
    }
```

### 为什么需要2次幂？



因为这里使用了Map中的哈希散列原理，2次幂是为了让散列更加均匀。

& : 两边都为1才为1

举个例子：

如果size为奇数，key & size-1, 奇数会成为偶数，低位会一直是0，因为&的机制是两个为1才为1，因此计算的哈希值会一直是偶数。会导致桶上面会有一半都浪费

如果size为偶数，key & size-1 ，虽然低位不会是0了，但是也会低几位的情况会有0的存在。相比size为奇数情况下会少浪费一些，但是还是不能更好的散列。

**这里看二次幂二进制的情况**

4  4-1 : 11

8  8-1 : 111

16 16-1: 1111

32 32-1 : 11111

**因此取2次幂来作为key是为了保证位上面都是1的情况，更好的散列**

### int tbIdx = idx - dbRouterConfig.getTbCount() * (dbIdx - 1);

我认为这一行代码很难理解，这一行代码的意思是由上面idx散列到哪一张表，这里求的是这一张表在哪个库中的第几张表。

 (dbIdx - 1) 是为了校验dbIdx，因为上面+1了

dbRouterConfig.getTbCount() * (dbIdx - 1) 计算需要跳过的表，白话来说就是要把不属于当前库的表都略过才是当前库

idx - dbRouterConfig.getTbCount() * (dbIdx - 1) 整体就是当前的表 - 需要略过的表 = 当前库中的表



## SPI机制

在SPI机制中，服务提供者提供实现了某个接口的类，并在resources/META-INF/services目录下创建一个以该接口全限定名命名的文件，在这个文件中列出具体的全限定实现类名。服务使用者通过标准API获取服务的实现时，通过查找到相关的SPI配置文件，就可以加载并初始化相应的服务实现。

Java中的SPI（Service Provider Interface）和Spring Boot中的SPI机制都是为了实现模块化和可插拔的设计，但它们的使用场景和实现方式有很大不同。下面是对这两者的详细对比和介绍。

### Java SPI（Service Provider Interface）

#### 概念
Java SPI 是一种服务发现机制，旨在为框架或应用程序提供可插拔的组件。通过 SPI，应用程序可以在运行时动态加载具体的实现类。

#### 实现步骤
1. **定义服务接口**：
   创建一个接口，作为服务的抽象定义。
   ```java
   public interface MyService {
       void execute();
   }
   ```

2. **实现服务接口**：
   创建服务接口的具体实现类。
   ```java
   public class MyServiceImpl implements MyService {
       @Override
       public void execute() {
           System.out.println("Executing service");
       }
   }
   ```

3. **创建服务提供者配置文件**：
   在 `META-INF/services` 目录下创建一个文件，文件名为接口的全限定名，内容为实现类的全限定名。
   ```
   META-INF/services/com.example.MyService
   ```
   文件内容：
   ```
   com.example.MyServiceImpl
   ```

4. **加载服务**：
   使用 `ServiceLoader` 动态加载服务实现。
   ```java
   ServiceLoader<MyService> loader = ServiceLoader.load(MyService.class);
   for (MyService service : loader) {
       service.execute();
   }
   ```

#### 优点
- 简单直接，JDK自带支持。
- 适用于一些简单的可插拔机制。

#### 缺点
- 配置文件容易出错，缺乏编译时检查。
- 不支持依赖注入，无法管理复杂的依赖关系。

### Spring Boot SPI

#### 概念
Spring Boot的SPI机制主要体现在其自动配置功能上，通过一系列的配置文件和注解，Spring Boot能够在运行时自动配置应用程序的各种组件。这种机制极大地简化了开发过程，增强了应用的可扩展性和可维护性。

#### 实现步骤
1. **定义自动配置类**：
   创建一个带有 `@Configuration` 注解的类，用于定义需要自动配置的Bean。
   ```java
   @Configuration
   public class MyAutoConfiguration {
       @Bean
       public MyService myService() {
           return new MyServiceImpl();
       }
   }
   ```

2. **创建 `spring.factories` 文件**：
   在 `META-INF` 目录下创建 `spring.factories` 文件，声明自动配置类。
   ```
   org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
   com.example.MyAutoConfiguration
   ```

3. **激活自动配置**：
   当Spring Boot应用启动时，`spring.factories` 中配置的自动配置类会被自动加载和应用。

#### 优点
- 强大的依赖注入和依赖管理能力。
- 丰富的配置和条件装配（如 `@Conditional` 注解）。
- 自动配置极大地简化了开发过程。

#### 缺点
- 学习曲线较陡，需要一定的Spring和Spring Boot知识。
- 过度使用可能导致配置复杂度增加。

### 对比

| 特性           | Java SPI                         | Spring Boot SPI                        |
| -------------- | -------------------------------- | -------------------------------------- |
| **目的**       | 服务发现和加载可插拔实现类       | 自动配置和模块化开发                   |
| **实现方式**   | `ServiceLoader` + 配置文件       | 自动配置类 + `spring.factories` 文件   |
| **依赖注入**   | 不支持                           | 支持                                   |
| **依赖管理**   | 手动管理                         | 自动管理                               |
| **配置灵活性** | 低                               | 高，支持条件配置和多种配置方式         |
| **适用场景**   | 简单的插件机制                   | 复杂的企业级应用开发和配置管理         |
| **优点**       | 简单直接，JDK自带支持            | 强大的依赖注入、自动配置和条件装配能力 |
| **缺点**       | 配置文件容易出错，缺乏编译时检查 | 学习曲线较陡，过度使用可能增加复杂度   |

### 总结

Java SPI 和 Spring Boot SPI 机制各有优劣，适用于不同的场景。Java SPI 适合简单的服务发现和加载场景，而 Spring Boot SPI 则适合复杂的企业级应用开发，提供了更强大的依赖注入和配置管理能力。选择哪种机制需要根据具体的应用场景和需求来决定。
