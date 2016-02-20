package be.uantwerpen;

import be.uantwerpen.models.Cloud;
import be.uantwerpen.models.User;
import be.uantwerpen.services.CloudManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudManagerApplication implements CommandLineRunner
{
	public static void main(String[] args)
	{
		SpringApplication.run(CloudManagerApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception
	{
		Cloud oneCloud = new Cloud("http://146.175.139.50:2633/RPC2", new User("thomas.huybrechts", "thomashuybrechts"));

		CloudManager cloudManager = new CloudManager(oneCloud);

		if(cloudManager.loadServerList())
		{
			cloudManager.printServerStatus();
		}

		System.exit(0);
		/*
		ServerMonitor serverMonitor = new ServerMonitor(2000);

		serverMonitor.addServer(new Server("146.175.139.73", 10));

		serverMonitor.startMonitor();

		while(true)
		{
			System.out.println("CPUload: " + serverMonitor.getServers().get(0).getAverageCPULoad(10));
			Thread.sleep(5000);
		}*/
	}
}
