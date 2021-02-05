# 基于SpringCloud的微信点餐系统

## 1. 简述

该项目是基于我功能完善单机版本的 https://github.com/yahahaaa/WX_diancan_Basic 项目扩展而来，在原来的版本上只保留核心功能，并将各个功能拆分为多个服务。结合我单机版本的微信点餐系统，采用了B2C模式，前后端分离架构。商品浏览页面（微信端）基于vue开发，商家后台页面（PC端）基于Thymeleaf模板引擎开发。对单机版本进行垂直扩展，采用微服务架构，**Basic** 版本的点餐系统卖家端实现微信网页授权登录、商品下单、微信JSAPI支付、取消订单、微信退款并探讨了秒杀的可行方案。卖家端实现微信扫码登录、商品类目增删、商品上下架、接单、查询订单和取消订单等功能。

### 1.1 开发环境

- IDEA
- Maven3.3.9
- SpringBoot2.0.2.RELEASE
- SpringCloud.Finchley
- Centos7
- Docker
- JDK8

### 1.2 技术选型

- MySQL5.7
- Redis3.2.8
- RabbitMQ
- JPA
- Eureka
- RabbitMQ
- Zuul
- Feign
- hystrix+hystrix dashboard
- sleuth + zipkin

### 1.3 技术描述

1. 用户端登录采用微信网页授权，商家后台采用微信扫码登录，支付方式为JSAPI方式。
2. NatApp做内网穿透，fiddler做手机代理并监控请求链路，Postman接口测试，junit4单元测试。
3. 后端分为订单服务、商品服务、用户服务、网关服务、配置中心与注册中心服务（Eureka），核心服务继续拆分为多模块结构。各个模块通过Feign实现接口调用。
4. 接入Hystrix做简单的容灾处理，如对多次请求超时的接口fallback。通过dashboard对服务监控。
5. 通过RabbitMQ实现下单异步化处理提高QPS，redis存储热点数据，并通过分布式锁确保redis操作原子性。
6. 利用sleuth+zipkin实现服务追踪。

## 2. 开发中遇到的问题

### 1.1 问题1

**当前版本使用Spring-Cloud-Bus自动更新配置无效**

自动更新配置理论图：

