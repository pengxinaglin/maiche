
 在 project 的 Applaction 类的 onCreate() 方法中，调用
 PayDebug.initEviroment(Debug.EVIROMENT);


   调用结算
   SettlementIntent settlementIntent = new SettlementIntent(MainActivity.this);
   settlementIntent.setAppToken("da203d23a205abe88d43c93946ab2fec20c0c87b");    //必传，网络请求的appToken
   settlementIntent.setCrmUserId("185");                                        // 必传，crm_user_id
   settlementIntent.setCrmUserName("杨明");                                      //必传，crm_user_name
   settlementIntent.setCustomerName("杨先生");                                  //必传 客户电话号码
   settlementIntent.setCustomerPhone("18811773062");                            //必传 客户电话号码
   settlementIntent.setPrice("20000");                                          // 应收金额，单位元
   settlementIntent.setTaskId("YD00123");                                       // 非必传 业务订单号，没有不传
   settlementIntent.setTaskType(1);                                             // 任务类型，1c2c交易 2回购 3金融
   settlementIntent.setFromBusiness(true);                                      //是否从业务方调用
   settlementIntent.setCashEnable(true);                                        //是否可以使用现金收款，不传默认false，不使用现金
   settlementIntent.setComment(true);                                           //设置备注
   startActivityForResult(settlementIntent, 0);


   线上收款
 OnlinePayIntent intent = new OnlinePayIntent(MainPageActivity.this);
                intent.setCrmUserId("" + GlobalData.userDataHelper.getUser().getId());
                intent.setCrmUserName(GlobalData.userDataHelper.getUser().getName());
                intent.setAppToken(GlobalData.userDataHelper.getLastAppToken());
                startActivity(intent);