/**
 *
 */
package com.github.torbinsky.morphia.mapping.lazy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class LazyFeatureDependencies {

	static Logger log = LoggerFactory.getLogger(LazyFeatureDependencies.class);
    private static Boolean fullFilled;

    private LazyFeatureDependencies() {
    }

    public static boolean assertDependencyFullFilled() {
        boolean fullfilled = testDependencyFullFilled();
        if (!fullfilled)
            log.warn("Lazy loading impossible due to missing dependencies.");
        return fullfilled;
    }

    public static boolean testDependencyFullFilled() {
        if (fullFilled != null){
        	log.trace("Fulfilled already set with result[" + fullFilled + "]");
            return fullFilled;
        }
        try {
            fullFilled = Class.forName("net.sf.cglib.proxy.Enhancer") != null
                    && Class.forName("com.thoughtworks.proxy.toys.hotswap.HotSwapping") != null;
        } catch (ClassNotFoundException e) {
            fullFilled = false;
            log.debug("Dependency wasn't fulfilled because the class wasn't found.", e);
        } catch (LinkageError e) {
            fullFilled = false;
            log.debug("Dependency wasn't fulfilled because there was a LinkageError.", e);
        }
        return fullFilled;
    }

    /**
     * @return A LazyProxyFactory
     */
    public static LazyProxyFactory createDefaultProxyFactory() {
        if (testDependencyFullFilled()) {
            String factoryClassName = "com.github.torbinsky.morphia.mapping.lazy.CGLibLazyProxyFactory";
            try {
                return (LazyProxyFactory) Class.forName(factoryClassName).newInstance();
            } catch (Exception e) {
                log.error("While instanciating " + factoryClassName, e);
            }
        }
        return null;
    }
}
