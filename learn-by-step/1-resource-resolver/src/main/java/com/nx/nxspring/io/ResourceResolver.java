package com.nx.nxspring.io;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * resource resolver
 *
 * @author nx-xn2002
 */
@AllArgsConstructor
@Slf4j
public class ResourceResolver {
    String basePackage;

    public <R> List<R> scan(Function<Resource, R> mapper) {
        //1. 将当前包名变成文件路径
        String basePackagePath = this.basePackage.replace(".", "/");
        log.info("scan path: {}", basePackagePath);
        try {
            //2. 查找文件路径下的资源列表
            Enumeration<URL> en = getClassLoader().getResources(basePackagePath);
            //3. 扫描读取并返回结果
            List<R> collector = new ArrayList<>();
            while (en.hasMoreElements()) {
                URI uri = en.nextElement().toURI();
                String uriStr = removeTrailingSlash(uriToString(uri));
                String uriBaseStr = uriStr.substring(0, uriStr.length() - basePackagePath.length());
                if (uri.toString().startsWith("file:")) {
                    uriBaseStr = uriBaseStr.substring(5);
                }
                if (uri.toString().startsWith("jar:")) {
                    scanFile(true, uriBaseStr, jarUriToPath(basePackagePath, uri), collector, mapper);
                } else {
                    scanFile(false, uriBaseStr, Paths.get(uri), collector, mapper);
                }
            }
            return collector;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 扫描文件
     *
     * @param isJar     is jar
     * @param base      base
     * @param root      root
     * @param collector collector
     * @param mapper    mapper
     * @throws IOException ioexception
     */
    <R> void scanFile(boolean isJar, String base, Path root, List<R> collector, Function<Resource, R> mapper) throws IOException {
        String baseDir = removeTrailingSlash(base);
        Files.walk(root).filter(Files::isRegularFile).forEach(file -> {
            Resource res = null;
            if (isJar) {
                res = new Resource(baseDir, removeLeadingSlash(file.toString()));
            } else {
                String path = file.toString();
                String name = removeLeadingSlash(path.substring(baseDir.length()));
                res = new Resource("file:" + path, name);
            }
            log.info("found resource: {}", res);
            R r = mapper.apply(res);
            if (r != null) {
                collector.add(r);
            }
        });
    }

    /**
     * jar 包 uri 转字符串
     *
     * @param basePackagePath base package path
     * @param jarUri          jar uri
     * @return {@link Path }
     * @throws IOException ioexception
     */
    Path jarUriToPath(String basePackagePath, URI jarUri) throws IOException {
        return FileSystems.newFileSystem(jarUri, Map.of()).getPath(basePackagePath);
    }

    /**
     * URL 转字符串
     *
     * @param uri uri
     * @return {@link String }
     */
    String uriToString(URI uri) {
        return URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8);
    }

    /**
     * 处理末尾
     *
     * @param s s
     * @return {@link String }
     */
    String removeTrailingSlash(String s) {
        if (s.endsWith("/") || s.endsWith("\\")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    /**
     * 处理开头
     *
     * @param s s
     * @return {@link String }
     */
    String removeLeadingSlash(String s) {
        if (s.startsWith("/") || s.startsWith("\\")) {
            s = s.substring(1);
        }
        return s;
    }

    /**
     * 获取类加载器
     * 优先获取当前线程的上下文类加载器。如果上下文类加载器为空，就会使用当前类的类加载器
     *
     * @return {@link ClassLoader }
     */
    private ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //如果上下文类加载器为空, 就使用当前类的类加载器
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        return classLoader;
    }
}
