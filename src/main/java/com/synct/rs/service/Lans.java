package com.synct.rs.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import java.sql.*;
import com.synct.rs.data.BmDao;


@Path("/lans")
public class Lans {
    @GET
    @Path("{desc}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchBy(@PathParam("desc") String desc, @QueryParam("usr") String usr,@QueryParam("pwd") String pwd ) throws Exception {
      if (!(new BmDao().Auth( usr, pwd)))
            return Response
           .status(401)
           .entity(" user : " + usr + ", pwd : " + pwd +" not valid"
          ).build();

        Gson gson = new Gson();
        String json = gson.toJson(new BmDao().getAllData(new BmDao().getKeys(desc,"ln"),new BmDao().getUserSql( usr,pwd)));
        return Response.ok(json, MediaType.APPLICATION_JSON).build();    }
}
