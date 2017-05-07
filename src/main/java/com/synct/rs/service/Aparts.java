package com.synct.rs.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import com.google.gson.*;
import com.synct.rs.data.BmDao;
import com.synct.rs.data.u4dao;
import com.synct.rs.data.u3;
import com.synct.rs.data.u4;

@Path("/aparts")
public class Aparts {

    @GET
    @Path("{desc}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchBy(@PathParam("desc") String desc, @QueryParam("usr") String usr,@QueryParam("pwd") String pwd )  throws Exception {
        // fetch 
        //return new Notification(desc, "Rise and shine.");
        //return Response
		//   .status(200)
		//   .entity("path desc:"+desc+" usr : " + usr + ", pwd : " + pwd
		//	).build();
        //    
        //return  Auth( usr, pwd);   
        //return new u4().getAll(desc);
        /*
        if (!(new BmDao().Auth( usr, pwd)))
            return Response
           .status(401)
           .entity(" user : " + usr + ", pwd : " + pwd +" not valid"
          ).build();

        Gson gson = new Gson();
        List<u4> u = new u4dao().getAll(desc);
        String json = gson.toJson(u);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();


        */
        Gson gson = new Gson();
        List<u3> u = new BmDao().getU3(usr,desc);
        String json = gson.toJson(u);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();       

    }

}
