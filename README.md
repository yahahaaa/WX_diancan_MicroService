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

问题 2 就是通过注解的方式增加事务失效的问题，下面是老师上课讲述的扣减库存源代码，扣减库存的方式在并发上来的时候可能会有问题，但是不是讨论的重点：

项目中扣减库存的逻辑为通过传入购物车信息（商品ID和购买商品数量），首先查询商品是否存在，判断库存是否充足，然后更新库存，将操作数据库的逻辑和消息发送的逻辑分开，要等到所有的数据库操作完成后再发送消息。

```java
@Override
public void decreaseStock(List<DecreaseStockInput> cartDTOList) {

    //操作数据库，调用下面的方法
    List<ProductInfo> productInfoList = decreaseStockProcess(cartDTOList);

   	int a = 10 / 0;

    //发送mq消,由订单服务接收消息，更改redis中存储的库存信息
    //将productInfo转为productInfoOutput
    List<ProductInfoOutput> results = productInfoList.stream().map(e -> {
        ProductInfoOutput productInfoOutput = new ProductInfoOutput();
        BeanUtils.copyProperties(e, productInfoOutput);
        return productInfoOutput;
    }).collect(Collectors.toList());
    amqpTemplate.convertAndSend("productInfo", JsonUtil.toJson(results));
}


/**
 * 扣减库存,高并发下先查库存然后再减库存有可能会发生超卖问题
 * 将数据库扣减库存提取出来，因为这一段如果出错，数据库内容一起回滚，和mq的逻辑分开
 * @param cartDTOList
 */
@Transactional
public List<ProductInfo> decreaseStockProcess(List<DecreaseStockInput> cartDTOList){
    List<ProductInfo> results = new ArrayList<>();
    for (DecreaseStockInput cartDTO : cartDTOList) {
        Optional<ProductInfo> productInfoOptional = productInfoRepository.findById(cartDTO.getProductId());
        //判断商品是否存在
        if (!productInfoOptional.isPresent()) {
            throw new ProductException(ResultEnum.PRODUCT_NOT_EXIT);
        }

        /** TODO
         * 下面这段代码在并发量高时可能会有超卖问题，
         * 可以在redis中添加一个标志位，首先到redis标志位中判断是否存在，如果存在
         * 使用数据库行锁 通过update语句，首先判断redis - quantity 是否大于0，大于0则更新库存的方式
         * 如果数据库库存小于0不够了，发送消息，令订单服务将redis标志位置为false.
         * 将TODO加入到 v2.0 版本
         */
        //判断库存是否足够
        ProductInfo productInfo = productInfoOptional.get();
        int result = productInfo.getProductStock() - cartDTO.getProductQuantity();
        if (result < 0) {
            throw new ProductException(ResultEnum.PRODUCT_STOCK_ERROR);
        }

        //更新库存
        productInfo.setProductStock(result);
        results.add(productInfo);
        productInfoRepository.save(productInfo);
    }
    return results;
}
```

上示代码中在添加 int a = 10 / 0 , 会在代码运行时抛出异常，此时按照一般理解，我已经在操作数据库的方法上添加了@Transactional注解，如果decreaseStock()方法出现异常，应该会回滚。

但是，事实是数据库并不会回滚，则是由于通过注解的方式添加到方法上，会由动态代理生成一个新的方法对象，这个新的方法上才会具有事务功能，而我们在decreaseStock()方法中调用的decreaseStockProcess()的方法只是一个普通的方法，没有事务功能。所以若想解决这个问题，需要在decreaseStock()方法上同样加入@Transactional注解

### 1.3 问题3

问题 3 是hystrix dashboard 在 windows 平台上无法正常使用的问题，捣鼓了半天，改了半天，结果部署到linux上就可以正常使用，可能是因为版本问题。

项目中采用hystrix做容灾处理，本项目中只针对多次访问超时的接口做了容灾降级处理，想要通过dashboard页面监控下，但是却总是

## 3. 服务追踪

## 4. Rancher 部署

