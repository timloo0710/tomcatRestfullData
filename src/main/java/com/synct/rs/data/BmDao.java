package com.synct.rs.data;

import java.util.*;
import java.sql.*;
import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.util.StringMapper;

import oracle.jdbc.pool.OracleDataSource;
import com.synct.rs.data.bmBase;
import com.synct.rs.data.bmP01;
import com.synct.rs.data.bmP02;
import com.synct.rs.data.bmP03;
import com.synct.rs.data.bmP04;
import com.synct.rs.data.Lan;
import com.synct.rs.data.bmStair;
import com.synct.rs.data.u3;
import com.synct.rs.data.u4;
import com.synct.rs.data.decBm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BmDao {

private Handle h;

 public BmDao() throws SQLException,IOException  {

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("/site.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        String dbusr = properties.getProperty("SynctConn.user");
        String dbpwd = properties.getProperty("SynctConn.password");
        String url = properties.getProperty("SynctConn.url");

		OracleDataSource ds = new OracleDataSource(); //JdbcConnectionPool.create("jdbc:oracle:thin:@192.168.0.10:1521:winbig5","HCGBM","HCGBM");
		ds.setUser( dbusr ); //
    	ds.setPassword( dbpwd ); //
    	ds.setURL( url ); //
		DBI dbi = new DBI(ds);
		h = dbi.open(); //Handle h


    }

    public boolean Auth(String usr,String pwd) {
        //String sql = "select count(*) cnt from  bwuser where usrid = '"+usr +"' and passwd = '"+pwd+ "'";
   		String cnt = h.createQuery("select count(*) from  bwuser where usrid = :usr and passwd = :pwd ")
                    .bind("usr", usr)
                    .bind("pwd", pwd)
                    .map(StringMapper.FIRST)
                    .first();   
    

       if ( Integer.parseInt(cnt.trim()) == 0 ) 
       	return false;
       else
       	return true;
    }



    public boolean AllowedIp(String usr,String ip) {
      System.out.println("#### ip #####"+ip);
      String cnt = h.createQuery("select count(*) from  bwuser where usrid = :usr and LINKIP = :ip ")
                    .bind("usr", usr)
                    .bind("ip", ip)
                    .map(StringMapper.FIRST)
                    .first();   
    

       if ( Integer.parseInt(cnt.trim()) == 0 ) 
        return false;
       else
        return true;
    }

    public boolean Freq(String usr) {
      java.util.Date date = new java.util.Date();   // given date
      Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
      calendar.setTime(date);   // assigns calendar to given date 
      int hour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format    not real time '2'
      System.out.println("#### hour #####"+hour);
      String cnt = h.createQuery("select count(*) from  bwuser where usrid = :usr and FREQUENCY = '1' ")
                    .bind("usr", usr)
                    .map(StringMapper.FIRST)
                    .first();   

       if ( Integer.parseInt(cnt.trim()) == 0 ) 
        //after 8 p.m., before 6 a.m.
        if (hour>=20 || hour<=6)
          return true;
        else
          return false;  
       else 
        return true;
    }


    private String getRole(String usr) { //,String pwd
      String role =  h.createQuery("select roleid from  bwuser where usrid = :usr ") //and passwd = :pwd .bind("pwd", pwd)
                      .bind("usr", usr)
                      .map(StringMapper.FIRST)
                      .first();   
      return role;                
    }


public List<String> getKeys(String desc ,String kind )
  {
  	List<String> ks ;
  	String sql = "";
  	if (kind.trim() == "b1")
  	{
     sql = "select distinct INDEX_KEY from bm_base where LICENSE_kind='1' and LICENSE_DESC like :desc";
    }
    else if (kind.trim() == "b2")
  	{
     sql = "select distinct INDEX_KEY from bm_base where LICENSE_kind='3' and LICENSE_DESC like :desc";
    }
    else if (kind.trim() == "bd1")
  	{
     sql = "select distinct INDEX_KEY from bm_p01 where  Comb_Addr1(ADDRADR_DESC,ADDRAD1,ADDRAD2,ADDRAD3,ADDRAD4,ADDRAD5,ADDRAD6,ADDRAD6_1,ADDRAD7,ADDRAD7_1,ADDRAD8) like  :desc ";
    }
    else if (kind.trim() == "bd2")
  	{
     sql = "select distinct INDEX_KEY from bm_p01 where  NAME like :desc ";
    }
     else if (kind.trim() == "ln")
  	{
     sql = "select distinct INDEX_KEY from bm_lan where GETLANNO(DIST,SECTION,ROAD_NO1,ROAD_NO2) like :desc ";
    
    }
	ks =h.createQuery(sql).bind("desc", "%"+desc+"%").map(StringMapper.FIRST).list();
	h.close();
	return ks;
  }

 

 public List<u3> getU3(String usr,String t_name ) throws Exception
  {
  	
    String sql = getSql( usr,"A") ;
    System.out.println("#########"+sql);
    List<u3> u = h.createQuery(sql).bind("t_name", "%"+t_name+"%").map(u3.class).list();
    System.out.println("#########"+u);
	//h.close();
	return u;
  }

 public List<u4> getU4(String usr,String addr) throws Exception
  {
  	
    String sql = getSql( usr,"B") ;
    List<u4> u =h.createQuery(sql).bind("addr", "%"+addr+"%").map(u4.class).list();
	h.close();
	return u;
  }

 public List<decBm> getdecBm(String usr,String d_name) throws Exception
  {
  	
    String sql = getSql( usr,"C") ;
    List<decBm> u =h.createQuery(sql).bind("d_name", "%"+d_name+"%").map(decBm.class).list();
	  h.close();
	  return u;
  }


    public String getSql(String usr,String type) throws Exception {
    	String role = getRole(usr); //, pwd
    	String sFields = "",sql = "",sqlr = "";
    	sFields = "SELECT distinct f.FIELD FROM BWFIELDSRIGHT r, BWFIELDS f  where r.FID = f.FID  AND (r.ROLEID= :role OR r.ROLEID= :usr ) AND f.TBL = "; //'UMVW_4G_U3'  "
    	if (type.trim() == "A")
    		sql = sFields +" 'UMVW_4G_U3' ";
    	else if (type.trim() == "B")
    		sql = sFields +" 'UMVW_4G_U4' ";
    	else
    		sql = sFields +" 'DECBM' ";

    	List<String>lsFs = h.createQuery(sql)
                            .bind("role", role)
                            .bind("usr", usr)
                            .map(StringMapper.FIRST).list();

        for (int i = 0; i < lsFs.size(); i++) {
            String tmp = lsFs.get(i);
            if ( tmp.trim().equals("DECARE"))
               sqlr =  " Comb_Addr1(GETZON(DECARE), (DECAR1||DECAD1), DECAD2,  DECAD3, DECAD4, DECAD5, DECAD6,DECAD6_1,DECAD7,' ',' ') DECARE,"+sqlr;
            else  
             sqlr =  lsFs.get(i)+","+sqlr;
            }
        if (sqlr.length()==0) 
        {   
          sqlr="No Fields";
          //throw new Err(Status.NON_AUTHORITATIVE_INFORMATION);
        }  
        else
        {  
        sqlr = sqlr.substring(0,sqlr.length()-1 );    
        }

    	if (type.trim() == "A")
    		sqlr = "SELECT "+ sqlr +" FROM UMVW_4G_U3 WHERE T3111NAME LIKE  :t_name ";
    	else if (type.trim() == "B")
    		sqlr = "SELECT "+ sqlr +" FROM UMVW_4G_U4 WHERE ADDRESS LIKE :addr ";
    	else
    		sqlr = "SELECT "+ sqlr +" FROM DECBM WHERE DECNAM LIKE :d_name";

    	System.out.println("#################:"+ sqlr );  

        return sqlr;

    }

    public List<String> getUserSql(String usr,String pwd) throws Exception {
   List<String> sqls=new ArrayList<String>(); 	
 try  { 

        String sqlBase = "";
        String sqlP01 = "";
        String sqlP02 = "";
        String sqlP03 = "";
        String sqlP04 = "";
        String sqlLan = "";
        String sqlStair = "";

        String role = getRole(usr); //, pwd
        if (role != null && !role.isEmpty())
        {
          //BASE
          String sSel = "SELECT distinct f.FIELD FROM BWFIELDSRIGHT r, BWFIELDS f  where r.FID = f.FID AND f.TBL=";
          String sWhr = "  AND (r.ROLEID= :role OR r.ROLEID= :usr )";
          List<String>lsbs = h.createQuery(sSel+"'BM_BASE'"+ sWhr)
                              .bind("role", role)
                              .bind("usr", usr)
                              .map(StringMapper.FIRST).list();

          for (int i = 0; i < lsbs.size(); i++) {
            String tmp = lsbs.get(i);
            if ( tmp.trim().equals("AIRRAID_U_AREA"))
               sqlBase =  " (AIRRAID_U_AREA+AIRRAID_D_AREA) AIRRAID_U_AREA,"+sqlBase;
            else if ( tmp.trim().equals("USE_CATEGORY_CODE1"))
               sqlBase =  " (USE_CATEGORY_CODE_DESC) USE_CATEGORY_CODE1,"+sqlBase;
            else if ( tmp.trim().equals("PECT_DATE"))
               sqlBase =  " ( GETPECT_DATE( :i_key )) PECT_DATE,"+sqlBase;
            else if ( tmp.trim().equals("RR_INVDATE"))
               sqlBase =  " ( GETRR_INVDATE( :i_key )) RR_INVDATE,"+sqlBase;
            else  
               sqlBase =  lsbs.get(i)+","+sqlBase;
          }
          if (sqlBase.length()==0) 
          {   
            sqlBase="No Fields";
          }  
          else
          {  
            sqlBase = sqlBase.substring(0,sqlBase.length()-1 );    
            sqlBase="SELECT "+ sqlBase +" FROM bm_base WHERE INDEX_KEY = :i_key";
          }
          
          //P01
          List<String> ls1 = h.createQuery(sSel+"'BM_P01'"+ sWhr)
                              .bind("role", role)
                              .bind("usr", usr)
                              .map(StringMapper.FIRST).list();

          for (int i = 0; i < ls1.size(); i++) {
            String tmp = ls1.get(i);  
            //System.out.println( tmp );    
            if ( tmp.trim().equals("ADDRADR") ) //==
               sqlP01 =  " Comb_Addr1(ADDRADR_DESC,ADDRAD1,ADDRAD2,ADDRAD3,ADDRAD4,ADDRAD5,ADDRAD6,ADDRAD6_1,ADDRAD7,ADDRAD7_1,ADDRAD8) addradr ,"+sqlP01;
            else  if ( tmp.trim().equals("BUILDING_NO"))
               sqlP01 =  " Comb_cdfh(BUILDING_NO, CHWANG, DONG, FLOOR, HOUSE)  building_no,"+sqlP01;
            else  
               sqlP01 =  ls1.get(i)+","+sqlP01;
          }
          if (sqlP01.length()==0) 
          {   
            sqlP01="No Fields";
          }  
          else
          {  
            sqlP01 = sqlP01.substring(0,sqlP01.length()-1 );    
            sqlP01="SELECT "+ sqlP01 +" FROM bm_P01 WHERE INDEX_KEY = :i_key";
          }  
          //System.out.println( sqlP01 );  
          //P02   
          List<String> ls2 = h.createQuery(sSel+"'BM_P02'"+sWhr)
                              .bind("role", role)
                              .bind("usr", usr)
                              .map(StringMapper.FIRST).list();

          for (int i = 0; i < ls2.size(); i++) {
               sqlP02 =  ls2.get(i)+","+sqlP02;
              }
          if (sqlP02.length()==0) 
          {   
            sqlP02="No Fields";
          }  
          else
          {  
            sqlP02 = sqlP02.substring(0,sqlP02.length()-1 );    
            sqlP02="SELECT "+ sqlP02 +" FROM bm_P02 WHERE INDEX_KEY = :i_key";
          }  
          //System.out.println( sqlP02 );  
          //P03 
          List<String> ls3 = h.createQuery(sSel+"'BM_P03'"+sWhr)
          .bind("role", role)
          .bind("usr", usr)
          .map(StringMapper.FIRST).list();

          for (int i = 0; i < ls3.size(); i++) {
               sqlP03 =  ls3.get(i)+","+sqlP03;
          }
          if (sqlP03.length()==0) 
          {   
            sqlP03="No Fields";
          }  
          else
          {  
            sqlP03 = sqlP03.substring(0,sqlP03.length()-1 );    
            sqlP03="SELECT "+ sqlP03 +" FROM bm_P03 WHERE INDEX_KEY = :i_key";
          }  
          //System.out.println( sqlP03 );  

          //P04 
          List<String> ls4 = h.createQuery(sSel+"'BM_P04'"+sWhr)
          .bind("role", role)
          .bind("usr", usr)
          .map(StringMapper.FIRST).list();

          for (int i = 0; i < ls4.size(); i++) {
               sqlP04 =  ls4.get(i)+","+sqlP04;
          }
          if (sqlP04.length()==0) 
          {   
            sqlP04="No Fields";
          }  
          else
          {  
            sqlP04 = sqlP04.substring(0,sqlP04.length()-1 );    
            sqlP04="SELECT "+ sqlP04 +" FROM bm_P04 WHERE INDEX_KEY = :i_key";
          }  
          //System.out.println( sqlP04 );  

          //Lan 
          List<String> lsn = h.createQuery(sSel+"'BM_LAN'"+sWhr)
          .bind("role", role)
          .bind("usr", usr)
          .map(StringMapper.FIRST).list();

          for (int i = 0; i < lsn.size(); i++) {
            String tmp = lsn.get(i);
            //System.out.println( tmp );    
            if ( tmp.trim().equals("LAN"))
               sqlLan =  " GETLANNO(DIST,SECTION,ROAD_NO1,ROAD_NO2) lan,"+sqlLan;
            else  
               sqlLan =  lsn.get(i)+","+sqlLan;
          }
          if (sqlLan.length()==0) 
          {   
            sqlLan="No Fields";
          }  
          else
          {  
            sqlLan = sqlLan.substring(0,sqlLan.length()-1 );    
            sqlLan="SELECT "+ sqlLan +" FROM bm_LAN WHERE INDEX_KEY = :i_key";
          }
          //System.out.println( sqlLan );   
          //Stair 
          List<String> lsr = h.createQuery(sSel+"'BM_STAIR'"+sWhr)
          .bind("role", role)
          .bind("usr", usr)
          .map(StringMapper.FIRST).list();

          for (int i = 0; i < lsr.size(); i++) {
            String tmp = lsr.get(i);
            //System.out.println( tmp );    
            if ( tmp.trim().equals("STORY_CODE"))
               sqlStair =  " GETSTC(STORY_CODE) STORY_CODE,"+sqlStair;
            else  
               sqlStair =  lsr.get(i)+","+sqlStair;
          }
          if (sqlStair.length()==0) 
          {   
            sqlStair="No Fields";
          }  
          else
          {  
            sqlStair = sqlStair.substring(0,sqlStair.length()-1 );        
            sqlStair="SELECT "+ sqlStair +" FROM bm_STAIR WHERE INDEX_KEY = :i_key";
          }
          //System.out.println(  sqlStair  );   
        }
        // else
        //{
        //}
        sqls.add(sqlBase);
        sqls.add(sqlP01);
        sqls.add(sqlP02);
        sqls.add(sqlP03);
        sqls.add(sqlP04);
        sqls.add(sqlLan);
        sqls.add(sqlStair);      
    } //try 
    finally
    {  
    	h.close();
    	return sqls;    
    }
}

  public List<bmBase> getAllData(List<String>keys,List<String>sqls) throws Exception {
    List<bmBase> bbs=new ArrayList<bmBase>();
    try  {  
            
            for(String i_key : keys) {
              String tmp = sqls.get(0).trim();
              if (tmp=="No Fields")  
                tmp= "select license_desc from bm_base where index_key = :i_key";//sqls.get(0)

               List<bmBase> lsBase = h.createQuery(tmp)  
                .bind("i_key", i_key).map(bmBase.class).list();
                
                for (bmBase m : lsBase)  
                {  
                  bmBase bb = m;//mapper.convertValue(m, bmBase.class); //new bmBase();  
                  //System.out.println(  bb  );  
                  tmp = sqls.get(1).trim();
                  if (!(tmp=="No Fields"))
                  {   
                     List<bmP01> p1s = h.createQuery(sqls.get(1))
                      .bind("i_key", i_key).map(bmP01.class).list();
                      bb.setbmP01s(p1s);
                  }
                  tmp = sqls.get(2).trim();
                  if (!(tmp=="No Fields"))
                  {                     
                     List<bmP02> p2s = h.createQuery(sqls.get(2))
                      .bind("i_key", i_key).map(bmP02.class).list();
                      bb.setbmP02s(p2s);
                  }
                  tmp = sqls.get(3).trim();
                  if (!(tmp=="No Fields"))
                  {                     
                     List<bmP03> p3s = h.createQuery(sqls.get(3))
                      .bind("i_key", i_key).map(bmP03.class).list();
                      bb.setbmP03s(p3s);
                   }
                  tmp = sqls.get(4).trim();
                  if (!(tmp=="No Fields"))
                  {                     
                     List<bmP04> p4s = h.createQuery(sqls.get(4))
                      .bind("i_key", i_key).map(bmP04.class).list();
                      bb.setbmP04s(p4s);
                  }
                  tmp = sqls.get(5).trim();
                  if (!(tmp=="No Fields"))
                  {                     
                     List<Lan> lans = h.createQuery(sqls.get(5))
                      .bind("i_key", i_key).map(Lan.class).list();
                      bb.setLans(lans);
                  }
                  tmp = sqls.get(6).trim();
                  if (!(tmp=="No Fields"))
                  {                     
                     //System.out.println( "sqls.get(5):"+ sqls.get(5)  );
                     List<bmStair> trs = h.createQuery(sqls.get(6))
                      .bind("i_key", i_key).map(bmStair.class).list();
                      bb.setbmStairs(trs);
                    }  
                      bbs.add(bb);
                } // for base 
            } //for key
      return bbs;
    } //try 
    finally
    {  
    	h.close();
    	//return sqls;    
    }


  }



}