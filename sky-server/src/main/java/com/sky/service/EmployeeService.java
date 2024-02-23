package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.Result;

public interface EmployeeService extends IService<Employee> {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工业务接口
     * @param employeeDTO
     * @return
     */
    Result insertEmp(EmployeeDTO employeeDTO);

    /**
     * 分页查询的实现
     * @param employeePageQueryDTO
     * @return
     */
    Result pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 员工状态的修改业务
     * @param id
     * @param status
     * @return
     */
    Result updateStatus(Long id, Integer status);

    /**
     * 根据id查询员工信息回显
     * @param id
     * @return
     */
    Result selectById(Long id);

    /**
     * 修改员工信息
     * @return
     */
    Result updateEmp(EmployeeDTO employeeDTO);
}
