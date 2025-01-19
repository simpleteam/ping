package ping;

import java.util.concurrent.ExecutionException;

import org.apache.log4j.BasicConfigurator;

import com.alarm.Alarm;
import com.alarm.TelegramAlarm;
import com.entity.Host;
import com.entity.ResultOfCheck;
import com.entity.TypeOfHost;
import com.service.CheckService;
import com.service.CheckTimeService;

public class Main {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
			
		
//		Runnable timeService = new CheckTimeService();
//		Thread t = new Thread(timeService);
//		t.start();
		
		
		CheckService sc = new CheckService();
		sc.service();

	}

}
