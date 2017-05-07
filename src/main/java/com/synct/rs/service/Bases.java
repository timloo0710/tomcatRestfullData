package com.synct.rs.service;

import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.synct.rs.data.BmDao;
import javax.servlet.http.HttpServletRequest; 


@Path("/bases")
public class Bases {
    @GET
    @Path("/build/{desc}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchBy(@PathParam("desc") String desc, @QueryParam("usr") String usr,@QueryParam("pwd") String pwd,@Context HttpServletRequest request )  throws Exception{
        
        String ip = getIpAddr(request);

        if (!(new BmDao().AllowedIp( usr, ip)))
            return Response.status(401).entity(" user : " + usr + ", ip : " + ip +" not allowed").build();

        if (!(new BmDao().Freq( usr)))
            return Response.status(401).entity(" user : " + usr + ", is not allowed at office hours").build();

        if (!(new BmDao().Auth( usr, pwd)))
            return Response.status(401).entity(" user : " + usr + ", pwd : " + pwd +" not valid").build();

        Gson gson = new Gson();
        String json = gson.toJson(new BmDao().getAllData(new BmDao().getKeys(desc,"b1"),new BmDao().getUserSql( usr,pwd)));
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/use/{desc}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchLicBy(@Context HttpServletRequest request,@PathParam("desc") String desc, @QueryParam("usr") String usr,@QueryParam("pwd") String pwd ) throws Exception {
       
        String ip = getIpAddr(request);

        if (!(new BmDao().AllowedIp( usr, ip)))
            return Response.status(401).entity(" user : " + usr + ", ip : " + ip +" not allowed").build();

        if (!(new BmDao().Freq( usr)))
            return Response.status(401).entity(" user : " + usr + ", is not allowed at office hours").build();

        if (!(new BmDao().Auth( usr, pwd)))
            return Response
           .status(401)
           .entity(" user : " + usr + ", pwd : " + pwd +" not valid"
          ).build();
        Gson gson = new Gson();
        String json = gson.toJson(new BmDao().getAllData(new BmDao().getKeys(desc,"b2"),new BmDao().getUserSql( usr,pwd)));
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

  public String getIpAddr(HttpServletRequest request) {
      String ip = request.getHeader("x-forwarded-for");
      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("Proxy-Client-IP");
      }
      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("WL-Proxy-Client-IP");
      }
      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getRemoteAddr();
      }
      return ip;
    }
}
