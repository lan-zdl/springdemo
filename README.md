##校园论坛

##资料

[Spring文档](https://spring.io/guides)

[Spring Web](https://spring.io/guides/gs/serving-web-content/)

[es](https://elasticsearch.cn/explore)

[Github desploy key](https://docs.github.com/cn/developers/overview/managing-deploy-keys#deploy-keys)

[Bookstrap](https://v3.bootcss.com/getting-started/)

[Github OAuth](https://docs.github.com/cn/developers/apps/building-oauth-apps/creating-an-oauth-app)

[Spring](https://docs.spring.io/spring-boot/docs/2.0.0.RC1/reference/htmlsingle/#boot-features-embedded-database-support)

[Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#setting-attribute-values)


##工具

[git](https://git-scm.com/download)

[Visual Paradigm](https://www.visual-paradigm.com/cn/)

##bash

mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate
执行时出现时区问题：执行set global time_zone='+8:00'


问题：

开始使用注解的方式，

当执行阅读数量增加时，一开始是将数据库中的viewCount提取来进行阅读数增加操作，这样多个用户同时打开这个问题的时候，
阅读量的增加就会出现问题，后面改用直接在数据库中进行update操作，每次访问这个问题数据库中的view_count直接加1