import lombok.Data;
import lombok.ToString;

@Data
public class Command {
    private int id;
    private String content;

    @Override
    public String toString() {
        return "Command{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}'+"\n";
    }
}
