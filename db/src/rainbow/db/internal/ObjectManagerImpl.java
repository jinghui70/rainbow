package rainbow.db.internal;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;

import rainbow.core.bundle.Bean;
import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.InjectProvider;
import rainbow.db.object.Name;
import rainbow.db.object.ObjectManager;
import rainbow.db.object.ObjectType;

@Bean(extension = InjectProvider.class)
public class ObjectManagerImpl extends InjectProvider implements ObjectManager {

	private static final Logger logger = LoggerFactory.getLogger(ObjectManagerImpl.class);

	@Override
	public String translate(String typeName, Object key) {
		ObjectType objectType = ExtensionRegistry.getExtensionObject(ObjectType.class, typeName);
		if (objectType == null) {
			logger.warn("objectType [{}] not available", typeName);
			return key.toString();
		} else
			return objectType.getObjectName(key);
	}

	@Override
	public <T> List<T> listSetName(List<T> source, List<ObjectNameRule> rules) {
		if (!Utils.isNullOrEmpty(source)) {
			for (ObjectNameRule rule : rules) {
				ObjectType objectType = ExtensionRegistry.getExtensionObject(ObjectType.class, rule.getObjType());
				if (objectType == null)
					logger.warn("objectType [{}] not available", rule.getObjType());
				else {
					for (Object obj : source) {
						try {
							Object key = rule.getGetKeyMethod().invoke(obj);
							String name = objectType.hasSubType() ? objectType.getObjectName(rule.getSubType(), key)
									: objectType.getObjectName(key);
							rule.getSetNameMethod().invoke(obj, name);
						} catch (Exception e) {
						}
					}
				}
			}
		}
		return source;
	}

	@Override
	public <T> T setName(T obj, List<ObjectNameRule> rules) {
		for (ObjectNameRule rule : rules) {
			ObjectType objectType = ExtensionRegistry.getExtensionObject(ObjectType.class, rule.getObjType());
			if (objectType == null)
				logger.warn("objectType [{}] not available", rule.getObjType());
			else {
				try {
					Object key = rule.getGetKeyMethod().invoke(obj);
					String name = objectType.hasSubType() ? objectType.getObjectName(rule.getSubType(), key)
							: objectType.getObjectName(key);
					rule.getSetNameMethod().invoke(obj, name);
				} catch (Exception e) {
				}
			}
		}
		return obj;
	}

	public List<ObjectNameRule> getObjectNameRule(Class<?> clazz) {
		ImmutableList.Builder<ObjectNameRule> builder = ImmutableList.builder();
		for (Method method : clazz.getMethods()) {
			String methodName = method.getName();
			if (methodName.startsWith("set") && method.getParameterTypes().length == 1
					&& method.getParameterTypes()[0] == String.class) {
				Name name = method.getAnnotation(Name.class);
				if (name != null) {
					ObjectNameRule rule = new ObjectNameRule();
					rule.setSetNameMethod(method);
					rule.setObjType(name.type());
					rule.setSubType(name.subType());

					String m = name.src();
					if (m.isEmpty()) {
						m = "get" + Utils.substringBetween(methodName, "set", "Name");
					} else {
						m = "get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, m);
					}
					try {
						rule.setGetKeyMethod(clazz.getMethod(m));
					} catch (NoSuchMethodException | SecurityException e) {
						logger.error("invalide @Name definition of class [{}] @ method [{}]", clazz.getName(),
								methodName, e);
						throw new RuntimeException(e);
					}
					builder.add(rule);
				}
			}
		}
		return builder.build();
	}

	@Override
	public Class<?> getInjectClass() {
		return ObjectManager.class;
	}

	@Override
	public Object getInjectObject(String name, String destClassName) {
		return this;
	}

}
