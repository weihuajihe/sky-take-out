package com.sky.service;

import com.sky.result.Result;
import com.sky.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：
 * @projectName sky-take-out
 * @date 2024/3/11 15:10
 */
public interface ReportService {
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    Result<TurnoverReportVO> turnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    Result<UserReportVO> userStatistics(LocalDate begin, LocalDate end);

    Result<OrderReportVO> orderStatistics(LocalDate begin, LocalDate end);

    Result<SalesTop10ReportVO> saleStatistics(LocalDate begin, LocalDate end);

    /**
     * 导出报表
     * @param response
     */
    void exportData(HttpServletResponse response);
}
