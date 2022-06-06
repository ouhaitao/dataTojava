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

# 演示代码
见测试用例