package me.whereareiam.socialismus.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Singleton
public class CacheInterceptor implements MethodInterceptor {
    private final SettingsConfig settingsConfig;
    private final LoggerUtil loggerUtil;
    private Cache<String, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    @Inject
    public CacheInterceptor(SettingsConfig settingsConfig, LoggerUtil loggerUtil) {
        this.settingsConfig = settingsConfig;
        this.loggerUtil = loggerUtil;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        loggerUtil.trace("Invoking method: " + methodName);

        if (!settingsConfig.performance.caching) {
            loggerUtil.debug("Caching is disabled. Proceeding without caching.");
            return invocation.proceed();
        }

        Cacheable cacheable = invocation.getMethod().getAnnotation(Cacheable.class);
        int duration = cacheable.duration();
        if (duration != -1) {
            cache = CacheBuilder.newBuilder()
                    .maximumSize(1000)
                    .expireAfterWrite(duration, TimeUnit.SECONDS)
                    .build();
        }
        String key = generateKey(invocation);

        long start = System.nanoTime();
        Object value = cache.getIfPresent(key);
        long end = System.nanoTime();

        if (value == null) {
            loggerUtil.trace("Cache miss for key: " + key);
            value = invocation.proceed();
            if (value != null) {
                long putStart = System.nanoTime();
                cache.put(key, value);
                long putEnd = System.nanoTime();
                loggerUtil.trace("Added value to cache for key: " + key + ". Time taken: " + (putEnd - putStart) + " ns");
            }
        } else {
            loggerUtil.trace("Cache hit for key: " + key + ". Time taken to retrieve: " + (end - start) + " ns");
        }

        loggerUtil.debug("Retrieved value from cache: " + value);
        return value;
    }

    private String generateKey(MethodInvocation invocation) {
        String methodName = invocation.getMethod().toString();
        String args = Arrays.toString(invocation.getArguments());
        return methodName + args;
    }
}
