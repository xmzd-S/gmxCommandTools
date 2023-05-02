import org.apache.commons.exec.*;
import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class InteractiveExec {
    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        ArrayList<Command> commands = getCommands();
        while (true) {
            System.out.println("输入工作目录的全路径：");
            Scanner scanner = new Scanner(System.in);
            String dir = scanner.next();
            File workSpace = new File(dir);
            while (true){
                System.out.println("输入0，重新更换工作目录");
                for(int i = 0 ; i< commands.size() ; i++){
                    System.out.println("输入"+commands.get(i).getId()+",调用"+commands.get(i).getContent());
                }
                int i = scanner.nextInt();
                if( i == 0){
                    break;
                } else {
                    try {
                        callCmd(commands.get(i+1).getContent(),workSpace);
                    }catch (Exception e){
                        System.out.println("输入错误");
                        Thread.sleep(1000);
                    }
                }
            }
        }
    }

    private static ArrayList<Command> getCommands() throws IOException {
        Properties properties = new Properties();
        properties.load(InteractiveExec.class.getResourceAsStream("command.properties"));
        ArrayList<Command> commands = new ArrayList<>();
        for (String key: properties.stringPropertyNames()){
            Command command = new Command();
            command.setId(Integer.valueOf(key));
            command.setContent(properties.getProperty(key));
            commands.add(command);
        }
        commands.sort(new ComparatorImpl());
        return commands;
    }


    public static void callCmd(String cmd,File workDir){
        CommandLine cmdLine =CommandLine.parse(cmd);
        DefaultExecutor executor = new DefaultExecutor();

        executor.setStreamHandler(new PumpStreamHandler(System.out, System.err, System.in));
        executor.setWorkingDirectory(workDir);
        int exitValue = 0;
        try {
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            executor.execute(cmdLine,resultHandler);
            resultHandler.waitFor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (exitValue == 0) {
            System.out.println("Command executed successfully");
        } else {
            System.out.println("Command failed with exit code " + exitValue);
        }
    }
}

