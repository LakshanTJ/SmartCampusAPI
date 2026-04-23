package com.smartcampus.exception.mappers;

import com.smartcampus.exception.RoomNotEmptyException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;

@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {
    @Override
    public Response toResponse(RoomNotEmptyException e) {
        return Response.status(409)
            .entity("{\"error\":\"Conflict\",\"message\":\"" + e.getMessage() + "\"}")
            .build();
    }
}