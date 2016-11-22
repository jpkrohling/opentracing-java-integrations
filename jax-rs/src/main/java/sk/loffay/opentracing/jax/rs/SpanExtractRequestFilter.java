package sk.loffay.opentracing.jax.rs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;


/**
 * @author Pavol Loffay
 */
@Provider
public class SpanExtractRequestFilter implements ContainerRequestFilter {

    public static ThreadLocal<Span> threadLocalSpan = new ThreadLocal<>();

    @Inject
    private Tracer tracer;

    public SpanExtractRequestFilter() {}

    public SpanExtractRequestFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (tracer != null) {
            SpanContext extractedSpanContext = tracer.extract(Format.Builtin.TEXT_MAP,
                    new TextMapExtractAdapter(toMap(requestContext.getHeaders())));

            Span currentSpan = tracer.buildSpan(requestContext.getUriInfo().getAbsolutePath().toString())
                    .asChildOf(extractedSpanContext)
                    .start();

            threadLocalSpan.set(currentSpan);
        }
    }

    private static Map<String, String> toMap(MultivaluedMap<String, String> multivaluedMap) {
        HashMap<String, String> map = new HashMap<>(multivaluedMap.size());

        multivaluedMap.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 0)
                .forEach(entry ->  map.put(entry.getKey(), entry.getValue().get(0).toString()));

        return map;
    }
}
