package be.uantwerpen.terminal;

import be.uantwerpen.services.ApplicationManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Thomas on 21/02/2016.
 */
public class Terminal
{
    private ApplicationManager application;

    private TerminalReader terminalReader;

    public Terminal(ApplicationManager application)
    {
        this.application = application;

        terminalReader = new TerminalReader();

        terminalReader.getObserver().addObserver(new Observer()
        {
            @Override
            public void update(Observable source, Object object)
            {
                executeCommand((String) object);
            }
        });
    }

    public void printTerminal(String message)
    {
        System.out.println(message);
    }

    public void printTerminalInfo(String message)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        System.out.println("[INFO - " + timeFormat.format(calendar.getTime()) + "] " + message);
    }

    public void printTerminalError(String message)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        System.out.println("[ERROR - " + timeFormat.format(calendar.getTime()) + "] " + message);
    }

    public void activateTerminal()
    {
        new Thread(terminalReader).start();
    }

    private void executeCommand(String commandString)
    {
        commandString = commandString.trim();

        if(commandString != null && !commandString.equals(""))
        {
            String command = commandString.split(" ", 2)[0].toLowerCase();

            switch(command)
            {
                case "exit":
                    exitSystem();
                    break;
                case "createvm":
                    if(commandString.split(" ", 3).length <= 2)
                    {
                        printTerminalInfo("Missing arguments! 'createVM {id | name} {value}'");
                    }
                    else
                    {
                        if(commandString.split(" ", 3)[1].equals("id"))
                        {
                            instantiateVM(Integer.parseInt(commandString.split(" ", 3)[2]));
                        }
                        else if(commandString.split(" ", 3)[1].equals("name"))
                        {
                            instantiateVM(commandString.split(" ", 3)[2]);
                        }
                        else
                        {
                            printTerminalInfo("Unknown option: '" + commandString.split(" ", 3)[1] + "'. Options: {id | name}");
                        }
                    }
                    break;
                case "deletevm":
                    if(commandString.split(" ", 2).length <= 1)
                    {
                        printTerminalInfo("Missing arguments! 'deleteVM {id}'");
                    }
                    else
                    {
                        deleteVM(Integer.parseInt(commandString.split(" ", 2)[1]));
                    }
                    break;
                case "showservers":
                    printServerList();
                    break;
                case "showtemplates":
                    printTemplatePool();
                    break;
                case "update":
                    if(commandString.split(" ", 2).length == 2)
                    {
                        if(commandString.split(" ", 2)[1].equals("force"))
                        {
                            forceUpdateCloudInfo();
                        }
                        else
                        {
                            printTerminalInfo("Unknown option for update: '" + commandString.split(" ", 2)[1]);
                        }
                    }
                    else
                    {
                        updateCloudInfo();
                    }
                    break;
                case "set":
                    if(commandString.split(" ", 3).length <= 2)
                    {
                        if(commandString.contains("help") || commandString.contains("?"))
                        {
                            printHelp("set");
                        }
                        else
                        {
                            printTerminalInfo("Missing arguments! 'set {property} {value}'");
                        }
                    }
                    else
                    {
                        String property = commandString.split(" ", 3)[1];
                        String value = commandString.split(" ", 3)[2];

                        if(setProperty(property, value))
                        {
                            printTerminalInfo("Property: " + property + " --> OK");
                        }
                        else
                        {
                            printTerminalInfo("Property: " + property + " --> ERROR");
                        }
                    }
                    break;
                case "get":
                    if(commandString.split(" ", 2).length <= 1)
                    {
                        printTerminalInfo("Missing arguments! 'get {property}'");
                    }
                    else
                    {
                        String property = commandString.split(" ", 2)[1];

                        if(property.contains("help") || property.contains("?"))
                        {
                            printHelp("get");
                        }
                        else
                        {
                            String value = getProperty(property);

                            printTerminalInfo("Property: " + property + " --> Value: " + value);
                        }
                    }
                    break;
                case "help":
                case "?":
                    printHelp("");
                    break;
                default:
                    printTerminalInfo("Command: '" + command + "' is not recognized.");
                    break;
            }
        }
        activateTerminal();
    }

    private void exitSystem()
    {
        application.exitSystem();
    }

    private boolean setProperty(String property, String value)
    {
        switch(property)
        {
            case "ip":
                application.setCloudIP(value);
                return true;
            case "user":
                if(value.trim().split(":").length == 2)
                {
                    String username = value.trim().split(":")[0];
                    String password = value.trim().split(":")[1];

                    application.setCloudUser(username, password);

                    return true;
                }
                else
                {
                    printTerminalInfo("Missing arguments! 'set User {username} {password}'");
                    return false;
                }
            default:
                return false;
        }
    }

    private String getProperty(String property)
    {
        switch(property)
        {
            case "ip":
                return application.getCloudIP();
            case "user":
                return application.getCloudUsername();
            default:
                return "UNKNOWN - Property does not exist.";
        }
    }

    private void instantiateVM(int templateId)
    {
        if(application.instantiateVM(templateId))
        {
            printTerminalInfo("VM successful created!");
        }
        else
        {
            printTerminalError("VM could not be instantiated!");
        }
    }

    private void instantiateVM(String templateName)
    {
        if(application.instantiateVM(templateName))
        {
            printTerminalInfo("VM successful created!");
        }
        else
        {
            printTerminalError("VM could not be instantiated!");
        }
    }

    private void deleteVM(int vmId)
    {
        if(application.deleteVM(vmId))
        {
            printTerminalInfo("VM successful deleted!");
        }
        else
        {
            printTerminalError("VM could not be deleted!");
        }
    }

    private void updateCloudInfo()
    {
        application.updateCloudInfo();
    }

    private void forceUpdateCloudInfo()
    {
        application.clearCloudInfo();
        application.updateCloudInfo();
    }

    private void printServerList()
    {
        application.printServersStatus();
    }

    private void printTemplatePool()
    {
        application.printTemplatePool();
    }

    private void printHelp(String command)
    {
        switch(command)
        {
            case "set":
                printTerminal("SET {property} {value}");
                printTerminal("----------------------");
                printTerminal("'ip' : set the ip address of the cloud server.");
                printTerminal("'user' : set the user credentials to login to the cloud server.");
                printTerminal("'help' / '?' : show all configurable properties.\n");
                break;
            case "get":
                printTerminal("GET {property}");
                printTerminal("--------------");
                printTerminal("'ip' : get the ip address of the cloud server.");
                printTerminal("'user' : get the user credentials to login to the cloud server.");
                printTerminal("'help' / '?' : show all configurable properties.\n");
                break;
            default:
                printTerminal("Available commands:");
                printTerminal("-------------------");
                printTerminal("'showServers' : show the list of all VMs on the cloud.");
                printTerminal("'showTemplates' : show the list of all templates on the cloud.");
                printTerminal("'update [force]' : retrieve the latest information of the cloud. Use [force] to clear the list and recreate the data.");
                printTerminal("'createVM {id | name} {value}' : instantiate a new VM from a known template with id or name.");
                printTerminal("'deleteVM {id}' : delete an existing VM on the cloud with the given id. CANNOT BE UNDONE!");
                printTerminal("'set {parameter} {value}' : set a value for a given property key.");
                printTerminal("'get {parameter}' : get the value of a given property key.");
                printTerminal("'stop' : shutdown the application.");
                printTerminal("'help' / '?' : show all available commands.\n");
                break;
        }
    }
}
