package ru.test;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.test.service.RankerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Produces("application/json; charset=UTF-8")
public class MainRest {

    @EJB
    private RankerService rankerService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @POST @Path(value = "/add")
    public Response addDomain(@Context UriInfo info) {
        String URI = info.getQueryParameters().getFirst("url");
        if (URI == null)
            return getError(400);
        try {
            return getSuccess(rankerService.add(URI));
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            return getError(400);
        }
    }

    @GET @Path(value = "/get/{n : \\d+}")
    public Response getDomains(@PathParam("n") int count) {
        if (count < 1)
            return getError(400);
        count = count > 100 ? 100 : count;
        List<Domain> list = rankerService.get(count);
        return getSuccess(list);
    }

    private String getJSON(Object o) {
        String body;
        try {
            body = objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        return body;
    }

    private Response getError(int code) {
        Map<String, String> map = new HashMap<>();
        map.put("state", "error");
        switch (code) {
            case 400:
                map.put("code", "400 Bad Request");
                break;
            case 404:
                map.put("code", "404 Not Found");
                break;
        }
        return Response.status(code).entity(getJSON(map)).build();
    }

    private Response getSuccess(Object o) {
        Map<String, Object> map = new HashMap<>();
        map.put("state", "success");
        map.put("result", o);
        return Response.status(200).entity(getJSON(map)).build();
    }
}
