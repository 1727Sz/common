package cn.sz1727l.core.util;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sz1727.core.tools.filter.WildcardFilter;

/**
 * cn.sz1727l.core.util.XClassUtil.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class XClassUtil extends ClassUtils {

	private static final Logger LOG = LoggerFactory.getLogger(XClassUtil.class);
	public static final Object[] NO_ARGS = new Object[] {};
	public static final Class<?>[] NO_ARGS_TYPE = new Class<?>[] {};

	private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_MAP = new HashMap<Class<?>, Class<?>>();
	private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAME_MAP = new HashMap<String, Class<?>>(32);

	static {
		WRAPPER_TO_PRIMITIVE_MAP.put(Boolean.class, Boolean.TYPE);
		WRAPPER_TO_PRIMITIVE_MAP.put(Byte.class, Byte.TYPE);
		WRAPPER_TO_PRIMITIVE_MAP.put(Character.class, Character.TYPE);
		WRAPPER_TO_PRIMITIVE_MAP.put(Short.class, Short.TYPE);
		WRAPPER_TO_PRIMITIVE_MAP.put(Integer.class, Integer.TYPE);
		WRAPPER_TO_PRIMITIVE_MAP.put(Long.class, Long.TYPE);
		WRAPPER_TO_PRIMITIVE_MAP.put(Double.class, Double.TYPE);
		WRAPPER_TO_PRIMITIVE_MAP.put(Float.class, Float.TYPE);
		WRAPPER_TO_PRIMITIVE_MAP.put(Void.TYPE, Void.TYPE);

		Set<Class<?>> primitiveTypes = new HashSet<Class<?>>(32);
		primitiveTypes.addAll(WRAPPER_TO_PRIMITIVE_MAP.values());
		for (Class<?> primitiveType : primitiveTypes) {
			PRIMITIVE_TYPE_NAME_MAP.put(primitiveType.getName(), primitiveType);
		}
	}

	private XClassUtil() {
	}

	public static boolean isConcrete(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz may not be null");
		}
		return !(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()));
	}

	/**
	 * Load a given resource.
	 * <p/>
	 * This method will try to load the resource using the following methods (in
	 * order):
	 * <ul>
	 * <li>From {@link Thread#getContextClassLoader()
	 * Thread.currentThread().getContextClassLoader()}
	 * <li>From {@link Class#getClassLoader() ClassUtils.class.getClassLoader()}
	 * <li>From the {@link Class#getClassLoader() callingClass.getClassLoader()
	 * * }
	 * </ul>
	 *
	 * @param resourceName
	 *            The name of the resource to load
	 * @param callingClass
	 *            The Class object of the calling object
	 *
	 * @return A URL pointing to the resource to load or null if the resource is
	 *         not found
	 */
	public static URL getResource(final String resourceName, final Class<?> callingClass) {
		URL url = AccessController.doPrivileged(new PrivilegedAction<URL>() {
			public URL run() {
				final ClassLoader cl = Thread.currentThread().getContextClassLoader();
				return cl != null ? cl.getResource(resourceName) : null;
			}
		});

		if (url == null) {
			url = AccessController.doPrivileged(new PrivilegedAction<URL>() {
				public URL run() {
					return ClassUtils.class.getClassLoader().getResource(resourceName);
				}
			});
		}

		if (url == null) {
			url = AccessController.doPrivileged(new PrivilegedAction<URL>() {
				public URL run() {
					return callingClass.getClassLoader().getResource(resourceName);
				}
			});
		}

		return url;
	}

	public static Enumeration<URL> getResources(final String resourceName, final Class<?> callingClass) {
		Enumeration<URL> enumeration = AccessController.doPrivileged(new PrivilegedAction<Enumeration<URL>>() {
			public Enumeration<URL> run() {
				try {
					final ClassLoader cl = Thread.currentThread().getContextClassLoader();
					return cl != null ? cl.getResources(resourceName) : null;
				} catch (IOException e) {
					LOG.error("XClassUtil.getResources error 1 << : ", e.getMessage(), e);
					return null;
				}
			}
		});

		if (enumeration == null) {
			enumeration = AccessController.doPrivileged(new PrivilegedAction<Enumeration<URL>>() {
				public Enumeration<URL> run() {
					try {
						return XClassUtil.class.getClassLoader().getResources(resourceName);
					} catch (IOException e) {
						LOG.error("XClassUtil.getResources error 2 << : ", e.getMessage(), e);
						return null;
					}
				}
			});
		}

		if (enumeration == null) {
			enumeration = AccessController.doPrivileged(new PrivilegedAction<Enumeration<URL>>() {
				public Enumeration<URL> run() {
					try {
						return callingClass.getClassLoader().getResources(resourceName);
					} catch (IOException e) {
						LOG.error("XClassUtil.getResources error 3 << : ", e.getMessage(), e);
						return null;
					}
				}
			});
		}

		return enumeration;
	}

	/**
	 * Load a class with a given name from the given classloader.
	 *
	 * @param className
	 *            the name of the class to load
	 * @param classLoader
	 *            the loader to load it from
	 * @return the instance of the class
	 * @throws ClassNotFoundException
	 *             if the class is not available in the class loader
	 */
	public static Class<?> loadClass(final String className, final ClassLoader classLoader) throws ClassNotFoundException {
		return classLoader.loadClass(className);
	}

	/**
	 * Ensure that the given class is properly initialized when the argument is
	 * passed in as .class literal. This method can never fail unless the
	 * bytecode is corrupted or the VM is otherwise seriously confused.
	 *
	 * @param clazz
	 *            the Class to be initialized
	 * @return the same class but initialized
	 */
	public static Class<?> initializeClass(Class<?> clazz) {
		try {
			return getClass(clazz.getName(), true);
		} catch (ClassNotFoundException e) {
			IllegalStateException ise = new IllegalStateException();
			ise.initCause(e);
			throw ise;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T instanciateClass(Class<? extends T> clazz, Object... constructorArgs) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Class<?>[] args;
		if (constructorArgs != null) {
			args = new Class[constructorArgs.length];
			for (int i = 0; i < constructorArgs.length; i++) {
				if (constructorArgs[i] == null) {
					args[i] = null;
				} else {
					args[i] = constructorArgs[i].getClass();
				}
			}
		} else {
			args = new Class[0];
		}

		// try the arguments as given
		Constructor<?> ctor = getConstructor(clazz, args);

		if (ctor == null) {
			// try again but adapt value classes to primitives
			ctor = getConstructor(clazz, wrappersToPrimitives(args));
		}

		if (ctor == null) {
			StringBuilder argsString = new StringBuilder(100);
			for (Class<?> arg : args) {
				argsString.append(arg.getName()).append(", ");
			}
			throw new NoSuchMethodException("could not find constructor on class: " + clazz + ", with matching arg params: " + argsString);
		}

		return (T) ctor.newInstance(constructorArgs);
	}

	public static Object instanciateClass(String name, Object... constructorArgs) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException, InvocationTargetException {
		return instanciateClass(name, constructorArgs, (ClassLoader) null);
	}

	public static Class<?>[] getParameterTypes(Object bean, String methodName) {
		if (!methodName.startsWith("set")) {
			methodName = "set" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
		}

		Method[] methods = bean.getClass().getMethods();

		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				return methods[i].getParameterTypes();
			}
		}

		return new Class[] {};
	}

	/**
	 * Returns a matching method for the given name and parameters on the given
	 * class If the parameterTypes arguments is null it will return the first
	 * matching method on the class.
	 *
	 * @param clazz
	 *            the class to find the method on
	 * @param name
	 *            the method name to find
	 * @param parameterTypes
	 *            an array of argument types or null
	 * @return the Method object or null if none was found
	 */
	public static Method getMethod(Class<?> clazz, String name, Class<?>[] parameterTypes) {
		return getMethod(clazz, name, parameterTypes, false);
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>[] parameterTypes, boolean acceptNulls) {
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(name)) {
				if (parameterTypes == null) {
					return methods[i];
				} else if (compare(methods[i].getParameterTypes(), parameterTypes, true, acceptNulls)) {
					return methods[i];
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static Constructor getConstructor(Class<?> clazz, Class<?>[] paramTypes) {
		return getConstructor(clazz, paramTypes, false);
	}

	/**
	 * Returns available constructor in the target class that as the parameters
	 * specified.
	 *
	 * @param clazz
	 *            the class to search
	 * @param paramTypes
	 *            the param types to match against
	 * @param exactMatch
	 *            should exact types be used (i.e. equals rather than
	 *            isAssignableFrom.)
	 * @return The matching constructor or null if no matching constructor is
	 *         found
	 */
	@SuppressWarnings("rawtypes")
	public static Constructor getConstructor(Class<?> clazz, Class<?>[] paramTypes, boolean exactMatch) {
		Constructor[] ctors = clazz.getConstructors();
		for (int i = 0; i < ctors.length; i++) {
			Class<?>[] types = ctors[i].getParameterTypes();
			if (types.length == paramTypes.length) {
				int matchCount = 0;
				for (int x = 0; x < types.length; x++) {
					if (paramTypes[x] == null) {
						matchCount++;
					} else {
						if (exactMatch) {
							if (paramTypes[x].equals(types[x]) || types[x].equals(paramTypes[x])) {
								matchCount++;
							}
						} else {
							if (paramTypes[x].isAssignableFrom(types[x]) || types[x].isAssignableFrom(paramTypes[x])) {
								matchCount++;
							}
						}
					}
				}
				if (matchCount == types.length) {
					return ctors[i];
				}
			}
		}
		return null;
	}

	/**
	 * A helper method that will find all matching methods on a class with the
	 * given parameter type
	 *
	 * @param implementation
	 *            the class to build methods on
	 * @param parameterTypes
	 *            the argument param types to look for
	 * @param voidOk
	 *            whether void methods shouldbe included in the found list
	 * @param matchOnObject
	 *            determines whether parameters of Object type are matched when
	 *            they are of Object.class type
	 * @param ignoredMethodNames
	 *            a Set of method names to ignore. Often 'equals' is not a
	 *            desired match. This argument can be null.
	 * @return a List of methods on the class that match the criteria. If there
	 *         are none, an empty list is returned
	 */
	public static List<Method> getSatisfiableMethods(Class<?> implementation, Class<?>[] parameterTypes, boolean voidOk, boolean matchOnObject, Set<String> ignoredMethodNames) {
		return getSatisfiableMethods(implementation, parameterTypes, voidOk, matchOnObject, ignoredMethodNames, null);
	}

	/**
	 * A helper method that will find all matching methods on a class with the
	 * given parameter type
	 *
	 * @param implementation
	 *            the class to build methods on
	 * @param parameterTypes
	 *            the argument param types to look for
	 * @param voidOk
	 *            whether void methods shouldbe included in the found list
	 * @param matchOnObject
	 *            determines whether parameters of Object type are matched when
	 *            they are of Object.class type
	 * @param ignoredMethodNames
	 *            a Set of method names to ignore. Often 'equals' is not a
	 *            desired match. This argument can be null.
	 * @param filter
	 *            Wildcard expression filter that allows methods to be matched
	 *            using wildcards i.e. 'get*'
	 * @return a List of methods on the class that match the criteria. If there
	 *         are none, an empty list is returned
	 */
	public static List<Method> getSatisfiableMethods(Class<?> implementation, Class<?>[] parameterTypes, boolean voidOk, boolean matchOnObject, Collection<String> ignoredMethodNames,
			WildcardFilter filter) {
		List<Method> result = new ArrayList<Method>();

		if (ignoredMethodNames == null) {
			ignoredMethodNames = Collections.emptySet();
		}

		Method[] methods = implementation.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			// supporting wildcards
			if (filter != null && filter.accept(method.getName())) {
				continue;
			}
			Class<?>[] methodParams = method.getParameterTypes();

			if (compare(methodParams, parameterTypes, matchOnObject)) {
				if (!ignoredMethodNames.contains(method.getName())) {
					String returnType = method.getReturnType().getName();
					if (("void".equals(returnType) && voidOk) || !"void".equals(returnType)) {
						result.add(method);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Match all method son a class with a defined return type
	 * 
	 * @param implementation
	 *            the class to search
	 * @param returnType
	 *            the return type to match
	 * @param matchOnObject
	 *            whether {@link Object} methods should be matched
	 * @param ignoredMethodNames
	 *            a set of method names to ignore
	 * @return the list of methods that matched the return type and criteria. If
	 *         none are found an empty result is returned
	 */
	public static List<Method> getSatisfiableMethodsWithReturnType(Class<?> implementation, Class<?> returnType, boolean matchOnObject, Set<String> ignoredMethodNames) {
		List<Method> result = new ArrayList<Method>();

		if (ignoredMethodNames == null) {
			ignoredMethodNames = Collections.emptySet();
		}

		Method[] methods = implementation.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			Class<?> returns = method.getReturnType();

			if (compare(new Class[] { returns }, new Class[] { returnType }, matchOnObject)) {
				if (!ignoredMethodNames.contains(method.getName())) {
					result.add(method);
				}
			}
		}

		return result;
	}

	/**
	 * Used for creating an array of class types for an array or single object
	 *
	 * @param object
	 *            single object or array. If this parameter is null or a zero
	 *            length array then {@link #NO_ARGS_TYPE} is returned
	 * @return an array of class types for the object
	 */
	public static Class<?>[] getClassTypes(Object object) {
		if (object == null) {
			return NO_ARGS_TYPE;
		}

		Class<?>[] types;

		if (object instanceof Object[]) {
			Object[] objects = (Object[]) object;
			if (objects.length == 0) {
				return NO_ARGS_TYPE;
			}
			types = new Class[objects.length];
			for (int i = 0; i < objects.length; i++) {
				Object o = objects[i];
				if (o != null) {
					types[i] = o.getClass();
				}
			}
		} else {
			types = new Class[] { object.getClass() };
		}

		return types;
	}

	public static String getClassName(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		String name = clazz.getName();
		return name.substring(name.lastIndexOf(".") + 1);
	}

	public static boolean compare(Class<?>[] c1, Class<?>[] c2, boolean matchOnObject) {
		return compare(c1, c2, matchOnObject, false);
	}

	/**
	 * Returns true if the types from array c2 are assignable to the types from
	 * c1 and the arrays are the same size. If matchOnObject argument is true
	 * and there is a parameter of type Object in c1 then the method returns
	 * false. If acceptNulls argument is true, null values are accepted in c2.
	 * 
	 * @param c1
	 *            parameter types array
	 * @param c2
	 *            parameter types array
	 * @param matchOnObject
	 *            return false if there is a parameter of type Object in c1
	 * @param acceptNulls
	 *            allows null parameter types in c2
	 * @return true if arrays are the same size and the types assignable from c2
	 *         to c1
	 */
	public static boolean compare(Class<?>[] c1, Class<?>[] c2, boolean matchOnObject, boolean acceptNulls) {
		if (c1.length != c2.length) {
			return false;
		}
		for (int i = 0; i < c1.length; i++) {
			if (!acceptNulls) {
				if ((c1[i] == null) || (c2[i] == null)) {
					return false;
				}
			} else {
				if (c1[i] == null) {
					return false;
				}
				if ((c2[i] == null) && (c1[i].isPrimitive())) {
					return false;
				}
				if (c2[i] == null) {
					return true;
				}
			}
			if (c1[i].equals(Object.class) && !matchOnObject) {
				return false;
			}
			if (!c1[i].isAssignableFrom(c2[i])) {
				return false;
			}
		}
		return true;
	}

	public static Class<?> wrapperToPrimitive(Class<?> wrapper) {
		return (Class<?>) XMapUtil.getObject(WRAPPER_TO_PRIMITIVE_MAP, wrapper, wrapper);
	}

	public static Class<?>[] wrappersToPrimitives(Class<?>[] wrappers) {
		if (wrappers == null) {
			return new Class<?>[] {};
		}

		if (wrappers.length == 0) {
			return wrappers;
		}

		Class<?>[] primitives = new Class[wrappers.length];

		for (int i = 0; i < wrappers.length; i++) {
			primitives[i] = (Class<?>) XMapUtil.getObject(WRAPPER_TO_PRIMITIVE_MAP, wrappers[i], wrappers[i]);
		}

		return primitives;
	}

	/**
	 * Provide a simple-to-understand class name (with access to only Java 1.4
	 * API).
	 *
	 * @param clazz
	 *            The class whose name we will generate
	 * @return A readable name for the class
	 */
	public static String getSimpleName(Class<?> clazz) {
		if (null == clazz) {
			return "null";
		} else {
			return classNameHelper(new BufferedReader(new CharArrayReader(clazz.getName().toCharArray())));
		}
	}

	private static String classNameHelper(Reader encodedName) {
		// I did consider separating this data from the code, but I could not
		// find a
		// solution that was as clear to read, or clearly motivated (these data
		// are not
		// used elsewhere).

		try {
			encodedName.mark(1);
			switch (encodedName.read()) {
			case -1:
				return "null";
			case 'Z':
				return "boolean";
			case 'B':
				return "byte";
			case 'C':
				return "char";
			case 'D':
				return "double";
			case 'F':
				return "float";
			case 'I':
				return "int";
			case 'J':
				return "long";
			case 'S':
				return "short";
			case '[':
				return classNameHelper(encodedName) + "[]";
			case 'L':
				return shorten(new BufferedReader(encodedName).readLine());
			default:
				encodedName.reset();
				return shorten(new BufferedReader(encodedName).readLine());
			}
		} catch (IOException e) {
			return "unknown type: " + e.getMessage();
		}
	}

	/**
	 * @param sClazz
	 *            A class name (with possible package and trailing semicolon)
	 * @return The short name for the class
	 */
	private static String shorten(String sClazz) {
		String clazz = sClazz;
		if (null != clazz && clazz.endsWith(";")) {
			clazz = clazz.substring(0, clazz.length() - 1);
		}
		if (null != clazz && clazz.lastIndexOf(".") > -1) {
			clazz = clazz.substring(clazz.lastIndexOf(".") + 1, clazz.length());
		}
		return clazz;
	}

	/**
	 * Simple helper for writing object equalities.
	 *
	 * @param a
	 *            object to compare
	 * @param b
	 *            object to be compared to
	 * @return true if the objects are equal (value or reference), false
	 *         otherwise
	 */
	public static boolean equal(Object a, Object b) {
		if (null == a) {
			return null == b;
		} else {
			return null != b && a.equals(b);
		}
	}

	public static int hash(Object[] state) {
		int hash = 0;
		for (int i = 0; i < state.length; ++i) {
			hash = hash * 31 + (null == state[i] ? 0 : state[i].hashCode());
		}
		return hash;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addLibrariesToClasspath(List urls) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		ClassLoader sys = ClassLoader.getSystemClassLoader();
		if (!(sys instanceof URLClassLoader)) {
			throw new IllegalArgumentException("System has been started with an unsupported classloader: " + sys.getClass().getName());
		}

		// system classloader is in this case the one that launched the
		// application,
		// which is usually something like a JDK-vendor proprietary
		// AppClassLoader
		URLClassLoader sysCl = (URLClassLoader) sys;

		// get a Method ref from the normal class, but invoke on a proprietary
		// parent
		// object,
		// as this method is usually protected in those classloaders
		Class refClass = URLClassLoader.class;
		Method methodAddUrl = refClass.getDeclaredMethod("addURL", new Class[] { URL.class });
		methodAddUrl.setAccessible(true);
		for (Iterator it = urls.iterator(); it.hasNext();) {
			URL url = (URL) it.next();
			methodAddUrl.invoke(sysCl, url);
		}
	}

	public static URL getClassPathRoot(Class<?> clazz) {
		CodeSource cs = clazz.getProtectionDomain().getCodeSource();
		return (cs != null ? cs.getLocation() : null);
	}
}
