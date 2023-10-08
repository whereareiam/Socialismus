package me.whereareiam.socialismus.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Singleton
public class CacheInterceptor implements MethodInterceptor {
    private Cache<String, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Cacheable cacheable = invocation.getMethod().getAnnotation(Cacheable.class);
        int duration = cacheable.duration();
        if (duration != -1) {
            cache = CacheBuilder.newBuilder()
                    .maximumSize(1000)
                    .expireAfterWrite(duration, TimeUnit.SECONDS)
                    .build();
        }
        String key = generateKey(invocation);
        Object value = cache.getIfPresent(key);
        if (value == null) {
            value = invocation.proceed();
            if (value != null) {
                cache.put(key, value);
            }
        }
        return value;
    }

    private String generateKey(MethodInvocation invocation) {
        String methodName = invocation.getMethod().toString();
        String args = Arrays.toString(invocation.getArguments());
        return methodName + args;
    }
}
