package ping;

import java.util.concurrent.ExecutionException;

import org.apache.log4j.BasicConfigurator;

import com.alarm.Alarm;
import com.alarm.TelegramAlarm;
import com.entity.Host;
import com.entity.ResultOfCheck;
import com.entity.TypeOfHost;
import com.service.CheckService;

public class Main {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
			
		CheckService sc = new CheckService();
		sc.service();

	}

}
