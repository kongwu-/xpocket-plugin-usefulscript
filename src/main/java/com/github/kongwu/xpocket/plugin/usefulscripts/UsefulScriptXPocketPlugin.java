package com.github.kongwu.xpocket.plugin.usefulscripts;

import com.perfma.xlab.xpocket.spi.AbstractXPocketPlugin;
import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

/**
 * 这个类主要用于插件整体的声明周期管理和日志输出等，如非必要可以不实现
 * @author kongwu <jiangxin1035@163.com>
 */
public class UsefulScriptXPocketPlugin extends AbstractXPocketPlugin {

    private static final String LOGO = "o   o  o-o  o--o o--o o   o o         o-o    o-o o--o  o-O-o o--o  o-O-o  o-o  \n" +
            "|   | |     |    |    |   | |        |      /    |   |   |   |   |   |   |     \n" +
            "|   |  o-o  O-o  O-o  |   | |    o-o  o-o  O     O-Oo    |   O--o    |    o-o  \n" +
            "|   |     | |    |    |   | |            |  \\    |  \\    |   |       |       | \n" +
            " o-o  o--o  o--o o     o-o  O---o    o--o    o-o o   o o-O-o o       o   o--o  ";


    private static final String PLUGIN_NAME = "usefulscripts";

    public static final String PLUGIN_BIN_PATH = OS.USER_HOME + File.separator + ".xpocket"
            + File.separator + ".usefulscripts" + File.separator;

    /**
     * 用于输出自定义LOGO
     * @param process 
     */
    @Override
    public void printLogo(XPocketProcess process) {
        process.output(LOGO);
    }

    /**
     * 插件会话被切出时被调用
     * @param context 
     */
    @Override
    public void switchOff(SessionContext context) {
        super.switchOff(context); 
    }

    /**
     * 插件会话被切入时被调用
     * @param context 
     */
    @Override
    public void switchOn(SessionContext context) {
        super.switchOn(context); 
    }

    /**
     * XPocket整体退出时被调用，用于清理插件本身使用的资源
     * @throws Throwable 
     */
    @Override
    public void destory() throws Throwable {
        super.destory();

    }

    /**
     * 插件首次被初始化时被调用
     * @param process 
     */
    @Override
    public void init(XPocketProcess process) {
        super.init(process);
        if(OS.isWindows()){
            process.output("Sorry, Windows OS is not supported");
            process.end();
            return ;
        }
        unpackScripts();
    }

    /**
     * 将 shell 解压到 ${user.home} 。由于暂时没有版本的概念，所以每次先清空上一次的解压目录
     */
    private void unpackScripts() {
        try {
            FileUtils.deleteDirectory(new File(PLUGIN_BIN_PATH));

            URI uri = UsefulScriptXPocketCommand.class.getResource("/bin").toURI();
            Files.createDirectories(Paths.get(PLUGIN_BIN_PATH));
            // 这里fileSystem 虽然没有引用，但这玩意是个很奇怪的设计，全局单例，必须要创建
            try (FileSystem fileSystem = (uri.getScheme().equals("jar") ? FileSystems.newFileSystem(uri, Collections.emptyMap()) : null)) {
                Path myPath = Paths.get(uri);
                Files.walkFileTree(myPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        String sourceName = file.getFileName().toString();
                        Files.copy(file, Paths.get(PLUGIN_BIN_PATH, sourceName), StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
