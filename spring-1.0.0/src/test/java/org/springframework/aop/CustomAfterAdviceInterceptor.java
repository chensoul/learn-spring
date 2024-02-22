package org.springframework.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class CustomAfterAdviceInterceptor implements MethodInterceptor {
  private CustomAfterAdvice afterAdvice;

  public CustomAfterAdviceInterceptor(CustomAfterAdvice afterAdvice) {
    this.afterAdvice = afterAdvice;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Object result = null;
    try {
      result = invocation.proceed();
    } catch (Throwable throwable) {
      System.out.println("出异常了");
    } finally {
      afterAdvice.after();
    }
    return result;
  }
}
