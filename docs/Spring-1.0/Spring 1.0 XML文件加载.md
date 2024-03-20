在spring 1.0中推荐使用的工厂类是XMLBeanFactory，它是DefaultListableBeanFactory的子类，主要是靠xml来定义元数据。本章就围绕此类以及xml文件的解析来展开

## 基本xml文件读取

spring 1.0中是利用Dom技术来实现文件读取的，并且采用的是DTD来校验文档，在后续版本中改用了XSD来校验文档。下面的xml是一个简单的配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC  "-//SPRING//DTD BEAN//EN" "http://www.springframework.org/dtd/spring-beans.dtd">
<beans>
  <bean id="user" class="entity.UserServiceImpl" />
</beans>
```

### EntityResolver

如果ＳＡＸ应用程序实现自定义处理外部实体,则必须实现此接口,并使用setEntityResolver方法向SAX 驱动器注册一个实例.也就是说,对于解析一个xml,sax首先会读取该xml文档上的声明,根据声明去寻找相应的dtd定义,以便对文档的进行验证,默认的寻找规则,(即:通过网络,实现上就是声明DTD的地址URI地址来下载DTD声明) 并进行认证,下载的过程是一个漫长的过程,而且当网络不可用时,这里会报错,就是因为相应的dtd没找到.

项目本身就可以提供一个如何寻找DTD 的声明方法,即由程序来实现寻找DTD声明的过程,比如我们将DTD放在项目的某处在实现时直接将此文档读取并返回个SAX即可,这样就避免了通过网络来寻找DTD的声明 spirng的BeansDtdResolver实现了EntityResolver接口，并且固定是从`/org/springframework/beans/factory/xml/` 类路径下找dtd文件，dtd文件在xml文件的声明中有写明是`spring-beans.dtd`

```java
public class BeansDtdResolver implements EntityResolver {

	private static final String DTD_NAME = "spring-beans";

	private static final String SEARCH_PACKAGE = "/org/springframework/beans/factory/xml/";

	protected final Log logger = LogFactory.getLog(getClass());

	public InputSource resolveEntity(String publicId, String systemId) throws IOException {
		logger.debug("Trying to resolve XML entity with public ID [" + publicId +
								 "] and system ID [" + systemId + "]");
		if (systemId != null && systemId.indexOf(DTD_NAME) > systemId.lastIndexOf("/")) {
			String dtdFile = systemId.substring(systemId.indexOf(DTD_NAME));
			logger.debug("Trying to locate [" + dtdFile + "] under [" + SEARCH_PACKAGE + "]");
			try {
				Resource resource = new ClassPathResource(SEARCH_PACKAGE + dtdFile, getClass());
				InputSource source = new InputSource(resource.getInputStream());
				source.setPublicId(publicId);
				source.setSystemId(systemId);
				logger.debug("Found beans DTD [" + systemId + "] in classpath");
				return source;
			}
			catch (IOException ex) {
				logger.debug("Could not resolve beans DTD [" + systemId + "]: not found in classpath", ex);
			}
		}
		// use the default behaviour -> download from website or wherever
		return null;
	}

}
```

为了应用上这个DTD的处理，主要是开启校验以及设置上EntityResolver即可

```java
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
factory.setValidating(true); //设置为true才会校验xml文件是否符合dtd规范
DocumentBuilder docBuilder = factory.newDocumentBuilder();
docBuilder.setEntityResolver( new BeansDtdResolver());
```

### ErrorHandler

此接口是用来实现定制的错误处理用的，spring的实现如下,此类是XmlBeanDefinitionReader的一个内部类

```java
private static class BeansErrorHandler implements ErrorHandler {

  private final static Log logger = LogFactory.getLog(XmlBeanFactory.class);

  public void error(SAXParseException ex) throws SAXException {
    throw ex;
  }

  public void fatalError(SAXParseException ex) throws SAXException {
    throw ex;
  }

  public void warning(SAXParseException ex) throws SAXException {
    logger.warn("Ignored XML validation warning: " + ex);
  }
}
```

有了这个错误处理类之后只需要调用下面的方法设置到DocumentBuilder对象即可

```java
docBuilder.setErrorHandler(new BeansErrorHandlerForTest());
```

### 读取xml元数据

知晓了EntityResolver与ErrorHandler的处理之后，就可以编写下面的代码来读取xml了

```java
@Test
public void testHello5() throws Exception{
  ClassLoader loader = Thread.currentThread()
    .getContextClassLoader();
  InputStream stream = loader.getResourceAsStream("hello.xml");
  Document doc = getDocument(stream);
  Element root = doc.getDocumentElement();//得到的是beans元素
  String defaultLazyInit = root.getAttribute(DefaultXmlBeanDefinitionParser.DEFAULT_LAZY_INIT_ATTRIBUTE);
  System.out.println("defaultLazyInit:" + defaultLazyInit);

  NodeList beanList = root.getChildNodes();//得到的是bean元素集合
  for (int i = 0; i < beanList.getLength(); i++) {
    Node node = beanList.item(i);
    if (node instanceof Element && "bean".equals(node.getNodeName())) {
      String id = ((Element) node).getAttribute("id");
      System.out.println("id: " + id);

      String classname = ((Element) node).getAttribute("class");
      System.out.println("classname:" + classname);

      String lazyInit = ((Element) node).getAttribute("lazy-init");
      System.out.println("lazyInit:" + lazyInit);
    }
  }
}