## 5. graylog 搭建

## 6. 下单优化

在下单优化的时候，不能一味的追求极致的异步化，异步化虽然会显著提升性能，但是会导致代码逻辑复杂化，所以我逐步探索在我这种小型的项目中可以比较适宜的方法。

我理解的优化方式有扣减库存先在哪执行，是先在redis中扣减库存再同步到数据库中，还是先扣减数据库中再同步到redis中；异步化的化，是异步化生成订单还是异步化扣减库存，还是全部异步化。如果是先扣减数据库内存再同步到redis，可提前判断库存不足的情况，但是对性能提升有限，需要用数据库乐观锁确保查询修改的原子性；如果是先扣减redis库存，需要用分布式锁确保redis查询修改的原子性，还需要保证数据库的最终一致性，不过最终一致性我们可以交由rabbitmq这种可靠性高的消息中间件，我们只需保证消息消费的幂等性。

### 6.1. 版本1—基础版本

- 调用商品服务通过商品id查询商品信息，若商品不存在抛出异常
- 遍历商品信息，通过商品价格 * 商品数量计算每种商品的价钱，调用订单服务，订单详情入库
- 调用商品服务扣减库存
  - 遍历购物车，通过商品id查询商品信息。
  - 判断库存是否充足，充足修改库存，返回成功，不充足抛出异常。

- 调用订单服务订单入库

### 6.2 版本2--先扣减数据库，异步方式同步redis

- 调用商品服务查询商品信息，若商品不存在抛出异常
- 首先遍历一遍商品信息，通过与redis中存储的库存信息进行比较，若商品数量不足直接抛出异常，若数量充足继续执行（即便由于网络问题导致订单服务消费端消息积压导致实际库存低于redis库存，也会在操作数据库的时候抛出异常）
- 再遍历一遍商品信息，通过商品 价格 * 商品数量计算每种商品的价钱，调用订单服务，订单详情入库
- 调用商品服务扣减库存
  - 将操作数据库的代码单独成一个事务方法
  - 通过乐观锁 update product_info set product_stock - productQuantity where product_id = product_id and product_stock - productQuantity >= 0 扣减库存
  - 返回的结果表示成功修改的数据行数若等于购买商品数量则表示扣减成功，不等表示扣减失败，直接抛出异常
  - 若所有的扣减库存操作成功，通过rabbitmq向订单服务发送消息，订单服务会修改redis库存信息
- 扣减库存成功后订单入库

### 6.3 版本3--先扣减redis库存，异步方式同步到数据库

- 调用商品服务查询商品信息，若商品不存在抛出异常
- 首先遍历一遍商品信息，在redis中扣减库存。扣减库存需注意，redis虽然是单线程操作的，但是我们需要先读取redis库存判断库存是否充足，然后再扣减redis库存，这一系列操作不能保证原子性，需要加锁，而普通的锁在水平扩展多台Tomcat服务器上无法使用，需要利用redis实现一个分布式锁，锁住这一些列操作。扣减库存后订单详情入库
- 通过RabbitMQ发送消息到商品服务异步扣减库存（这里将订单ID一起发送，用于生成去重表主键id）
  - RabbitMQ通过重试机制来保证可靠性，所以为了保证每条消息消费的幂等性，在每个消息消费时，通过订单id生成一个全局唯一主键值插入去重表，若重复消费，由主键的唯一性会抛出异常
  - 扣减库存的方式依然采用上文乐观锁的方式，库存扣减成功后，通过openfeign调用订单接口修改订单状态。
- 发送消息后直接将订单入库，不过需要新设置一个订单状态，当前订单状态为等待中，可以在前端给用户一个友好的等待界面，等待商品服务扣减数据库库存后修改订单状态为新订单后便跳转到支付接口。

我在想，如果不设置等待状态，redis扣减库存后，发送消息，消息发送后直接订单入库跳转支付。需要想方法确保消息一定会被成功消费并且数据库成功扣减库存。不过这些超出我现在能力范围了，以后搞明白了再想想。

