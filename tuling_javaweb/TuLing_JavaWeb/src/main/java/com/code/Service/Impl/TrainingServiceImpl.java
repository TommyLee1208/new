package com.code.Service.Impl;

import com.code.Service.TrainingService;
import com.code.mapper.TrainingMapper;
import com.code.pojo.TrainingEnrollment;
import com.code.pojo.TrainingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    private TrainingMapper trainingMapper;
    /**
     * 管理发布培训活动
     * @param trainingEvent
     */



    @Override
    public void publishTrainingEvent(TrainingEvent trainingEvent) {
        // 设置默认值
        trainingEvent.setCreate_time(LocalDateTime.now());
        trainingEvent.setEnrolled_count(0); // 初始报名人数为0

        // 保存培训活动信息到数据库
        trainingMapper.insertEvent(trainingEvent);
    }

    /**
     * 获取所有培训活动信息
     * @return
     */
    @Override
    public List<TrainingEvent> getAllTrainingEvents() {
        return trainingMapper.getAllTrainingEvents();
    }

    @Override
    public boolean enrollTrainingEvent(Integer event_id) {
        // 检查活动是否已满员
        int currentCount = trainingMapper.getEnrolledCount(event_id);
        if (currentCount >= 20) {
            return false; // 活动已满员
        }

        // 尝试更新报名人数
        int updatedRows = trainingMapper.incrementEnrolledCount(event_id);

        return updatedRows > 0; // 返回是否成功更新
    }
}