private Document getDocument(InputStream is) throws ParserConfigurationException, IOException, SAXException {
  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  factory.setValidating(true); //设置为true才会校验xml文件是否符合dtd规范
  DocumentBuilder docBuilder = factory.newDocumentBuilder();
  //不添加这个错误Handler会特别的慢
  docBuilder.setErrorHandler(new BeansErrorHandlerForTest());
  docBuilder.setEntityResolver( new BeansDtdResolver());
  //调用parse方法时会调用BeansDtdResolver的resolveEntity()方法,就是在这里解析xml文件是否符合dtd规范
  Document doc = docBuilder.parse(is);
  return doc;
}
```

输出结果如下，有了这些从xml中获取到的数据就可以创建相应的BeanDefinition，这样就可以注册到容器中，之后就可以利用getBean获取对象了

```
defaultLazyInit:false
id: user
classname:entity.UserServiceImpl
lazyInit:default
```

## XmlBeanFactory

XMLBeanFactory类的核心代码如下,只有几个构造函数加一个字段，非常简单的一个类，从中可以发现此工厂类是靠一个Reader对象来读取xml的

```java
public class XmlBeanFactory extends DefaultListableBeanFactory {
  //这里实例化Reader时传递了工厂对象给reader,这样reader读取到的BeanDefinition就可以注册到此工厂对象
  //XMLBeanFactory工厂对象是实现了BeanDefinitionRegistry接口了的
  private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);

  public XmlBeanFactory(Resource resource) throws BeansException {
    this(resource, null);
  }

  public XmlBeanFactory(InputStream is) throws BeansException {
    this(new InputStreamResource(is, "(no description)"), null);
  }
  public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
    super(parentBeanFactory);
    //读取xml,生成BeanDefinition,注册到此工厂对象中
    this.reader.loadBeanDefinitions(resource);
  }
}
```

## XmlBeanDefinitionReader

此类的父类是AbstractBeanDefinitionReader，代码如下,从此代码中可以清晰知道读取到的bean类默认是靠线程上下文类加载器加载的，并且也提供了调整这个默认加载器的方法。

读取到Bean相关信息是存放到通过构造函数传递过来的BeanDefinitionRegistry中去的，但怎么读取bean信息此类完全没有涉及，这也给了子类极大的灵活性

```java
public abstract class AbstractBeanDefinitionReader {

  private BeanDefinitionRegistry beanFactory;

  private ClassLoader beanClassLoader = Thread.currentThread().getContextClassLoader();

  protected AbstractBeanDefinitionReader(BeanDefinitionRegistry beanFactory) {
    this.beanFactory = beanFactory;
  }


  public BeanDefinitionRegistry getBeanFactory() {
    return beanFactory;
  }

  public void setBeanClassLoader(ClassLoader beanClassLoader) {
    this.beanClassLoader = beanClassLoader;
  }

  public ClassLoader getBeanClassLoader() {
    return beanClassLoader;
  }
}
```

XmlBeanDefinitionReader类就像其名锁说明的，它是从xml中读取信息并生成BeanDefinition，结合其继承的父类一起思考，那么这些BeanDefinition之后就会保存在BeanDefinitionRegistry中，此注册表通常也会是一个bean的工厂对象，就像XMLBeanFactory处理的那样，此类的结构如下图



其中最重要的方法就是loadBeanDefinition，此方法就是从一个资源中读取信息并完成BeanDefinition注册的，但Reader自身并没有去真正解析xml，转而是交给XmlBeanDefinitionParser解析器去实现的

```java
public void loadBeanDefinitions(Resource resource) throws BeansException {
  InputStream is = null;
  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  factory.setValidating(this.validating);
  DocumentBuilder docBuilder = factory.newDocumentBuilder();
  docBuilder.setErrorHandler(new BeansErrorHandler());
  docBuilder.setEntityResolver(this.entityResolver != null ? this.entityResolver : new BeansDtdResolver());
  is = resource.getInputStream();
  Document doc = docBuilder.parse(is);
  registerBeanDefinitions(doc, resource);
}

	public void registerBeanDefinitions(Document doc, Resource resource) throws BeansException {
		XmlBeanDefinitionParser parser = (XmlBeanDefinitionParser) BeanUtils.instantiateClass(this.parserClass);
   //这里把注册表与类加载器都传递给解析器了，传递resource主要是为了生成异常信息更有描述性，其实可以不传resource过去
		parser.registerBeanDefinitions(getBeanFactory(), getBeanClassLoader(), doc, resource);
	}
```

reader的默认解析器是DefaultXmlBeanDefinitionParser,也可以通过相应的方法进行调整，从这些代码可以看出reader基本上是会和parser配对使用的

```java
private Class parserClass = DefaultXmlBeanDefinitionParser.class;

