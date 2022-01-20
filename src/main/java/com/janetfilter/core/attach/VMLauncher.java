package com.janetfilter.core.attach;

import com.janetfilter.core.Launcher;
import com.janetfilter.core.utils.ProcessUtils;
import com.janetfilter.core.utils.WhereIsUtils;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.IOException;

public class VMLauncher {
    public static void attachVM(String agentFile, String pid, String args) {
        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(agentFile, args);
            vm.detach();
        } catch (IOException e) {
            if (e.getMessage().startsWith("Non-numeric value found")) {
                System.out.println("WARN: The jdk used by `ja-netfilter` does not match the attached jdk version");
            }
        } catch (Throwable e) {
            System.err.println("Attach failed: " + pid);
            e.printStackTrace(System.err);
            return;
        }

        System.out.println("ATTACHED SUCCESSFULLY: " + pid);
    }

    public static void launch(File thisJar, VMDescriptor descriptor, String args) throws Exception {
        File javaCommand = WhereIsUtils.findJava();
        if (null == javaCommand) {
            throw new Exception("Can not locate java command, unable to start attach mode.");
        }

        ProcessBuilder pb;
        double version = Double.parseDouble(System.getProperty("java.specification.version"));
        if (version > 1.8D) {
            pb = buildProcess(javaCommand, thisJar, descriptor.getId(), args);
        } else {
            File toolsJar = WhereIsUtils.findToolsJar();
            if (null == toolsJar) {
                throw new Exception("Can not locate tools.jar file, unable to start attach mode.");
            }

            pb = buildProcess(javaCommand, thisJar, descriptor.getId(), args, toolsJar);
        }

        int exitValue = ProcessUtils.start(pb);
        if (0 != exitValue) {
            throw new Exception("Attach mode failed: " + exitValue);
        }
    }

    private static ProcessBuilder buildProcess(File java, File thisJar, String id, String args) {
        String[] cmdArray = new String[]{
                java.getAbsolutePath(),
                "-Djanf.debug=" + System.getProperty("janf.debug", "0"),
                "-jar",
                thisJar.getAbsolutePath(),
                Launcher.ATTACH_ARG,
                id, args
        };

        return new ProcessBuilder(cmdArray);
    }

    private static ProcessBuilder buildProcess(File java, File thisJar, String id, String args, File toolsJar) {
        String[] cmdArray = new String[]{
                java.getAbsolutePath(),
                "-Djanf.debug=" + System.getProperty("janf.debug", "0"),
                "-Xbootclasspath/a:" + toolsJar.getAbsolutePath(),
                "-jar",
                thisJar.getAbsolutePath(),
                Launcher.ATTACH_ARG,
                id, args
        };

        return new ProcessBuilder(cmdArray);
    }
}
