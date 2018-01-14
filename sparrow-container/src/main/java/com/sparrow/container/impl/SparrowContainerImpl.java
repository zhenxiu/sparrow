/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sparrow.container.impl;

import com.sparrow.cg.Generator4MethodAccessor;
import com.sparrow.cg.MethodAccessor;
import com.sparrow.constant.CONFIG;
import com.sparrow.constant.CONSTANT;
import com.sparrow.constant.SYS_OBJECT_NAME;
import com.sparrow.constant.magic.SYMBOL;
import com.sparrow.container.BeanDefinition;
import com.sparrow.container.Container;
import com.sparrow.core.TypeConvertor;
import com.sparrow.enums.CONTAINER;
import com.sparrow.exception.DuplicateActionMethodException;
import com.sparrow.support.Initializer;
import com.sparrow.support.Login;
import com.sparrow.support.LoginDialog;
import com.sparrow.support.protocol.Result;
import com.sparrow.utility.Config;
import com.sparrow.utility.StringUtility;
import com.sparrow.utility.Xml;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author harry
 */
public class SparrowContainerImpl implements Container {

    private static Logger logger = LoggerFactory.getLogger(SparrowContainerImpl.class);

    private static final String DTD_FILE_NAME = "beanFactory.dtd";
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String REF = "ref";
    private static final String SCOPE = "scope";
    private static final String PLACE_HOLDER_KEY = "place-holder-key";
    private static final String CLASS_NAME = "class";
    private static final String BEAN = "bean";
    private static final String IMPORT = "import";
    private static final String BEANS = "beans";
    private static final String SINGLETON = "singleton";
    private static final String PROPERTY = "property";

    /**
     * beanDefinition缓存 <p>spring context WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    private Generator4MethodAccessor generator4MethodAccessor = null;
    /**
     * 对象缓存
     */
    private final Map<String, Object> beanFactoryCache = new ConcurrentHashMap<String, Object>();
    /**
     * impl 生成的代理bean的缓存
     */
    private final Map<String, MethodAccessor> proxyBeanCache = new ConcurrentHashMap<String, MethodAccessor>();
    /**
     * 实体的field 访问方法缓存
     */
    private final Map<String, List<TypeConvertor>> fieldCache = new ConcurrentHashMap<String, List<TypeConvertor>>();
    /**
     * action实体对象的操作方法缓存
     */
    private final Map<String, Map<String, Method>> actionMethodCache = new ConcurrentHashMap<String, Map<String, Method>>();
    /**
     * 拦截器
     */
    private final List<Object> interceptorList = new ArrayList<Object>();
    /**
     * bean分类
     */
    private final Map<String, Map<String, Object>> typeBeanFactory = new ConcurrentHashMap<String, Map<String, Object>>();

    private String xmlName;
    private String systemConfigPath;

    private Generator4MethodAccessor getGenerator4MethodAccessor() {
        if (this.generator4MethodAccessor != null) {
            return this.generator4MethodAccessor;
        }
        this.generator4MethodAccessor = this.getBean("generator4MethodAccessor");
        if (this.generator4MethodAccessor != null) {
            return this.generator4MethodAccessor;
        }
        try {
            generator4MethodAccessor = (Generator4MethodAccessor) Class.forName("com.sparrow.cg.impl.Generator4MethodAccessorImpl").newInstance();
        } catch (Exception e) {
            logger.error("can't find class com.sparrow.cg.impl.Generator4MethodAccessorImpl", e);
        }
        return generator4MethodAccessor;
    }

    public SparrowContainerImpl() {
        this.xmlName = "/beans.xml";
        this.systemConfigPath = "/system_config.properties";
    }