public void setParserClass(Class parserClass) {
  if (this.parserClass == null || !XmlBeanDefinitionParser.class.isAssignableFrom(parserClass)) {
    throw new IllegalArgumentException("parserClass must be a XmlBeanDefinitionParser");
  }
  this.parserClass = parserClass;
}
```

## XMLBeanDefinitionParser

XMLBeanDefinitionReader中使用的解析器是DefaultXmlBeanDefinitionParser，此类实现了XMLBeanDefinitionParser接口，并且这个接口在spring 1.0中只有这一个实现类，此接口的代码如下

```java
public interface XmlBeanDefinitionParser {
  void registerBeanDefinitions(BeanDefinitionRegistry beanFactory, ClassLoader beanClassLoader,Document doc, Resource resource) throws BeansException;
}
```

在DefaultXmlBeanDefinitionParser类的loadBeanDefinitions方法中就实现了xml文件的解析操作，其核心代码如下,其中真正完成bean元素解析的是loadBeanDefinition这个protected修饰符修饰的方法，需要特别注意的是*** DefaultXmlBeanDefinitionParser类只有一个公有方法就是`registerBeanDefinitions`***

```java
public void registerBeanDefinitions(BeanDefinitionRegistry beanFactory, ClassLoader beanClassLoader,
                                    Document doc, Resource resource) {
  this.beanFactory = beanFactory;
  this.beanClassLoader = beanClassLoader;
  this.resource = resource;

  //root就是beans元素
  Element root = doc.getDocumentElement();

  this.defaultLazyInit = root.getAttribute(DEFAULT_LAZY_INIT_ATTRIBUTE);
  this.defaultDependencyCheck = root.getAttribute(DEFAULT_DEPENDENCY_CHECK_ATTRIBUTE);
  this.defaultAutowire = root.getAttribute(DEFAULT_AUTOWIRE_ATTRIBUTE);

  // nl就是bean元素的集合
  NodeList nl = root.getChildNodes();
  int beanDefinitionCounter = 0;
  for (int i = 0; i < nl.getLength(); i++) {
    Node node = nl.item(i);
    if (node instanceof Element && BEAN_ELEMENT.equals(node.getNodeName())) {
      beanDefinitionCounter++;
      loadBeanDefinition((Element) node);
    }
  }
}
```

### 解析beans的属性

就是下面的代码,beans的全局属性只有3个,当前只是获取到三个全局属性的值，等到具体解析到某个元素的设置之后再与这些全局的设置一起考虑得出元素最终的属性设置结果，比如某个元素的lazy-init属性没有设置，但有设置全局的default-lazy-init，那么最终此元素的lazy-init就以全局的设置为准。

- default-lazy-init
- default-dependency-check
- default-autowire

```java
this.defaultLazyInit = root.getAttribute(DEFAULT_LAZY_INIT_ATTRIBUTE);
this.defaultDependencyCheck = root.getAttribute(DEFAULT_DEPENDENCY_CHECK_ATTRIBUTE);
this.defaultAutowire = root.getAttribute(DEFAULT_AUTOWIRE_ATTRIBUTE);
```

### bean元素解析

bean元素解析是靠loadBeanDefinition方法处理的，代码如下，此方法主要干了以下几个事情

- id名处理
- 别名处理
- 利用parseBeanDefinition把xml中的bean元素解析成一个BeanDefinition
- 调用工厂对象的registerBeanDefinition方法注册BeanDefinition

```java
protected void loadBeanDefinition(Element ele) {
  String id = ele.getAttribute(ID_ATTRIBUTE);
  String nameAttr = ele.getAttribute(NAME_ATTRIBUTE);
  List aliases = new ArrayList();
  if (nameAttr != null && !"".equals(nameAttr)) {
    String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, BEAN_NAME_DELIMITERS, true, true);
    aliases.addAll(Arrays.asList(nameArr));
  }

  if (id == null || "".equals(id) && !aliases.isEmpty()) {
    id = (String) aliases.remove(0);
  }

  AbstractBeanDefinition beanDefinition = parseBeanDefinition(ele, id);

  if (id == null || "".equals(id)) {
    if (beanDefinition instanceof RootBeanDefinition) {
      id = ((RootBeanDefinition) beanDefinition).getBeanClassName();
    }
    else {
      throw new BeanDefinitionStoreException(this.resource, "",
                                             "Child bean definition has neither 'id' nor 'name'");
    }
  }
  
  this.beanFactory.registerBeanDefinition(id, beanDefinition);
  for (Iterator it = aliases.iterator(); it.hasNext();) {
    this.beanFactory.registerAlias(id, (String) it.next());
  }
}
```

#### id与name处理

name处理逻辑

- 直接读取bean元素的name属性值，由于可以设置多个所以就进行拆分，其分隔符由本类的常量`public static final String BEAN_NAME_DELIMITERS = ",; ";`定义
- 把名字全部添加到别名中
- 如果id没设置，就取第一个名字为id值，并把此名从别名中删除，剩下的保留在别名中

id处理逻辑如下

- 从bean元素的id属性去获取
- 如果没设置id属性，那么就从name属性中去获取，name属性中设置的第一个名字作为id的值
- 如果id属性与name属性都没有设置，并且当前bean是一个RootBeanDefinition，那么取类的名字作为id名，否则就报错，也就是是ChildBeanDefinition时不能既不设置id也不设置name

#### 别名处理

别名来自于bean元素的name属性的值，之后会注册到一个别名map中,此map中键是bean元素的别名值，值是bean元素的id值，而且所有bean的别名都不能相同

```java
private final Map aliasMap = Collections.synchronizedMap(new HashMap());

for (Iterator it = aliases.iterator(); it.hasNext();) {
  this.beanFactory.registerAlias(id, (String) it.next());
}

