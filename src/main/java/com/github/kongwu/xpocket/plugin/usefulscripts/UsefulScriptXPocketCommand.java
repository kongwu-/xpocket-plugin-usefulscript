package com.github.kongwu.xpocket.plugin.usefulscripts;

import com.perfma.xlab.xpocket.spi.XPocketPlugin;
import com.perfma.xlab.xpocket.spi.command.AbstractXPocketCommand;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.command.XPocketProcessTemplate;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

import static com.github.kongwu.xpocket.plugin.usefulscripts.UsefulScriptXPocketPlugin.PLUGIN_BIN_PATH;

/**
 * 用于实现每个命令的核心逻辑，一个或者多个命令指向一个类。
 *
 * @author kongwu <jiangxin1035@163.com>
 */

@CommandInfo(name = "ap", usage = "ap path0 path1 ... pathn", index = 0)

@CommandInfo(name = "rp", usage = "tcp-connection-state-counter", index = 1)

@CommandInfo(name = "tcp-connection-state-counter", usage = "ap path0 path1 ... pathn", index = 2)

@CommandInfo(name = "uq", usage = "uq foo.txt", index = 3)

@CommandInfo(name = "show-busy-java-threads", usage = "show-busy-java-threads, show-busy-java-threads -p <指定的Java进程Id>", index = 4)

@CommandInfo(name = "show-duplicate-java-classes", usage = "show-duplicate-java-classes -L path/to/lib_dir1", index = 5)

@CommandInfo(name = "find-in-jars", usage = "find-in-jars 'log4j\\.properties' -d /path/to/find/directory", index = 6)
public class UsefulScriptXPocketCommand extends AbstractXPocketCommand {

    @Override
    public void init(XPocketPlugin plugin) {
        super.init(plugin);
    }

    @Override
    public void invoke(XPocketProcess process) {
        XPocketProcessTemplate.execute(process, (cmd, args) -> OS.run(createExecArgs(cmd, args)));

    }

    /**
     * 创建 exec 参数
     * @param cmd useful-scripts cmd
     * @param args args
     * @return
     */
    private String[] createExecArgs(String cmd, String[] args) {
        String[] execArgs = new String[args.length + 2];
        if ("show-duplicate-java-classes".equals(cmd)) {
            execArgs[0] = "python3";
        } else {
            execArgs[0] = "sh";
        }

        execArgs[1] = PLUGIN_BIN_PATH + cmd;
        System.arraycopy(args, 0, execArgs, 2, args.length);
        return execArgs;
    }

}