![image-20210205110338335](https://github.com/yahahaaa/picture/blob/main/%E8%87%AA%E5%8A%A8%E6%9B%B4%E6%96%B0%E9%85%8D%E7%BD%AE%E7%90%86%E8%AE%BA%E5%9B%BE.PNG?raw=true)

项目中为集中管理配置文件，添加统一配置中心服务，用于在项目启动后其他服务通过统一配置中心到GitHub上获取服务配置文件。具体过程为配置中心通过HTTP请求到Github，然后Github通过Webhooks发送POST请求至 /monitor（注意需要通过配置开放该URI） 接口上。统一配置中心在收到POST请求后会在本地创建一份最新的配置文件，并通过RbbitMQ消息中间件发送消息至目标服务，目标服务在收到消息后会刷新配置文件。理论总是美好的，但现实却有问题。

**我如何完成配置并解决问题：**

----

1.config 服务引入依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-monitor</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

----

2.config 服务配置文件bootstrap.yml(截取部分)

```yml
spring:
  application:
    name: config
  cloud:
    config:
      server:
        git:
          uri: https://github.com/Leeeeeming/weixindiancan_config.git
          username: xxxxxxx@qq.com
          password: xxxxxx
          basedir: G:\log\weixindiancai_app\config ## 本地配置文件存储地址
          
## 将全部接口暴露
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

目标服务（以订单服务为例）引入依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

订单服务配置文件（bootstrap.yml）

需要注意的是这个profile变量值，表示的是Github上存储的yml文件名 “ - ” 后面的后缀名，如果文件名为order-test，这里的profile就是test；如果文件名是order-dev，这里的profile就是dev；如果文件名是order-prod，这里的profile是prod；如果是order，什么都不加，就可以用任意的单词代替，这里用a代替。

```yml
spring:
  application:
    name: order
  ## 通过应用名+分支名+环境名定位配置文件
  cloud:
    config:
      discovery:
        enabled: true
        service-id: CONFIG  # 配置中心
      profile: a # 环境名
      label: main # git分支
```

----

3.首先为了能够让GitHub访问到本机，需要使用内网穿透，我使用的natApp，将外网域名映射到了我的本机80端口上：

![image-20210205103540544](https://github.com/yahahaaa/picture/blob/main/natapp.PNG?raw=true)

----

4.新建Github仓库专门存放配置文件，并在设置中修改Webhooks，Github会通过POST请求该url：

![image-20210205105620937](https://github.com/yahahaaa/picture/blob/main/image-20210205105620937.png?raw=true)

----

5.在仓库中添加配置文件，其中order-dev中的dev一般对应配置文件环境变量，就是用来定位配置文件的变量，对应上文中目标服务配置文件的profile的内容。将除了本地配置文件的服务应用名、统一配置中心相关与暴露接口的配置留下，其余的可以上传到Github，并将application.yml改名为bootstrap.yml.

在order.yml中加入一段测试配置

```yml
girl:
  name: lili
  age: 12
```

![image-20210205111642287](https://github.com/yahahaaa/picture/blob/main/github%E9%85%8D%E7%BD%AE.png?raw=true)

在order服务中添加个Controller用来返回 girl 的信息：

```java
@RestController
@RequestMapping("/env")
@RefreshScope //重新刷新类使类内属性重新获取
public class EnvController { 

    @Autowired
    private GirlConfig girlConfig;

    @GetMapping("/girl/print")
    public String getGirlPrint(){
        return "name:" + girlConfig.getName() + "age:" + girlConfig.getAge();
    }
}
```

启动统一配置中心服务，查看natapp后台，webhooks请求成功，内网穿透返回的状态200表示成功

![image-20210205120739441](https://github.com/yahahaaa/picture/blob/main/natapp%E8%AF%B7%E6%B1%82.PNG?raw=true)

----

6.统一配置中心拉取Github远程仓库在本地创建Git仓库将远程配置文件clone到目录中

![image-20210205120528863](https://github.com/yahahaaa/picture/blob/main/%E9%85%8D%E7%BD%AE.PNG?raw=true)

----

7.启动order服务，order启动成功，可以获取到 girls 的配置信息

![image-20210205154951090](https://github.com/yahahaaa/picture/blob/main/girl.PNG?raw=true)

----

8.问题来了，如果我们此时修改Github上的配置，可以发现，本地 Git 已更新，但是无论请求多少次上图中的 print 接口，也不会获取最新的配置信息。在浏览器打开RabbitMQ监控应用，可以看到生成了一个消息总线交换机，并绑定了两个队列，其中一个队列就对应上文中的订单服务。

![image-20210205161146690](https://github.com/yahahaaa/picture/blob/main/%E4%BA%A4%E6%8D%A2%E6%9C%BA.PNG?raw=true)

![image-20210205161242672](https://github.com/yahahaaa/picture/blob/main/%E7%BB%91%E5%AE%9A%E9%98%9F%E5%88%97.PNG?raw=true)

----

9.当Webhooks请求成功后，配置中心就会通过交换机向所有队列广播消息，消费端会确认是否为自己的消息，从而进行消费。当我试图修改Github上配置信息后，打开队列信息界面可以发现，消息确实已经被消费掉，但是依然不论请求多少次 print 接口，变量值都没有变化。

![image-20210205162158018](https://github.com/yahahaaa/picture/blob/main/%E6%B6%88%E8%B4%B9%E6%B6%88%E6%81%AF.PNG?raw=true)

----

10. 所以问题就出在订单服务的 spring-cloud-starter-bus-amqp 组件消费消息时出现了问题。在配置中心添加配置，将spring-cloud-starter-bus-amqp组件的日志级别调整为debug，重新发布webhooks，并注意控制台的打印日志：

    ![image-20210205165709980](https://github.com/yahahaaa/picture/blob/main/%E5%8C%B9%E9%85%8D%E9%94%99%E8%AF%AF.PNG?raw=true)

好像是因为消息匹配错误，这里想要用order:** 去匹配 order : 0 : xxxxxxxx，这里的0的位置好像代指的端口号，前面使用service-name : profile : ** 后面profile的位置变成了端口号，所以导致配置刷新失败。所以我们需要修改订单服务的匹配逻辑，可以通过dubug的方式修改是可以成功的，这里也可以加一个配置进行修改：

```yml
## 自动刷新配置
bus:
  id: ${spring.application.name}:${spring.cloud.config.profile}:${random.value}
```

好了，万事大吉

### 1.2 问题2

问题 2 就是通过注解的方式增加事务失效的问题，其实这个问题以前也遇到过，这次是因为马虎忘掉了，特此记录下来

### 1.3 问题3

问题 3 是hystrix dashboard 在 windows 平台上无法正常使用的问题，捣鼓了半天，改了半天，结果部署到linux上就可以正常使用，可能是因为版本问题。

## 3. 性能优化问题（看情况更新了）

## 4. Rancher 部署问题

