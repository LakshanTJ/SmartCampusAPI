package com.smartcampus.exception.mappers;

import com.smartcampus.exception.SensorUnavailableException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;

@Provider
public class SensorUnavailableMapper
    implements ExceptionMapper<SensorUnavailableException> {
    @Override
    public Response toResponse(SensorUnavailableException e) {
        return Response.status(403)
            .entity("{\"error\":\"Forbidden\",\"message\":\"" + e.getMessage() + "\"}")
            .build();
    }
}