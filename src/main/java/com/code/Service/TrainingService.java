package com.code.Service;

import com.code.pojo.TrainingEnrollment;
import com.code.pojo.TrainingEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TrainingService {

    /**
     * 管理员发布培训活动
     * @param trainingEvent
     */
    void publishTrainingEvent(TrainingEvent trainingEvent);

    /**
     * 管理员查看所有培训活动
     * @return
     */
    List<TrainingEvent> getAllTrainingEvents();


    /**
     * 用户报名培训活动
     * @param event_id
     * @return
     */
    boolean enrollTrainingEvent(Integer event_id);
}
