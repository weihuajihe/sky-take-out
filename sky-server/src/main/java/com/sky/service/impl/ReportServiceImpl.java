package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.constant.OrderStatus;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrdersMapper;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.service.ReportService;
import com.sky.service.UserService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：
 * @projectName sky-take-out
 * @date 2024/3/11 15:11
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UserService userService;

    @Autowired
    private WorkspaceService workspaceService;
    @Override
    public Result<TurnoverReportVO> turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        ArrayList<BigDecimal> turnoverList = new ArrayList<>();
        dateList.forEach(date->{
            LocalDateTime min = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime max = LocalDateTime.of(date, LocalTime.MAX);
            List<Orders> orders = ordersMapper.selectList(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus, OrderStatus.COMPLETED)
                    .between(Orders::getOrderTime, min, max));
            BigDecimal daily = new BigDecimal(0);
            for (Orders order : orders) {
                daily = daily.add(order.getAmount());
            }
            turnoverList.add(daily);

        });
        return Result.success(TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build());

    }

    @Override
    public Result<UserReportVO> userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        ArrayList<Long> newUserList = new ArrayList<>();
        ArrayList<Long> totalUserList = new ArrayList<>();
        dateList.forEach(date -> {
            LocalDateTime min = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime max = LocalDateTime.of(date, LocalTime.MAX);
            long totalCount = userService.count(new LambdaQueryWrapper<User>().lt(User::getCreateTime, max));
            long newCount = userService.count(new LambdaQueryWrapper<User>().between(User::getCreateTime,min, max));
            newUserList.add(newCount);
            totalUserList.add(totalCount);

        });
        return Result.success(UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build());

    }

    @Override
    public Result<OrderReportVO> orderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        ArrayList<Integer> orderCountList = new ArrayList<>();
        ArrayList<Integer> validOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        Double orderCompletionRate = 0.0;

        dateList.forEach(date -> {
            LocalDateTime min = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime max = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount =
                    (int)ordersService.count(new LambdaQueryWrapper<Orders>().between(Orders::getOrderTime,
                    min
                    , max));
            
            orderCountList.add(orderCount);
            Integer validCount = (int)ordersService.count(new LambdaQueryWrapper<Orders>().between(Orders::getOrderTime, min
                    , max).eq(Orders::getStatus, OrderStatus.COMPLETED));
            
            validOrderCountList.add(validCount);
        });
        for (Integer orderCount : orderCountList) {
            totalOrderCount += orderCount;
        }
        for (Integer validCount : validOrderCountList) {
            validOrderCount+=validCount;
        }
        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return Result.success(OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .validOrderCount(validOrderCount)
                .build());
    }

    @Override
    public Result<SalesTop10ReportVO> saleStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime min = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime max = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> saleTop = ordersMapper.getSaleTop(min, max);
        List<String> names = saleTop.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = saleTop.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return Result.success(SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(names, ","))
                .numberList(StringUtils.join(numbers, ","))
                .build());
    }

    @Override
    public void exportData(HttpServletResponse response) {
        //1.查询前30日数据库，获取营业数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        //查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //2.通过POI,将数据写入excel文件
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {

            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //时间
            sheet.getRow(1).getCell(2).setCellValue("时间："+begin+"至"+end);

            //填充概览数据
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                begin = begin.plusDays(1);
                //查询某一天的数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN),
                        LocalDateTime.of(begin,
                        LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(begin.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());

            }
            //通过输出流将excel文件通过response响应下载到客户端
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            out.close();
            excel.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    public List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;

    }


}
