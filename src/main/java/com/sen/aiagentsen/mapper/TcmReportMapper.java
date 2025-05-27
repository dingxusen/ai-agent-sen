package com.sen.aiagentsen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sen.aiagentsen.model.TcmReport;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TcmReportMapper extends BaseMapper<TcmReport> {
    // 这里可以添加自定义查询方法
}
