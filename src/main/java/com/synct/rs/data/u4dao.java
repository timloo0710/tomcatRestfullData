package com.synct.rs.data;


import java.util.*;
import java.sql.*;
import org.skife.jdbi.v2.*;

import com.synct.rs.data.u4;
import oracle.jdbc.pool.OracleDataSource;

public class u4dao {

 private Handle h;

 public u4dao() throws SQLException  {

		OracleDataSource ds = new OracleDataSource(); //JdbcConnectionPool.create("jdbc:oracle:thin:@192.168.0.10:1521:winbig5","HCGBM","HCGBM");
		ds.setUser( "HCGBM" );
    	ds.setPassword( "HCGBM" );
    	ds.setURL( "jdbc:oracle:thin:@192.168.0.10:1521:winbig5" );
		DBI dbi = new DBI(ds);
		h = dbi.open(); //Handle h


    }

  public List<u4> getAll(String desc)
  {
  	  //new ArrayList<u4>(); 
  	//Connection con = null;
  	//String sql = "SELECT LICENSE, LICENSEDATE, T4111ADKINDNAME FROM UMVW_4G_U4 where ADDRESS like  '%"+desc +"%' ";
    String sql =  "SELECT  * FROM UMVW_4G_U4 where ADDRESS like :desc  "; // , LICENSEDATE, T4111ADKINDNAME  new BmDao().get LICENSE
    List<u4> u =h.createQuery(sql).bind("desc", "%"+desc+"%").map(u4.class).list();
	   h.close();

	   return u;

  }


}
