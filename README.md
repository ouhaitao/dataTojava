# 根据建表SQL自动生成以下业务代码
- DO
- DTO
- 基本CRUD代码
  - controller
  - serviceInterface
  - serviceImpl
  - mapperInterface
  - mapperXml

# 注意事项
- 生成的代码是基于Spring、mybatis
- 各个类、xml的包名必填
- 全局源码输出路径或者各个类、xml的输出路径必填其中一个；优先使用各个类、xml自己的配置
- 业务名称(businessName)或者各个类、xml的名字必填其中一个；如果只填写了业务名称，则会自动生成各个类的、xml的文件名

# 演示结果
详情见测试用例，这里只展示生成的DO、DTO
```sql
-- 建表SQL
CREATE TABLE `user` (
  `id` int(11) NOT NULL COMMENT '用户id',
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
```
```java
public class UserDO {

  /**
   * 用户id
   */
  private Integer id;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;

}
```
```java
public class UserDTO {

  /**
   * 用户id
   */
  private Integer id;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
}
```