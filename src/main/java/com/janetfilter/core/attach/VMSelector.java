package com.janetfilter.core.attach;

import com.janetfilter.core.utils.DateUtils;
import com.janetfilter.core.utils.ProcessUtils;
import com.janetfilter.core.utils.WhereIsUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class VMSelector {
    private final File thisJar;
    private List<VMDescriptor> descriptors;

    public VMSelector(File thisJar) {
        this.thisJar = thisJar;
    }

    private List<VMDescriptor> getVMList() throws Exception {
        File jpsCommand = WhereIsUtils.findJPS();
        if (null == jpsCommand) {
            throw new Exception("jps command not found");
        }

        List<String> list = new ArrayList<>();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ProcessUtils.start(new ProcessBuilder(jpsCommand.getAbsolutePath(), "-lv"), bos);

        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bos.toByteArray())));
        while ((line = reader.readLine()) != null) {
            list.add(line);
        }

        String processId = ProcessUtils.currentId();
        return list.stream()
                .map(s -> {
                    String[] section = (s + "   ").split(" ", 3);
                    return new VMDescriptor(section[0].trim(), section[1].trim(), section[2].trim());
                })
                .filter(d -> !d.getId().equals(processId) && !"sun.tools.jps.Jps".equals(d.getClassName()) && !"jdk.jcmd/sun.tools.jps.Jps".equals(d.getClassName()))
                .sorted(Comparator.comparingInt(d -> Integer.parseInt(d.getId())))
                .collect(Collectors.toList());
    }

    private String getInput() throws IOException {
        return new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
    }

    private void processSelect() throws Exception {
        System.out.print("  Select: ");
        String input = getInput();

        switch (input) {
            case "Q":
            case "q":
                System.exit(0);
            case "R":
            case "r":
                System.out.println("  =========================== " + DateUtils.formatDateTime() + " ============================");
                select();
                return;
            case "":
                processSelect();
                return;
            default:
                int index;
                try {
                    index = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    invalidInput(input);
                    return;
                }

                if (index < 1) {
                    invalidInput(input);
                    return;
                }

                if (index > descriptors.size()) {
                    invalidInput(input);
                    return;
                }

                System.out.print("  Agent args: ");
                input = getInput();
                try {
                    VMLauncher.launch(thisJar, descriptors.get(index - 1), input);
                } catch (Exception e) {
                    System.err.println("> Attach to: " + index + " failed.");
                    e.printStackTrace(System.err);
                    return;
                }
                break;
        }
    }

    private void invalidInput(String input) throws Exception {
        System.err.println("> Invalid input: " + input);
        processSelect();
    }

    public void select() throws Exception {
        boolean first = null == descriptors;
        List<VMDescriptor> temp = getVMList();
        if (null != descriptors && !descriptors.isEmpty()) {
            temp.forEach(d -> d.setOld(descriptors.stream().anyMatch(d1 -> d.getId().equals(d1.getId()))));
        }

        descriptors = temp;
        System.out.println("  Java Virtual Machine List: (Select and attach" + (first ? "" : ", + means the new one") + ")");

        int index = 1;
        for (VMDescriptor d : descriptors) {
            System.out.printf("  %3d]:%s%s %s%n", index++, d.getOld() ? " " : "+", d.getId(), d.getClassName());
        }
        System.out.println("    r]: <Refresh virtual machine list>");
        System.out.println("    q]: <Quit the ja-netfilter>");

        processSelect();
    }
}
