package com.symphony.bot;


import com.symphony.bot.resources.AppAuthResource;
import com.symphony.bot.resources.RFQBotResource;
import com.symphony.bot.resources.RFQInfoResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class RFQApplication extends Application<SymphonyConfiguration> {
    public static void main(String[] args) throws Exception {
        new RFQApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<SymphonyConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );


    }

    @Override
    public void run(SymphonyConfiguration configuration, Environment environment) throws Exception{
        // Enable CORS headers
//        final FilterRegistration.Dynamic cors =
//                environment.servlets().addFilter("CORS", CrossOriginFilter.class);
//
//        // Configure CORS parameters
//        cors.setInitParameter("allowedOrigins", "*");
//        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
//        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("crossOriginRequsts", CrossOriginFilter.class);
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new RFQBotResource(configuration));
        environment.jersey().register(new RFQInfoResource(configuration));
        environment.jersey().register(new AppAuthResource(configuration));
    }
}