public void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException {
  logger.debug("Registering alias '" + alias + "' for bean with name '" + beanName + "'");
  synchronized (this.aliasMap) {
    Object registeredName = this.aliasMap.get(alias);
    //由于所有的别名都在一个map中，从这里可以看出所有bean的别名不能相同
    if (registeredName != null) {
      throw new BeanDefinitionStoreException("Cannot register alias '" + alias + "' for bean name '" + beanName +
                                             "': it's already registered for bean name '" + registeredName + "'");
    }
    //map的key是别名，值是id
    this.aliasMap.put(alias, beanName);
  }
}
```

#### class与parent解析

后续所有的解析都是在parseBeanDefinition中完成，最终解析的结果以一个AbstractBeanDefinition返回。其中class与parent解析比较简单，有以下几个注意点

- class属性与parent不能都不设置，这样的话会抛出异常

```java
if (ele.hasAttribute(CLASS_ATTRIBUTE)) {
  className = ele.getAttribute(CLASS_ATTRIBUTE);
}
String parent = null;
if (ele.hasAttribute(PARENT_ATTRIBUTE)) {
  parent = ele.getAttribute(PARENT_ATTRIBUTE);
}
if (className == null && parent == null) {
  throw new BeanDefinitionStoreException(this.resource, beanName, "Either 'class' or 'parent' is required");
}
```

- 如果给parser指定了类加载器，会先利用此加载器加载解析出来的class

```java
if (this.beanClassLoader != null) {
  Class clazz = Class.forName(className, true, this.beanClassLoader);
  rbd = new RootBeanDefinition(clazz, cargs, pvs);
} else {
  rbd = new RootBeanDefinition(className, cargs, pvs);
}
```

- 只要bean元素没有指定class，那么就创建出一个ChildBeanDefinition，否则都是创建出RootBeanDefinition，既指定class也指定parent等价于只指定了class，会忽略掉parent，在spring 1.0中子ChildBeanDefinition不能指定class

```java
AbstractBeanDefinition bd = null;
if (className != null) {
  RootBeanDefinition rbd = null;
  if (this.beanClassLoader != null) {
    Class clazz = Class.forName(className, true, this.beanClassLoader);
    rbd = new RootBeanDefinition(clazz, cargs, pvs);
  } else {
    rbd = new RootBeanDefinition(className, cargs, pvs);
  }
  bd = rbd;
} else { //className==null就创建子bd
  bd = new ChildBeanDefinition(parent, pvs);
}
...
 return bd;
```

- 其中独属于RootBeanDefinition的解析有以下几个，这点也与RootBeanDefinition类型的成员一致
  - depends-on
  - dependency-check
  - autowire
  - init-method
  - destroy-method
- 父子BeanDefinition都有的解析有如下几个
  - singleton
  - lazy-init

#### depends-on解析

此属性独属于RootBeanDefinition，并且也没有全局的只需要考虑，其代码如下

```java
public static final String DEPENDS_ON_ATTRIBUTE = "depends-on";      
if (ele.hasAttribute(DEPENDS_ON_ATTRIBUTE)) {
  String dependsOn = ele.getAttribute(DEPENDS_ON_ATTRIBUTE);
  rbd.setDependsOn(StringUtils.tokenizeToStringArray(dependsOn, BEAN_NAME_DELIMITERS, true, true));
}

public void setDependsOn(String[] dependsOn) {
  this.dependsOn = dependsOn;
}
```

#### dependencyCheck

此属性独属于RootBeanDefinition，有全局的属性需要考虑，其代码如下

```java
public static final String DEPENDENCY_CHECK_ATTRIBUTE = "dependency-check"; 
public static final String DEFAULT_DEPENDENCY_CHECK_ATTRIBUTE = "default-dependency-check";
public static final String DEFAULT_VALUE = "default";

private String defaultDependencyCheck;
this.defaultDependencyCheck = root.getAttribute(DEFAULT_DEPENDENCY_CHECK_ATTRIBUTE);

String dependencyCheck = ele.getAttribute(DEPENDENCY_CHECK_ATTRIBUTE);
if (DEFAULT_VALUE.equals(dependencyCheck)) {
  dependencyCheck = this.defaultDependencyCheck;
}
rbd.setDependencyCheck(getDependencyCheck(dependencyCheck));
```

其中defaultDependencyCheck变量保存的就是从beans元素读取到的属性`default-dependency-check` 的值,其中dependencyCheck变量读取的bean元素本身的依赖检查属性设置的值，如果你没有设置此属性值或者设置的属性值是default，那么依赖检查就采用全局的设置，如果全局的设置是simple，那么此bean的依赖检查值就是simple的。

读取到这个依赖检查值之后会调用getDependencyCheck方法来获取一个对应的整数值，最后在把这个值设置到RootBeanDefinition里去,需要注意的是几个常量来自于RootBeanDefinition

```java
public static final int DEPENDENCY_CHECK_NONE = 0;

public static final int DEPENDENCY_CHECK_OBJECTS = 1;

public static final int DEPENDENCY_CHECK_SIMPLE = 2;

public static final int DEPENDENCY_CHECK_ALL = 3;

protected int getDependencyCheck(String att) {
  int dependencyCheckCode = RootBeanDefinition.DEPENDENCY_CHECK_NONE;
  if (DEPENDENCY_CHECK_ALL_ATTRIBUTE_VALUE.equals(att)) {
    dependencyCheckCode = RootBeanDefinition.DEPENDENCY_CHECK_ALL;
  } else if (DEPENDENCY_CHECK_SIMPLE_ATTRIBUTE_VALUE.equals(att)) {
    dependencyCheckCode = RootBeanDefinition.DEPENDENCY_CHECK_SIMPLE;
  } else if (DEPENDENCY_CHECK_OBJECTS_ATTRIBUTE_VALUE.equals(att)) {
    dependencyCheckCode = RootBeanDefinition.DEPENDENCY_CHECK_OBJECTS;
  }
  // else leave default value
  return dependencyCheckCode;
}

