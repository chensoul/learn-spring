package org.springframework.context;

import org.junit.Test;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.support.StaticMessageSource;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class MessageSourceTest {

  @Test
  public void testMessageFormat() {
    MessageFormat format = new MessageFormat(" {0} love {1}");
    Object[] args = new Object[]{"cj", "java"};
    String result = format.format(args); //需要的参数必须是数组
    System.out.println(result);
  }

  @Test
  public void testResourceBundle() {
    ResourceBundle bundle = ResourceBundle.getBundle("messages/label", Locale.CHINA,
            Thread.currentThread()
                    .getContextClassLoader());
    String l1 = bundle.getString("l1");
    System.out.println(l1);
    System.out.println("=======================");
    String l2 = bundle.getString("l2");
    System.out.println(l2);
    MessageFormat format = new MessageFormat(l2);
    Object[] args = new Object[]{"cj"};
    String s = format.format(args);
    System.out.println(s);
  }

  @Test
  public void testStaticMessageSource() {
    StaticMessageSource source = new StaticMessageSource();
    source.addMessage("h", Locale.ENGLISH, "hello");
    System.out.println(source.getMessage("h", null, Locale.ENGLISH));


    source.addMessage("h2", Locale.ENGLISH, "hello {0} world");
    System.out.println(source.getMessage("h2", new Object[]{"cj"}, Locale.ENGLISH));
  }

  @Test
  public void testResourceBundleMessageSource() {
    ResourceBundleMessageSource source = new ResourceBundleMessageSource();
    // messages目录在test的resources目录下,有2个文件名字分别是label.properties,label_de.properties
    source.setBasename("messages/label");
    // 这里的code指的就是消息的键,也就是下面的l1 和l2这样的东西
    source.setUseCodeAsDefaultMessage(true);
    System.out.println(source.getMessage("l1", null, Locale.GERMAN));
    System.out.println(source.getMessage("l1", null, Locale.ENGLISH));
    // l2里面有参数{0},最后被这里的Paul Smith替换
    System.out.println(source.getMessage("l2", new Object[]{"Paul Smith"}, Locale.GERMAN));
    System.out.println(source.getMessage("l2", new Object[]{"Paul Smith"}, Locale.ENGLISH));
  }

  @Test
  public void testReloadableResourceBundleMessageSource() throws Exception {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("messages/label");
    messageSource.setCacheSeconds(20);
    System.out.println(messageSource.getMessage("l1", null, Locale.ENGLISH));
    TimeUnit.SECONDS.sleep(10);
    System.out.println(messageSource.getMessage("l1", null, Locale.ENGLISH));
    TimeUnit.SECONDS.sleep(12);
    System.out.println(messageSource.getMessage("l1", null, Locale.ENGLISH));
  }


  @Test
  public void testMessageSourceAccessor() throws Exception {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("messages/label");
    messageSource.setCacheSeconds(20);
    MessageSourceAccessor accessor = new MessageSourceAccessor(messageSource);

    System.out.println(accessor.getMessage("l1"));
    System.out.println(accessor.getMessage("l1", Locale.ENGLISH));

  }


  @Test
  public void testMessageSourceResourceBundle() throws Exception {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("messages/label");
    messageSource.setCacheSeconds(20);
    //此类没实现完整,忽略
    MessageSourceResourceBundle bundle = new MessageSourceResourceBundle(messageSource, Locale.ENGLISH);

    //System.out.println(bundle.);
    //System.out.println(accessor.getMessage("l1",Locale.ENGLISH));

  }
}
