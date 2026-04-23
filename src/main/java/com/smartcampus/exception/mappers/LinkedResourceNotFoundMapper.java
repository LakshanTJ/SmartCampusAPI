package com.smartcampus.exception.mappers;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;

@Provider
public class LinkedResourceNotFoundMapper
    implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException e) {
        return Response.status(422)
            .entity("{\"error\":\"Unprocessable Entity\",\"message\":\"" + e.getMessage() + "\"}")
            .build();
    }
}