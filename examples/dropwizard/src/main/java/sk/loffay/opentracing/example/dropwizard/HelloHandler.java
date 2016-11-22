/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.loffay.opentracing.example.dropwizard;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import io.opentracing.Span;
import io.opentracing.Tracer;
import sk.loffay.opentracing.jax.rs.CurrentSpan;

/**
 * @author Pavol Loffay
 */
@Path("/")
public class HelloHandler {

    private Tracer tracer;

    public HelloHandler(Tracer tracer) {
        this.tracer = tracer;
    }

    @GET
    @Path("/foo")
    public Response foo(@BeanParam CurrentSpan currentSpan) {
        Span childSpan = tracer.buildSpan("child")
                .asChildOf(currentSpan.injectedSpan())
                .start();
        /**
         * Business logic
         */
        childSpan.finish();

        return Response.ok().entity("/foo").build();
    }

    @GET
    @Path("/bar")
    public Response bar() {
        return Response.ok().entity("/bar").build();
    }
}
