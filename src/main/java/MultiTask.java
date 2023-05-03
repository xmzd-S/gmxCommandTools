import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;

public class MultiTask {
    public static ArrayList<Task> getCommands() throws IOException {
        Properties properties = new OrderedProperties();
        properties.load(new FileInputStream("/usr/local/gromacs/bin/MultiTask.properties"));
        ArrayList<Task> commands = new ArrayList<>();
        int i = 1;
        for (String workDir: properties.stringPropertyNames()){
            Task task = new Task();
            task.setId(i++);
            task.setWorkDir(workDir);
            task.setCommand(properties.getProperty(workDir));
            commands.add(task);
        }
        commands.sort(new Comparator<Task>(){

            @Override
            public int compare(Task o1, Task o2) {
                return o1.getId() > o2.getId()?1:-1;
            }
        });
        return commands;
    }
}