    private void readProperty(Element element) throws Exception {
        String propertyName = element.getAttribute(NAME).trim();
        String refBeanName = element.getAttribute(REF);
        String value = element.getAttribute(VALUE);
        Element parentElement = (Element) element.getParentNode();
        String parentBeanName = parentElement.getAttribute(NAME);
        String placeHolderKey = parentElement.getAttribute(PLACE_HOLDER_KEY);
        String scope = parentElement.getAttribute(SCOPE)
            .trim().toLowerCase();
        // 如果当前对象的父对象是单例
        // 则注入该对象
        if (SINGLETON.equals(scope) || SYMBOL.EMPTY.equals(scope)) {
            loadSingleton(propertyName, refBeanName, value, parentBeanName, placeHolderKey);
            return;
        }

        // 如果非单例对象 则将类定义压入到缓存
        BeanDefinition beanDefinition = this.beanDefinitionMap
            .get(refBeanName);
        if (beanDefinition == null) {
            beanDefinition = new BeanDefinition(this.getBean(refBeanName)
                .getClass());
        }
        // 获取依赖类并压入缓存
        this.beanDefinitionMap.get(parentBeanName).getRelyOnClass()
            .put(refBeanName, beanDefinition);
    }

    private void loadSingleton(String propertyName, String refBeanName, String value, String parentBeanName,
        String placeHolderKey) throws Exception {
        if (StringUtility.isNullOrEmpty(refBeanName)) {
            this.setValue(this.getBean(parentBeanName), propertyName,
                value, placeHolderKey);
        }

        // 引用必须在该对象初始化之前先被初始化
        if (this.beanFactoryCache.get(refBeanName) == null) {
            logger.error("error: ref bean "
                + refBeanName
                + " must be initialization! because " + parentBeanName + " Class is Singleton");
            System.exit(0);
        }
        // 注入该对象
        this.setRely(this.getBean(parentBeanName), propertyName,
            this.getBean(refBeanName));
    }

    /**
     * 读xml标签初始化bean 并加入到bean factory 和bean defination facotory
     */
    private void readBean(Element el) {
        if (BEANS.equalsIgnoreCase(el.getTagName().trim())) {
            return;
        }
        // 是否为单子实例
        String scope = el.getAttribute(SCOPE);
        // class名
        String className = el.getAttribute(CLASS_NAME);
        // bean名
        String beanName = el.getAttribute(NAME).trim();
        //构造参数
        String constructorArg = el.getAttribute("constructor-arg");
        //controller名
        String controller = el.getAttribute("controller");
        //拦截器
        String interceptor = el.getAttribute("interceptor");
        //远程bean
        String remote = el.getAttribute("remote");

        String container = null;

        Class<?> beanClass;
        try {
            beanClass = Class.forName(className);
        } catch (Exception e) {
            logger.error("bean name error :" + beanName, e);
            return;
        }
        // 如果是单子对象
        if (!StringUtility.isNullOrEmpty(scope) && !SINGLETON.equalsIgnoreCase(scope)) {
            // 如果不是单子则缓存该类的元数据
            this.cacheBeanDefinition(beanName, beanClass);
        }
        try {
            Object instance = getInstance(constructorArg, beanClass);
            assembleController(beanName, controller, beanClass);

            if (Boolean.TRUE.toString().equalsIgnoreCase(interceptor)) {
                container = CONTAINER.INTERCEPTOR.toString().toUpperCase();
            }

            if (!StringUtility.isNullOrEmpty(remote)) {
                container = remote.toUpperCase();
            }

            if (container != null) {
                if (!this.typeBeanFactory.containsKey(container)) {
                    this.typeBeanFactory.put(container, new HashMap<String, Object>(64));
                }
                this.typeBeanFactory.get(container).put(beanName, instance);
            }

            this.beanFactoryCache.put(beanName, instance);
        } catch (Exception e) {
            logger.error(beanName, e);
        }
    }

