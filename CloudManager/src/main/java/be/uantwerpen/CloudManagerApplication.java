package be.uantwerpen;

import be.uantwerpen.models.User;
import be.uantwerpen.services.ApplicationManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudManagerApplication
{
	private static ApplicationManager application;

	private static String cloudIP;
	private static User cloudUser;

	public static void main(String[] args)
	{
		//Default values
		cloudIP = "127.0.0.1:9869";
		cloudUser = new User();

		application = new ApplicationManager();

		//Start Spring application
		SpringApplication.run(CloudManagerApplication.class, args);

		//Read arguments
		argsCommand(args);

		//Set cloud parameters
		application.setCloud(cloudIP, cloudUser);

		//System ready to operate
		ApplicationManager.systemReady();
	}

/*
	@Override
	public void run(String... strings) throws Exception
	{
		/*Cloud oneCloud = new Cloud("http://146.175.139.50:2633/RPC2", new User("thomas.huybrechts", "thomashuybrechts"));

		CloudManager cloudManager = new CloudManager(oneCloud);

		if(cloudManager.loadServerList())
		{
			cloudManager.printServerStatus();
		}

		/*
		ServerMonitor serverMonitor = new ServerMonitor(2000);

		serverMonitor.addServer(new Server("146.175.139.73", 10));

		serverMonitor.startMonitor();

		while(true)
		{
			System.out.println("CPUload: " + serverMonitor.getServers().get(0).getAverageCPULoad(10));
			Thread.sleep(5000);
		}
	}*/

	private static void argsCommand(String[] args)
	{
		for(int i = 0; i < args.length; i++)
		{
			if(!args[i].contains("spring"))
			{
				switch(args[i].toLowerCase())
				{
					case "-ip":
						if(args[i + 1].trim().split(":").length == 2)
						{
							cloudIP = args[i + 1].trim();
							i++;
						}
						else
						{
							System.out.println("Invalid arguments for -ip");
						}
						break;
					case "-user":
						if(args[i + 1].trim().split(":").length == 2)
						{
							cloudUser = new User(args[i + 1].split(":", 2)[0].trim(), args[i + 1].split(":", 2)[1].trim());
							i++;
						}
						else
						{
							System.out.println("Invalid arguments for -user");
						}
						break;
					case "-help":
						System.out.println("Cloud Manager application - 2016");
						System.out.println("Starts the manager application for monitoring OpenNebula VM's with the SystemTracker software installed.");
						System.out.println("\nOptions:");
						System.out.println("\t-ip {ip}:{port}\t\tThe given ip to connect to the cloud server.");
						System.out.println("\t-user {name}:{password}\tThe given user credentials to access the cloud server.");
						System.out.println("\t-help\t\t\tDisplays the current help options.");
						System.exit(0);
					default:
						System.out.println("Unknown option '" + args[i] + "'");
						break;
				}
			}
		}
	}
}
