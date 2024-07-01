package com.egon;

import com.egon.controller.AverageSpeedMonitorController;
import com.egon.controller.DangerousDriverController;
import com.egon.controller.OverSpeedController;
import com.egon.controller.TaoPaiCarController;
import com.egon.simulator.CheckPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App 
{
    public static void main( String[] args ) throws Exception {

        final Logger log = LoggerFactory.getLogger(App.class);

        Runnable averageSpeedMonitor = () -> {
            try {
                log.info("拥堵情况监控启动");
                AverageSpeedMonitorController.main(args);
            } catch (Exception e) {
                log.error("拥堵情况监控启动失败");
                throw new RuntimeException(e);
            }
        };
        Runnable dangerousDriver = () -> {
            try {
                log.info("危险驾驶监控启动");
                DangerousDriverController.main(args);
            } catch (Exception e) {
                log.error("危险驾驶监控启动失败");
                throw new RuntimeException(e);
            }
        };
        Runnable overSpeed = () -> {
            try {
                log.info("超速监控启动");
                OverSpeedController.main(args);
            } catch (Exception e) {
                log.error("超速监控启动失败");
                throw new RuntimeException(e);
            }
        };
        Runnable taoPaiCar = () -> {
            try {
                log.info("套牌监控启动");
                TaoPaiCarController.main(args);
            } catch (Exception e) {
                log.error("套牌监控启动失败");
                throw new RuntimeException(e);
            }
        };

        new Thread(averageSpeedMonitor).start();
        new Thread(dangerousDriver).start();
        new Thread(overSpeed).start();
        new Thread(taoPaiCar).start();
        CheckPoint.producer();
    }
}
