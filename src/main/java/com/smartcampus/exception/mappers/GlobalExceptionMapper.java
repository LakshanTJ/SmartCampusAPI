package com.smartcampus.exception.mappers;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());
    
    @Override
    public Response toResponse(Throwable e) {
        LOGGER.severe("CAUGHT EXCEPTION: " + e.getClass().getName() + " - " + e.getMessage());
        e.printStackTrace();
        return Response.status(500)
            .entity("{\"error\":\"Internal Server Error\",\"message\":\"" + e.getMessage() + "\"}")
            .build();
    }
}