    private void assembleController(String beanName, String controller, Class<?> beanClass) {
        if (!Boolean.TRUE.toString().equalsIgnoreCase(controller)) {
            return;
        }
        Method[] methods = beanClass.getDeclaredMethods();
        Map<String, Method> methodMap = new HashMap<String, Method>(methods.length);
        for (Method method : methods) {
            if (method.getModifiers() == Modifier.PRIVATE) {
                continue;
            }
            if (methodMap.containsKey(method.getName())) {
                throw new DuplicateActionMethodException("Duplicate for the method name " + beanName + " " + method.getName() + "!");
            }
            methodMap.put(method.getName(), method);
        }
        this.actionMethodCache.put(beanName, methodMap);
    }

    private Object getInstance(String constructorArg,
        Class<?> beanClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        if (StringUtility.isNullOrEmpty(constructorArg)) {
            return beanClass.newInstance();
        }
        //是否存在对象
        Object constructorObj = null;
        Constructor constructor;
        if (!constructorArg.contains(SYMBOL.COMMA)) {
            constructorObj = this.getBean(constructorArg);
        }

        //如果是对象
        if (constructorObj != null) {
            //是否有该对象的构造
            constructor = beanClass.getConstructor(constructorObj.getClass());
            if (constructor == null) {
                constructor = beanClass.getConstructor(constructorObj.getClass().getInterfaces()[0]);
            }
            if (constructor == null) {
                constructor = beanClass.getConstructor(String.class);
                if (constructor != null) {
                    constructorObj = constructorArg;
                }
            }
        } else {
            constructor = beanClass.getConstructor(String.class);
            if (constructor != null) {
                constructorObj = constructorArg;
            }
        }
        if (constructor != null) {
            return constructor.newInstance(constructorObj);
        }
        return null;
    }

    /**
     * bean definition cache
     *
     * @param beanName xml config
     * @param beanClass class
     */
    private void cacheBeanDefinition(String beanName, Class beanClass) {
        BeanDefinition beanDefinition = new BeanDefinition(beanClass);
        this.beanDefinitionMap.put(beanName, beanDefinition);

        // 如果是非单例对象则生成代理访问对象，提高反射效率
        // 除实体对象外全部为非单例对象
        this.getProxyBean(beanClass);
        // 初始化bean 的get set 方法
        this.getFieldList(beanClass);
    }

    private void initBeanByElement(Element element) throws Exception {
        NodeList nodeList = element.getChildNodes();
        // 如果子节点>0则先加载子节点对象
        if (nodeList.getLength() > 0) {
            loadChildren(element, nodeList);
            return;
        }
        // 如果没有依赖对象的单独节点
        String tagName = element.getTagName().trim();
        if (PROPERTY.equalsIgnoreCase(tagName)) {
            this.readProperty(element);
            return;
        }
        if (BEAN.equalsIgnoreCase(tagName)) {
            this.readBean(element);
            return;
        }
        //-<import resource="dao.xml"/>-->
        if (IMPORT.equalsIgnoreCase(tagName)) {
            String resource = element.getAttribute("resource");
            if (!resource.startsWith(SYMBOL.SLASH)) {
                resource = SYMBOL.SLASH + resource;
            }
            logger.info("-------------init bean " + resource + " ...---------------------------");
            Document doc = Xml.getXmlDocumentByPath(resource,
                DTD_FILE_NAME);
            if (doc != null) {
                this.initBeanByElement(doc.getDocumentElement());
            }
        }
    }

    private void loadChildren(Element element, NodeList nodeList) throws Exception {
        Node n;
        this.readBean(element);
        for (int i = 0; i < nodeList.getLength(); i++) {
            n = nodeList.item(i);
            if (n.getNodeType() != 1) {
                continue;
            }
            Element e = (Element) n;
            this.initBeanByElement(e);
        }
    }

