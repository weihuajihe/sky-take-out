package com.sky.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
    implements EmployeeService{

    @Autowired
    private EmployeeMapper employeeMapper;



    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();
        System.out.println(DigestUtils.md5DigestAsHex(password.getBytes()));

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //
        if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public Result insertEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //对象的属性拷贝
        BeanUtils.copyProperties(employeeDTO,employee);
        //设置账号状态
        employee.setStatus(StatusConstant.ENABLE);
        //设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置创建时间和修改事件
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //设置创建人和修改人的ID
        //  interceptor拦截器中获得这次请求线程中的id  使用TheadLocal
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());
        int rows = employeeMapper.insert(employee);
        if(rows == 0){
            return Result.error("添加员工失败！");
        }
        return Result.success();

    }

    @Override
    public Result pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(!StringUtils.isEmpty(employeePageQueryDTO.getName()),Employee::getName,
                employeePageQueryDTO.getName());
        Page<Employee> page = new Page<>(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> employeePage = employeeMapper.selectPage(page, lambdaQueryWrapper);
        long total = employeePage.getTotal();
        List<Employee> records = employeePage.getRecords();
        PageResult pageResult = new PageResult(total, records);
        return Result.success(pageResult);
    }

    @Override
    public Result updateStatus(Long id, Integer status) {
        Employee employee = employeeMapper.selectById(id);
        employee.setStatus(status);
        int rows = employeeMapper.updateById(employee);
        if(rows == 0){
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
        return Result.success();
    }

    @Override
    public Result selectById(Long id) {
        Employee employee = employeeMapper.selectById(id);


        if(null != employee){

            //隐藏密码

            employee.setPassword(PasswordConstant.HIDE_PASSWORD);
            return Result.success(employee);
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    @Override
    public Result updateEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //BeanUtils对象属性拷贝方法
        //DigestUtils数据库MD5加密
        BeanUtils.copyProperties(employeeDTO,employee);
        employee.setUpdateTime(LocalDateTime.now());
        //ThreadLocal
        employee.setUpdateUser(BaseContext.getCurrentId());
        int row = employeeMapper.updateById(employee);
        return Result.success();
    }

}
