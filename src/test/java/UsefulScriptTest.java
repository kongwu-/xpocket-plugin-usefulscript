import com.github.kongwu.xpocket.plugin.usefulscripts.OS;

import java.io.IOException;

public class UsefulScriptTest {
    public static void main(String[] args) throws IOException {
        String body = OS.run("java", "-version");
        System.out.println(body);

        OS.copyDirectory("C:\\Users\\jiang\\Desktop\\111","C:\\Users\\jiang\\Desktop\\222");



//        URL resource = UsefulScriptXPocketCommand.class.getClassLoader().getResource("bin/").toURI();
//        System.out.println(resource);

    }
}
