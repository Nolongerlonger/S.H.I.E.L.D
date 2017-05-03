##介绍

S.H.I.E.L.D是一个由java编写的美剧订阅系统，通过java爬虫实现对人人美剧的搜索以及订阅，部署在能够联网的计算机上面之后，通过新增订阅功能，提供订阅的email，就能够完成对美剧的订阅，当有新的美剧上线时候能够发送订阅更新邮件到订阅邮箱之中

##配置文件相关

###系统配置文件 system.config

位于程序根目录下
第一行第一个数字，0代表自动开始模式，1代表手动开始模式，手动模式下能够使用搜索功能、以及新增监听
第一行第二个数字，代表多线程池的容量，默认是19
    
    0 19
    [mailAdress] [linkUrl]
    [mailAdress] [linkUrl]
    [mailAdress] [linkUrl]
    [mailAdress] [linkUrl]

###用户配置文件 userData/${userMailAddress}.log

    startTime:          //代表开始时间
    linkUrl：            //订阅的url
    mail:               //代表订阅的邮箱
    mailNum:            //已经发送的邮件的数量
    
    mail-0：
    ......               //mail-0的具体内容
    
    mail-1:
    ......               //mail-1的具体内容
    
##日志系统

###系统错误日志 error.log

S.H.I.E.L.D系统内置了一个简单的日志操作系统

    #发生错误的代码的类名_具体的方法名+具体的错误信息+日志生成时间
    LogUtil_readAUserLog+\t\t+错误信息+自动打印的时间
    
###系统错误代号

    -1      系统无法新建错误日志文件          系统无法启动            可尝试手动创建userData/error.log文件
    -2      系统无法新增一行错误日志          系统退出
    -3      系统无法新建system.config文件    系统退出               可尝试手动在jar文件夹下新建配置文件
    -4      系统找不到system.config文件      系统退出






