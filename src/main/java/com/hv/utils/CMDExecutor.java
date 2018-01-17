package com.hv.utils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by shanush on 1/15/2018.
 */
//public class CMDExecutor {
//
//
//    public static void main(String[] args) {
//        Process process = null;
//        try {
//            String ss = null;
//            process = Runtime.getRuntime().exec("cmd.exe /c start");
//
//
//            BufferedReader stdInput  = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            // read the output from the command
//            System.out.println("Here is the standard output of the command:\n");
//            int s ;
//               while ((s = stdInput.read()) != -1) {
//                   System.out.println(s);
//               }
//
//            // read any errors from the attempted command
//            System.out.println("Here is the standard error of the command (if any):\n");
//            while ((s = stdError.read()) != -1) {
//                System.out.println(s);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//       } finally {
//           if (process != null)
//               process.destroy();
//       }
//
//    }
//
//}
public class CMDExecutor {
    public StreamWrapper getStreamWrapper(InputStream is, String type){
        return new StreamWrapper(is, type);
    }
    private class StreamWrapper extends Thread {
        InputStream is = null;
        String type = null;
        String message = null;

        public String getMessage() {
            return message;
        }

        StreamWrapper(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                String line = null;
                while ( (line = br.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                message = buffer.toString();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }


    // this is where the action is
    public static String executeCmdComand(String comand) {
        Runtime rt = Runtime.getRuntime();
        CMDExecutor rte = new CMDExecutor();
        StreamWrapper error, output = null;

        try {
            Process proc = rt.exec(comand);
            error = rte.getStreamWrapper(proc.getErrorStream(), "ERROR");
            output = rte.getStreamWrapper(proc.getInputStream(), "OUTPUT");
            int exitVal = 0;

            error.start();
            output.start();
            error.join(3000);
            output.join(3000);
            proc.waitFor(15000, TimeUnit.MILLISECONDS);
            System.out.println("Output: "+output.message+"\nError: "+error.message);
            return output.message;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return output.message;
    }

    public static void main(String[] args) {
        String result = CMDExecutor.executeCmdComand("docker ps");
        if (result.contains("selenium/hub")) {
            List<String> containersRunning = Arrays.asList(result.split("\n"));
            for (int i = 1; i < containersRunning.size(); i++) {
                System.out.println(containersRunning.get(i));
            }
        } else {
            System.out.println("Starting new selenium-hub with docker compose file");
            CMDExecutor.executeCmdComand("docker-compose up");
        }
        String result1 = CMDExecutor.executeCmdComand("docker ps");
        if (result1.contains("selenium/hub")) {
            System.out.println("Selenium hub started");

        }
//        System.out.println(result.contains("selenium/hub"));
//        System.out.println(result.split("\n")[2]);

//        if(CMDExecutor.executeCmdComand("docker ps").contains("selenium/hub"));
    }
}
