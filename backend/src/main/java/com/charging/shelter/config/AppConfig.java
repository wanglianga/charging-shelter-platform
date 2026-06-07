package com.charging.shelter.config;

import com.charging.shelter.entity.*;
import com.charging.shelter.enums.*;
import com.charging.shelter.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class AppConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public CommandLineRunner initData(
            UserService userService,
            ShelterService shelterService,
            SocketService socketService,
            FeeRuleService feeRuleService,
            AlarmService alarmService,
            InspectionService inspectionService,
            ReservationService reservationService,
            OrderService orderService
    ) {
        return args -> {
            initUsers(userService);
            initSheltersAndSockets(shelterService, socketService);
            initFeeRules(feeRuleService);
            initAlarms(alarmService);
            initInspections(inspectionService);
        };
    }

    private void initUsers(UserService userService) {
        if (userService.findAll().isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setRealName("系统管理员");
            admin.setPhone("13800000000");
            admin.setRole(UserRole.PROPERTY);
            userService.save(admin);

            User owner1 = new User();
            owner1.setUsername("owner1");
            owner1.setPassword("owner123");
            owner1.setRealName("张三");
            owner1.setPhone("13800000001");
            owner1.setRole(UserRole.OWNER);
            owner1.setAddress("1号楼1单元101");
            owner1.setCarNumber("京A12345");
            userService.save(owner1);

            User owner2 = new User();
            owner2.setUsername("owner2");
            owner2.setPassword("owner123");
            owner2.setRealName("李四");
            owner2.setPhone("13800000002");
            owner2.setRole(UserRole.OWNER);
            owner2.setAddress("2号楼3单元202");
            owner2.setCarNumber("京B67890");
            userService.save(owner2);

            User operator = new User();
            operator.setUsername("operator");
            operator.setPassword("operator123");
            operator.setRealName("王运维");
            operator.setPhone("13900000001");
            operator.setRole(UserRole.OPERATOR);
            userService.save(operator);

            User officer = new User();
            officer.setUsername("officer");
            officer.setPassword("officer123");
            officer.setRealName("赵安全");
            officer.setPhone("13900000002");
            officer.setRole(UserRole.SAFETY_OFFICER);
            userService.save(officer);
        }
    }

    private void initSheltersAndSockets(ShelterService shelterService, SocketService socketService) {
        if (shelterService.findAll().isEmpty()) {
            Shelter shelter1 = new Shelter();
            shelter1.setName("1号充电车棚");
            shelter1.setLocation("小区北门东侧");
            shelter1.setTotalSockets(10);
            shelter1.setAvailableSockets(8);
            shelter1.setDescription("主要服务1-3号楼业主");
            shelter1.setLatitude(39.9042);
            shelter1.setLongitude(116.4074);
            shelterService.save(shelter1);

            Shelter shelter2 = new Shelter();
            shelter2.setName("2号充电车棚");
            shelter2.setLocation("小区西门南侧");
            shelter2.setTotalSockets(8);
            shelter2.setAvailableSockets(6);
            shelter2.setDescription("主要服务4-6号楼业主");
            shelter2.setLatitude(39.9050);
            shelter2.setLongitude(116.4060);
            shelterService.save(shelter2);

            for (int i = 1; i <= 10; i++) {
                ChargingSocket socket = new ChargingSocket();
                socket.setSocketCode("S1-" + String.format("%02d", i));
                socket.setShelterId(1L);
                socket.setLocation("车位" + i);
                socket.setStatus(i <= 8 ? SocketStatus.AVAILABLE : SocketStatus.CHARGING);
                socket.setPowerRating(7.0);
                socket.setQrCode("QR-S1-" + String.format("%02d", i));
                socketService.save(socket);
            }

            for (int i = 1; i <= 8; i++) {
                ChargingSocket socket = new ChargingSocket();
                socket.setSocketCode("S2-" + String.format("%02d", i));
                socket.setShelterId(2L);
                socket.setLocation("车位" + i);
                socket.setStatus(i <= 6 ? SocketStatus.AVAILABLE : SocketStatus.FAULTY);
                socket.setPowerRating(7.0);
                socket.setQrCode("QR-S2-" + String.format("%02d", i));
                socketService.save(socket);
            }
        }
    }

    private void initFeeRules(FeeRuleService feeRuleService) {
        if (feeRuleService.findAll().isEmpty()) {
            FeeRule rule = new FeeRule();
            rule.setName("标准收费规则");
            rule.setUnitPrice(1.5);
            rule.setBaseFee(2.0);
            rule.setOvertimePenalty(5.0);
            rule.setFreeMinutes(30);
            rule.setMaxChargingHours(8);
            rule.setActive(true);
            rule.setDescription("小区统一充电收费标准");
            feeRuleService.save(rule);
        }
    }

    private void initAlarms(AlarmService alarmService) {
        if (alarmService.findAll().isEmpty()) {
            Alarm alarm1 = new Alarm();
            alarm1.setType(AlarmType.SOCKET_TRIP);
            alarm1.setStatus(AlarmStatus.PENDING);
            alarm1.setShelterId(1L);
            alarm1.setSocketId(9L);
            alarm1.setDescription("1号车棚9号插座跳闸");
            alarm1.setLocation("1号车棚车位9");
            alarmService.createAlarm(alarm1);

            Alarm alarm2 = new Alarm();
            alarm2.setType(AlarmType.FLYING_WIRE_CHARGING);
            alarm2.setStatus(AlarmStatus.PROCESSING);
            alarm2.setShelterId(2L);
            alarm2.setDescription("2号车棚附近发现飞线充电");
            alarm2.setLocation("2号车棚西侧");
            alarm2.setHandlerId(5L);
            alarmService.createAlarm(alarm2);
        }
    }

    private void initInspections(InspectionService inspectionService) {
        if (inspectionService.findAll().isEmpty()) {
            Inspection inspection1 = new Inspection();
            inspection1.setType(InspectionType.NIGHT_PATROL);
            inspection1.setOfficerId(5L);
            inspection1.setShelterId(1L);
            inspection1.setLocation("1号车棚");
            inspection1.setHasIssue(false);
            inspection1.setInspectionTime(LocalDateTime.now().minusHours(8));
            inspectionService.save(inspection1);

            Inspection inspection2 = new Inspection();
            inspection2.setType(InspectionType.FIRE_EXIT);
            inspection2.setOfficerId(5L);
            inspection2.setShelterId(2L);
            inspection2.setLocation("2号车棚消防通道");
            inspection2.setHasIssue(true);
            inspection2.setIssueDescription("消防通道有杂物堆放");
            inspection2.setInspectionTime(LocalDateTime.now().minusHours(4));
            inspectionService.save(inspection2);
        }
    }
}
