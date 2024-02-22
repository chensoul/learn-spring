package org.springframework.aop;

import org.aopalliance.aop.Advice;

public interface CustomAfterAdvice extends Advice {
  void after();
}
