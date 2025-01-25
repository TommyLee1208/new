package com.code.mapper;

import com.code.pojo.TrainingEnrollment;
import com.code.pojo.TrainingEvent;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TrainingMapper {

    /**
     * 管理员发布培训活动
     * @param trainingEvent
     */
    @Insert("INSERT INTO training_events (event_name, start_time, end_time, create_time, location, enrolled_count) " +
            "VALUES (#{event_name}, #{start_time}, #{end_time}, #{create_time}, #{location}, #{enrolled_count})")
    void insertEvent(TrainingEvent trainingEvent);

    /**
     * 获取所有培训活动
     * @return
     */
    @Select("SELECT * FROM training_events")
    List<TrainingEvent> getAllTrainingEvents();

    /**
     * 获取活动的当前报名人数
     */
    @Select("SELECT enrolled_count FROM training_events WHERE id = #{event_id}")
    int getEnrolledCount(@Param("event_id") Integer event_id);

    /**
     * 更新活动的报名人数
     */
    @Update("UPDATE training_events SET enrolled_count = enrolled_count + 1 WHERE id = #{event_id} AND enrolled_count < 20")
    int incrementEnrolledCount(@Param("event_id") Integer event_id);
}