public void setDependencyCheck(int dependencyCheck) {
  this.dependencyCheck = dependencyCheck;
}
```

其中在xml中设置依赖检查属性的可能的值有如下几个

- all
- none
- simple
- objects
- default：意思是采用全局属性的值，最后的值会是上面4种中的其中一个

#### 自动装配(autowire)解析

autowire的解析与依赖检查的解析逻辑是一模一样的，只是需要注意的是每个bean元素都可以有自己的自动装配模式，装配模式的值有如下一些

```java
public static final int AUTOWIRE_NO = 0;

public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

public static final int AUTOWIRE_CONSTRUCTOR = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

public static final int AUTOWIRE_AUTODETECT = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;
```

#### init-method与destroy-method

```java
String initMethodName = ele.getAttribute(INIT_METHOD_ATTRIBUTE);
if (!initMethodName.equals("")) {
  rbd.setInitMethodName(initMethodName);
}
String destroyMethodName = ele.getAttribute(DESTROY_METHOD_ATTRIBUTE);
if (!destroyMethodName.equals("")) {
  rbd.setDestroyMethodName(destroyMethodName);
}
```

#### 是否单例解析

```java
if (ele.hasAttribute(SINGLETON_ATTRIBUTE)) {
  bd.setSingleton(TRUE_VALUE.equals(ele.getAttribute(SINGLETON_ATTRIBUTE)));
}
```

#### lazy-init

```java
public static final String TRUE_VALUE = "true";

String lazyInit = ele.getAttribute(LAZY_INIT_ATTRIBUTE);
if (DEFAULT_VALUE.equals(lazyInit) && bd.isSingleton()) {
  // just apply default to singletons, as lazy-init has no meaning for prototypes
  lazyInit = this.defaultLazyInit;
}
bd.setLazyInit(TRUE_VALUE.equals(lazyInit));
```

#### 属性解析

在spring 1.0中属性设置是类似下面这样设置属性的，value与ref是property的子元素，不能直接写值或者作为property的属性进行设置

```xml
<bean id="p" class="entity.ParentClass">
  <property name="p1"><value>aa</value></property>
  <property name="p2"><value>bb</value></property>
  <property name="p2"><ref>bb</ref></property>
</bean>
```

bean元素的属性设置解析是靠getPropertyValueSubElements方法实现的，代码如下

```java
protected MutablePropertyValues getPropertyValueSubElements(String beanName, Element beanEle) {
  NodeList nl = beanEle.getChildNodes();
  MutablePropertyValues pvs = new MutablePropertyValues();
  for (int i = 0; i < nl.getLength(); i++) {
    Node node = nl.item(i);
    if (node instanceof Element && PROPERTY_ELEMENT.equals(node.getNodeName())) {
      parsePropertyElement(beanName, pvs, (Element) node);
    }
  }
  return pvs;
}
```

每一个属性子元素的解析是靠parsePropertyElement方法来完成的，先解析property元素name属性的值，接着又交给getPropertyValue去解析值，也就是解析value与ref这种子元素里填的数据，最后添加到pvs变量中

```java
protected void parsePropertyElement(String beanName, MutablePropertyValues pvs, Element ele)
  throws DOMException {
  String propertyName = ele.getAttribute(NAME_ATTRIBUTE);
  if ("".equals(propertyName)) {
    throw new BeanDefinitionStoreException(this.resource, beanName,
                                           "Tag 'property' must have a 'name' attribute");
  }
  Object val = getPropertyValue(ele, beanName);
  pvs.addPropertyValue(new PropertyValue(propertyName, val));
}
```

getPropertyValue方法的处理如下,property元素只应该有一个子元素，所以nl变量的size应该不超过1，遍历这个子元素并且忽略描述子元素，因为spirng1.0没有用上它。如果一个子元素都找不到就报异常，最后交给parsePropertySubelement方法去处理这个子元素获取其中的数据并返回

```java
protected Object getPropertyValue(Element ele, String beanName) {
  // should only have one element child: value, ref, collection
  NodeList nl = ele.getChildNodes();
  Element valueRefOrCollectionElement = null;
  for (int i = 0; i < nl.getLength(); i++) {
    if (nl.item(i) instanceof Element) {
      Element candidateEle = (Element) nl.item(i);
      if (DESCRIPTION_ELEMENT.equals(candidateEle.getTagName())) {
        // keep going: we don't use this value for now
      } else {
        // child element is what we're looking for
        valueRefOrCollectionElement = candidateEle;
      }
    }
  }
  if (valueRefOrCollectionElement == null) {
    throw new BeanDefinitionStoreException(this.resource, beanName,
                                           "<property> element must have a subelement like 'value' or 'ref'");
  }
  return parsePropertySubelement(valueRefOrCollectionElement, beanName);
}
```

由于property元素下可以有如下几个子元素,所以parsePropertySubelement分别对这些元素进行了解析，我们重点讲value、ref和集合元素的解析

- bean
- ref
- idref
- list
- set
- map
- props
- value
- null

```java
protected Object parsePropertySubelement(Element ele, String beanName) {
    if (ele.getTagName()
            .equals(BEAN_ELEMENT)) {
      return parseBeanDefinition(ele, "(inner bean definition)");
    } else if (ele.getTagName()
            .equals(REF_ELEMENT)) {
      // a generic reference to any name of any bean
      String beanRef = ele.getAttribute(BEAN_REF_ATTRIBUTE);
      if ("".equals(beanRef)) {
        // a reference to the id of another bean in the same XML file
        beanRef = ele.getAttribute(LOCAL_REF_ATTRIBUTE);
        if ("".equals(beanRef)) {
          throw new BeanDefinitionStoreException(this.resource, beanName,
                  "Either 'bean' or 'local' is required for a reference");
        }
      }
      return new RuntimeBeanReference(beanRef);
    } else if (ele.getTagName()
            .equals(IDREF_ELEMENT)) {
      // a generic reference to any name of any bean
      String beanRef = ele.getAttribute(BEAN_REF_ATTRIBUTE);
      if ("".equals(beanRef)) {
        // a reference to the id of another bean in the same XML file
        beanRef = ele.getAttribute(LOCAL_REF_ATTRIBUTE);
        if ("".equals(beanRef)) {
          throw new BeanDefinitionStoreException(this.resource, beanName,
                  "Either 'bean' or 'local' is required for an idref");
        }
      }
      return beanRef;
    } else if (ele.getTagName()
            .equals(LIST_ELEMENT)) {
      return getList(ele, beanName);
    } else if (ele.getTagName()
            .equals(SET_ELEMENT)) {
      return getSet(ele, beanName);
    } else if (ele.getTagName()
            .equals(MAP_ELEMENT)) {
      return getMap(ele, beanName);
    } else if (ele.getTagName()
            .equals(PROPS_ELEMENT)) {
      return getProps(ele, beanName);
    } else if (ele.getTagName()
            .equals(VALUE_ELEMENT)) {
      // it's a literal value
      return getTextValue(ele, beanName);
    } else if (ele.getTagName()
            .equals(NULL_ELEMENT)) {
      // it's a distinguished null value
      return null;
    }
    throw new BeanDefinitionStoreException(this.resource, beanName,
            "Unknown subelement of <property>: <" + ele.getTagName() + ">");
  }
