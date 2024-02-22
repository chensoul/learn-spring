package org.springframework.tx;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

import java.lang.reflect.Method;

public class MyPointcutForTx implements Pointcut {
  @Override
  public ClassFilter getClassFilter() {
    return new ClassFilter() {
      @Override
      public boolean matches(Class clazz) {
        return DeptServiceImplDeclarative.class.isAssignableFrom(clazz);
      }
    };
  }

  @Override
  public MethodMatcher getMethodMatcher() {
    return new MethodMatcher() {
      @Override
      public boolean matches(Method m, Class targetClass) {
        return m.getName()
                .equals("insert");
      }

      @Override
      public boolean isRuntime() {
        return false;
      }

      @Override
      public boolean matches(Method m, Class targetClass, Object[] args) {
        return false;
      }
    };
  }
}
