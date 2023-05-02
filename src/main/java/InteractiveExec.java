import org.apache.commons.exec.*;
import org.apache.commons.exec.util.DebugUtils;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class InteractiveExec {
    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        while (true) {
            System.out.println("输入工作目录的全路径：");
            Scanner scanner = new Scanner(System.in);
            String dir = scanner.next();
            File workSpace = new File(dir);
            String[] cmds = new String[]{
                    "gmx  grompp -f ions.mdp -c complex_solv.gro -p topol.top -o ions.tpr",
                    "gmx  genion -s ions.tpr -o complex_solv_ions.gro -p topol.top -pname NA -nname CL -neutral",
                    "gmx  grompp -f em.mdp -c complex_solv_ions.gro -p topol.top -maxwarn 1 -o em.tpr ",
                    "gmx  mdrun -v -deffnm em",
                    "gmx  make_ndx -f UNL.gro -o index_peiti.ndx",
                    "gmx  genrestr -f UNL.gro -n index_peiti.ndx -o posre_peiti.itp -fc 1000 1000 1000 ",
                    "gmx  make_ndx -f em.gro -o index.ndx ",
                    "gmx grompp -f nvt.mdp -c em.gro -r em.gro -p topol.top -n index.ndx -o nvt.tpr ",
                    "gmx mdrun -gpu_id 0 -ntomp 7 -nb gpu -v -deffnm nvt",
                    "gmx grompp -f npt.mdp -c nvt.gro -t nvt.cpt -r nvt.gro -p topol.top -n index.ndx -o npt.tpr -maxwarn 1 ",
                    "gmx mdrun -gpu_id 0 -ntomp 7 -nb gpu -v -deffnm npt ",
                    "gmx grompp -f md.mdp -c npt.gro -t npt.cpt -p topol.top -n index.ndx -o md_0_100.tpr ",
                    "gmx mdrun -gpu_id 0 -ntomp 7 -nb gpu -v -deffnm md_0_100",
                    "gmx trjconv -s md_0_100.tpr -f md_0_100.xtc -o md_0_100_center.xtc -center -pbc mol -ur compact",
                    "gmx rms -s md_0_100.tpr -f md_0_100_center.xtc -o rmsd.xvg -tu ns",
                    "gmx gyrate -s md_0_100.tpr -f md_0_100_center.xtc -o gyrate.xvg",
                    "gmx rmsf -s  md_0_100.tpr -f md_0_100_center.xtc -o fws-rmsf.xvg -ox fws-avg.pdb -res -oq fws-bfac.pdb",
                    "gmx sasa -f md_0_100_center.xtc -s md_0_100.tpr -o area.xvg -odg dgsolv.xvg -or resarea.xvg -oa atomarea.xvg -surface  -tv volume.xvg",
            };
            while (true){
                System.out.println("输入0，重新更换工作目录");
                for(int i = 1 ; i<= cmds.length ; i++){
                    System.out.println("输入"+i+",调用"+cmds[i-1]);
                }
                System.out.println("输入50，依次调用ions到md");
                System.out.println("输入100，依次调用nvt到md");
                System.out.println("输入200，依次调用分析的5个命令");
                System.out.println("python");
                int i = scanner.nextInt();
                if( i == 0){
                    break;
                } else if (i == 1) {
                    callCmd(cmds[0],workSpace);
                }else if (i == 2){
                    callCmd(cmds[1],workSpace);
                } else if (i == 3) {
                    callCmd( cmds[2],workSpace);
                } else if ( i == 4) {
                    callCmd(cmds[3],workSpace);
                } else if ( i == 5) {
                    callCmd(cmds[4], workSpace);
                } else if (i == 6) {
                    callCmd(cmds[5],workSpace);
                } else if (i == 7) {
                    callCmd(cmds[6],workSpace);
                }else if (i == 8) {
                    callCmd(cmds[7],workSpace);
                }else if (i == 9) {
                    callCmd(cmds[8],workSpace);
                }else if (i == 10) {
                    callCmd(cmds[9],workSpace);
                }else if (i == 11) {
                    callCmd(cmds[10],workSpace);
                } else if (i == 12) {
                    callCmd(cmds[11],workSpace);
                }else if (i == 13) {
                    callCmd(cmds[12],workSpace);
                }else if (i == 14) {
                    callCmd(cmds[13],workSpace);
                }else if (i == 15) {
                    callCmd(cmds[14],workSpace);
                }else if (i == 16) {
                    callCmd(cmds[15],workSpace);
                }else if (i == 17) {
                    callCmd(cmds[16],workSpace);
                }else if (i == 18) {
                    callCmd(cmds[18],workSpace);
                }else if (i == 50) {
                    for (int a = 0 ; a<= 12;a++){
                        callCmd(cmds[a],workSpace);
                    }

                } else if (i == 100) {
                    for (int a = 8 ; a<= 13;a++){
                        callCmd(cmds[a],workSpace);
                    }
                } else if(i == 200){
                    for (int a = 13 ; a<= 17;a++){
                        callCmd(cmds[a],workSpace);
                    }
                }
                else if(i == 300){
                    String output = new ProcessExecutor().command("python")
                            .readOutput(true).redirectInput(System.in).redirectError(System.out).execute()
                            .outputUTF8();

                    System.out.println(output);
                }
                else {
                    System.out.println("输入错误，请重新输入！！！！！！");
                }
            }
        }
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