```

##### 解析内部bean

内部bean指的下面xml这种配置的bean,直接递归调用parseBeanDefinition来实现内部bean的解析，结果是一个AbstractBeanDefinition类型的对象

```xml
<bean id="userService" class="entity.UserServiceImpl"  >
  <property name="dao">
    <ref bean="userDao"></ref>
    <bean class="entity.UserDaoImpl"/>
  </property>
</bean>
```

##### value子元素解析

value子元素的解析是靠getTextValue完成的，主要干了以下的事情

- 如果value内部什么都没有，就返回空字符串
- 如果value内部有多个子元素或者第一个子元素不是一个Text类型就抛异常
- 返回value内部的文本数据

```java
  protected String getTextValue(Element ele, String beanName) {
    NodeList nl = ele.getChildNodes();
    if (nl.item(0) == null) {
      // treat empty value as empty String
      return "";
    }
    if (nl.getLength() != 1 || !(nl.item(0) instanceof Text)) {
      throw new BeanDefinitionStoreException(this.resource, beanName,
              "Unexpected element or type mismatch: expected single node of " +
                      nl.item(0)
                              .getClass() + " to be of type Text: " + "found " + ele, null);
    }
    Text t = (Text) nl.item(0);
    // This will be a String
    return t.getData();
  }
```

##### ref子元素解析

由于ref子元素可以像下面这样设置,其中bean属性的值是任何bean的任何名字，包含别名都可以，而local属性的值必须是其它bean的id值，它是会被DTD进行校验的，所以相应的local属性的值指向的bean只能在同一个xml中，不能指向其它的xml元数据文件，比如父工厂所用的xml元数据

```xml
<property name="p2"><ref bean="c1" local="c2"></ref> </property>
```

DTD文件对于这2个属性的设置如下：

```xml
<!ATTLIST ref bean CDATA #IMPLIED>
<!ATTLIST ref local IDREF #IMPLIED>
```

获取到引用的bean的名字之后，就传递RuntimeBeanReference封装起来

```java
public static final String BEAN_REF_ATTRIBUTE = "bean";
public static final String LOCAL_REF_ATTRIBUTE = "local";

