package com.service;

import org.apache.log4j.Logger;

import com.alarm.Alarm;
import com.alarm.TelegramAlarm;
import com.entity.Host;
import com.entity.ResultOfCheck;
import com.entity.TypeOfHost;
import com.util.PingChecker;

public class CheckDbStatService implements Runnable {
	
	
	private static Logger log = Logger.getLogger(CheckTimeService.class.getName());
	private static final String URL = "https://dbstatuser:Fphks28CvWk43h@db-stat.ics.gov.ua";
	private static boolean isAvailable = true;
	private static Alarm alarm = new TelegramAlarm();
	private static final int TIMEOUT = 1000 * 60;
	
	public void run() {
		
		while(true) {
			PingChecker checker = new PingChecker();
			ResultOfCheck r = checker.check(new Host(URL,"_db_stat.ics.gov.ua_",TypeOfHost.HTTP));
				if(r.isAvailable() == false && isAvailable == true) {
					isAvailable = false;
					log.info(" " + r.getHost().getUrl() + " " + r.getHost().getName() + " DOWN");
					System.out.println(r.getHost().getUrl() + " " + r.getHost().getName() + " Down");
					alarm.alarm(r, "_DOWN");
				}
		
				if(r.isAvailable() == true && isAvailable == false) {
					isAvailable = true;
					log.info(" " + r.getHost().getUrl() + " " + r.getHost().getName() + " UP");
					System.out.println(r.getHost().getUrl() + " " + r.getHost().getName() + " Up");
					alarm.alarm(r, "_UP");
				}
				
				
				try {
					Thread.currentThread().sleep(TIMEOUT);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		}
		
	}
	
	

}
