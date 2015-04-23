package edu.mit.mitmobile2.shared.runtime;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by grmartin on 4/22/15.
 */
public class RuntimeUtils {
    private static Set<String> RUNTIME_PACKAGES;

    public static String[] STANDARD_JRE_EE_AND_JDK_PACKAGES = {
        "java", "javax", "org.ietf.jgss", "org.omg", "org.w3c.dom", "org.xml.sax", "javafx",
        "sun", "oracle", "netscape.javascript"
    };

    static {
        RUNTIME_PACKAGES = new CopyOnWriteArraySet<>();

        RUNTIME_PACKAGES.addAll(Arrays.asList(STANDARD_JRE_EE_AND_JDK_PACKAGES));
    }

    public static Set<String> getRuntimePackages() {
        return new HashSet<>(RUNTIME_PACKAGES);
    }

    public static void addToRuntimePackages(Collection<Package> packages) {
        for (Package pkg : packages) {
            RUNTIME_PACKAGES.add(pkg.getName());
        }
    }

    public static void addStringsToRuntimePackages(Collection<String> packages) {
        RUNTIME_PACKAGES.addAll(packages);
    }

    public static void addToRuntimePackages(Package... packages) {
        for (Package pkg : packages) {
            RUNTIME_PACKAGES.add(pkg.getName());
        }
    }

    public static Set<String> toPackageNameSet(Package... packages) {
        Set<String> returnSet = new HashSet<>(packages.length);
        for (Package pkg : packages) {
            returnSet.add(pkg.getName());
        }
        return returnSet;
    }

    public static Set<String> toPackageNameSet(Collection<Package> packages) {
        Set<String> returnSet = new HashSet<>(packages.size());
        for (Package pkg : packages) {
            returnSet.add(pkg.getName());
        }
        return returnSet;
    }

    public static void addToRuntimePackages(String... packages) {
        Collections.addAll(RUNTIME_PACKAGES, packages);
    }

    public static boolean isInPackage(Package pkg, Class klass) {
        return pkg != null && isInPackage(pkg.getName(), klass);
    }

    public static boolean isInPackage(String pkg, Class klass) {

        boolean nonValidPkgAndClass = (pkg == null || (pkg.length() < 1 || pkg.trim().length() < 1) || klass == null);

        boolean startsWith = !nonValidPkgAndClass && klass.getPackage().getName().startsWith(pkg);

        return !nonValidPkgAndClass && startsWith;
    }

    public static boolean isInPackages(Collection<Package> packages, Class klass) {
        return packages != null && isInPackageNames(toPackageNameSet(packages), klass);
    }

    public static boolean isInPackageNames(Collection<String> packages, Class klass) {
        if (packages == null || klass == null) return false;
        for (String pkgName : packages) {
            if (isInPackage(pkgName, klass)) return true;
        }
        return false;
    }

    public static boolean isInRuntimePackage(Class klass) {
        return isInPackageNames(RUNTIME_PACKAGES, klass);
    }
}
