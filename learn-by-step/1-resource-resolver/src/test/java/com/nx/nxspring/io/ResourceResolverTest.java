package com.nx.nxspring.io;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class ResourceResolverTest {

    @Test
    void scanTest() {
        String pkg = "com.nx.nxspring.loadexample";
        ResourceResolver resolver = new ResourceResolver(pkg);
        List<String> scannedClasses = resolver.scan(res -> {
            String name = res.name();
            if (name.endsWith(".class")) {
                return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
            }
            return null;
        });
        if (scannedClasses != null) {
            Collections.sort(scannedClasses);
            System.out.println("-----ScannedClasses-----");
            scannedClasses.forEach(System.out::println);
            System.out.println("------------------------");
        }
    }
}