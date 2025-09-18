package com.bill.test.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.bill.test.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.cursor.Cursor;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wencai
 * @since 2025-08-22
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 根据ID查询系统配置
     *
     * @param queryWrapper 系统配置ID
     * @return SysConfig对象
     */
    @Select("SELECT * FROM sys_config ${ew.customSqlSegment}")
    SysConfig getSysConfigById(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);


    /**
     * 流式查询
     *
     * @param queryWrapper queryWrapper
     * @return SysConfig对象
     */
    @Select("SELECT * FROM sys_config ${ew.customSqlSegment}")
    Cursor<SysConfig> selectCursor(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);


    SysConfig getSysConfigByXml(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}
