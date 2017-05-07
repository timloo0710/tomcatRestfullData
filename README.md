# tomcatRestfullData
資料交換，透過RESFULL的方式。傳回JSON格式的DATA

連ORACLE 8/9。
JDBC DRIVER , 因為ORACLE 政策，不放在一般的常用的 網上儲存庫，
自建在LOCAL 端的 倉庫。

有IP 管控。
有上班時間管控。

簡單的帳號/密碼管控。傳參數方式。

連線資訊，放在RESOURCES下的SITE.PROPERTIES裏。要換資料庫，在此設定。

用jersey庫來解析符合RESTFUL 格式的REQUEST。
在WEB.XML裏設定。

用google.gson庫，轉換資料庫MODEL DATA變成 JSON 格式的DATA。

在service目錄下的程式，就是做這些事。

及解析用戶端的需求ＵＲＬ參數及ＭＥＴＨＯＤ方法。

在data 目錄下，分兩類的，一是和來源ＴＡＢＬＥ，對應的ＣＬＡＳＳ,(orm 對應關係)，就是要交換ＤＡＴＡ的那些欄位field。
不必把所有欄位都抄上來。用lombok減少set 和　get 的code. 欄位多時，省不少力。

用jdbi　這個老牌的程式庫，把JDBC 連上ＤＢ後的傳回ＤＡＴＡ，按欄位轉進對應的class. 




