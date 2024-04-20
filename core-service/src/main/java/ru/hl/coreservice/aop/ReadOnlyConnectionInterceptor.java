package ru.hl.primaryservice.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import static ru.hl.primaryservice.config.DataSourceConfig.DataSourceTypes.SLAVE;
import static ru.hl.primaryservice.config.DataSourceConfig.DataSourceTypeContextHolder.clearDataSourceType;
import static ru.hl.primaryservice.config.DataSourceConfig.DataSourceTypeContextHolder.setDataSourceType;

@Aspect
@Component
@ConditionalOnProperty(value = "spring.datasource.primary.slave.readonly-slave-enabled", havingValue = "true")
public class ReadOnlyConnectionInterceptor implements Ordered {

  private int order;

  @Value("20")
  public void setOrder(int order) {
    this.order = order;
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Pointcut(value="execution(public * *(..))")
  public void anyPublicMethod() { }

  @Around("@annotation(readOnlyConnection)")
  public Object proceed(ProceedingJoinPoint pjp, ReadOnlyConnection readOnlyConnection) throws Throwable {
    try {
      setDataSourceType(SLAVE);
      Object result = pjp.proceed();
      clearDataSourceType();
      return result;
    } finally {
      // restore state
      clearDataSourceType();
    }
  }
}