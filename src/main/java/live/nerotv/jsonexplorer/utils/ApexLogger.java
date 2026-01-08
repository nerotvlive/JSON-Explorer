package live.nerotv.jsonexplorer.utils;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ApexLogger {

    private boolean showErrors = false;
    private boolean isLocked = false;
    private boolean sendDebug = false;
    private String prefix;

    public ApexLogger(String name) {
        prefix = "[%time%] (%type%) " + name + " | ";
    }

    private String getPrefix() {
        return prefix.replaceFirst("%time%", new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(Calendar.getInstance().getTime()));
    }

    public void setName(String name, boolean lock) {
        if (!isLocked) {
            isLocked = lock;
            prefix = "%time% | " + name + " | ";
        }
    }

    public void enableDebug() {
        sendDebug = true;
    }

    public void disableDebug() {
        sendDebug = false;
    }

    public boolean isDebugging() {
        return sendDebug;
    }

    public void log(String message) {
        System.out.println(getPrefix().replace("%type%", "LOG") + message);
    }

    public void dbg(String debugMessage) {
        if (sendDebug)
            System.out.println("\u001B[34m" + getPrefix().replace("%type%", "DEB") + debugMessage + "\u001B[0m");
    }

    public void deb(String debugMessage) {
        dbg(debugMessage);
    }

    public void err(String errorMessage) {
        System.out.println("\u001B[31m" + getPrefix().replace("%type%", "ERR") + errorMessage + "\u001B[0m");
        if (showErrors) {
            JOptionPane.showMessageDialog(null, errorMessage,
                    "NEXUS App (Error)", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void err(String errorMessage, boolean showError) {
        System.out.println("\u001B[31m" + getPrefix().replace("%type%", "ERR") + errorMessage + "\u001B[0m");
        if (showError) {
            JOptionPane.showMessageDialog(null, errorMessage,
                    "NEXUS App (Error)", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void printErr(String prefix, String type, String message, String reason, StackTraceElement[] cause, String... possibleFixes) {
        StringBuilder output = new StringBuilder();
        if(message!=null) {
            if(prefix == null) {
                prefix = "AN ERROR OCCURRED";
            }
            if(type == null) {
                type = "ERROR";
            }
            String s1 = "===("+prefix+")===============================================================/"+type+"/===";
            output.append(s1).append("\n");
            err(s1,false);
            output.append(message).append("\n");
            err(message,false);
            if(reason!=null) {
                output.append("Reason: ").append(reason).append("\n");
                err("Reason: "+reason,false);
            }
            if(possibleFixes!=null) {
                String p = "Possible fix(es): ";
                for(String fix:possibleFixes) {
                    output.append(p).append(fix).append("\n");
                    err(p+fix,false);
                    p = "";
                }
            }
            if(cause!=null) {
                output.append("\nCaused by:\n");
                err(" ",false);
                err("Caused by:",false);
                for(StackTraceElement element:cause) {
                    output.append(" ").append(element.toString()).append("\n");
                    err(" "+element.toString(),false);
                }
            }
            String s2 = "===/"+type+"/===============================================================("+prefix+")===";
            output.append(s2).append("\n");
            err(s2,false);
        }
        if (showErrors) {
            JOptionPane.showMessageDialog(null, output.toString(),
                    "NEXUS App (Error)", JOptionPane.ERROR_MESSAGE);
        }
    }
}