if (ele.getTagName()
    .equals(REF_ELEMENT)) {
  // a generic reference to any name of any bean
  String beanRef = ele.getAttribute(BEAN_REF_ATTRIBUTE);
  if ("".equals(beanRef)) {
    // a reference to the id of another bean in the same XML file
    beanRef = ele.getAttribute(LOCAL_REF_ATTRIBUTE);
    if ("".equals(beanRef)) {
      throw new BeanDefinitionStoreException(this.resource, beanName,
                                             "Either 'bean' or 'local' is required for a reference");
    }
  }
  return new RuntimeBeanReference(beanRef);
```

RuntimeBeanReference类非常简单，为什么不直接返回引用bean的值这个字符串而要用一个类型封装，这是因为要与value子元素的数据进行区分，不然获取属性值的时候都是字符串类型了，要记得往MutablePropertyValues添加数据时其PropertyValue中并没有表明当前属性的类型，也就是它到底是什么，只是简单的记录值而已

```java
public class RuntimeBeanReference {

  private final String beanName;

  public RuntimeBeanReference(String beanName) {
    this.beanName = beanName;
  }

  public String getBeanName() {
    return beanName;
  }
}
```

##### 集合元素解析

property的集合元素指的是下面的配置

```xml
<property name="p2">
  <list>
    <value>1</value>
    <value>2</value>
  </list>
</property>
```

4个集合元素的解析都大同小异，这里以list的解析来说明,由于list里的子元素与property元素的子元素一样，所以这里有个递归调用parsePropertySubElement，解析最终的结果都存放到ManagedList里了

```java
protected List getList(Element collectionEle, String beanName) {
  NodeList nl = collectionEle.getChildNodes();
  ManagedList l = new ManagedList();
  for (int i = 0; i < nl.getLength(); i++) {
    if (nl.item(i) instanceof Element) {
      Element ele = (Element) nl.item(i);
      l.add(parsePropertySubelement(ele, beanName));
    }
  }
  return l;
}
```

ManagedList代码如下,它相当于一个标识类型，除了继承ArrayList，里面什么都没有，map与set类似，props子元素使用类型Properties来保存解析到的数据

```java
public class ManagedList extends ArrayList {}
public class ManagedSet extends HashSet {}
public class ManagedMap extends HashMap {}
```

#### 构造函数参数解析

构造函数配置的解析是在parseBeanDefinition方法中当class属性值不为空的时候开始解析的，这也符合逻辑，指定了class才有解析构造函数参数的必要

```java
protected AbstractBeanDefinition parseBeanDefinition(Element ele, String beanName) {  
  if (className != null) {
    ConstructorArgumentValues cargs = getConstructorArgSubElements(beanName, ele);
  }
}
```

在理解构造函数解析之前，先看看spirng 1.0中在xml中是如何配置构造函数的

```xml
<bean id="normal" class="entity.NormalClass">
  <constructor-arg index="0" type="int">
    <value>100</value>
  </constructor-arg>
  <constructor-arg index="1">
    <value>cj</value>
  </constructor-arg>
  <constructor-arg >
    <ref>cj</ref>
  </constructor-arg>
</bean>
```

知道了xml接口之后就比较好理解构造函数配置了，getConstructorArgSubElements代码如下，它遍历每一个子元素，针对每一个contructor-arg子元素都交给parseConstructorArgElement方法去解析

```java
protected ConstructorArgumentValues getConstructorArgSubElements(String beanName, Element beanEle)
  throws ClassNotFoundException {
  NodeList nl = beanEle.getChildNodes();
  ConstructorArgumentValues cargs = new ConstructorArgumentValues();
  for (int i = 0; i < nl.getLength(); i++) {
    Node node = nl.item(i);
    if (node instanceof Element && CONSTRUCTOR_ARG_ELEMENT.equals(node.getNodeName())) {
      parseConstructorArgElement(beanName, cargs, (Element) node);
    }
  }
  return cargs;
}
```

parseConstructorArgElement方法的代码如下，其中getPropertyValue前面有过介绍，这里再说明一下，此方法就是得到property元素最终的值,有了这个值之后就可以创建一个PropertyValue添加到pvs中，由于此方法并没有限定为只能解析property子元素,所以它用在了解析constructor-arg元素的值上，也就是此方法可以用在解析任何与property子元素内部一样结构的元素上

```java
protected void parseConstructorArgElement(String beanName, ConstructorArgumentValues cargs, Element ele)
  throws DOMException, ClassNotFoundException {

  Object val = getPropertyValue(ele, beanName);
  String indexAttr = ele.getAttribute(INDEX_ATTRIBUTE);
  String typeAttr = ele.getAttribute(TYPE_ATTRIBUTE);
  if (!"".equals(indexAttr)) {
    try {
      //有设置index属性值
      int index = Integer.parseInt(indexAttr);
      if (index < 0) {
        throw new BeanDefinitionStoreException(this.resource, beanName, "'index' cannot be lower than 0");
      }
      if (!"".equals(typeAttr)) {
        cargs.addIndexedArgumentValue(index, val, typeAttr);
      } else {
        cargs.addIndexedArgumentValue(index, val);
      }
    } catch (NumberFormatException ex) {
      throw new BeanDefinitionStoreException(this.resource, beanName,
                                             "Attribute 'index' of tag 'constructor-arg' must be an integer");
    }
  } else {
    //没有设置index属性值
    if (!"".equals(typeAttr)) {
      cargs.addGenericArgumentValue(val, typeAttr);
    } else {
      cargs.addGenericArgumentValue(val);
    }
  }
}
```

从这里可以看出你设置的索引值可以是一个很大的值，完全与bean对应的类的构造函数索引不对应都可以，比如索引值设置为1000都可以

# Resource

在XMLBeanDefinitionReader中我们看到过Resource类型，这个类型是spring提供的对资源的一个抽象描述，此资源有可能是一个文件系统下的文件，也可能是类路径下的一个文件或其它任何资源数据，其类型层次结构如下



其中InputStreamSource接口如下

```java
public interface InputStreamSource {
  InputStream getInputStream() throws IOException;
}
```

而Resource接口代码如下

```java
public interface Resource extends InputStreamSource {
  boolean exists();
  boolean isOpen();
  URL getURL() throws IOException；
  File getFile() throws IOException;
  String getDescription();
}
```

## FileSystemResource

此类代表着文件系统下的文件，代码如下

```java
public class FileSystemResource extends AbstractResource {

	private final File file;

	public FileSystemResource(File file) {
		this.file = file;
	}

	public FileSystemResource(String path) {
		this.file = new File(path);
	}

	public boolean exists() {
		return this.file.exists();
	}

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(this.file);
	}

	public URL getURL() throws IOException {
		return new URL(URL_PROTOCOL_FILE + ":" + this.file.getAbsolutePath());
	}

	public File getFile() {
		return file;
	}

	public String getDescription() {
		return "file [" + this.file.getAbsolutePath() + "]";
	}

}
```

父类AbstractResource实现如下

```java
public abstract class AbstractResource implements Resource {

