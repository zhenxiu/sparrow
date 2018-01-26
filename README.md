- DO（Data Object）：与数据库表结构一一对应，通过DAO层向上传输数据源对象。 
- DTO（Data Transfer Object）：数据传输对象，Service和Manager向外传输的对象。 
- BO（Business Object）：业务对象。可以由Service层输出的封装业务逻辑的对象。 
- QUERY：数据查询对象，各层接收上层的查询请求。注：超过2个参数的查询封装，禁止使用Map类来传输。  
- VO（View Object）：显示层对象，通常是Web向模板渲染引擎层传输的对象

- 方法名、参数名、成员变量、局部变量都统一使用lowerCamelCase风格，必须遵从 驼峰形式。
      
- 类名使用UpperCamelCase风格，必须遵从驼峰形式，但以下情形例外：（领域模型 的相关命名）DO / DTO / VO / DAO等。 