    /**
     * 注入
     *
     * @param currentObject 对象
     * @param relyBeanName 依赖bean name
     * @param relyBean 依赖的bean
     * @throws Exception
     */
    private <T> void setRely(T currentObject, String relyBeanName, T relyBean)
        throws Exception {

        // set方法
        String setBeanMethod;
        // 设置bean类别
        Class<?> setBeanType = relyBean.getClass();
        setBeanMethod = StringUtility.getSetMethodNameByField(relyBeanName);
        Class<?> currentClass = currentObject.getClass();
        if (setBeanType == null) {
            setBeanType = this.beanFactoryCache.get(relyBeanName).getClass();
        }
        Method method = null;
        try {
            method = currentClass.getMethod(setBeanMethod, setBeanType);
        } catch (NoSuchMethodException e) {
            Class<?>[] interfaces = setBeanType.getInterfaces();
            if (interfaces.length == 0 && setBeanType.getSuperclass() != null) {
                interfaces = setBeanType.getSuperclass().getInterfaces();
            }
            for (Class<?> interfaceClazz : interfaces) {
                try {
                    method = currentClass.getMethod(setBeanMethod,
                        interfaceClazz);
                    break;
                } catch (NoSuchMethodException ex) {
                    Class<?>[] superClass = interfaceClazz.getInterfaces();
                    if (superClass != null && superClass.length > 0) {
                        try {
                            method = currentClass.getMethod(setBeanMethod,
                                superClass[0]);
                        } catch (NoSuchMethodException e1) {
                            logger.error(setBeanMethod
                                + " method not found!", e1);
                        } catch (NullPointerException e2) {
                            logger.error(interfaceClazz
                                + " interface not found!", e2);
                        }
                    }
                }
            }
        }
        if (method != null) {
            method.invoke(currentObject, relyBean);
        }
    }

    /**
     * 注入
     *
     * @param currentObject 对象
     * @param relyBeanName 依赖bean name
     * @throws Exception
     */
    private <T> void setValue(T currentObject, String relyBeanName, String value, String placeHolderKey)
        throws Exception {

        if (value.startsWith(SYMBOL.DOLLAR + SYMBOL.BIG_LEFT_PARENTHESIS) && value.endsWith(SYMBOL.BIG_RIGHT_PARENTHESIS)) {
            String key = value.replace(SYMBOL.DOLLAR + SYMBOL.BIG_LEFT_PARENTHESIS, SYMBOL.EMPTY);
            key = key.replace(SYMBOL.BIG_RIGHT_PARENTHESIS, SYMBOL.EMPTY);
            Properties properties = new Properties();
            try {
                properties.load(SparrowContainerImpl.class.getResourceAsStream(SYMBOL.SLASH + placeHolderKey + ".properties"));
            } catch (IOException ignore) {
            }
            value = properties.getProperty(key);
        }
        // set方法
        String setBeanMethod = StringUtility.getSetMethodNameByField(relyBeanName);
        Class<?> currentClass = currentObject.getClass();

        Method method;
        try {
            method = currentClass.getMethod(setBeanMethod, String.class);
            method.invoke(currentObject, value);
        } catch (NoSuchMethodException e) {
            try {
                method = currentClass.getMethod(setBeanMethod, Integer.class);
                method.invoke(currentClass, Integer.valueOf(value));
            } catch (NoSuchMethodException e2) {
                logger.error("no such method " + setBeanMethod, e2);
            }
        }
    }

    @Override
    public MethodAccessor getProxyBean(Class<?> clazz) {
        MethodAccessor methodAccessor = this.proxyBeanCache.get(clazz.getSimpleName());
        if (methodAccessor != null) {
            return methodAccessor;
        }
        synchronized (this) {
            methodAccessor = this.proxyBeanCache.get(clazz.getSimpleName());
            if (methodAccessor != null) {
                return methodAccessor;
            }
            methodAccessor = this.getGenerator4MethodAccessor().newMethodAccessor(clazz);
            this.proxyBeanCache.put(clazz.getSimpleName(), methodAccessor);
            return methodAccessor;
        }
    }