  protected static final String URL_PROTOCOL_FILE = "file";

  public boolean exists() {
    // try file existence
    try {
      return getFile().exists();
    }
    catch (IOException ex) {
      // fall back to stream existence
      try {
        InputStream is = getInputStream();
        is.close();
        return true;
      }
      catch (IOException ex2) {
        return false;
      }
    }
  }

  public boolean isOpen() {
    return false;
  }

  public URL getURL() throws IOException {
    throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
  }


  public File getFile() throws IOException {
    throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
  }
}
```

所以FileSystemResource的isOpen总是返回false，getUrl会返回文件地址

## ClassPathResource

此类代表类路径下的一个资源，实例化此类时如果传递了class信息，就用这个传递的class对象去找资源，也就是资源是相对于这个类的位置去找，而没有传递的话是靠线程上下文的类加载器去找资源，核心代码如下：

```java
public class ClassPathResource extends AbstractResource {

  private final String path;
  private Class clazz;
  
  public ClassPathResource(String path, Class clazz) {
    this.path = path;
    this.clazz = clazz;
  }
  public InputStream getInputStream() throws IOException {
    InputStream is = null;
    if (this.clazz != null) {
      is = this.clazz.getResourceAsStream(this.path);
    }
    else {
      ClassLoader ccl = Thread.currentThread().getContextClassLoader();
      is = ccl.getResourceAsStream(this.path);
    }
    if (is == null) {
      throw new FileNotFoundException("Could not open " + getDescription());
    }
    return is;
  }
}
```

## InputStreamResource

这个类与接口InputStreamSource名字很想象，但含义不一样，代码很简单

```java
public class InputStreamResource extends AbstractResource {

	private InputStream inputStream;

	private final String description;

	public InputStreamResource(InputStream inputStream, String description) {
		if (inputStream == null) {
			throw new IllegalArgumentException("inputStream must not be null");
		}
		this.inputStream = inputStream;
		this.description = description;
	}

	public boolean exists() {
		return true;
	}

	public boolean isOpen() {
		return true;
	}

	public InputStream getInputStream() throws IOException, IllegalStateException {
		if (this.inputStream == null) {
			throw new IllegalStateException("InputStream has already been read - " +
			                                "do not use InputStreamResource if a stream needs to be read multiple times");
		}
		InputStream result = this.inputStream;
		this.inputStream = null;
		return result;
	}

}
```

# 附录

## dom读取xml文件

假定有以下的xml文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<book id="1">
  <name>Java核心技术</name>
  <author>Cay S. Horstmann</author>
  <isbn lang="CN">1234567</isbn>
  <tags>
    <tag>Java</tag>
    <tag>Network</tag>
  </tags>
  <pubDate/>
</book>
```

编写下面的代码就可以读取xml文件

```java
@Test
public void readBookXml() throws Exception {
  ClassLoader loader = Thread.currentThread()
    .getContextClassLoader();
  InputStream stream = loader.getResourceAsStream("book.xml");
  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
  DocumentBuilder db = dbf.newDocumentBuilder();
  Document doc = db.parse(stream);
  printNode(doc, 0);
}

void printNode(Node n, int indent) {
  for (int i = 0; i < indent; i++) {
    System.out.print(' ');
  }
  switch (n.getNodeType()) {
    case Node.DOCUMENT_NODE: // Document节点
      System.out.println("Document: " + n.getNodeName());
      break;
    case Node.ELEMENT_NODE: // 元素节点
      System.out.println("Element: " + n.getNodeName());
      break;
    case Node.TEXT_NODE: // 文本
      System.out.println("Text: " + n.getNodeName() + " = " + n.getNodeValue());
      break;
    case Node.ATTRIBUTE_NODE: // 属性
      System.out.println("Attr: " + n.getNodeName() + " = " + n.getNodeValue());
      break;
    default: // 其他
      System.out.println("NodeType: " + n.getNodeType() + ", NodeName: " + n.getNodeName());
  }
  for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling()) {
    printNode(child, indent + 1);
  }
}
```

输出的结果如下：

```
Document: #document
 Element: book
  Text: #text = 
    
  Element: name
   Text: #text = Java核心技术
  Text: #text = 
    
  Element: author
   Text: #text = Cay S. Horstmann
  Text: #text = 
    
  Element: isbn
   Text: #text = 1234567
  Text: #text = 
    
  Element: tags
   Text: #text = 
        
   Element: tag
    Text: #text = Java
   Text: #text = 
        
   Element: tag
    Text: #text = Network
   Text: #text = 
    
  Text: #text = 
    
  Element: pubDate
  Text: #text = 
```

## Dtd文件的处理

> [https://blog.csdn.net/sicofield/article/details/79282918](https://gitee.com/link?target=https%3A%2F%2Fblog.csdn.net%2Fsicofield%2Farticle%2Fdetails%2F79282918)