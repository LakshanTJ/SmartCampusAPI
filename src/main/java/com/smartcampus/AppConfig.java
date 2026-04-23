package com.smartcampus;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/v1")
public class AppConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(com.smartcampus.resource.DiscoveryResource.class);
        classes.add(com.smartcampus.resource.RoomResource.class);
        classes.add(com.smartcampus.resource.SensorResource.class);
        classes.add(com.smartcampus.resource.SensorReadingResource.class);
        classes.add(com.smartcampus.exception.mappers.RoomNotEmptyMapper.class);
        classes.add(com.smartcampus.exception.mappers.LinkedResourceNotFoundMapper.class);
        classes.add(com.smartcampus.exception.mappers.SensorUnavailableMapper.class);
        classes.add(com.smartcampus.exception.mappers.GlobalExceptionMapper.class);
        classes.add(com.smartcampus.filter.LoggingFilter.class);
        return classes;
    }
}