    @Override
    public List<TypeConvertor> getFieldList(Class clazz) {
        String clazzName = clazz.getSimpleName();
        List<TypeConvertor> typeConvertors = fieldCache.get(clazzName);
        if (typeConvertors != null) {
            return typeConvertors;
        }
        synchronized (this) {
            typeConvertors = fieldCache.get(clazzName);
            if (typeConvertors != null) {
                return typeConvertors;
            }

            // 初始化bean 的get set 方法
            typeConvertors = new ArrayList<TypeConvertor>();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("get")) {
                    typeConvertors.add(new TypeConvertor(StringUtility.getFieldByGetMethod(methodName), method.getReturnType()));
                }
            }
            this.fieldCache.put(clazzName, typeConvertors);
            return typeConvertors;
        }
    }

    @Override
    public Map<String, Method> getActionMethod(String clazzName) {
        return this.actionMethodCache.get(clazzName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName) {
        if (beanName == null) {
            return null;
        }
        // 先从缓存中获取，可能该对象已经被初始化
        T bean = (T) this.beanFactoryCache.get(beanName);
        if (bean != null) {
            return bean;
        }
        // 如果没有被初始化获取该类的元数据
        if (this.beanDefinitionMap.get(beanName) == null) {
            logger.warn(beanName + " object null at SparrowContainerImpl");
            return null;
        }
        try {
            // 类的元数据
            BeanDefinition beanDefinition = this.beanDefinitionMap
                .get(beanName);
            // 获取当前类
            Class<?> beanClass = beanDefinition.getBeanClass();
            // 初始化当前对象
            T currentObject = (T) beanClass.newInstance();
            // 初始化失败
            if (currentObject == null) {
                logger.warn(beanClass.getClass().getName() + "null");
                return null;
            }
            // 注入依赖对象
            if (beanDefinition.getRelyOnClass().size() != 0) {
                Iterator<String> bit = beanDefinition.getRelyOnClass()
                    .keySet().iterator();
                String key;
                while (bit.hasNext()) {
                    key = bit.next();
                    this.setRely(currentObject, key, this.getBean(key));
                }
            }
            return currentObject;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, Object> getAllBean() {
        return this.beanFactoryCache;
    }

    @Override
    public void init() {
        logger.info("-----------------Ioc container init ....-------------------");
        try {
            logger.info("-------------system config file init ...-------------------");
            initSystemConfig();

            logger.info("-------------init bean ...---------------------------");
            Document doc = Xml.getXmlDocumentByPath(this.xmlName, DTD_FILE_NAME
            );
            if (doc != null) {
                this.initBeanByElement(doc.getDocumentElement());
            }
            logger.info("-------------init initializer ...--------------------------");
            Initializer initializer = this.getBean(
                SYS_OBJECT_NAME.INITIALIZER_SERVER);

            if (initializer != null) {
                initializer.init();
            }
            logger.info("-----------------Ioc container init success...-------------------");
        } catch (Exception e) {
            logger.error("ioc init error", e);
        } finally {
            //<!--global bean-->
            this.cacheBeanDefinition("result", Result.class);
            this.cacheBeanDefinition("login", Login.class);
            this.cacheBeanDefinition("loginDialog", LoginDialog.class);
        }
    }

    private void initSystemConfig() throws IOException {
        if (StringUtility.isNullOrEmpty(this.systemConfigPath)) {
            return;
        }
        Config.initSystem(this.systemConfigPath);
        String internationalization = Config
            .getValue(CONFIG.INTERNATIONALIZATION);
        if (StringUtility.isNullOrEmpty(internationalization)) {
            internationalization = Config
                .getValue(CONFIG.LANGUAGE);
        }
        if (StringUtility.isNullOrEmpty(internationalization)) {
            internationalization = CONSTANT.DEFAULT_LANGUAGE;
        }
        String[] internationalizationArray = internationalization
            .split(SYMBOL.COMMA);
        for (String i18n : internationalizationArray) {
            Config.initInternationalization(i18n);
        }
    }

    @Override
    public Map<String, Object> getBeanMap(CONTAINER container) {
        return this.typeBeanFactory.get(container.toString());
    }
}