package com.bill.test.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bill.test.entity.Country;
import com.bill.test.mapper.CountryMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wencai
 * @since 2025-08-25
 */
@Service
public class CountryService extends ServiceImpl<CountryMapper, Country> {

}
