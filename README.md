1.  好车无忧网络请求接口说明
   1. 请求action 设定:
      所有好车无忧的服务接口，请在 HttpConstants 定义请求action String ：
      命名规则 ACTION_<动作(get,post,check,upload....)>_<对象名称(+List)>;
   2. 参数封装:
      参照HCHttpRequestParam 中login 方法,temp：
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("字段名1", username);
      params.put("字段名2", password);
      .....
      return getRequest(HttpConstants.ACTION_NAME,params);
   3. 请求调用:
      AppHttpServer.getInstance().post(HttpRequestParam.<方法>,this); // activity ，fragment 基类中实现 HCHttpCallback接口，有网络请求的子类复写父类中网络请求方法. 实现自己的逻辑；
      senerio one:
        同一个activity ，fragment 中有多个请求。
        则在HCHttpCallBack 对应的回调方法中使用如下方式进行区别
        if (action.equals(HttpConstants.ACTION_NAME)) {
        } else if (action.equals(HttpConstants.ACTION_NAME2)) {
        } ...
       senerio two:
        第三方网络请求请单独封装对应的请求框架
   4. 结果解析:
      HCHttpCallback 方法返回的HCHttpResponse response 对象携带服务器返回的格式化信息,如需读取具体对象信息
      
      Type type = new TypeToken<Class>(){}.getType();
      Class class = response.getData(type);
      example:
       1.Int
            int result = Integer.parseInt(response.getData());
       2. String 
            String result = response.getData();
       3. model CheckTaskEntity(对象)
          Type type = new TypeToken<CheckTaskEntity>(){}.getType();
          CheckTaskEntity checkTask  = response.getData(type);
       4. model list ArrayList<CheckTaskEntity>;
          Type type = new TypeToken<ArrayList<CheckTaskEntity>>(){}.getType();
          ArrayList<CheckTaskEntity> checkTaskList  = response.getData(type);
     /// 

     参考文件
    app/src/main/java/com/haoche51/user/net/AppHttpServer.java
    app/src/main/java/com/haoche51/user/net/HCHttpCallback.java
    app/src/main/java/com/haoche51/user/net/HCHttpRequestParam.java
    app/src/main/java/com/haoche51/user/net/HCHttpResponse.java
    app/src/main/java/com/haoche51/user/net/HCHttpResponseParser.java
    app/src/main/java/com/haoche51/user/net/HttpConstants.java
    迁移完成后请删除垃圾代码

2.编码规范
    1.activity 在com.haoche51.user.activity 下，按照业务功能分包。
    例：
      验车任务列表页。 com.haoche51.user.activity.checktask.ListActivity.java
      详情页为detail.java
      根据网络请求需求，继承CommonTitleBaseActivity
    2.fragment 放在com.haoche51.user.fragment下，按业务分包.同activity
    3. layout:
          a) activity/fragment ,<activity/fragment>_<业务功能名称>.xml
          b) activity/fragment,中包含layout  <layout>_<业务功能名称>
    4. drawable 
          a) icon  ic_<功能>.png
          b) 其他drawable 样式 以实际功能命名<shape/selector/...>_xxxx.xml
    5. java 类，函数前按照android_studio 自动生成的注释。标记清楚 用途，变量定义
    6. 变量、方法命名 
          1.常量:放在常量类中，全部大写.
          2.变量以字母下划线命名 
          3. 函数使用驼峰式命名

    7. layout文件中id命名
       1. <view类型缩写>_<layout文件名>_<业务名>
