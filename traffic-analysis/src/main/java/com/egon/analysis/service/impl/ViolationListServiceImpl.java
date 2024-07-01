package com.egon.analysis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.egon.analysis.entity.po.ViolationList;
import com.egon.analysis.service.ViolationListService;
import com.egon.analysis.mappers.ViolationListMapper;
import org.springframework.stereotype.Service;

/**
* @author MrHao
* @description 针对表【t_violation_list】的数据库操作Service实现
* @createDate 2024-05-02 20:38:32
*/
@Service
public class ViolationListServiceImpl extends ServiceImpl<ViolationListMapper, ViolationList>
    implements ViolationListService{

}




